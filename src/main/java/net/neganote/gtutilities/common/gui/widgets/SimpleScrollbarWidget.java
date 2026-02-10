package net.neganote.gtutilities.common.gui.widgets;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import lombok.Getter;

import java.util.function.Consumer;

public class SimpleScrollbarWidget extends Widget {

    private int minScroll = 0;
    private int maxScroll = 0;
    @Getter
    private int currentScroll = 0;
    private int pageSize = 1;

    private boolean dragging = false;
    private int dragYOffset = 0;

    private final Consumer<Integer> onScrollChanged;

    public SimpleScrollbarWidget(int x, int y, int height, Consumer<Integer> onScrollChanged) {
        super(new Position(x, y), new Size(12, height));
        this.onScrollChanged = onScrollChanged;
    }

    public void setRange(int min, int max, int pageSize) {
        this.minScroll = min;
        this.maxScroll = Math.max(min, max);
        this.pageSize = Math.max(1, pageSize);
        setScroll(this.currentScroll);
    }

    public void setScroll(int newScroll) {
        int clamped = Mth.clamp(newScroll, minScroll, maxScroll);
        if (this.currentScroll != clamped) {
            this.currentScroll = clamped;
            if (onScrollChanged != null) onScrollChanged.accept(this.currentScroll);
        }
    }

    public int getRange() {
        return maxScroll - minScroll;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY,
                                 float partialTicks) {
        ResourceLocation scroll = new ResourceLocation("minecraft",
                "textures/gui/container/creative_inventory/tabs.png");
        Position pos = getPosition();
        Size size = getSize();

        graphics.fill(pos.x, pos.y, pos.x + size.width, pos.y + size.height, 0xFF202020);
        graphics.fill(pos.x + 10, pos.y, pos.x + 11, pos.y + size.height, 0xFF353535);
        graphics.fill(pos.x, pos.y, pos.x + 1, pos.y + size.height, 0xFF000000);

        int handleX = pos.x + (getSizeWidth() - 12) / 2;
        int handleY = pos.y + ((this.currentScroll - this.minScroll) * (getSizeHeight() - 15) / getRange());

        if (getRange() > 0) {
            graphics.blit(scroll, handleX, handleY, 232, 0, getSizeWidth(), getSizeHeight());
        } else {
            graphics.blit(scroll, handleX, pos.y, 244, 0, getSizeWidth(), getSizeHeight());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOverElement(mouseX, mouseY) || getRange() == 0) return false;

        Position pos = getPosition();
        int relY = (int) mouseY - pos.y;

        int handleHeight = 15;
        int trackHeight = getSize().height - handleHeight;

        int currentYOffset = (this.currentScroll - this.minScroll) * trackHeight / getRange();

        if (relY < currentYOffset) {
            setScroll(this.currentScroll - pageSize);
        } else if (relY > currentYOffset + handleHeight) {
            setScroll(this.currentScroll + pageSize);
        } else {
            this.dragging = true;
            this.dragYOffset = relY - currentYOffset;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.dragging && getRange() > 0) {
            Position pos = getPosition();
            int handleHeight = 15;
            int trackHeight = getSize().height - handleHeight;

            double handleUpperEdgeY = mouseY - pos.y - this.dragYOffset;
            double position = Mth.clamp(handleUpperEdgeY / trackHeight, 0.0, 1.0);

            int calculatedScroll = this.minScroll + (int) Math.round(position * getRange());
            setScroll(calculatedScroll);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (getRange() > 0 && this.getSizeHeight() + this.getPositionY() >= mouseY && this.getPositionY() <= mouseY) {
            int delta = (int) (wheelDelta * pageSize);
            setScroll(this.currentScroll - delta);
            return true;
        }
        return false;
    }
}
