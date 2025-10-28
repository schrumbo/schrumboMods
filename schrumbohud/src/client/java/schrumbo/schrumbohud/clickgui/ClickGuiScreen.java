package schrumbo.schrumbohud.clickgui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import schrumbo.schrumbohud.SchrumboHUDClient;
import schrumbo.schrumbohud.Utils.RenderUtils;
import schrumbo.schrumbohud.clickgui.categories.*;
import schrumbo.schrumbohud.clickgui.widgets.ColorPickerWidget;
import schrumbo.schrumbohud.clickgui.widgets.Widget;
import schrumbo.schrumbohud.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private int panelX = 50;
    private int panelY = 50;
    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 400;
    private static final int TITLE_BAR_HEIGHT = 25;

    private boolean draggingPanel = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    private final List<Category> categories = new ArrayList<>();
    private int scrollOffset = 0;
    private int contentHeight = 0;

    public ClickGuiScreen() {
        super(Text.literal("SchrumboHUD Settings"));

        categories.add(new GeneralCategory());
        categories.add(new PresetsCategory());
        categories.add(new PositionCategory());
        categories.add(new BackgroundCategory());
        categories.add(new OutlineCategory());
        categories.add(new SlotCategory());
        categories.add(new TextCategory());
    }

    @Override
    protected void init() {
        super.init();
        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;

        initializeCategories();
    }

    private void initializeCategories() {
        int contentWidth = PANEL_WIDTH - 20;
        int currentY = 0;

        for (Category category : categories) {
            category.setPosition(
                    panelX + 10,
                    panelY + TITLE_BAR_HEIGHT + 10 + currentY - scrollOffset,
                    contentWidth
            );
            currentY += category.getTotalHeight() + 10;
        }

        contentHeight = currentY;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.fillGradient(0, 0, this.width, this.height, 0x80000000, 0x80000000);

        renderPanel(context);

        int contentX = panelX + 10;
        int contentY = panelY + TITLE_BAR_HEIGHT + 10;
        int contentWidth = PANEL_WIDTH - 20;
        int contentAreaHeight = PANEL_HEIGHT - TITLE_BAR_HEIGHT - 20;

        context.enableScissor(contentX, contentY, contentX + contentWidth, contentY + contentAreaHeight);

        for (Category category : categories) {
            category.render(context, mouseX, mouseY, delta);
        }

        context.disableScissor();

        renderScrollbar(context);

        for (Category category : categories) {
            if (!category.isCollapsed()) {
                for (Widget widget : category.widgets) {
                    if (widget instanceof ColorPickerWidget colorPicker) {
                        colorPicker.renderPopup(context, mouseX, mouseY, delta);
                    }
                }
            }
        }
    }

    private void renderPanel(DrawContext context) {
        var config = SchrumboHUDClient.config;

        int bgColor = config.getColorWithAlpha(0x1a1a1a, 0.95f);
        RenderUtils.fillRoundedRect(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0.0f, bgColor);

        int outlineColor = config.getColorWithAlpha(config.colors.accent, 1.0f);
        RenderUtils.drawRoundedRect(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0.0f, 2, outlineColor);

        int titleBgColor = config.getColorWithAlpha(config.colors.accent, 0.3f);
        RenderUtils.fillRoundedRect(context, panelX, panelY, PANEL_WIDTH, TITLE_BAR_HEIGHT, 0.0f, titleBgColor);

        String title = "SchrumboHUD Settings";
        int titleX = panelX + (PANEL_WIDTH - client.textRenderer.getWidth(title)) / 2;
        int titleY = panelY + (TITLE_BAR_HEIGHT - 8) / 2;
        context.drawText(client.textRenderer, Text.literal(title), titleX, titleY, 0xFFFFFF, true);
    }

    private void renderScrollbar(DrawContext context) {
        var config = SchrumboHUDClient.config;

        int scrollbarWidth = 4;
        int scrollbarX = panelX + PANEL_WIDTH - scrollbarWidth - 3;
        int scrollbarY = panelY + TITLE_BAR_HEIGHT + 10;
        int scrollbarHeight = PANEL_HEIGHT - TITLE_BAR_HEIGHT - 20;

        int trackColor = config.getColorWithAlpha(0x000000, 0.3f);
        context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth,
                scrollbarY + scrollbarHeight, trackColor);

        int maxScroll = Math.max(0, contentHeight - (PANEL_HEIGHT - TITLE_BAR_HEIGHT - 20));
        if (maxScroll > 0) {
            float thumbHeightRatio = (float) (PANEL_HEIGHT - TITLE_BAR_HEIGHT - 20) / contentHeight;
            int thumbHeight = Math.max(20, (int) (scrollbarHeight * thumbHeightRatio));
            int thumbY = scrollbarY + (int) ((float) scrollOffset / maxScroll * (scrollbarHeight - thumbHeight));

            int thumbColor = config.getColorWithAlpha(config.colors.accent, 0.8f);
            RenderUtils.fillRoundedRect(context, scrollbarX, thumbY, scrollbarWidth, thumbHeight, 2.0f, thumbColor);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        for (Category category : categories) {
            if (category.mouseClicked(mouseX, mouseY, button)) {
                initializeCategories();
                return true;
            }
        }

        if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH &&
                mouseY >= panelY && mouseY <= panelY + TITLE_BAR_HEIGHT) {
            draggingPanel = true;
            dragOffsetX = (int) (mouseX - panelX);
            dragOffsetY = (int) (mouseY - panelY);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        for (Category category : categories) {
            if (category.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }

        if (draggingPanel) {
            panelX = (int) (mouseX - dragOffsetX);
            panelY = (int) (mouseY - dragOffsetY);
            initializeCategories();
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseReleased(mouseX, mouseY, button);

        // Check categories first
        for (Category category : categories) {
            if (category.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        // Release panel dragging
        if (draggingPanel) {
            draggingPanel = false;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH &&
                mouseY >= panelY + TITLE_BAR_HEIGHT && mouseY <= panelY + PANEL_HEIGHT) {

            int maxScroll = Math.max(0, contentHeight - (PANEL_HEIGHT - TITLE_BAR_HEIGHT - 20));
            scrollOffset = Math.max(0, Math.min(maxScroll,
                    scrollOffset - (int) (verticalAmount * 20)));

            initializeCategories();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        ConfigManager.save();
        super.close();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            ConfigManager.save();
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
