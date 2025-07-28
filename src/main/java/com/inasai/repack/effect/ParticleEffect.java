package com.inasai.repack.effect;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.config.category.DeathConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import java.util.Random;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ParticleEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();

    public static boolean isActive = false;
    private static long startTime = 0;
    private static final long DURATION_MS = 2500;

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
                                Minecraft.getInstance().player.getX() + (random.nextDouble() - 0.5),
                                Minecraft.getInstance().player.getY() + 0.5 + (random.nextDouble() - 0.5),
                                Minecraft.getInstance().player.getZ() + (random.nextDouble() - 0.5),
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