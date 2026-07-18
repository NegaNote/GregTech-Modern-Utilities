package net.neganote.gtutilities.utils;

import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;

public enum UtilColor {

    WHITE(DyeColor.WHITE),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY),
    GRAY(DyeColor.GRAY),
    BLACK(DyeColor.BLACK),
    BROWN(DyeColor.BROWN),
    RED(DyeColor.RED),
    ORANGE(DyeColor.ORANGE),
    YELLOW(DyeColor.YELLOW),
    LIME(DyeColor.LIME),
    GREEN(DyeColor.GREEN),
    CYAN(DyeColor.CYAN),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE),
    BLUE(DyeColor.BLUE),
    PURPLE(DyeColor.PURPLE),
    MAGENTA(DyeColor.MAGENTA),
    PINK(DyeColor.PINK);

    public final DyeColor dye;

    private static final Map<DyeColor, UtilColor> DYE_MAP = new HashMap<>();

    static {
        for (UtilColor color : values()) {
            DYE_MAP.put(color.dye, color);
        }
    }

    UtilColor(DyeColor dye) {
        this.dye = dye;
    }

    public static UtilColor fromDye(DyeColor vanillaDye) {
        UtilColor result = DYE_MAP.get(vanillaDye);
        if (result == null) throw new IllegalArgumentException("Unknown Vanilla dye: " + vanillaDye);
        return result;
    }
}
