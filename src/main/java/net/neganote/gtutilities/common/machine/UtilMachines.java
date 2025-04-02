package net.neganote.gtutilities.common.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.client.renderer.machine.MaintenanceHatchPartRenderer;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.electric.ConverterMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.client.renderer.machine.UtilConverterRenderer;
import net.neganote.gtutilities.common.machine.multiblock.QuantumPowerSubstationMachine;
import net.neganote.gtutilities.config.UtilConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.GTValues.V;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_LAMINATED_GLASS;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_PALLADIUM_SUBSTATION;
import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

@SuppressWarnings("unused")
public class UtilMachines {

    static {
        REGISTRATE.creativeModeTab(() -> GregTechModernUtilities.UTIL_CREATIVE_TAB);
    }

    // Edited slightly from GTMachines
    public static MachineDefinition STERILE_CLEANING_MAINTENANCE_HATCH = null;

    static {
        if (UtilConfig.INSTANCE.features.sterileHatchEnabled) {
            STERILE_CLEANING_MAINTENANCE_HATCH = REGISTRATE
                    .machine("sterile_cleaning_maintenance_hatch",
                            holder -> new CleaningMaintenanceHatchPartMachine(holder, CleanroomType.STERILE_CLEANROOM))
                    .langValue("Sterile Cleaning Maintenance Hatch")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.MAINTENANCE)
                    .tooltips(Component.translatable("gtceu.universal.disabled"),
                            Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.0"),
                            Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.1"))
                    .tooltipBuilder((stack, tooltips) -> tooltips.add(Component.literal("  ").append(Component
                            .translatable(CleanroomType.STERILE_CLEANROOM.getTranslationKey())
                            .withStyle(ChatFormatting.GREEN))))
                    .renderer(() -> new MaintenanceHatchPartRenderer(GTValues.UHV,
                            GregTechModernUtilities.id("block/machine/part/maintenance.sterile_cleaning")))
                    // Tier can always be changed later
                    .register();
        }
    }

    // Copied from GTMachineUtils
    public static MachineDefinition[] registerConverter(int amperage) {
        return registerTieredMachines(amperage + "a_energy_converter",
                (holder, tier) -> new ConverterMachine(holder, tier, amperage),
                (tier, builder) -> builder
                        .rotationState(RotationState.ALL)
                        .langValue("%s %s§eA§r Energy Converter".formatted(VCF[tier] + VN[tier] + ChatFormatting.RESET,
                                amperage))
                        .renderer(() -> new UtilConverterRenderer(tier, amperage))
                        .tooltips(Component.translatable("gtceu.machine.energy_converter.description"),
                                Component.translatable("gtceu.machine.energy_converter.tooltip_tool_usage"),
                                Component.translatable("gtceu.machine.energy_converter.tooltip_conversion_native",
                                        FeCompat.toFeLong(V[tier] * amperage,
                                                FeCompat.ratio(true)),
                                        amperage, V[tier], GTValues.VNF[tier]),
                                Component.translatable("gtceu.machine.energy_converter.tooltip_conversion_eu", amperage,
                                        V[tier], GTValues.VNF[tier],
                                        FeCompat.toFeLong(V[tier] * amperage,
                                                FeCompat.ratio(false))))
                        .register(),
                ALL_TIERS);
    }

