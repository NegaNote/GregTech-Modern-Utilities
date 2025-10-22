package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;

import net.neganote.gtutilities.config.UtilConfig;

import com.tterrag.registrate.util.entry.ItemEntry;

import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

/**
 * Item registry file for the LuV and ZPM tool power units.
 */
public class UtilToolItems {

    public static ItemEntry<ComponentItem> POWER_UNIT_ZPM = null;
    public static ItemEntry<ComponentItem> POWER_UNIT_LUV = null;

    static {

        if (UtilConfig.INSTANCE.features.customLuVToolsEnabled || GTCEu.isDataGen()) {
            POWER_UNIT_LUV = REGISTRATE.item("luv_power_unit", ComponentItem::create)
                    .lang("LuV Power Unit")
                    .properties(p -> p.stacksTo(8))
                    .model((ctx, prov) -> prov.generated(ctx, prov.modLoc("item/tools/power_unit_luv")))
                    .onRegister((c) -> c.attachComponents(ElectricStats.createElectricItem(102400000L, GTValues.LuV)))
                    .register();
        }

        if (UtilConfig.INSTANCE.features.customZPMToolsEnabled || GTCEu.isDataGen()) {
            POWER_UNIT_ZPM = REGISTRATE.item("zpm_power_unit", ComponentItem::create)
                    .lang("ZPM Power Unit")
                    .properties(p -> p.stacksTo(8))
                    .model((ctx, prov) -> prov.generated(ctx, prov.modLoc("item/tools/power_unit_zpm")))
                    .onRegister((c) -> c.attachComponents(ElectricStats.createElectricItem(409600000L, GTValues.ZPM)))
                    .register();
        }
    }

    public static void init() {}
}
