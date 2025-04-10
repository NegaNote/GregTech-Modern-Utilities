package net.neganote.gtutilities.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class UtilLangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("tooltip.omnibreaker.can_break_anything", "The Omni-breaker can insta-mine ANYTHING!");
        provider.add("tooltip.omnibreaker.charge_status", "Energy: %s EU / %s EU");
        provider.add("tooltip.omnibreaker.right_click_function", "Break individual blocks with right-click!");

        provider.add("tooltip.quantum_active_transformer.uses_coolant", "Drains %s to function!");

        provider.add("gtmutils.multiblock.quantum_active_transformer.coolant_usage", "§cDrains %sL of %s per second");

        provider.add("gtmutils.gui.qat_wireless_configurator.title", "Wireless frequency");
    }
}