    // Copied from GTMachineUtils
    public static MachineDefinition[] registerTieredMachines(String name,
                                                             BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                             BiFunction<Integer, MachineBuilder<MachineDefinition>, MachineDefinition> builder,
                                                             int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = REGISTRATE
                    .machine(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                            holder -> factory.apply(holder, tier))
                    .tier(tier);
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static MachineDefinition[] ENERGY_CONVERTER_64A = null;

    static {
        if (UtilConfig.INSTANCE.features.converters64aEnabled &&
                ConfigHolder.INSTANCE.compat.energy.enableFEConverters) {
            ENERGY_CONVERTER_64A = registerConverter(64);
        }
    }

    public static MultiblockMachineDefinition QUANTUM_POWER_SUBSTATION = null;

    static {
        if (UtilConfig.INSTANCE.features.qpssEnabled) {
            QUANTUM_POWER_SUBSTATION = REGISTRATE
                    .multiblock("quantum_power_substation", QuantumPowerSubstationMachine::new)
                    .langValue("Quantum Power Substation")
                    .rotationState(RotationState.ALL)
                    .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                    .tooltips(Component.translatable("gtceu.machine.power_substation.tooltip.0"),
                            Component.translatable("gtceu.machine.power_substation.tooltip.1"),
                            Component.translatable("gtceu.machine.power_substation.tooltip.2",
                                    QuantumPowerSubstationMachine.MAX_BATTERY_LAYERS),
                            Component.translatable("gtceu.machine.power_substation.tooltip.3"),
                            Component.translatable("gtceu.machine.power_substation.tooltip.4",
                                    QuantumPowerSubstationMachine.PASSIVE_DRAIN_MAX_PER_STORAGE / 1000))
                    .tooltipBuilder(
                            (stack,
                             components) -> components
                                     .add(Component.translatable("gtceu.machine.power_substation.tooltip.5")
                                             .append(Component.translatable("gtceu.machine.power_substation.tooltip.6")
                                                     .withStyle(TooltipHelper.RAINBOW_HSL_SLOW))))
                    .appearanceBlock(CASING_PALLADIUM_SUBSTATION)
                    .pattern(definition -> FactoryBlockPattern.start(RIGHT, BACK, UP)
                            .aisle("XXSXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX")
                            .aisle("XXXXX", "XCCCX", "XCCCX", "XCCCX", "XXXXX")
                            .aisle("GGGGG", "GBBBG", "GBBBG", "GBBBG", "GGGGG")
                            .setRepeatable(1, QuantumPowerSubstationMachine.MAX_BATTERY_LAYERS)
                            .aisle("GGGGG", "GGGGG", "GGGGG", "GGGGG", "GGGGG")
                            .where('S', controller(blocks(definition.getBlock())))
                            .where('C', blocks(CASING_PALLADIUM_SUBSTATION.get()))
                            .where('X',
                                    blocks(CASING_PALLADIUM_SUBSTATION.get())
                                            .setMinGlobalLimited(QuantumPowerSubstationMachine.MIN_CASINGS)
                                            .or(autoAbilities(true, false, false))
                                            .or(abilities(PartAbility.INPUT_ENERGY, PartAbility.SUBSTATION_INPUT_ENERGY,
                                                    PartAbility.INPUT_LASER).setMinGlobalLimited(1))
                                            .or(abilities(PartAbility.OUTPUT_ENERGY,
                                                    PartAbility.SUBSTATION_OUTPUT_ENERGY,
                                                    PartAbility.OUTPUT_LASER).setMinGlobalLimited(1)))
                            .where('G', blocks(CASING_LAMINATED_GLASS.get()))
                            .where('B', Predicates.powerSubstationBatteries())
                            .build())
                    .shapeInfos(definition -> {
                        List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
                        MultiblockShapeInfo.ShapeInfoBuilder builder = MultiblockShapeInfo.builder()
                                .aisle("ICSCO", "NCMCT", "GGGGG", "GGGGG", "GGGGG")
                                .aisle("CCCCC", "CCCCC", "GBBBG", "GBBBG", "GGGGG")
                                .aisle("CCCCC", "CCCCC", "GBBBG", "GBBBG", "GGGGG")
                                .aisle("CCCCC", "CCCCC", "GBBBG", "GBBBG", "GGGGG")
                                .aisle("CCCCC", "CCCCC", "GGGGG", "GGGGG", "GGGGG")
                                .where('S', definition, Direction.NORTH)
                                .where('C', CASING_PALLADIUM_SUBSTATION)
                                .where('G', CASING_LAMINATED_GLASS)
                                .where('I', GTMachines.ENERGY_INPUT_HATCH[HV], Direction.NORTH)
                                .where('N', GTMachines.SUBSTATION_ENERGY_INPUT_HATCH[EV], Direction.NORTH)
                                .where('O', GTMachines.ENERGY_OUTPUT_HATCH[HV], Direction.NORTH)
                                .where('T', GTMachines.SUBSTATION_ENERGY_OUTPUT_HATCH[EV], Direction.NORTH)
                                .where('M',
                                        ConfigHolder.INSTANCE.machines.enableMaintenance ?
                                                GTMachines.MAINTENANCE_HATCH.getBlock().defaultBlockState().setValue(
                                                        GTMachines.MAINTENANCE_HATCH.get().getRotationState().property,
                                                        Direction.NORTH) :
                                                CASING_PALLADIUM_SUBSTATION.get().defaultBlockState());

                        GTCEuAPI.PSS_BATTERIES.entrySet().stream()
                                // filter out empty batteries in example structures, though they are still
                                // allowed in the predicate (so you can see them on right-click)
                                .filter(entry -> entry.getKey().getCapacity() > 0)
                                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                                .forEach(entry -> shapeInfo.add(builder.where('B', entry.getValue().get()).build()));

                        return shapeInfo;
                    })
                    .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_palladium_substation"),
                            GTCEu.id("block/multiblock/power_substation"))
                    .register();
        }
    }

    public static void init() {}
}
