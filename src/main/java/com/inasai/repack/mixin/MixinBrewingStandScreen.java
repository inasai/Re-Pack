package com.inasai.repack.mixin;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@OnlyIn(Dist.CLIENT)
@Mixin(BrewingStandScreen.class)
public abstract class MixinBrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu> {
    private static final Logger LOGGER = LogUtils.getLogger();

    public MixinBrewingStandScreen(BrewingStandMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    protected void repack_renderBrewingGuide(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, CallbackInfo ci) {
        if (RePackConfig.enableBrewingGuide.get()) {
            String style = RePackConfig.brewingGuideStyle.get();
            ResourceLocation guideTexture = ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "textures/gui/brewing_guide/" + style + ".png");

            LOGGER.debug("RePack: BrewingGuideMixin - Attempting to render brewing guide. Style: {}, Path: {}", style, guideTexture);

            int guiLeft = this.leftPos;
            int guiTop = this.topPos;

            int textureWidth = RePackConfig.brewingGuideWidth.get();
            int textureHeight = RePackConfig.brewingGuideHeight.get();
            int offsetX = RePackConfig.brewingGuideOffsetX.get();
            int offsetY = RePackConfig.brewingGuideOffsetY.get();
            RePackConfig.GuidePosition position = RePackConfig.brewingGuidePosition.get();

            int renderX = 0;
            int renderY = 0;

            switch (position) {
                case LEFT:
                    renderX = guiLeft - textureWidth - offsetX;
                    renderY = guiTop + offsetY;
                    break;
                case RIGHT:
                    renderX = guiLeft + this.imageWidth + offsetX;
                    renderY = guiTop + offsetY;
                    break;
                case TOP:
                    renderX = guiLeft + offsetX;
                    renderY = guiTop - textureHeight - offsetY;
                    break;
                case BOTTOM:
                    renderX = guiLeft + offsetX;
                    renderY = guiTop + this.imageHeight + offsetY;
                    break;
            }

            // Перевірка, чи текстура існує перед рендерингом (не обов'язково, але корисно для відладки)
            // Примітка: Minecraft може кинути виняток, якщо текстура не знайдена, тому це більше для логування.
            // try {
            //     Minecraft.getInstance().getTextureManager().bindForSetup(guideTexture);
            // } catch (Exception e) {
            //     LOGGER.error("RePack: Failed to bind brewing guide texture: {}", guideTexture, e);
            //     return; // Не рендеримо, якщо текстура не завантажилася
            // }

            pGuiGraphics.blit(guideTexture, renderX, renderY, 0, 0, textureWidth, textureHeight);

            LOGGER.debug("RePack: BrewingGuideMixin - Blit called for guide. Position: X={}, Y={}, Size: W={}, H={}. Offset: {},{}", renderX, renderY, textureWidth, textureHeight, offsetX, offsetY);
        } else {
            LOGGER.debug("RePack: BrewingGuideMixin - Brewing guide is disabled in config.");
        }
    }
}