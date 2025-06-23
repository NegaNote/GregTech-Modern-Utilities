package net.neganote.gtutilities.datagen.lang;

import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.config.UtilConfig;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ObjectValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UtilLangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("tooltip.omnibreaker.swappable_tools", "Also contains multiple tools in one!");
        provider.add("tooltip.omnibreaker.tool_mode", "Current mode: %s");
        provider.add("tooltip.omnibreaker.tool_mode_0", "Omnibreaker");
        provider.add("tooltip.omnibreaker.tool_mode_1", "Wrench");
        provider.add("tooltip.omnibreaker.tool_mode_2", "Screwdriver");
        provider.add("tooltip.omnibreaker.tool_mode_3", "Wire-cutter");
        provider.add("tooltip.omnibreaker.tool_mode_4", "Crowbar");
        provider.add("tooltip.omnibreaker.can_break_anything", "The Omni-breaker can insta-mine ANYTHING!");
        provider.add("tooltip.omnibreaker.charge_status", "Energy: %s EU / %s EU");
        provider.add("tooltip.omnibreaker.right_click_function", "Break individual blocks with right-click!");
        provider.add("tooltip.omnibreaker.modern_vajra", "A Modern Vajra");

        provider.add("tooltip.pterb_machine.uses_coolant", "Drains %s to function!");
        provider.add("tooltip.pterb_machine.input_coolant_before_use", "Always input coolant before turning it on!");

        provider.add("gtmutils.machine.64a_energy_converter.tooltip_tool_usage",
                "Starts as §fEU Converter§7, use Soft Mallet to change");

        provider.add("gtmutils.multiblock.pterb_machine.coolant_usage", "§cDrains %sL of %s per second");
        provider.add("gtmutils.pterb_machine.invalid_frequency", "PTERBs will not work on frequency 0!");

        provider.add("gtmutils.gui.pterb.wireless_configurator.title", "Wireless frequency");

        provider.add("tooltip.pterb_machine.purpose", "Wireless Active Transformer (PTERB)");
        provider.add("tooltip.pterb_machine.frequencies",
                "All PTERBs with the same frequency act like a single Active Transformer.");
        provider.add("gtmutils.pterb.current_frequency", "Current frequency: %s");

        provider.add("config.jade.plugin_gtmutils.pterb_info", "PTERB Info");

        provider.add("material.gtmutils.quantum_coolant", "Quantum Coolant");

        dfs(provider, new HashSet<>(), UtilConfig.CONFIG_HOLDER.getValueMap());
    }

    private static void dfs(RegistrateLangProvider provider, Set<String> added, Map<String, ConfigValue<?>> map) {
        for (var entry : map.entrySet()) {
            var id = entry.getValue().getId();
            if (added.add(id)) {
                provider.add(String.format("config.%s.option.%s", GregTechModernUtilities.MOD_ID, id), id);
            }
            if (entry.getValue() instanceof ObjectValue objectValue) {
                dfs(provider, added, objectValue.get());
            }
        }
    }
}
