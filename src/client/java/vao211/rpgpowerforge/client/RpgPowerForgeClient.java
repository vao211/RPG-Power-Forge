package vao211.rpgpowerforge.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import vao211.rpgpowerforge.client.screen.UpgradePowerTableScreen;
import vao211.rpgpowerforge.registry.ModRegistry;

public class RpgPowerForgeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModRegistry.UPGRADE_POWER_TABLE_SCREEN_HANDLER, UpgradePowerTableScreen::new);
    }
}
