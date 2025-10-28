package schrumbo.schrumbohud.clickgui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import schrumbo.schrumbohud.SchrumboHUDClient;
import schrumbo.schrumbohud.Utils.RenderUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorPickerWidget extends Widget {
    private final Supplier<Integer> colorGetter;
    private final Consumer<Integer> colorSetter;

    private boolean popupOpen = false;
    private boolean draggingSV = false;
    private boolean draggingHue = false;

    private float hue = 0;
    private float saturation = 1;
    private float value = 1;

    private static final int WIDGET_HEIGHT = 40;
    private static final int POPUP_WIDTH = 240;
    private static final int POPUP_HEIGHT = 215;
    private static final int PICKER_SIZE = 140;
    private static final int SLIDER_WIDTH = 20;
    private static final int SLIDER_SPACING = 15;
    private static final int PADDING = 20;
    private static final int TITLE_BAR_HEIGHT = 30;
    private static final int CLICK_BLOCK_PADDING = 10;
    private static final int POPUP_SPACING = 40;
    private static final int KNOB_WIDTH = 28;
    private static final int KNOB_HEIGHT = 8;

    private int popupX;
    private int popupY;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    public ColorPickerWidget(int x, int y, int width, String label, Supplier<Integer> colorGetter, Consumer<Integer> colorSetter) {
        super(x, y, width, WIDGET_HEIGHT, label);
        this.colorGetter = colorGetter;
        this.colorSetter = colorSetter;

        int color = colorGetter.get();
        float[] hsv = rgbToHsv(color);
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.value = hsv[2];
    }

    public void setPanelBounds(int panelX, int panelY, int panelWidth, int panelHeight) {
        this.panelX = panelX;
        this.panelY = panelY;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var config = SchrumboHUDClient.config;

        int bgColor = config.getColorWithAlpha(0x1a1a1a, 0.8f);
        RenderUtils.fillRoundedRect(context, x, y, width, WIDGET_HEIGHT, 0.0f, bgColor);

        context.drawText(MinecraftClient.getInstance().textRenderer,
                Text.literal(label), x + 8, y + 6, 0xFFFFFF, true);

        int previewSize = 20;
        int previewX = x + width - 8 - previewSize;
        int previewY = y + (WIDGET_HEIGHT - previewSize) / 2;

        int currentColor = colorGetter.get();
        RenderUtils.fillRoundedRect(context, previewX, previewY, previewSize, previewSize, 0.2f, 0xFF000000 | currentColor);
        RenderUtils.drawRoundedRect(context, previewX, previewY, previewSize, previewSize, 0.2f, 1, config.colors.accent);
    }

    public void renderPopup(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!popupOpen) return;

        var config = SchrumboHUDClient.config;
        var client = MinecraftClient.getInstance();

        popupX = panelX + panelWidth + POPUP_SPACING;

        popupY = panelY + (panelHeight - POPUP_HEIGHT) / 2;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        if (popupX + POPUP_WIDTH > screenWidth - 10) {
            popupX = panelX - POPUP_WIDTH - POPUP_SPACING;
        }

        popupY = Math.max(10, Math.min(popupY, screenHeight - POPUP_HEIGHT - 10));

        int bgColor = config.getColorWithAlpha(0x1a1a1a, 0.95f);
        RenderUtils.fillRoundedRect(context, popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT, 0.0f, bgColor);

        int outlineColor = config.getColorWithAlpha(config.colors.accent, 1.0f);
        RenderUtils.drawRoundedRect(context, popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT, 0.0f, 2, outlineColor);

        int titleBgColor = config.getColorWithAlpha(config.colors.accent, 0.3f);
        RenderUtils.fillRoundedRect(context, popupX, popupY, POPUP_WIDTH, TITLE_BAR_HEIGHT, 0.0f, titleBgColor);

        String title = "Color Picker";
        int titleWidth = client.textRenderer.getWidth(title);
        context.drawText(client.textRenderer, Text.literal(title),
                popupX + (POPUP_WIDTH - titleWidth) / 2, popupY + 11, 0xFFFFFF, true);

        int contentY = popupY + TITLE_BAR_HEIGHT + 15;

        int pickerX = popupX + PADDING;
        renderSVPicker(context, pickerX, contentY, mouseX, mouseY);

        int sliderX = pickerX + PICKER_SIZE + SLIDER_SPACING;
        renderHueSlider(context, sliderX, contentY, mouseX, mouseY);
    }

    private void renderSVPicker(DrawContext context, int px, int py, int mouseX, int mouseY) {
        var matrices = context.getMatrices();
        matrices.push();

        int baseColor = hsvToRgb(hue, 1.0f, 1.0f);
        for (int i = 0; i < PICKER_SIZE; i++) {
            float s = (float) i / PICKER_SIZE;
            int gradColor = lerpColor(0xFFFFFFFF, 0xFF000000 | baseColor, s);
            context.fill(px + i, py, px + i + 1, py + PICKER_SIZE, gradColor);
        }

        for (int i = 0; i < PICKER_SIZE; i++) {
            float v = 1.0f - ((float) i / PICKER_SIZE);
            int alpha = (int) ((1.0f - v) * 255);
            int blackOverlay = (alpha << 24);
            context.fill(px, py + i, px + PICKER_SIZE, py + i + 1, blackOverlay);
        }

        int cursorX = px + (int) (saturation * PICKER_SIZE);
        int cursorY = py + (int) ((1.0f - value) * PICKER_SIZE);
        int cursorRadius = 3;

        RenderUtils.fillCircle(context, cursorX, cursorY, cursorRadius, 0xFFFFFFFF);


        matrices.pop();
    }

    private void renderHueSlider(DrawContext context, int sx, int sy, int mouseX, int mouseY) {
        var config = SchrumboHUDClient.config;

        int trackBgColor = config.getColorWithAlpha(0x000000, 0.3f);
        RenderUtils.fillRoundedRect(context, sx, sy, SLIDER_WIDTH, PICKER_SIZE, 3.0f, trackBgColor);

        for (int i = 0; i < PICKER_SIZE; i++) {
            float h = (float) i / PICKER_SIZE;
            int color = 0xFF000000 | hsvToRgb(h, 1.0f, 1.0f);

            if (i == 0) {
                RenderUtils.fillRoundedRect(context, sx, sy, SLIDER_WIDTH, 1, 3.0f, color);
            } else if (i == PICKER_SIZE - 1) {
                RenderUtils.fillRoundedRect(context, sx, sy + i, SLIDER_WIDTH, 1, 3.0f, color);
            } else {
                context.fill(sx, sy + i, sx + SLIDER_WIDTH, sy + i + 1, color);
            }
        }

        int outlineColor = config.getColorWithAlpha(0xFFFFFF, 0.2f);
        RenderUtils.drawRoundedRect(context, sx, sy, SLIDER_WIDTH, PICKER_SIZE, 3.0f, 1, outlineColor);

        int knobY = sy + (int) (hue * PICKER_SIZE) - KNOB_HEIGHT / 2;
        knobY = Math.max(sy - KNOB_HEIGHT / 2, Math.min(knobY, sy + PICKER_SIZE - KNOB_HEIGHT / 2));
        int knobX = sx + (SLIDER_WIDTH - KNOB_WIDTH) / 2;

        boolean hovered = mouseX >= sx - 5 && mouseX <= sx + SLIDER_WIDTH + 5 &&
                mouseY >= knobY && mouseY <= knobY + KNOB_HEIGHT;

        int knobBgColor = (hovered || draggingHue) ? 0xFFFFFFFF : 0xFFE0E0E0;
        RenderUtils.drawRectWithCutCorners(context, knobX, knobY, KNOB_WIDTH, KNOB_HEIGHT, 2, knobBgColor);

        int knobOutlineColor = (hovered || draggingHue) ?
                config.colors.accent :
                config.getColorWithAlpha(0x000000, 0.3f);
        RenderUtils.drawRectWithCutCorners(context, knobX, knobY, KNOB_WIDTH, KNOB_HEIGHT, 2, knobOutlineColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        if (popupOpen) {
            int paddedX = popupX - CLICK_BLOCK_PADDING;
            int paddedY = popupY - CLICK_BLOCK_PADDING;
            int paddedWidth = POPUP_WIDTH + (CLICK_BLOCK_PADDING * 2);
            int paddedHeight = POPUP_HEIGHT + (CLICK_BLOCK_PADDING * 2);

            if (mouseX >= paddedX && mouseX <= paddedX + paddedWidth &&
                    mouseY >= paddedY && mouseY <= paddedY + paddedHeight) {

                return handlePopupClick(mouseX, mouseY);
            } else {
                popupOpen = false;
                return true;
            }
        }

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            popupOpen = true;
            return true;
        }

        return false;
    }

    private boolean handlePopupClick(double mouseX, double mouseY) {
        int contentY = popupY + TITLE_BAR_HEIGHT + 15;
        int pickerX = popupX + PADDING;
        int sliderX = pickerX + PICKER_SIZE + SLIDER_SPACING;

        if (mouseX >= pickerX && mouseX <= pickerX + PICKER_SIZE &&
                mouseY >= contentY && mouseY <= contentY + PICKER_SIZE) {
            draggingSV = true;
            updateSVFromMouse(mouseX, mouseY, pickerX, contentY);
            return true;
        }

        if (mouseX >= sliderX && mouseX <= sliderX + SLIDER_WIDTH &&
                mouseY >= contentY && mouseY <= contentY + PICKER_SIZE) {
            draggingHue = true;
            updateHueFromMouse(mouseY, contentY);
            return true;
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!popupOpen || button != 0) return false;

        int contentY = popupY + TITLE_BAR_HEIGHT + 15;
        int pickerX = popupX + PADDING;

        if (draggingSV) {
            updateSVFromMouse(mouseX, mouseY, pickerX, contentY);
            return true;
        }

        if (draggingHue) {
            updateHueFromMouse(mouseY, contentY);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        if (draggingSV || draggingHue) {
            draggingSV = false;
            draggingHue = false;
            return true;
        }

        return false;
    }

    private void updateSVFromMouse(double mouseX, double mouseY, int pickerX, int contentY) {
        saturation = Math.max(0, Math.min(1, (float) (mouseX - pickerX) / PICKER_SIZE));
        value = Math.max(0, Math.min(1, 1.0f - (float) (mouseY - contentY) / PICKER_SIZE));

        int newColor = hsvToRgb(hue, saturation, value);
        colorSetter.accept(newColor);
    }

    private void updateHueFromMouse(double mouseY, int contentY) {
        hue = Math.max(0, Math.min(1, (float) (mouseY - contentY) / PICKER_SIZE));

        int newColor = hsvToRgb(hue, saturation, value);
        colorSetter.accept(newColor);
    }

    private int lerpColor(int from, int to, float t) {
        int aFrom = (from >> 24) & 0xFF;
        int rFrom = (from >> 16) & 0xFF;
        int gFrom = (from >> 8) & 0xFF;
        int bFrom = from & 0xFF;

        int aTo = (to >> 24) & 0xFF;
        int rTo = (to >> 16) & 0xFF;
        int gTo = (to >> 8) & 0xFF;
        int bTo = to & 0xFF;

        int a = (int) (aFrom + (aTo - aFrom) * t);
        int r = (int) (rFrom + (rTo - rFrom) * t);
        int g = (int) (gFrom + (gTo - gFrom) * t);
        int b = (int) (bFrom + (bTo - bFrom) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static float[] rgbToHsv(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0;
        if (delta != 0) {
            if (max == r) h = ((g - b) / delta) % 6;
            else if (max == g) h = ((b - r) / delta) + 2;
            else h = ((r - g) / delta) + 4;
            h /= 6;
            if (h < 0) h += 1;
        }

        float s = (max == 0) ? 0 : delta / max;
        float v = max;

        return new float[]{h, s, v};
    }

    private static int hsvToRgb(float h, float s, float v) {
        int i = (int) (h * 6);
        float f = h * 6 - i;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        float r, g, b;
        switch (i % 6) {
            case 0: r = v; g = t; b = p; break;
            case 1: r = q; g = v; b = p; break;
            case 2: r = p; g = v; b = t; break;
            case 3: r = p; g = q; b = v; break;
            case 4: r = t; g = p; b = v; break;
            case 5: r = v; g = p; b = q; break;
            default: r = g = b = 0;
        }

        int red = (int) (r * 255);
        int green = (int) (g * 255);
        int blue = (int) (b * 255);

        return (red << 16) | (green << 8) | blue;
    }

    public boolean isPopupOpen() {
        return popupOpen;
    }
}
