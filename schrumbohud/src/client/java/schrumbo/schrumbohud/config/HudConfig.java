package schrumbo.schrumbohud.config;

public class HudConfig {

    // Inventory HUD
    public boolean enabled = true;
    public int x = 10;
    public int y = 10;
    public float scale = 1.0f;

    // Background
    public boolean backgroundEnabled = true;
    public float backgroundOpacity = 1.0f;
    public int backgroundColor = 0xFF1E1E2E; // Mocha Base

    // Outline
    public boolean outlineEnabled = true;
    public float outlineOpacity = 1.0f;
    public int outlineColor = 0xFF89B4FA; // Mocha Blue

    // Text
    public boolean textShadowEnabled = true;
    public float textShadowOpacity = 1.0f;
    public int textColor = 0xFFCDD6F4; // Mocha Text

    // Corner
    public float cornerRadius = 0.2f;

    // Slot Background
    public boolean slotBackgroundEnabled = true;
    public float slotBackgroundOpacity = 1.0f;
    public int slotBackgroundColor = 0xFF313244; // Mocha Surface0

    /**
     * @return Background color with opacity applied (ARGB)
     */
    public int getBackgroundColorWithOpacity() {
        int alpha = (int) (backgroundOpacity * 255);
        return (alpha << 24) | (backgroundColor & 0x00FFFFFF);
    }

    /**
     * @return Outline color with opacity applied (ARGB)
     */
    public int getOutlineColorWithOpacity() {
        int alpha = (int) (outlineOpacity * 255);
        return (alpha << 24) | (outlineColor & 0x00FFFFFF);
    }

    /**
     * @return Slot background color with opacity applied (ARGB)
     */
    public int getSlotBackgroundColorWithOpacity() {
        int alpha = (int) (slotBackgroundOpacity * 255);
        return (alpha << 24) | (slotBackgroundColor & 0x00FFFFFF);
    }

    /**
     * @return Text color with opacity applied (ARGB)
     */
    public int getTextColorWithOpacity() {
        int alpha = (int) (textShadowOpacity * 255);
        return (alpha << 24) | (textColor & 0x00FFFFFF);
    }

    /**
     * Toggle HUD visibility
     */
    public void toggle() {
        this.enabled = !this.enabled;
    }

    /**
     * Reset all values to defaults
     */
    public void reset() {
        this.enabled = true;
        this.x = 10;
        this.y = 10;
        this.scale = 1.0f;

        this.backgroundEnabled = true;
        this.backgroundOpacity = 1.0f;
        this.backgroundColor = 0xFF1E1E2E;

        this.outlineEnabled = true;
        this.outlineOpacity = 1.0f;
        this.outlineColor = 0xFF89B4FA;

        this.textShadowEnabled = true;
        this.textShadowOpacity = 1.0f;
        this.textColor = 0xFFCDD6F4;

        this.cornerRadius = 0.2f;

        this.slotBackgroundEnabled = true;
        this.slotBackgroundOpacity = 1.0f;
        this.slotBackgroundColor = 0xFF313244;
    }
}
