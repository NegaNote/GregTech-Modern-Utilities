package net.neganote.gtutilities.common.machine;

import com.gregtechceu.gtceu.GTCEu;
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
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.client.renderer.machine.MaintenanceHatchPartRenderer;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.electric.ConverterMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.client.renderer.machine.UtilConverterRenderer;
import net.neganote.gtutilities.common.machine.multiblock.QuantumActiveTransformerMachine;
import net.neganote.gtutilities.config.UtilConfig;

import java.util.Locale;
import java.util.function.BiFunction;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.GTValues.V;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.Predicates.controller;
import static com.gregtechceu.gtceu.common.data.GTBlocks.HIGH_POWER_CASING;
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

    public static MultiblockMachineDefinition QUANTUM_ACTIVE_TRANSFORMER = null;

    static {
        if (UtilConfig.INSTANCE.features.quantumActiveTransformerEnabled) {
            QUANTUM_ACTIVE_TRANSFORMER = REGISTRATE
                    .multiblock("quantum_active_transformer", QuantumActiveTransformerMachine::new)
                    .langValue("Quantum Active Transformer")
                    .rotationState(RotationState.ALL)
                    .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                    .appearanceBlock(HIGH_POWER_CASING)
                    .tooltips(Component.translatable("gtceu.machine.active_transformer.tooltip.0"),
                            Component.translatable("gtceu.machine.active_transformer.tooltip.1"))
                    .tooltipBuilder(
                            (stack,
                             components) -> components.add(Component.translatable("gtceu.machine.active_transformer.tooltip.2")
                                    .append(Component.translatable("gtceu.machine.active_transformer.tooltip.3")
                                            .withStyle(TooltipHelper.RAINBOW_HSL_SLOW))))
                    .pattern((definition) -> FactoryBlockPattern.start()
                            .aisle("XXX", "XXX", "XXX")
                            .aisle("XXX", "XCX", "XXX")
                            .aisle("XXX", "XSX", "XXX")
                            .where('S', controller(blocks(definition.getBlock())))
                            .where('X', blocks(GTBlocks.HIGH_POWER_CASING.get()).setMinGlobalLimited(12)
                                    .or(QuantumActiveTransformerMachine.getHatchPredicates()))
                            .where('C', blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                            .build())
                    .workableCasingRenderer(GTCEu.id("block/casings/hpca/high_power_casing"),
                            GTCEu.id("block/multiblock/data_bank"))
                    .register();
        }

    }


    public static void init() {}
}
