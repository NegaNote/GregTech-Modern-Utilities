package net.neganote.gtutilities.common.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import net.minecraft.network.chat.Component;
import net.neganote.gtutilities.config.UtilConfig;
import net.neganote.gtutilities.integration.ae2.machine.*;

import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

public class UtilAEMachines {

    public static MachineDefinition EXPANDED_ME_PATTERN_BUFFER = null;
    public static MachineDefinition EXPANDED_ME_PATTERN_BUFFER_PROXY = null;
    public static MachineDefinition ME_TAG_STOCKING_INPUT_BUS = null;
    public static MachineDefinition ME_ENLARGED_STOCKING_INPUT_BUS = null;
    public static MachineDefinition ME_ENLARGED_TAG_STOCKING_INPUT_BUS = null;
    public static MachineDefinition ME_TAG_STOCKING_INPUT_HATCH = null;
    public static MachineDefinition ME_ENLARGED_STOCKING_INPUT_HATCH = null;
    public static MachineDefinition ME_ENLARGED_TAG_STOCKING_INPUT_HATCH = null;

    static {
        if (UtilConfig.INSTANCE.features.expandedBuffersEnabled && GTCEu.Mods.isAE2Loaded() || GTCEu.isDataGen()) {
            EXPANDED_ME_PATTERN_BUFFER = REGISTRATE
                    .machine("expanded_me_pattern_buffer", ExpandedPatternBufferPartMachine::new)
                    .tier(ZPM)
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                            PartAbility.EXPORT_ITEMS)
                    .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
                    .langValue("Expanded ME Pattern Buffer")
                    .tooltips(
                            Component.translatable("block.gtmutils.pattern_buffer.desc.0"),
                            Component.translatable("block.gtceu.pattern_buffer.desc.1"),
                            Component.translatable("block.gtmutils.pattern_buffer.desc.2"),
                            Component.translatable("gtceu.part_sharing.enabled"))
                    .register();

            EXPANDED_ME_PATTERN_BUFFER_PROXY = REGISTRATE
                    .machine("expanded_me_pattern_buffer_proxy", ExpandedPatternBufferProxyPartMachine::new)
                    .tier(ZPM)
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                            PartAbility.EXPORT_ITEMS)
                    .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
                    .langValue("Expanded ME Pattern Buffer Proxy")
                    .tooltips(
                            Component.translatable("block.gtmutils.pattern_buffer_proxy.desc.0"),
                            Component.translatable("block.gtceu.pattern_buffer_proxy.desc.2"),
                            Component.translatable("gtceu.part_sharing.enabled"))
                    .register();

