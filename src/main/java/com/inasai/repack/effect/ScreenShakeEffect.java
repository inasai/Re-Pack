package com.inasai.repack.effect;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.config.category.DeathConfig;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.logging.LogUtils;

import java.util.Random;

import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ScreenShakeEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();

    public static boolean isActive = false;
    public static long startTime = 0;
    public static final long DURATION_MS = 1000;
    public static final float MAX_INTENSITY = 0.05F;

    public static void activate() {
        isActive = true;
        startTime = System.currentTimeMillis();
        LOGGER.debug("RePack: ScreenShakeEffect activated.");
    }

    public static void deactivate() {
        isActive = false;
        LOGGER.debug("RePack: ScreenShakeEffect deactivated.");
    }

    public static float getCurrentShakeIntensity() {
        if (isActive && RePackConfig.deathConfig.specialDeathScreenEffect.get() == DeathConfig.ScreenEffectType.SHAKE) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime < DURATION_MS) {
                return MAX_INTENSITY * ((float)(DURATION_MS - elapsedTime) / DURATION_MS);
            } else {
                deactivate();
            }
        }
        return 0.0F;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            if (isActive && System.currentTimeMillis() - startTime >= DURATION_MS) {
                deactivate();
                LOGGER.info("RePack: Screen shake effect duration ended. Deactivating.");
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {

    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {

    }
}