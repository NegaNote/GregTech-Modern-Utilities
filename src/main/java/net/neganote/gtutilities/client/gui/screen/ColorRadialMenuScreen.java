package net.neganote.gtutilities.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neganote.gtutilities.network.UtilsNetwork;
import net.neganote.gtutilities.network.packet.SelectColorPacket;
import net.neganote.gtutilities.utils.UtilColor;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.NotNull;

public class ColorRadialMenuScreen extends Screen {

    private final InteractionHand hand;
    private static final int RADIUS = 85;
    private static final int INNER_RADIUS = 20;
    private static final int ITEM_RADIUS = 60;

    public ColorRadialMenuScreen(InteractionHand hand) {
        super(Component.translatable("gui.gtmutils.color_select.title"));
        this.hand = hand;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        UtilColor[] colors = UtilColor.values();
        int numSegments = colors.length;
        float segmentAngle = 360.0f / numSegments;

        double distToCenter = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));
        double mouseAngle = Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
        mouseAngle = (mouseAngle + 360 + 90) % 360;

        boolean hoveringSolvent = distToCenter < INNER_RADIUS;

        int solventColor = hoveringSolvent ? 0xFFFF55 : 0xFFFFFF;
        Component solventText = Component.translatable("behaviour.paintspray.solvent.short");
        guiGraphics.drawCenteredString(this.font, solventText, centerX, centerY - 4, solventColor);

        for (int i = 0; i < numSegments; i++) {
            float startAngleDeg = i * segmentAngle;
            float endAngleDeg = (i + 1) * segmentAngle;
            boolean hoveringThis = !hoveringSolvent && distToCenter <= RADIUS && distToCenter > INNER_RADIUS &&
                    mouseAngle >= startAngleDeg && mouseAngle < endAngleDeg;

            float midAngleRad = (float) Math.toRadians(startAngleDeg - 90);
            float itemAngleRad = (float) Math.toRadians(((startAngleDeg + endAngleDeg) / 2.0f) - 90);

            int x1 = centerX + (int) (Mth.cos(midAngleRad) * INNER_RADIUS);
            int y1 = centerY + (int) (Mth.sin(midAngleRad) * INNER_RADIUS);

            guiGraphics.fill(x1, y1, x1 + 1, y1 + 1, 0xAAFFFFFF);

            int itemX = centerX + (int) (Mth.cos(itemAngleRad) * ITEM_RADIUS) - 8;
            int itemY = centerY + (int) (Mth.sin(itemAngleRad) * ITEM_RADIUS) - 8;

            if (hoveringThis) {
                RenderSystem.setShaderColor(1, 1, 1, 0.2f);
                guiGraphics.fill(itemX - 4, itemY - 4, itemX + 20, itemY + 20, 0x44FFFFFF);
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }

            ItemStack dyeStack = new ItemStack(getDyeItem(colors[i]));
            guiGraphics.renderFakeItem(dyeStack, itemX, itemY);

            if (hoveringThis) {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("color.minecraft." + colors[i].dye.getSerializedName()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double distToCenter = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));

        if (distToCenter < INNER_RADIUS) {
            sendColorSelection(-1);
            return true;
        }

        if (distToCenter <= RADIUS) {
            double angle = Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
            angle = (angle + 360 + 90) % 360;

            UtilColor[] colors = UtilColor.values();
            int selectedSegment = (int) (angle / (360.0f / colors.length));

            if (selectedSegment >= 0 && selectedSegment < colors.length) {
                sendColorSelection(selectedSegment);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendColorSelection(int id) {
        UtilsNetwork.CHANNEL.sendToServer(new SelectColorPacket(hand, id));
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        this.onClose();
    }

    private Item getDyeItem(UtilColor color) {
        ResourceLocation id = new ResourceLocation("minecraft", color.dye.getSerializedName() + "_dye");
        return BuiltInRegistries.ITEM.get(id);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
