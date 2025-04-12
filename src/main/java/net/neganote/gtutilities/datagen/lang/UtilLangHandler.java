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
        provider.add("tooltip.omnibreaker.can_break_anything", "The Omni-breaker can insta-mine ANYTHING!");
        provider.add("tooltip.omnibreaker.charge_status", "Energy: %s EU / %s EU");
        provider.add("tooltip.omnibreaker.right_click_function", "Break individual blocks with right-click!");

        provider.add("tooltip.power_wormhole_machine.uses_coolant", "Drains %s to function!");

        provider.add("gtmutils.multiblock.power_wormhole_machine.coolant_usage", "§cDrains %sL of %s per second");

        provider.add("gtmutils.gui.pterb.wireless_configurator.title", "Wireless frequency");

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
