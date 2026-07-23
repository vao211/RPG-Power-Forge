package vao211.rpgpowerforge;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vao211.rpgpowerforge.event.ModDropEvents;
import vao211.rpgpowerforge.registry.ModRegistry;

public class RpgPowerForge implements ModInitializer {
    public static final String MOD_ID = "rpgpowerforge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing RPG Power Forge...");
        ModRegistry.registerAll();
        ModDropEvents.register();
        LOGGER.info("RPG Power Forge loaded successfully!");
    }
}