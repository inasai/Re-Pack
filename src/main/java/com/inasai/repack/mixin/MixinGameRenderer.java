package com.inasai.repack.mixin;

import com.inasai.repack.effect.ScreenShakeEffect;

import net.minecraft.client.renderer.GameRenderer;

import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Unique
    private static final Random repack_random = new Random();

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void repack_applyScreenShake(float pPartialTick, long pRenderTime, PoseStack pPoseStack, CallbackInfo ci) {
        float shakeIntensity = ScreenShakeEffect.getCurrentShakeIntensity();
        if (shakeIntensity > 0) {
            float translateX = (repack_random.nextFloat() * 2.0F - 1.0F) * shakeIntensity * 10.0F;
            float translateY = (repack_random.nextFloat() * 2.0F - 1.0F) * shakeIntensity * 10.0F;

            pPoseStack.translate(translateX, translateY, 0.0F);
        }
    }
}