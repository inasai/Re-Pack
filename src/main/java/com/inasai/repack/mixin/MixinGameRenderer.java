package com.inasai.repack.mixin; // Цей рядок додаємо

import com.inasai.repack.event.SpecialScreenEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft; // Цей імпорт тут не потрібен, але не завадить. Можна видалити.
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
        if (SpecialScreenEffects.isShakeEffectActive) {
            float shakeIntensity = SpecialScreenEffects.MAX_SHAKE_INTENSITY * ((float)(SpecialScreenEffects.SHAKE_DURATION_MS - (System.currentTimeMillis() - SpecialScreenEffects.shakeStartTime)) / SpecialScreenEffects.SHAKE_DURATION_MS);
            if (shakeIntensity > 0) {
                float translateX = (random.nextFloat() * 2.0F - 1.0F) * shakeIntensity * 10.0F;
                float translateY = (random.nextFloat() * 2.0F - 1.0F) * shakeIntensity * 10.0F;

                pPoseStack.translate(translateX, translateY, 0.0F);
            }
        }
    }
}