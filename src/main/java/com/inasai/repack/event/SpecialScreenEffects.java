package com.inasai.repack.event;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.Random;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SpecialScreenEffects {
    private static final Random random = new Random();
    public static boolean isShakeEffectActive = false;
    public static long shakeStartTime = 0;
    public static final long SHAKE_DURATION_MS = 1000; // Тривалість тремтіння (1 секунда)
    public static final float MAX_SHAKE_INTENSITY = 0.05F; // Максимальна інтенсивність тремтіння

    public static void activateShakeEffect() {
        isShakeEffectActive = true;
        shakeStartTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            if (isShakeEffectActive) {
                if (System.currentTimeMillis() - shakeStartTime >= SHAKE_DURATION_MS) {
                    isShakeEffectActive = false; // Вимикаємо ефект після закінчення тривалості
                }
            }
        }
    }

    // Mixin для тряски екрану буде в іншому файлі, але тут ми тримаємо прапорець active

    // Може бути використано для майбутніх ефектів, якщо потрібен рендеринг над GUI
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        // Логіка для GIF буде тут
        if (RePackConfig.specialDeathScreenEffect.get() == RePackConfig.ScreenEffectType.GIF) {
            // Наразі тут нічого немає, оскільки GIF складніший і вимагатиме завантаження анімації.
            // Це буде реалізовано пізніше.
        }
    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        // Цей метод використовується для впливу на поле зору, але не для тряски
        // Залишаємо його тут як приклад, якщо знадобиться.
    }
}