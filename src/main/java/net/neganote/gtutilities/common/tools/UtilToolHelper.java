package net.neganote.gtutilities.common.tools;

import net.minecraft.world.item.ItemStack;
import net.neganote.gtutilities.common.item.UtilToolItems;
import net.neganote.gtutilities.config.UtilConfig;

import java.util.function.Supplier;

/**
 * Helper for the LuV and ZPM broken tool stacks.
 */
public class UtilToolHelper {

    public static final Supplier<ItemStack> SUPPLY_POWER_UNIT_LUV;
    public static final Supplier<ItemStack> SUPPLY_POWER_UNIT_ZPM;

    static {
        if (UtilConfig.INSTANCE.features.customLuVToolsEnabled) {
            SUPPLY_POWER_UNIT_LUV = () -> UtilToolItems.POWER_UNIT_LUV != null ?
                    UtilToolItems.POWER_UNIT_LUV.get().getDefaultInstance() : ItemStack.EMPTY;
        } else {
            SUPPLY_POWER_UNIT_LUV = () -> ItemStack.EMPTY;
        }

        if (UtilConfig.INSTANCE.features.customZPMToolsEnabled) {
            SUPPLY_POWER_UNIT_ZPM = () -> UtilToolItems.POWER_UNIT_ZPM != null ?
                    UtilToolItems.POWER_UNIT_ZPM.get().getDefaultInstance() : ItemStack.EMPTY;
        } else {
            SUPPLY_POWER_UNIT_ZPM = () -> ItemStack.EMPTY;
        }
    }
}
