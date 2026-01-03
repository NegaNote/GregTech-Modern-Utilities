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

        provider.add("item.gtceu.tool.mv_screwdriver", "%s Electric Screwdriver (MV)");
        provider.add("item.gtceu.tool.hv_screwdriver", "%s Electric Screwdriver (HV)");
        provider.add("item.gtceu.tool.ev_screwdriver", "%s Electric Screwdriver (EV)");
        provider.add("item.gtceu.tool.iv_screwdriver", "%s Electric Screwdriver (IV)");
        provider.add("item.gtceu.tool.luv_screwdriver", "%s Electric Screwdriver (LuV)");
        provider.add("item.gtceu.tool.zpm_screwdriver", "%s Electric Screwdriver (ZPM)");

        provider.add("item.gtceu.tool.mv_chainsaw", "%s Chainsaw (MV)");
        provider.add("item.gtceu.tool.hv_chainsaw", "%s Chainsaw (HV)");
        provider.add("item.gtceu.tool.ev_chainsaw", "%s Chainsaw (EV)");
        provider.add("item.gtceu.tool.iv_chainsaw", "%s Chainsaw (IV)");
        provider.add("item.gtceu.tool.luv_chainsaw", "%s Chainsaw (LuV)");
        provider.add("item.gtceu.tool.zpm_chainsaw", "%s Chainsaw (ZPM)");

        provider.add("item.gtceu.tool.mv_buzzsaw", "%s Buzzsaw (MV)");
        provider.add("item.gtceu.tool.hv_buzzsaw", "%s Buzzsaw (HV)");
        provider.add("item.gtceu.tool.ev_buzzsaw", "%s Buzzsaw (EV)");
        provider.add("item.gtceu.tool.iv_buzzsaw", "%s Buzzsaw (IV)");
        provider.add("item.gtceu.tool.luv_buzzsaw", "%s Buzzsaw (LuV)");
        provider.add("item.gtceu.tool.zpm_buzzsaw", "%s Buzzsaw (ZPM)");

        provider.add("item.gtceu.tool.mv_wrench", "%s Wrench (MV)");
        provider.add("item.gtceu.tool.ev_wrench", "%s Wrench (EV)");
        provider.add("item.gtceu.tool.luv_wrench", "%s Wrench (LuV)");
        provider.add("item.gtceu.tool.zpm_wrench", "%s Wrench (ZPM)");

        provider.add("item.gtceu.tool.mv_wirecutter", "%s Wire Cutter (MV)");
        provider.add("item.gtceu.tool.ev_wirecutter", "%s Wire Cutter (EV)");
        provider.add("item.gtceu.tool.luv_wirecutter", "%s Wire Cutter (LuV)");
        provider.add("item.gtceu.tool.zpm_wirecutter", "%s Wire Cutter (ZPM)");

        provider.add("item.gtceu.tool.luv_drill", "%s Drill (LuV)");
        provider.add("item.gtceu.tool.zpm_drill", "%s Drill (ZPM)");

        provider.add("tooltip.pterb_machine.uses_coolant", "Drains %s to function!");
        provider.add("tooltip.pterb_machine.input_coolant_before_use", "Always input coolant before turning it on!");

        provider.add("gtmutils.machine.64a_energy_converter.tooltip_tool_usage",
                "Starts as §fEU Converter§7, use Soft Mallet to change");

        provider.add("gtmutils.multiblock.pterb_machine.coolant_usage", "§cDrains %smb of %s per second");
        provider.add("gtmutils.pterb_machine.invalid_frequency", "WATs will not work on frequency 0!");

        provider.add("gtmutils.gui.pterb.wireless_configurator.title", "Wireless frequency");

        provider.add("tooltip.pterb_machine.purpose", "Power Transfer Einstein-Rosen Bridge (PTERB)");
        provider.add("tooltip.pterb_machine.frequencies",
                "All WATs with the same frequency will wirelessly transfer energy between each other like a single Active Transformer.");
        provider.add("gtmutils.pterb.current_frequency", "Current frequency: %s");

        provider.add("config.jade.plugin_gtmutils.pterb_info", "WAT Info");

        multiLang(provider, "gtceu.placeholder_info.watfrequency",
                "Returns the current frequency used by a Wireless Active Transformer.",
                "Usage:",
                "  {watfrequency} -> Current frequency: (insert frequency here)");

        provider.add("block.gtmutils.pattern_buffer.desc.0",
                "§fAllows expanded direct §6AE2 pattern storage §ffor GregTech Multiblocks.");
        provider.add("block.gtmutils.pattern_buffer.desc.2",
                "§fLink §6Expanded Pattern Buffer Proxies §fwith a §bdatastick §fto link machines together!");
        provider.add("block.gtmutils.pattern_buffer_proxy.desc.0",
                "§fAllows linking many machines to a singular §6Expanded ME Pattern Buffer§f.");

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

    protected static void multiLang(RegistrateLangProvider provider, String key, String... values) {
        for (var i = 0; i < values.length; i++) {
            provider.add(getSubKey(key, i), values[i]);
        }
    }

    protected static String getSubKey(String key, int index) {
        return key + "." + index;
    }
}
