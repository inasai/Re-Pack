package com.inasai.repack.effect;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig; // Імпорт для доступу до DeathConfig
import com.inasai.repack.config.category.DeathConfig; // Імпорт для доступу до ScreenEffectType

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ParticleEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();

    public static boolean isActive = false;
    private static long startTime = 0;
    private static final long DURATION_MS = 2000; // Тривалість ефекту частинок (2 секунди)

    public static void activate() {
        isActive = true;
        startTime = System.currentTimeMillis();
        LOGGER.debug("RePack: ParticleEffect activated.");
    }

    public static void deactivate() {
        isActive = false;
        LOGGER.debug("RePack: ParticleEffect deactivated.");
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            if (isActive && RePackConfig.deathConfig.specialDeathScreenEffect.get() == DeathConfig.ScreenEffectType.PARTICLES) {
                if (System.currentTimeMillis() - startTime < DURATION_MS) {
                    for (int i = 0; i < 10; i++) {
                        Minecraft.getInstance().level.addParticle(
                                ParticleTypes.SMOKE,
                                Minecraft.getInstance().player.getX() + (random.nextDouble() - 0.5) * 1.0,
                                Minecraft.getInstance().player.getY() + 0.5 + (random.nextDouble() - 0.5) * 1.0,
                                Minecraft.getInstance().player.getZ() + (random.nextDouble() - 0.5) * 1.0,
                                (random.nextDouble() - 0.5) * 0.05,
                                0.05 + random.nextDouble() * 0.05,
                                (random.nextDouble() - 0.5) * 0.05
                        );
                    }
                } else {
                    deactivate();
                    LOGGER.info("RePack: Particle effect duration ended. Deactivating.");
                }
            }
        }
    }
}