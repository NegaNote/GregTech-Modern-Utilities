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
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternError;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.neganote.gtutilities.common.materials.UtilMaterials;
import net.neganote.gtutilities.config.UtilConfig;
import net.neganote.gtutilities.utils.EnergyUtils;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;

// A lot of this is copied from the Active Transformer
public class WEBHubMachine extends WorkableElectricMultiblockMachine
                           implements IControllable, IExplosionMachine, IFancyUIMachine,
                           IDisplayUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WEBHubMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static Map<Integer, Set<Pair<ResourceLocation, BlockPos>>> ENERGY_INPUTS = new HashMap<>();
    public static Map<Integer, Set<Pair<ResourceLocation, BlockPos>>> ENERGY_OUTPUTS = new HashMap<>();

    public static void addEnergyInputs(int freq, List<IMultiPart> parts) {
        Set<Pair<ResourceLocation, BlockPos>> inputPairs = ENERGY_INPUTS.computeIfAbsent(freq,
                (f) -> new HashSet<>());
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                inputPairs.add(pair);
            }
        }
        ENERGY_INPUTS.put(freq, inputPairs);
    }

    public static void removeEnergyInputs(int freq, List<IMultiPart> parts) {
        Set<Pair<ResourceLocation, BlockPos>> inputPairs = ENERGY_INPUTS.computeIfAbsent(freq,
                (f) -> new HashSet<>());
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                inputPairs.remove(pair);
            }
        }
        ENERGY_INPUTS.put(freq, inputPairs);
    }

    public static void addEnergyOutputs(int freq, List<IMultiPart> parts) {
        Set<Pair<ResourceLocation, BlockPos>> outputPairs = ENERGY_OUTPUTS.computeIfAbsent(freq,
                (f) -> new HashSet<>());
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                outputPairs.add(pair);
            }
        }
        ENERGY_OUTPUTS.put(freq, outputPairs);
    }

    public static void removeEnergyOutputs(int freq, List<IMultiPart> parts) {
        Set<Pair<ResourceLocation, BlockPos>> outputPairs = ENERGY_OUTPUTS.computeIfAbsent(freq,
                (f) -> new HashSet<>());
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                outputPairs.remove(pair);
            }
        }
        
        ENERGY_OUTPUTS.put(freq, outputPairs);
    }

    public static EnergyContainerList getWirelessEnergyInputs(int freq, ServerLevel serverLevel) {
        Set<Pair<ResourceLocation, BlockPos>> inputPairs = ENERGY_INPUTS.computeIfAbsent(freq,
                (f) -> new HashSet<>());
        List<IEnergyContainer> energyContainerList = new ArrayList<>();
        for (Pair<ResourceLocation, BlockPos> pair : inputPairs) {
            ServerLevel dimension = serverLevel.getServer()
                    .getLevel(ResourceKey.create(Registries.DIMENSION, pair.getFirst()));
            if (dimension != null) {
                MetaMachine machine = MetaMachine.getMachine(dimension, pair.getSecond());
                if (machine instanceof EnergyHatchPartMachine hatch) {
                    energyContainerList.add(hatch.energyContainer);
                }
                if (machine instanceof LaserHatchPartMachine hatch) {
                    // unfortunately the laser hatch's buffer is private, so I have to do this instead
                    for (var handlerList : hatch.getRecipeHandlers()) {
                        var containers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                                .filter(IEnergyContainer.class::isInstance)
                                .map(IEnergyContainer.class::cast)
                                .toList();
                        energyContainerList.addAll(containers);
                    }
                }
            }
        }
        return new EnergyContainerList(energyContainerList);
    }

    public static EnergyContainerList getWirelessEnergyOutputs(int freq, ServerLevel serverLevel) {
        Set<Pair<ResourceLocation, BlockPos>> outputPairs = ENERGY_OUTPUTS.computeIfAbsent(freq,
                (f) -> new HashSet<>());
        List<IEnergyContainer> energyContainerList = new ArrayList<>();
        for (Pair<ResourceLocation, BlockPos> pair : outputPairs) {
            ServerLevel dimension = serverLevel.getServer()
                    .getLevel(ResourceKey.create(Registries.DIMENSION, pair.getFirst()));
            if (dimension != null) {
                MetaMachine machine = MetaMachine.getMachine(dimension, pair.getSecond());

                if (machine instanceof EnergyHatchPartMachine hatch) {
                    energyContainerList.add(hatch.energyContainer);
                }
                if (machine instanceof LaserHatchPartMachine hatch) {
                    // unfortunately the laser hatch's buffer is private, so I have to do this instead
                    for (var handlerList : hatch.getRecipeHandlers()) {
                        var containers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                                .filter(IEnergyContainer.class::isInstance)
                                .map(IEnergyContainer.class::cast)
                                .toList();
                        energyContainerList.addAll(containers);
                    }
                }
            }
        }
        return new EnergyContainerList(energyContainerList);
    }

    private List<IMultiPart> localPowerInput;

    protected ConditionalSubscriptionHandler converterSubscription;

    @Getter
    private int coolantDrain;

    @Persisted
    @DescSynced
    @Getter
    private int frequency;

    @Persisted
    @DescSynced
    private int coolantTimer = 0;

    public WEBHubMachine(IMachineBlockEntity holder) {
        super(holder);
        this.localPowerInput = new ArrayList<>();

        this.converterSubscription = new ConditionalSubscriptionHandler(this, this::convertEnergyTick,
                this::isSubscriptionActive);

        this.frequency = 0;
    }

    public void explode() {
        removeWirelessEnergy();

        long inputVoltage = 0;

        if (!localPowerInput.isEmpty()) {
            EnergyContainerList localInputs = EnergyUtils.getEnergyListFromMultiParts(localPowerInput);
            inputVoltage = localInputs.getInputVoltage();
        }

        long tier = GTUtil.getFloorTierByVoltage(inputVoltage);

        doExplosion(15f + tier);
    }

    public void convertEnergyTick() {
        if (frequency == 0) {
            getRecipeLogic().setStatus(RecipeLogic.Status.SUSPEND);
            return;
        }
        if (isWorkingEnabled()) {
            getRecipeLogic()
                    .setStatus(isSubscriptionActive() ? RecipeLogic.Status.WORKING : RecipeLogic.Status.SUSPEND);
        }
        if (isWorkingEnabled() && getRecipeLogic().getStatus() == RecipeLogic.Status.WORKING &&
                UtilConfig.coolantEnabled() && coolantTimer == 0 && frequency != 0) {

            var coolantTanks = getCapabilitiesFlat(IO.IN, GTRecipeCapabilities.FLUID).stream()
                    .map(NotifiableFluidTank.class::cast).toList();

            List<FluidIngredient> left = List
                    .of(FluidIngredient.of(UtilMaterials.QuantumCoolant.getFluid(), coolantDrain));

            for (var tank : coolantTanks) {
                left = tank.handleRecipe(IO.IN, null, left, false);
                if (left == null) {
                    break;
                }
            }

            if (left != null && !left.isEmpty()) {
                if (!ConfigHolder.INSTANCE.machines.harmlessActiveTransformers) {
                    explode();
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
                EnergyContainerList powerInput = getWirelessEnergyInputs(frequency, serverLevel);
                EnergyContainerList powerOutput = getWirelessEnergyOutputs(frequency, serverLevel);
                long canDrain = powerInput.getEnergyStored();
                long totalDrained = powerOutput.changeEnergy(canDrain);
                powerInput.removeEnergy(totalDrained);
            }
        }
        converterSubscription.updateSubscription();
    }

    private int calculateCoolantDrain() {
        long inputAmperage = 0;
        long inputVoltage = 0;

        if (!localPowerInput.isEmpty()) {
            EnergyContainerList localInputs = EnergyUtils.getEnergyListFromMultiParts(localPowerInput);
            inputAmperage = localInputs.getInputAmperage();
            inputVoltage = localInputs.getInputVoltage();
        }

        long scalingFactor = inputAmperage * inputVoltage;

        int coolantDrain = UtilConfig.INSTANCE.features.pterbCoolantBaseDrain +
                (int) (scalingFactor * UtilConfig.INSTANCE.features.pterbCoolantIOMultiplier);
        if (coolantDrain <= 0) {
            coolantDrain = 1;
        }
        return coolantDrain;
    }

    @SuppressWarnings("RedundantIfStatement") // It is cleaner to have the final return true separate.
    protected boolean isSubscriptionActive() {
        if (!isFormed()) return false;

        if (localPowerInput == null) return false;

        return true;
    }

    @Override
    public boolean onWorking() {
        return super.onWorking();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        if (frequency == 0) {
            setWorkingEnabled(false);
        }

        // capture all energy containers
        List<IMultiPart> localPowerInput = new ArrayList<>();
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getPrioritySortedParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;
            for (var handlerList : part.getRecipeHandlers()) {
                var handlerIO = handlerList.getHandlerIO();
                // If IO not compatible
                if (io != IO.BOTH && handlerIO != IO.BOTH && io != handlerIO) continue;
                var energyContainers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                        .filter(IEnergyContainer.class::isInstance)
                        .map(IEnergyContainer.class::cast)
                        .toList();
                if (!energyContainers.isEmpty()) {
                    if (handlerIO == IO.IN) {
                        localPowerInput.add(part);
                    }
                }
            }
        }

        // Invalidate the structure if there is not at least one output or one input
        if (localPowerInput.isEmpty()) {
            this.onStructureInvalid();
            getMultiblockState().setError(new PatternError());
            return;
        }

        this.localPowerInput = localPowerInput;

        this.coolantDrain = calculateCoolantDrain();

        if (frequency != 0 && isActive()) {
            addWirelessEnergy();
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
        removeWirelessEnergy();
        if ((isWorkingEnabled() && recipeLogic.getStatus() == RecipeLogic.Status.WORKING) &&
                !ConfigHolder.INSTANCE.machines.harmlessActiveTransformers) {
            explode();
        }
        super.onStructureInvalid();
        this.localPowerInput = new ArrayList<>();
        setWorkingEnabled(false);
        converterSubscription.unsubscribe();
    }

    private void removeWirelessEnergy() {
        removeEnergyInputs(frequency, localPowerInput);
    }

    private void addWirelessEnergy() {
        addEnergyInputs(frequency, localPowerInput);
    }

    public static TraceabilityPredicate getHatchPredicates() {
        var predicate = abilities(PartAbility.INPUT_ENERGY).setPreviewCount(1)
                .or(abilities(PartAbility.SUBSTATION_INPUT_ENERGY).setPreviewCount(1))
                .or(abilities(PartAbility.INPUT_LASER).setPreviewCount(1));
        if (UtilConfig.coolantEnabled()) {
            predicate = predicate.or(abilities(PartAbility.IMPORT_FLUIDS).setExactLimit(1));
        }
        return predicate;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        if (isFormed()) {
            if (frequency == 0) {
                textList.add(Component.translatable("gtmutils.web_machines.invalid_frequency")
                        .withStyle(ChatFormatting.RED));
                return;
            }
            if (!isWorkingEnabled()) {
                textList.add(Component.translatable("gtceu.multiblock.work_paused"));
            } else if (isActive()) {
                long inputAmperage = 0;
                long inputVoltage = 0;

                if (!localPowerInput.isEmpty()) {
                    EnergyContainerList localInputs = EnergyUtils.getEnergyListFromMultiParts(localPowerInput);
                    inputAmperage = localInputs.getInputAmperage();
                    inputVoltage = localInputs.getInputVoltage();
                }

                long inputTotal = inputVoltage * inputAmperage;

                textList.add(Component.translatable("gtceu.multiblock.running"));
                if (inputTotal > 0) {
                    textList.add(Component
                            .translatable("gtceu.multiblock.active_transformer.max_input",
                                    FormattingUtil.formatNumbers(
                                            Math.abs(inputTotal))));
                }
                if (UtilConfig.coolantEnabled()) {
                    textList.add(Component
                            .translatable("gtmutils.multiblock.web_hub_machine.coolant_usage",
                                    FormattingUtil.formatNumbers(coolantDrain),
                                    UtilMaterials.QuantumCoolant.getLocalizedName()));
                }
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
        removeWirelessEnergy();
        frequency = Integer.parseInt(str);
        if (frequency == 0) {
            setWorkingEnabled(false);
        }
        if (frequency != 0) {
            addWirelessEnergy();
        }
    }

    public String getFrequencyString() {
        return Integer.valueOf(frequency).toString();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        if (frequency == 0) {
            super.setWorkingEnabled(false);
            return;
        }
        super.setWorkingEnabled(isWorkingAllowed);
        if (frequency != 0) {
            if (isWorkingAllowed) {
                addWirelessEnergy();
            } else {
                removeWirelessEnergy();
            }
        }
        if (!isWorkingAllowed) {
            coolantTimer = 0;
        }
    }

    @Override
    public void attachConfigurators(@NotNull ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new IFancyConfigurator() {

            @Override
            public Component getTitle() {
                return Component.translatable("gtmutils.gui.web_hub.wireless_configurator.title");
            }

            @Override
            public IGuiTexture getIcon() {
                return new ItemStackTexture(GTItems.SENSOR_UV.asItem());
            }

            @Override
            public Widget createConfigurator() {
                return new WidgetGroup(0, 0, 130, 25)
                        .addWidget(new TextFieldWidget().setNumbersOnly(0, Integer.MAX_VALUE)
                                .setTextResponder(WEBHubMachine.this::setFrequencyFromString)
                                .setTextSupplier(WEBHubMachine.this::getFrequencyString));
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
