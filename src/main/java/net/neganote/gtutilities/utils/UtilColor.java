package net.neganote.gtutilities.utils;

import net.minecraft.world.item.DyeColor;

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

    UtilColor(DyeColor dye) {
        this.dye = dye;
    }

    public static UtilColor fromDye(DyeColor vanillaDye) {
        for (var value : values()) {
            if (value.dye == vanillaDye) return value;
        }
        throw new IllegalArgumentException("Unknown Vanilla dye: " + vanillaDye);
    }
}
