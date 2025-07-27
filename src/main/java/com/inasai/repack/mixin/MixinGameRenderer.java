package com.inasai.repack.mixin;

import com.inasai.repack.effect.ScreenShakeEffect; // Змінено імпорт на новий ScreenShakeEffect
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft; // Цей імпорт тут не потрібен, можна видалити.
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    private static final Random random = new Random();

    @Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
    private void repack_applyScreenShake(float pPartialTick, long pRenderTime, PoseStack pPoseStack, CallbackInfo ci) {
        float shakeIntensity = ScreenShakeEffect.getCurrentShakeIntensity(); // Отримуємо інтенсивність через метод
        if (shakeIntensity > 0) {
            float translateX = (random.nextFloat() * 2.0F - 1.0F) * shakeIntensity * 10.0F;
            float translateY = (random.nextFloat() * 2.0F - 1.0F) * shakeIntensity * 10.0F;

            pPoseStack.translate(translateX, translateY, 0.0F);
        }
    }
}