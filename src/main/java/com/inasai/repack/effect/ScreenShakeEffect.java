package com.inasai.repack.effect;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig; // Імпорт для доступу до DeathConfig
import com.inasai.repack.config.category.DeathConfig; // Імпорт для доступу до ScreenEffectType

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent; // Поки не використовується, але імпорт залишаємо
import net.minecraftforge.client.event.RenderGuiEvent; // Поки не використовується, але імпорт залишаємо
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ScreenShakeEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();

    public static boolean isActive = false;
    public static long startTime = 0;
    public static final long DURATION_MS = 1000; // Тривалість тремтіння (1 секунда)
    public static final float MAX_INTENSITY = 0.05F; // Максимальна інтенсивність тремтіння

    public static void activate() {
        isActive = true;
        startTime = System.currentTimeMillis();
        LOGGER.debug("RePack: ScreenShakeEffect activated.");
    }

    public static void deactivate() {
        isActive = false;
        LOGGER.debug("RePack: ScreenShakeEffect deactivated.");
    }

    // Цей метод потрібен, щоб MixinGameRenderer міг отримати інтенсивність тряски
    public static float getCurrentShakeIntensity() {
        if (isActive && RePackConfig.deathConfig.specialDeathScreenEffect.get() == DeathConfig.ScreenEffectType.SHAKE) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime < DURATION_MS) {
                // Інтенсивність зменшується до нуля до кінця тривалості
                return MAX_INTENSITY * ((float)(DURATION_MS - elapsedTime) / DURATION_MS);
            } else {
                deactivate(); // Вимкнути, якщо час вичерпано
            }
        }
        return 0.0F;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            // Тут ми тільки перевіряємо, чи завершився ефект, але не рендеримо
            if (isActive && System.currentTimeMillis() - startTime >= DURATION_MS) {
                deactivate();
                LOGGER.info("RePack: Screen shake effect duration ended. Deactivating.");
            }
        }
    }

    // Залишаємо ці методи, як заглушки для майбутнього або якщо знадобляться
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        // Логіка для GIF буде тут
        // if (RePackConfig.deathConfig.specialDeathScreenEffect.get() == DeathConfig.ScreenEffectType.GIF) { ... }
    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        // Цей метод використовується для впливу на поле зору, але не для тряски
    }
}