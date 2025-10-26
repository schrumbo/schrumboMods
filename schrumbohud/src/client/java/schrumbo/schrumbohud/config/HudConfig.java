package schrumbo.schrumbohud.config;

public class HudConfig {
    //inventoryhud
    public boolean enabled = true;
    public int x = 10;
    public int y = 10;
    public float scale = 1.0f;

    //background
    public boolean backgroundEnabled = true;
    public float backgroundOpacity = 1.0f;
    public String backgroundColor = "#1e1e2e"; // Mocha Base

    //background outline
    public boolean outlineEnabled = true;
    public float outlineOpacity = 1.0f;
    public String outlineColor = "#89b4fa"; // Mocha Blue

    //text
    public boolean textShadowEnabled = true;
    public float textShadowOpacity = 1.0f;
    public String textColor = "#cdd6f4"; // Mocha Text

    //corner
    public float cornerRadius = 0.2f;

    //individual slots
    public boolean slotBackgroundEnabled = true;
    public float slotBackgroundOpacity = 1.0f;
    public String slotBackgroundColor = "#313244"; // Mocha Surface0
}