package net.neganote.gtutilities.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.neganote.gtutilities.common.item.InfiniteSprayCanBehaviour;
import net.neganote.gtutilities.common.item.InfiniteSprayCanItem;
import net.neganote.gtutilities.utils.UtilColor;

public class SprayCanHudOverlay {

    public static final IGuiOverlay HUD_SPRAY_CAN = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack stack = mc.player.getMainHandItem();
        if (!(stack.getItem() instanceof InfiniteSprayCanItem)) {
            stack = mc.player.getOffhandItem();
        }

        if (stack.getItem() instanceof InfiniteSprayCanItem) {
            UtilColor color = InfiniteSprayCanBehaviour.getColor(stack);
            Component text;

            if (color != null) {
                Component colorName = Component.translatable("color.minecraft." + color.dye.getSerializedName());
                text = Component.translatable("behaviour.paintspray.infinite.status.color", colorName);
            } else {
                text = Component.translatable("behaviour.paintspray.infinite.status.solvent");
            }

            int x = width / 2;
            int y = height - 53;

            int textWidth = mc.font.width(text);
            guiGraphics.drawString(mc.font, text, x - (textWidth / 2), y, 0xFFFFFF);
        }
    };
}
