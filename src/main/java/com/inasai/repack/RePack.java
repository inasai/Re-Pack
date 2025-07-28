package com.inasai.repack;

import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.event.ClientSetup;
import com.inasai.repack.event.category.DeathEvents;
import com.inasai.repack.event.category.GuideEvents;
import com.inasai.repack.effect.ParticleEffect;
import com.inasai.repack.effect.ScreenShakeEffect;
import com.inasai.repack.sound.ModSounds;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

@Mod(RePack.MOD_ID)
public class RePack {
    public static final String MOD_ID = "repack";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RePack() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = net.minecraftforge.common.MinecraftForge.EVENT_BUS;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RePackConfig.CLIENT_SPEC);

        modEventBus.register(ClientSetup.class);

        ModSounds.register(modEventBus);

        forgeEventBus.register(DeathEvents.class);
        LOGGER.info("RePack: DeathEvents registered to Forge Event Bus.");

        forgeEventBus.register(ParticleEffect.class);
        LOGGER.info("RePack: ParticleEffect registered to Forge Event Bus.");

        forgeEventBus.register(ScreenShakeEffect.class);
        LOGGER.info("RePack: ScreenShakeEffect registered to Forge Event Bus.");

        // forgeEventBus.register(GuideEvents.class); nothing
        // LOGGER.info("RePack: GuideEvents registered to Forge Event Bus.");
    }
}