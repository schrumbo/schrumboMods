package schrumbo.schrumbohud;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schrumbo.schrumbohud.config.ConfigManager;
import schrumbo.schrumbohud.config.HudConfig;
import schrumbo.schrumbohud.keybind.KeybindHandler;

import java.util.Objects;

public class SchrumboHUDClient implements ClientModInitializer {
	public static final String MOD_ID = "schrumbohud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager configManager;
	public static HudConfig config;
	@Override
	public void onInitializeClient() {
		LOGGER.info("initializing SchrumboHUD");

		configManager = new ConfigManager();
		config = configManager.load();

		KeybindHandler.register();
		LOGGER.info("SchrumboHUD initialized");
	}
}