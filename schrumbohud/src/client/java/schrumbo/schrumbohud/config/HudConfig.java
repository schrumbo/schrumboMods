package schrumbo.schrumbohud.config;

import com.google.gson.annotations.SerializedName;

public class HudConfig {


    public boolean enabled = true;

    @SerializedName("anchor")
    public Anchor anchor = new Anchor();

    @SerializedName("position")
    public Position position = new Position();

    @SerializedName("colors")
    public Colors colors = new Colors();


    public static class Anchor {
        @SerializedName("horizontal")
        public HorizontalAnchor horizontal = HorizontalAnchor.LEFT;

        @SerializedName("vertical")
        public VerticalAnchor vertical = VerticalAnchor.TOP;
    }

    public enum HorizontalAnchor {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAnchor {
        TOP,
        CENTER,
        BOTTOM
    }

    public static class Position{
        public int x = 10;
        public int y = 10;
    }

    public static class Colors {
        @SerializedName("background")
        public int background = 0x1E1E2E;

        @SerializedName("border")
        public int border = 0x45475A;

        @SerializedName("text")
        public int text = 0xCDD6F4;

        @SerializedName("accent")
        public int accent = 0xCBA6F7;
    }

    public float scale = 1.0f;
    public float cornerRadius = 0.2f;

    public boolean backgroundEnabled = true;
    public float backgroundOpacity = 1.0f;

    public boolean outlineEnabled = true;
    public float outlineOpacity = 1.0f;

    public boolean textShadowEnabled = true;
    public float textShadowOpacity = 1.0f;

    public boolean slotBackgroundEnabled = true;
    public float slotBackgroundOpacity = 1.0f;

    /**
     * Used to apply opacity to a color
     * @param color a color wihtout opacity applied
     * @param opacity wanted opacity
     * @return color with opacity
     */
    public int getColorWithAlpha(int color, float opacity){
        int alpha = (int) (opacity * 255);
        return (alpha << 24) | (color & 0x00FFFFFF);
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

        position.x = 10;
        position.y = 10;
        this.scale = 1.0f;

        this.backgroundEnabled = true;
        this.backgroundOpacity = 1.0f;
        colors.background = 0xFF1E1E2E;

        this.outlineEnabled = true;
        this.outlineOpacity = 1.0f;
        colors.border = 0xFF89B4FA;

        this.textShadowEnabled = true;
        this.textShadowOpacity = 1.0f;
        colors.text = 0xFFCDD6F4;

        this.cornerRadius = 0.2f;

        this.slotBackgroundEnabled = true;
        this.slotBackgroundOpacity = 1.0f;
        colors.accent = 0xFF313244;
    }
}
