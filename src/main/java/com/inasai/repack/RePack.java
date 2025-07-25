package com.inasai.repack;

import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.event.ClientSetup;
import com.inasai.repack.event.DeathEventHandler; // <-- Додати цей імпорт
import com.inasai.repack.sound.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import org.slf4j.Logger;

@Mod(RePack.MOD_ID)
public class RePack {
    public static final String MOD_ID = "repack";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RePack() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = net.minecraftforge.common.MinecraftForge.EVENT_BUS; // <-- Отримати Forge Event Bus

        // Register our mod's config.
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RePackConfig.CLIENT_SPEC);

        // Register the event bus subscriber for client-side events (for ClientSetup, etc.)
        modEventBus.register(ClientSetup.class);

        // Реєстрація звуків
        ModSounds.register(modEventBus);

        // <-- ДОДАТИ ЦЮ РЕЄСТРАЦІЮ ДЛЯ ОБРОБНИКА ПОДІЙ СМЕРТІ
        forgeEventBus.register(DeathEventHandler.class);
        LOGGER.info("RePack: DeathEventHandler registered to Forge Event Bus."); // Додатковий лог
    }
}