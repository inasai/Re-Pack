package com.inasai.repack;

import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.event.ClientSetup;
import com.inasai.repack.event.category.DeathEvents; // Новий імпорт для DeathEvents
import com.inasai.repack.event.category.GuideEvents; // Новий імпорт для GuideEvents (хоча поки порожній)
import com.inasai.repack.effect.ParticleEffect; // Новий імпорт для ParticleEffect
import com.inasai.repack.effect.ScreenShakeEffect; // Новий імпорт для ScreenShakeEffect
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
        IEventBus forgeEventBus = net.minecraftforge.common.MinecraftForge.EVENT_BUS;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RePackConfig.CLIENT_SPEC);

        // Реєстрація класів подій
        modEventBus.register(ClientSetup.class); // Цей клас для реєстрації екрану конфігурації (MOD Bus)

        ModSounds.register(modEventBus); // Реєстрація звуків (MOD Bus)

        forgeEventBus.register(DeathEvents.class); // Реєстрація обробника подій смерті (FORGE Bus)
        LOGGER.info("RePack: DeathEvents registered to Forge Event Bus.");

        forgeEventBus.register(ParticleEffect.class); // Реєстрація обробника частинок (FORGE Bus)
        LOGGER.info("RePack: ParticleEffect registered to Forge Event Bus.");

        forgeEventBus.register(ScreenShakeEffect.class); // Реєстрація обробника тряски екрану (FORGE Bus)
        LOGGER.info("RePack: ScreenShakeEffect registered to Forge Event Bus.");

        // forgeEventBus.register(GuideEvents.class); // Можна розкоментувати, якщо GuideEvents буде мати події, що вимагають FORGE Bus
        // LOGGER.info("RePack: GuideEvents registered to Forge Event Bus.");
    }
}