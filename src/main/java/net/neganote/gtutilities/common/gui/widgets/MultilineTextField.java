package net.neganote.gtutilities.common.gui.widgets;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.Util;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MultilineTextField extends WidgetGroup {

    public static final int DEFAULT_MAX_LENGTH = Integer.MAX_VALUE;
    private static final int ACTION_SET_TEXT = 1;

    private static final int KEY_ESCAPE = 256;
    private static final int KEY_ENTER = 257;
    private static final int KEY_KP_ENTER = 335;
    private static final int KEY_TAB = 258;

    private static MultilineTextField focusedField = null;

    private final Supplier<String> textSupplier;
    private final Consumer<String> textConsumer;
    private final Component placeholder;
    private final Supplier<Integer> borderColorSupplier;

    private int maxLength = DEFAULT_MAX_LENGTH;

    @OnlyIn(Dist.CLIENT)
    private net.minecraft.client.gui.Font font;
    @OnlyIn(Dist.CLIENT)
    private CachedTextField textField;

    private double scrollAmount = 0.0;
    private boolean dragging = false;
    private boolean hasFocus = false;

    private String lastSent = "";

    public MultilineTextField(
                              int x, int y, int width, int height,
                              Supplier<String> textSupplier,
                              Consumer<String> textConsumer) {
        this(x, y, width, height, textSupplier, textConsumer, Component.empty(), null);
    }

    public MultilineTextField(
                              int x, int y, int width, int height,
                              Supplier<String> textSupplier,
                              Consumer<String> textConsumer,
                              Component placeholder) {
        this(x, y, width, height, textSupplier, textConsumer, placeholder, null);
    }

    public MultilineTextField(
                              int x, int y, int width, int height,
                              Supplier<String> textSupplier,
                              Consumer<String> textConsumer,
                              Component placeholder,
                              Supplier<Integer> borderColorSupplier) {
        super(new Position(x, y), new Size(width, height));
        this.textSupplier = textSupplier;
        this.textConsumer = textConsumer;
        this.placeholder = placeholder == null ? Component.empty() : placeholder;
        this.borderColorSupplier = borderColorSupplier;

        String init = safe(textSupplier.get());
        this.lastSent = init;

        if (isRemote()) {
            initClient(width);
            setValueClient(init);
        }
    }

    public MultilineTextField setMaxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);
        if (isRemote() && textField != null) {
            textField.setCharacterLimit(this.maxLength);
            // przytnij aktualną wartość jeśli trzeba
            String v = safe(textField.value());
            if (v.length() > this.maxLength) {
                setValueClient(v.substring(0, this.maxLength));
                sendToServer(true);
            }
        }
        return this;
    }

    public String getValue() {
        if (isRemote() && textField != null) return safe(textField.value());
        return safe(textSupplier.get());
    }

    public void setValue(String v) {
        v = clampToMax(safe(v));
        textConsumer.accept(v);
        if (isRemote()) {
            setValueClient(v);
        }
    }

    public void setDirectly(String newText) {
        setValue(newText);
        sendToServer(true);
    }

    // ====== networking ======
    private void sendToServer() {
        sendToServer(false);
    }

    private void sendToServer(boolean force) {
        String v = clampToMax(getValue());
        textConsumer.accept(v);

        if ((force || !v.equals(lastSent)) && isRemote()) {
            writeClientAction(ACTION_SET_TEXT, buf -> buf.writeUtf(v, maxLength));
            lastSent = v;
        }
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == ACTION_SET_TEXT) {
            String v = buffer.readUtf(maxLength);
            textConsumer.accept(v);
            return;
        }
        super.handleClientAction(id, buffer);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        String fromMachine = clampToMax(safe(textSupplier.get()));

        if (isRemote() && textField != null) {
            if (!hasFocus && !fromMachine.equals(textField.value())) {
                setValueClient(fromMachine);
                lastSent = fromMachine;
            }
        }
    }

    // ====== client init ======
    @OnlyIn(Dist.CLIENT)
    private void initClient(int width) {
        this.font = net.minecraft.client.Minecraft.getInstance().font;

        this.textField = new CachedTextField(font, width - 4);
        this.textField.setCharacterLimit(this.maxLength);

        this.textField.setCursorListener(() -> {
            clampScroll();
            ensureCursorVisible();
        });

        this.textField.setValueListener((str) -> {
            clampScroll();
            ensureCursorVisible();
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void setValueClient(String v) {
        if (textField == null) return;
        textField.setValue(v);
        clampScroll();
        ensureCursorVisible();
    }

    // ====== focus ======
    @OnlyIn(Dist.CLIENT)
    private void takeFocus() {
        if (focusedField != this) {
            if (focusedField != null) focusedField.loseFocus();
            focusedField = this;
        }
        hasFocus = true;
    }

    @OnlyIn(Dist.CLIENT)
    private void loseFocus() {
        hasFocus = false;
        dragging = false;
        if (focusedField == this) focusedField = null;
    }

    // ====== scrolling helpers ======
    @OnlyIn(Dist.CLIENT)
    private double getMaxScroll() {
        if (textField == null || font == null) return 0.0;
        int textH = textField.lineCount() * font.lineHeight;
        int viewH = getSize().height - 4;
        return (double) Math.max(textH - viewH, 0);
    }

    @OnlyIn(Dist.CLIENT)
    private void setScrollAmount(double a) {
        scrollAmount = Mth.clamp(a, 0.0, getMaxScroll());
    }

    @OnlyIn(Dist.CLIENT)
    private void clampScroll() {
        setScrollAmount(scrollAmount);
    }

    @OnlyIn(Dist.CLIENT)
    private void ensureCursorVisible() {
        if (textField == null || font == null) return;

        int viewH = getSize().height - 4;
        int caretLine = textField.lineAtCursor();
        int caretY = caretLine * font.lineHeight;

        double top = scrollAmount;
        double bottom = scrollAmount + viewH - font.lineHeight;

        if ((double) caretY < top) {
            setScrollAmount((double) caretY);
        } else if ((double) caretY > bottom) {
            setScrollAmount((double) caretY - (double) (viewH - font.lineHeight));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void moveCursorToMouse(double mx, double my) {
        if (textField == null) return;
        double relX = mx - (getPosition().x + 2);
        double relY = my - (getPosition().y + 2) + scrollAmount;
        textField.seekCursorToPoint(relX, relY);
        ensureCursorVisible();
    }

    @OnlyIn(Dist.CLIENT)
    private boolean blink() {
        return (Util.getMillis() / 500L) % 2L == 0L;
    }

    // ====== clipboard + shortcuts ======
    @OnlyIn(Dist.CLIENT)
    public static boolean isCtrlDown() {
        return net.minecraft.client.gui.screens.Screen.hasControlDown();
    }

    @OnlyIn(Dist.CLIENT)
    private void clipboardSet(String s) {
        net.minecraft.client.Minecraft.getInstance().keyboardHandler.setClipboard(s);
    }

    @OnlyIn(Dist.CLIENT)
    private String clipboardGet() {
        return net.minecraft.client.Minecraft.getInstance().keyboardHandler.getClipboard();
    }

    @OnlyIn(Dist.CLIENT)
    private void selectAll() {
        if (textField == null) return;
        String v = safe(textField.value());
        int len = v.length();

        textField.seekCursor(Whence.ABSOLUTE, 0);
        textField.setSelecting(true);
        textField.seekCursor(Whence.ABSOLUTE, len);
    }

    @OnlyIn(Dist.CLIENT)
    private void copySelection() {
        if (textField == null) return;
        if (!textField.hasSelection()) return;

        Selection sel = textField.selection();
        int a = Math.min(sel.begin, sel.end);
        int b = Math.max(sel.begin, sel.end);

        String v = safe(textField.value());
        a = Mth.clamp(a, 0, v.length());
        b = Mth.clamp(b, 0, v.length());

        if (a < b) clipboardSet(v.substring(a, b));
    }

    @OnlyIn(Dist.CLIENT)
    private void cutSelection() {
        if (textField == null) return;
        if (!textField.hasSelection()) return;

        copySelection();

        Selection sel = textField.selection();
        int a = Math.min(sel.begin, sel.end);
        int b = Math.max(sel.begin, sel.end);

        String v = safe(textField.value());
        a = Mth.clamp(a, 0, v.length());
        b = Mth.clamp(b, 0, v.length());

        if (a < b) {
            String nv = v.substring(0, a) + v.substring(b);
            nv = clampToMax(nv);
            textField.setValue(nv);
            textField.seekCursor(Whence.ABSOLUTE, a);
            textField.setSelecting(false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void pasteClipboard() {
        if (textField == null) return;
        String clip = safe(clipboardGet());
        if (clip.isEmpty()) return;

        // kontrolujemy maxLength “po fakcie”
        textField.insertText(clip);
        String v = clampToMax(safe(textField.value()));
        if (!v.equals(textField.value())) {
            textField.setValue(v);
        }
    }

    // ====== input ======
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isActive() || button != 0) return false;

        if (!isMouseOverElement(mouseX, mouseY)) {
            if (hasFocus) loseFocus();
            return false;
        }

        takeFocus();
        dragging = true;

        if (textField != null) {
            if (!net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
                textField.setSelecting(false);
            }
            moveCursorToMouse(mouseX, mouseY);
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && hasFocus && textField != null) {
            moveCursorToMouse(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (!isMouseOverElement(mouseX, mouseY) || font == null) return false;
        setScrollAmount(scrollAmount - wheelDelta * font.lineHeight);
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!hasFocus || textField == null) return false;

        if (keyCode == KEY_ESCAPE) {
            // zostawiamy ESC do wyjścia z GUI; nie kasujemy focusu na siłę
            return false;
        }

        // CTRL shortcuts
        if (isCtrlDown()) {
            if (net.minecraft.client.gui.screens.Screen.isSelectAll(keyCode)) {
                selectAll();
                sendToServer();
                return true;
            }
            if (net.minecraft.client.gui.screens.Screen.isCopy(keyCode)) {
                copySelection();
                return true;
            }
            if (net.minecraft.client.gui.screens.Screen.isCut(keyCode)) {
                cutSelection();
                clampScroll();
                ensureCursorVisible();
                sendToServer();
                return true;
            }
            if (net.minecraft.client.gui.screens.Screen.isPaste(keyCode)) {
                pasteClipboard();
                clampScroll();
                ensureCursorVisible();
                sendToServer();
                return true;
            }
        }

        // Enter / Tab
        if (keyCode == KEY_ENTER || keyCode == KEY_KP_ENTER) {
            textField.insertText("\n");
            clampScroll();
            ensureCursorVisible();
            sendToServer();
            return true;
        }
        if (keyCode == KEY_TAB) {
            textField.insertText("\t");
            clampScroll();
            ensureCursorVisible();
            sendToServer();
            return true;
        }

        // reszta (strzałki, backspace, delete, home/end, itp.)
        boolean handled = textField.keyPressed(keyCode);
        if (handled) {
            clampScroll();
            ensureCursorVisible();
            sendToServer();
        }

        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char codePoint, int modifiers) {
        if (!hasFocus || textField == null) return false;

        // nie wstawiaj znaków kontrolnych (poza \t i \n obsłużonych wyżej)
        if (codePoint < 32) return true;

        textField.insertText(String.valueOf(codePoint));
        String v = clampToMax(safe(textField.value()));
        if (!v.equals(textField.value())) {
            textField.setValue(v);
        }

        clampScroll();
        ensureCursorVisible();
        sendToServer();
        return true;
    }

    // ====== render ======
    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY,
                                 float partialTicks) {
        if (textField == null || font == null) {
            super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
            return;
        }

        int x0 = getPosition().x;
        int y0 = getPosition().y;
        int w = getSize().width;
        int h = getSize().height;

        int bg = 0xFF202020;
        Integer suppliedBorder = borderColorSupplier != null ? borderColorSupplier.get() : null;
        int border = suppliedBorder != null ? suppliedBorder : hasFocus ? 0xFFFFFFFF : 0xFF808080;

        graphics.fill(x0 - 1, y0 - 1, x0 + w + 1, y0 + h + 1, 0xAA000000);
        graphics.fill(x0, y0, x0 + w, y0 + h, bg);
        graphics.fill(x0, y0, x0 + w, y0 + 1, border);
        graphics.fill(x0, y0 + h - 1, x0 + w, y0 + h, border);
        graphics.fill(x0, y0, x0 + 1, y0 + h, border);
        graphics.fill(x0 + w - 1, y0, x0 + w, y0 + h, border);

        int clipL = x0 + 2;
        int clipT = y0 + 2;
        int clipR = x0 + w - 2;
        int clipB = y0 + h - 2;
        graphics.enableScissor(clipL, clipT, clipR, clipB);

        int firstLine = (int) (scrollAmount / font.lineHeight);
        int y = clipT - (int) scrollAmount + firstLine * font.lineHeight;

        int selectionBegin = textField.hasSelection() ? textField.selection().begin : -1;
        int selectionEnd = textField.hasSelection() ? textField.selection().end : -1;
        int selectionColor = 0x80007FFF;

        for (int idx = firstLine; idx < textField.lineCount(); idx++) {
            if (y > clipB) break;

            Line ln = textField.line(idx);
            String full = safe(textField.value());
            String str = full.substring(ln.begin, ln.end);

            if (textField.hasSelection()) {
                int lineStartChar = ln.begin;
                int lineEndChar = ln.end;

                if (!(selectionEnd <= lineStartChar || selectionBegin >= lineEndChar)) {
                    int selStartInLine = Math.max(0, selectionBegin - lineStartChar);
                    int selEndInLine = Math.min(str.length(), selectionEnd - lineStartChar);

                    if (selStartInLine < selEndInLine) {
                        String preSel = str.substring(0, selStartInLine);
                        String selectionText = str.substring(selStartInLine, selEndInLine);

                        int selX = clipL + font.width(preSel);
                        int selW = font.width(selectionText);
                        graphics.fill(selX, y, selX + selW, y + font.lineHeight, selectionColor);
                    }
                }
            }

            graphics.drawString(font, str, clipL, y, 0xFFFFFFFF);
            y += font.lineHeight;
        }

        // caret
        if (hasFocus && blink()) {
            int curLine = textField.lineAtCursor();
            Line ln = textField.line(curLine);

            String full = safe(textField.value());
            int cursor = Mth.clamp(textField.cursor(), 0, full.length());

            int cx = clipL + font.width(full.substring(ln.begin, cursor));
            int cy = clipT + curLine * font.lineHeight - (int) scrollAmount;

            if (cy >= clipT && cy < clipB) {
                graphics.fill(cx, cy, cx + 1, cy + font.lineHeight, 0xFFFFFFFF);
            }
        }

        graphics.disableScissor();

        // placeholder
        if (safe(textField.value()).isEmpty() && !hasFocus && !placeholder.getString().isEmpty()) {
            graphics.drawString(font, placeholder, clipL, clipT, 0xFF808080);
        }

        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
    }

    // ===== helpers =====
    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private String clampToMax(String s) {
        if (s.length() <= maxLength) return s;
        return s.substring(0, maxLength);
    }

    // ===== cached multiline field (client-only) =====
    private static final class Line {

        final int begin;
        final int end;

        Line(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }
    }

    private static final class Selection {

        final int begin;
        final int end;

        Selection(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static final class CachedTextField extends net.minecraft.client.gui.components.MultilineTextField {

        private List<Line> cache = null;

        CachedTextField(net.minecraft.client.gui.Font font, int w) {
            super(font, w);
            rebuild();
        }

        private List<Line> cacheList() {
            if (cache == null) cache = new ArrayList<>();
            return cache;
        }

        int lineCount() {
            return cacheList().size();
        }

        Line line(int idx) {
            List<Line> c = cacheList();
            if (c.isEmpty()) return new Line(0, 0);
            int i = Mth.clamp(idx, 0, c.size() - 1);
            return c.get(i);
        }

        int lineAtCursor() {
            return super.getLineAtCursor();
        }

        Selection selection() {
            var sv = super.getSelected();
            return new Selection(sv.beginIndex(), sv.endIndex());
        }

        @Override
        public void setValue(String v) {
            super.setValue(v);
            rebuild();
        }

        @Override
        public void insertText(String t) {
            super.insertText(t);
            rebuild();
        }

        private void rebuild() {
            List<Line> c = cacheList();
            c.clear();
            for (var sv : super.iterateLines()) {
                c.add(new Line(sv.beginIndex(), sv.endIndex()));
            }
        }
    }
}
