package schrumbo.schrumbohud.clickgui.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandManager {
    public void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("test_command").executes(context -> {
                context.getSource().sendFeedback(() -> Text.literal("Called /test_command."), false);
                return 1;
            }));
        });
    }
}
