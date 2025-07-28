package com.inasai.repack.mixin;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.config.category.GuideConfig;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;

import org.spongepowered.asm.mixin.Unique;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(BrewingStandScreen.class)
public abstract class MixinBrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu> {
    @Unique
    private static final Logger repack_LOGGER = LogUtils.getLogger();

    public MixinBrewingStandScreen(BrewingStandMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    protected void repack_renderBrewingGuide(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, CallbackInfo ci) {
        for (GuideConfig.BrewingGuideConfig guideConfig : RePackConfig.guideConfig.BREWING_GUIDES) {
            if (guideConfig.enableBrewingGuide.get()) {
                String style = guideConfig.brewingGuideStyle.get();
                ResourceLocation guideTexture = ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "textures/gui/brewing_guide/" + style + ".png");

                repack_LOGGER.debug("RePack: BrewingGuideMixin - Attempting to render brewing guide '{}'. Style: {}, Path: {}", guideConfig.id, style, guideTexture);

                int guiLeft = this.leftPos;
                int guiTop = this.topPos;

                int textureWidth = guideConfig.brewingGuideWidth.get();
                int textureHeight = guideConfig.brewingGuideHeight.get();
                int offsetX = guideConfig.brewingGuideOffsetX.get();
                int offsetY = guideConfig.brewingGuideOffsetY.get();
                GuideConfig.GuidePosition position = guideConfig.brewingGuidePosition.get();

                int renderX = 0;
                int renderY = 0;

                switch (position) {
                    case LEFT -> {
                        renderX = guiLeft - textureWidth - offsetX;
                        renderY = guiTop + offsetY;
                    }
                    case RIGHT -> {
                        renderX = guiLeft + this.imageWidth + offsetX;
                        renderY = guiTop + offsetY;
                    }
                    case TOP -> {
                        renderX = guiLeft + offsetX;
                        renderY = guiTop - textureHeight - offsetY;
                    }
                    case BOTTOM -> {
                        renderX = guiLeft + offsetX;
                        renderY = guiTop + this.imageHeight + offsetY;
                    }
                }

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                pGuiGraphics.blit(guideTexture, renderX, renderY, 0, 0, textureWidth, textureHeight);

                RenderSystem.disableBlend();

                repack_LOGGER.debug("RePack: BrewingGuideMixin - Blit called for guide '{}'. Position: X={}, Y={}, Size: W={}, H={}. Offset: {},{}", guideConfig.id, renderX, renderY, textureWidth, textureHeight, offsetX, offsetY);
            } else {
                repack_LOGGER.debug("RePack: BrewingGuideMixin - Brewing guide '{}' is disabled in config.", guideConfig.id);
            }
        }
    }
}