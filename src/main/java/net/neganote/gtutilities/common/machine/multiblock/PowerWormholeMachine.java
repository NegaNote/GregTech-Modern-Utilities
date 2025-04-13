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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.neganote.gtutilities.common.materials.UtilMaterials;
import net.neganote.gtutilities.config.UtilConfig;
import net.neganote.gtutilities.saveddata.PTERBSavedData;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;

// A lot of this is copied from the Active Transformer
public class PowerWormholeMachine extends WorkableElectricMultiblockMachine
                                  implements IControllable, IExplosionMachine, IFancyUIMachine,
                                  IDisplayUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PowerWormholeMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private List<IMultiPart> localPowerOutput;

    private List<IMultiPart> localPowerInput;

    protected ConditionalSubscriptionHandler converterSubscription;

    @Persisted
    @DescSynced
    private BlockPos coolantHatchPos;

    @Persisted
    @DescSynced
    private int frequency;

    @Persisted
    @DescSynced
    private long inputAmperage;

    @Persisted
    @DescSynced
    private long inputVoltage;

    @Persisted
    @DescSynced
    private long outputAmperage;

    @Persisted
    @DescSynced
    private long outputVoltage;

    @Persisted
    @DescSynced
    private int coolantTimer = 0;

    public PowerWormholeMachine(IMachineBlockEntity holder) {
        super(holder);
        this.localPowerOutput = new ArrayList<>();
        this.localPowerInput = new ArrayList<>();

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
                UtilConfig.coolantEnabled() && coolantTimer == 0 && frequency != 0) {

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
        if (isWorkingEnabled() && getRecipeLogic().getStatus() == RecipeLogic.Status.WORKING) {
            coolantTimer = (coolantTimer + 1) % 20;
        }
        if (isWorkingEnabled() && frequency != 0) {
            if (getLevel() instanceof ServerLevel serverLevel) {
                PTERBSavedData savedData = PTERBSavedData.getOrCreate(serverLevel.getServer().overworld());

                EnergyContainerList powerInput = savedData.getWirelessEnergyInputs(frequency);
                EnergyContainerList powerOutput = savedData.getWirelessEnergyOutputs(frequency);
                long canDrain = powerInput.getEnergyStored();
                long totalDrained = powerOutput.changeEnergy(canDrain);
                powerInput.removeEnergy(totalDrained);
            }
        }
        converterSubscription.updateSubscription();
    }

    private int calculateCoolantDrain() {
        long scalingFactor;

        scalingFactor = Math.max(inputAmperage * inputVoltage,
                outputAmperage * outputVoltage);
        return UtilConfig.INSTANCE.features.pterbCoolantBaseDrain +
                (int) (scalingFactor * UtilConfig.INSTANCE.features.pterbCoolantIOMultiplier);
    }

    @SuppressWarnings("RedundantIfStatement") // It is cleaner to have the final return true separate.
    protected boolean isSubscriptionActive() {
        if (!isFormed()) return false;

        if (localPowerInput == null) return false;
        if (localPowerOutput == null) return false;

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
        List<IMultiPart> localPowerInput = new ArrayList<>();
        List<IMultiPart> localPowerOutput = new ArrayList<>();
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getPrioritySortedParts()) {
            if (part instanceof FluidHatchPartMachine machine) {
                this.coolantHatchPos = machine.getPos();
                continue;
            }
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;
            for (var handler : part.getRecipeHandlers()) {
                var handlerIO = handler.getHandlerIO();
                // If IO not compatible
                if (io != IO.BOTH && handlerIO != IO.BOTH && io != handlerIO) continue;
                if (handler.getCapability() == EURecipeCapability.CAP &&
                        handler instanceof IEnergyContainer) {
                    if (handlerIO == IO.IN) {
                        localPowerInput.add(part);
                    } else if (handlerIO == IO.OUT) {
                        localPowerOutput.add(part);
                    }
                    traitSubscriptions.add(handler.addChangedListener(converterSubscription::updateSubscription));
                }
            }
        }

        // Invalidate the structure if there is not at least one output or one input
        if (localPowerInput.isEmpty() && localPowerOutput.isEmpty()) {
            this.onStructureInvalid();
        }

        this.localPowerInput = localPowerInput;
        this.localPowerOutput = localPowerOutput;

        if (getLevel() instanceof ServerLevel serverLevel && frequency != 0) {
            PTERBSavedData savedData = PTERBSavedData.getOrCreate(serverLevel);
            savedData.addEnergyInputs(frequency, localPowerInput);
            savedData.addEnergyOutputs(frequency, localPowerOutput);
            savedData.saveDataToCache();
        }

        converterSubscription.updateSubscription();
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
        if (getLevel() instanceof ServerLevel serverLevel) {
            PTERBSavedData savedData = PTERBSavedData.getOrCreate(serverLevel.getServer().overworld());
            savedData.removeEnergyInputs(frequency, localPowerInput);
            savedData.removeEnergyOutputs(frequency, localPowerOutput);
            savedData.saveDataToCache();
        }
        this.localPowerOutput = new ArrayList<>();
        this.localPowerInput = new ArrayList<>();
        this.inputAmperage = 0;
        this.inputVoltage = 0;
        this.outputAmperage = 0;
        this.outputVoltage = 0;
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
                                        Math.abs(inputVoltage * inputAmperage))));
                textList.add(Component
                        .translatable("gtceu.multiblock.active_transformer.max_output",
                                FormattingUtil.formatNumbers(
                                        Math.abs(outputVoltage * outputAmperage))));
                textList.add(Component
                        .translatable("gtmutils.multiblock.power_wormhole_machine.coolant_usage",
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
        if (getLevel() instanceof ServerLevel serverLevel) {
            PTERBSavedData savedData = PTERBSavedData.getOrCreate(serverLevel.getServer().overworld());
            savedData.removeEnergyInputs(frequency, localPowerInput);
            savedData.removeEnergyOutputs(frequency, localPowerOutput);
            savedData.saveDataToCache();
        }
        frequency = Integer.parseInt(str);
        if (getLevel() instanceof ServerLevel serverLevel && frequency != 0) {
            PTERBSavedData savedData = PTERBSavedData.getOrCreate(serverLevel.getServer().overworld());
            savedData.addEnergyInputs(frequency, localPowerInput);
            savedData.addEnergyOutputs(frequency, localPowerOutput);
            savedData.saveDataToCache();
        }
    }

    public String getFrequencyString() {
        return Integer.valueOf(frequency).toString();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
        if (getLevel() instanceof ServerLevel serverLevel && frequency != 0) {
            PTERBSavedData savedData = PTERBSavedData.getOrCreate(serverLevel.getServer().overworld());
            if (isWorkingAllowed) {
                savedData.addEnergyInputs(frequency, localPowerInput);
                savedData.addEnergyOutputs(frequency, localPowerOutput);
                savedData.saveDataToCache();
            } else {
                savedData.removeEnergyInputs(frequency, localPowerInput);
                savedData.removeEnergyOutputs(frequency, localPowerOutput);
                savedData.saveDataToCache();
            }
        }
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new IFancyConfigurator() {

            @Override
            public Component getTitle() {
                return Component.translatable("gtmutils.gui.pterb.wireless_configurator.title");
            }

            @Override
            public IGuiTexture getIcon() {
                return new ItemStackTexture(GTItems.SENSOR_UV.asItem());
            }

            @Override
            public Widget createConfigurator() {
                return new WidgetGroup(0, 0, 130, 25)
                        .addWidget(new TextFieldWidget().setNumbersOnly(0, Integer.MAX_VALUE)
                                .setTextResponder(PowerWormholeMachine.this::setFrequencyFromString)
                                .setTextSupplier(PowerWormholeMachine.this::getFrequencyString));
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