            if (UtilConfig.INSTANCE.features.tagStockingEnabled || GTCEu.isDataGen()) {
                ME_TAG_STOCKING_INPUT_BUS = REGISTRATE
                        .machine("me_tag_stocking_input_bus", METagStockingInputBusPartMachine::new)
                        .tier(LuV)
                        .rotationState(RotationState.ALL)
                        .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                PartAbility.EXPORT_ITEMS)
                        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_tag_bus"))
                        .langValue("ME Tag Stocking Input Bus")
                        .tooltips(
                                Component.translatable("gtceu.machine.item_bus.import.tooltip"),
                                Component.translatable("block.gtmutils.tag_stocking_bus.desc"),
                                Component.translatable("gtceu.part_sharing.enabled"))
                        .register();
            }

            if (UtilConfig.INSTANCE.features.enlargedStockingEnabled || GTCEu.isDataGen()) {
                ME_ENLARGED_STOCKING_INPUT_BUS = REGISTRATE
                        .machine("me_enlarged_stocking_input_bus", MEEnlargedStockingInputBusPartMachine::new)
                        .tier(LuV)
                        .rotationState(RotationState.ALL)
                        .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                PartAbility.EXPORT_ITEMS)
                        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_bus"))
                        .langValue("ME Enlarged Stocking Input Bus")
                        .tooltips(
                                Component.translatable("gtceu.machine.item_bus.import.tooltip"),
                                Component.translatable("block.gtmutils.enlarged_stocking_bus.desc"),
                                Component.translatable("gtceu.part_sharing.enabled"))
                        .register();
            }

            if ((UtilConfig.INSTANCE.features.enlargedStockingEnabled &&
                    UtilConfig.INSTANCE.features.tagStockingEnabled) || GTCEu.isDataGen()) {
                ME_ENLARGED_TAG_STOCKING_INPUT_BUS = REGISTRATE
                        .machine("me_enlarged_tag_stocking_input_bus", MEEnlargedTagStockingInputBusPartMachine::new)
                        .tier(ZPM)
                        .rotationState(RotationState.ALL)
                        .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                PartAbility.EXPORT_ITEMS)
                        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_tag_bus"))
                        .langValue("ME Enlarged Tag Stocking Input Bus")
                        .tooltips(
                                Component.translatable("gtceu.machine.item_bus.import.tooltip"),
                                Component.translatable("block.gtmutils.tag_enlarged_stocking_bus.desc"),
                                Component.translatable("gtceu.part_sharing.enabled"))
                        .register();
            }

            if (UtilConfig.INSTANCE.features.tagStockingEnabled || GTCEu.isDataGen()) {
                ME_TAG_STOCKING_INPUT_HATCH = REGISTRATE
                        .machine("me_tag_stocking_input_hatch", METagStockingInputHatchPartMachine::new)
                        .tier(LuV)
                        .rotationState(RotationState.ALL)
                        .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                PartAbility.EXPORT_ITEMS)
                        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_tag_hatch"))
                        .langValue("ME Tag Stocking Input Hatch")
                        .tooltips(
                                Component.translatable("gtceu.machine.fluid_hatch.import.tooltip"),
                                Component.translatable("block.gtmutils.tag_stocking_hatch.desc"),
                                Component.translatable("gtceu.part_sharing.enabled"))
                        .register();
            }

            if (UtilConfig.INSTANCE.features.enlargedStockingEnabled || GTCEu.isDataGen()) {
                ME_ENLARGED_STOCKING_INPUT_HATCH = REGISTRATE
                        .machine("me_enlarged_stocking_input_hatch", MEEnlargedStockingInputHatchPartMachine::new)
                        .tier(LuV)
                        .rotationState(RotationState.ALL)
                        .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                PartAbility.EXPORT_ITEMS)
                        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_hatch"))
                        .langValue("ME Enlarged Stocking Input Hatch")
                        .tooltips(
                                Component.translatable("gtceu.machine.fluid_hatch.import.tooltip"),
                                Component.translatable("block.gtmutils.enlarged_stocking_hatch.desc"),
                                Component.translatable("gtceu.part_sharing.enabled"))
                        .register();
            }

            if ((UtilConfig.INSTANCE.features.enlargedStockingEnabled &&
                    UtilConfig.INSTANCE.features.tagStockingEnabled) || GTCEu.isDataGen()) {
                ME_ENLARGED_TAG_STOCKING_INPUT_HATCH = REGISTRATE
                        .machine("me_enlarged_tag_stocking_input_hatch",
                                MEEnlargedTagStockingInputHatchPartMachine::new)
                        .tier(ZPM)
                        .rotationState(RotationState.ALL)
                        .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                PartAbility.EXPORT_ITEMS)
                        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_tag_hatch"))
                        .langValue("ME Enlarged Tag Stocking Input Hatch")
                        .tooltips(
                                Component.translatable("gtceu.machine.fluid_hatch.import.tooltip"),
                                Component.translatable("block.gtmutils.tag_enlarged_stocking_hatch.desc"),
                                Component.translatable("gtceu.part_sharing.enabled"))
                        .register();
            }
        }
    }

    public static void init() {}
}
