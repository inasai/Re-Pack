package com.inasai.repack;

import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.event.ClientSetup;
import com.inasai.repack.sound.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext; // Додано для реєстрації подій
import net.minecraftforge.eventbus.api.IEventBus; // Імпортуйте IEventBus
import org.slf4j.Logger;

@Mod(RePack.MOD_ID)
public class RePack {
    public static final String MOD_ID = "repack";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RePack() {
        // Отримуємо IEventBus з FMLJavaModLoadingContext в конструкторі
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register our mod's config.
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RePackConfig.CLIENT_SPEC); // Ця лінія поки залишається, але її теж можна покращити

        // Register the event bus subscriber for client-side events
        modEventBus.register(ClientSetup.class);

        // Реєстрація звуків
        ModSounds.register(modEventBus);
    }
}