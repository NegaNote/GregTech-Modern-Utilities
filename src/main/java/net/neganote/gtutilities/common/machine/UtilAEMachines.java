package net.neganote.gtutilities.common.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import net.minecraft.network.chat.Component;
import net.neganote.gtutilities.config.UtilConfig;
import net.neganote.gtutilities.integration.ae2.machine.ExpandedPatternBufferPartMachine;
import net.neganote.gtutilities.integration.ae2.machine.ExpandedPatternBufferProxyPartMachine;

import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

public class UtilAEMachines {

    static {
        if (UtilConfig.INSTANCE.features.aeMachinesEnabled || GTCEu.isDataGen()) {
            final MachineDefinition EXPANDED_ME_PATTERN_BUFFER = REGISTRATE
                    .machine("expanded_me_pattern_buffer", ExpandedPatternBufferPartMachine::new)
                    .tier(ZPM)
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                            PartAbility.EXPORT_ITEMS)
                    .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch"))
                    .langValue("Expanded ME Pattern Buffer (72 Slots)")
                    .tooltips(
                            Component.translatable("block.gtceu.pattern_buffer.desc.0"),
                            Component.literal("§6Expanded to 72 Slots"),
                            Component.translatable("block.gtceu.pattern_buffer.desc.2"),
                            Component.translatable("gtceu.part_sharing.enabled"))
                    .register();

            final MachineDefinition EXPANDED_ME_PATTERN_BUFFER_PROXY = REGISTRATE
                    .machine("expanded_me_pattern_buffer_proxy", ExpandedPatternBufferProxyPartMachine::new)
                    .tier(ZPM)
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                            PartAbility.EXPORT_ITEMS)
                    .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
                    .langValue("Expanded ME Pattern Buffer Proxy")
                    .tooltips(
                            Component.translatable("block.gtceu.pattern_buffer_proxy.desc.0"),
                            Component.literal("§6Compatible with 72 Slot Buffer"),
                            Component.translatable("block.gtceu.pattern_buffer_proxy.desc.2"),
                            Component.translatable("gtceu.part_sharing.enabled"))
                    .register();
        }
    }

    public static void init() {}
}
