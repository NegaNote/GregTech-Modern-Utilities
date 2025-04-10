package net.neganote.gtutilities.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IExplosionMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.neganote.gtutilities.common.materials.UtilMaterials;
import net.neganote.gtutilities.config.UtilConfig;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;

// A lot of this is just from the original normal active transformer
public class QuantumActiveTransformerMachine extends WorkableElectricMultiblockMachine
                                             implements IControllable, IExplosionMachine, IFancyUIMachine,
                                             IDisplayUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            QuantumActiveTransformerMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private IEnergyContainer powerOutput;
    private IEnergyContainer powerInput;
    protected ConditionalSubscriptionHandler converterSubscription;

    @Persisted
    @DescSynced
    private BlockPos coolantHatchPos;

    @Persisted
    @DescSynced
    private int frequency;

    @Persisted
    @DescSynced
    private int coolantTimer = 0;

    public QuantumActiveTransformerMachine(IMachineBlockEntity holder) {
        super(holder);
        this.powerOutput = new EnergyContainerList(new ArrayList<>());
        this.powerInput = new EnergyContainerList(new ArrayList<>());

        this.converterSubscription = new ConditionalSubscriptionHandler(this, this::convertEnergyTick,
                this::isSubscriptionActive);

        this.frequency = 0;
    }

    public void convertEnergyTick() {
        if (isWorkingEnabled()) {
            getRecipeLogic()
                    .setStatus(isSubscriptionActive() ? RecipeLogic.Status.WORKING : RecipeLogic.Status.SUSPEND);
        }
        if (isWorkingEnabled() && getRecipeLogic().getStatus() == RecipeLogic.Status.WORKING &&
                UtilConfig.coolantEnabled() && coolantTimer == 0) {

            FluidHatchPartMachine coolantHatch = Objects.requireNonNull(
                    (FluidHatchPartMachine) getMachine(Objects.requireNonNull(getLevel()), coolantHatchPos));

            FluidStack coolant = coolantHatch.tank.getFluidInTank(0);
            int amountToDrain = calculateCoolantDrain();
            if (coolant.getFluid() == UtilMaterials.QuantumCoolant.getFluid() && coolant.getAmount() >= amountToDrain) {
                coolantHatch.tank.handleRecipe(IO.IN, null,
                        List.of(FluidIngredient.of(amountToDrain, UtilMaterials.QuantumCoolant.getFluid())), null,
                        false);
            } else {
                if (!ConfigHolder.INSTANCE.machines.harmlessActiveTransformers) {
                    doExplosion(6.0f + getTier());
                } else {
                    coolantTimer = 0;
                    getRecipeLogic().setStatus(RecipeLogic.Status.SUSPEND);
                    converterSubscription.updateSubscription();
                    return;
                }
            }
        }
        coolantTimer = (coolantTimer + 1) % 20;
        if (isWorkingEnabled()) {
            long canDrain = powerInput.getEnergyStored();
            long totalDrained = powerOutput.changeEnergy(canDrain);
            powerInput.removeEnergy(totalDrained);
        }
        converterSubscription.updateSubscription();
    }

    private int calculateCoolantDrain() {
        return UtilConfig.INSTANCE.features.qatCoolantBaseDrain +
                (int) (powerOutput.getInputPerSec() * UtilConfig.INSTANCE.features.qatCoolantIOMultiplier);
    }

    @SuppressWarnings("RedundantIfStatement") // It is cleaner to have the final return true separate.
    protected boolean isSubscriptionActive() {
        if (!isFormed()) return false;

        if (powerInput == null || powerInput.getEnergyStored() <= 0) return false;
        if (powerOutput == null) return false;
        if (powerOutput.getEnergyStored() >= powerOutput.getEnergyCapacity()) return false;

        return true;
    }

    @Override
    public boolean onWorking() {
        return super.onWorking();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        // capture all energy containers
        List<IEnergyContainer> powerInput = new ArrayList<>();
        List<IEnergyContainer> powerOutput = new ArrayList<>();
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getPrioritySortedParts()) {
            if (part instanceof FluidHatchPartMachine machine) {
                this.coolantHatchPos = machine.getPos();
            }
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;
            for (var handler : part.getRecipeHandlers()) {
                var handlerIO = handler.getHandlerIO();
                // If IO not compatible
                if (io != IO.BOTH && handlerIO != IO.BOTH && io != handlerIO) continue;
                if (handler.getCapability() == EURecipeCapability.CAP &&
                        handler instanceof IEnergyContainer container) {
                    if (handlerIO == IO.IN) {
                        powerInput.add(container);
                    } else if (handlerIO == IO.OUT) {
                        powerOutput.add(container);
                    }
                    traitSubscriptions.add(handler.addChangedListener(converterSubscription::updateSubscription));
                }
            }
        }

        // Invalidate the structure if there is not at least one output and one input
        if (powerInput.isEmpty() || powerOutput.isEmpty()) {
            this.onStructureInvalid();
        }

        this.powerOutput = new EnergyContainerList(powerOutput);
        this.powerInput = new EnergyContainerList(powerInput);

        converterSubscription.updateSubscription();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        if (UtilConfig.coolantEnabled() && coolantHatchPos != null) {
            coolantTimer = 0;
            if (isWorkingAllowed) {
                // Player is unpausing machine
                FluidHatchPartMachine coolantHatch = Objects.requireNonNull(
                        (FluidHatchPartMachine) getMachine(Objects.requireNonNull(getLevel()), coolantHatchPos));
                FluidStack coolant = coolantHatch.tank.getFluidInTank(0);
                int amountToDrain = calculateCoolantDrain();
                if (!(coolant.getFluid() == UtilMaterials.QuantumCoolant.getFluid() &&
                        coolant.getAmount() >= amountToDrain)) {
                    if (!ConfigHolder.INSTANCE.machines.harmlessActiveTransformers) {
                        doExplosion(6.0f + getTier());
                    } else {
                        coolantTimer = 0;
                        super.setWorkingEnabled(false);
                        converterSubscription.updateSubscription();
                        return;
                    }
                }
            }
            super.setWorkingEnabled(isWorkingAllowed);
        } else {
            super.setWorkingEnabled(isWorkingAllowed);
        }
    }

    @NotNull
    private List<IMultiPart> getPrioritySortedParts() {
        return getParts().stream().sorted(Comparator.comparing(part -> {
            if (part instanceof MetaMachine partMachine) {
                Block partBlock = partMachine.getBlockState().getBlock();

                if (PartAbility.OUTPUT_ENERGY.isApplicable(partBlock))
                    return 1;

                if (PartAbility.SUBSTATION_OUTPUT_ENERGY.isApplicable(partBlock))
                    return 2;

                if (PartAbility.OUTPUT_LASER.isApplicable(partBlock))
                    return 3;
            }

            return 4;
        })).toList();
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureInvalid() {
        coolantTimer = 0;
        if ((isWorkingEnabled() && recipeLogic.getStatus() == RecipeLogic.Status.WORKING) &&
                !ConfigHolder.INSTANCE.machines.harmlessActiveTransformers) {
            doExplosion(6f + getTier());
        }
        super.onStructureInvalid();
        this.powerOutput = new EnergyContainerList(new ArrayList<>());
        this.powerInput = new EnergyContainerList(new ArrayList<>());
        this.coolantHatchPos = null;
        getRecipeLogic().setStatus(RecipeLogic.Status.SUSPEND);
        converterSubscription.unsubscribe();
    }

    public static TraceabilityPredicate getEnergyHatchPredicates() {
        return abilities(PartAbility.INPUT_ENERGY).setPreviewCount(1)
                .or(abilities(PartAbility.OUTPUT_ENERGY).setPreviewCount(2))
                .or(abilities(PartAbility.SUBSTATION_INPUT_ENERGY).setPreviewCount(1))
                .or(abilities(PartAbility.SUBSTATION_OUTPUT_ENERGY).setPreviewCount(1))
                .or(abilities(PartAbility.INPUT_LASER).setPreviewCount(1))
                .or(abilities(PartAbility.OUTPUT_LASER).setPreviewCount(1));
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        if (isFormed()) {
            if (!isWorkingEnabled()) {
                textList.add(Component.translatable("gtceu.multiblock.work_paused"));
            } else if (isActive()) {
                textList.add(Component.translatable("gtceu.multiblock.running"));
                textList.add(Component
                        .translatable("gtceu.multiblock.active_transformer.max_input",
                                FormattingUtil.formatNumbers(
                                        Math.abs(powerInput.getInputVoltage() * powerInput.getInputAmperage()))));
                textList.add(Component
                        .translatable("gtceu.multiblock.active_transformer.max_output",
                                FormattingUtil.formatNumbers(
                                        Math.abs(powerOutput.getOutputVoltage() * powerOutput.getOutputAmperage()))));
                textList.add(Component
                        .translatable("gtceu.multiblock.active_transformer.average_in",
                                FormattingUtil.formatNumbers(Math.abs(powerInput.getInputPerSec() / 20))));
                textList.add(Component
                        .translatable("gtceu.multiblock.active_transformer.average_out",
                                FormattingUtil.formatNumbers(Math.abs(powerOutput.getOutputPerSec() / 20))));
                textList.add(Component
                        .translatable("gtmutils.multiblock.quantum_active_transformer.coolant_usage",
                                FormattingUtil.formatNumbers(calculateCoolantDrain()),
                                UtilMaterials.QuantumCoolant.getLocalizedName()));
                if (!ConfigHolder.INSTANCE.machines.harmlessActiveTransformers) {
                    textList.add(Component
                            .translatable("gtceu.multiblock.active_transformer.danger_enabled"));

                }
            } else {
                textList.add(Component.translatable("gtceu.multiblock.idling"));
            }
        }
    }

    public void setFrequencyFromString(String str) {
        this.frequency = Integer.parseInt(str);
    }

    public String getFrequencyString() {
        return Integer.valueOf(frequency).toString();
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new IFancyConfigurator() {

            @Override
            public Component getTitle() {
                return Component.translatable("gtmutils.gui.qat_wireless_configurator.title");
            }

            @Override
            public IGuiTexture getIcon() {
                return new ItemStackTexture(GTItems.SENSOR_UV.asItem());
            }

            @Override
            public Widget createConfigurator() {
                return new WidgetGroup(0, 0, 130, 25)
                        .addWidget(new TextFieldWidget().setNumbersOnly(0, Integer.MAX_VALUE)
                                .setTextResponder(QuantumActiveTransformerMachine.this::setFrequencyFromString)
                                .setTextSupplier(QuantumActiveTransformerMachine.this::getFrequencyString));
            }
        });
    }

    @Override
    public @NotNull Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .setMaxWidthLimit(150)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public @NotNull ModularUI createUI(@NotNull Player entityPlayer) {
        return new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
    }
}
