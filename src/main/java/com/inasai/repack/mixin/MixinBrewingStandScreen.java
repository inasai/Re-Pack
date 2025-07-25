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
    protected void repack_renderBrewingGuide(GuiGraphics p_283626_, float p_282542_, int p_281297_, int p_283307_, CallbackInfo ci) {
        // Перевіряємо, чи увімкнений гайд у конфігу
        if (RePackConfig.enableBrewingGuide.get()) {
            String style = RePackConfig.brewingGuideStyle.get();
            // Виправлення: Використання ResourceLocation.fromNamespaceAndPath
            ResourceLocation guideTexture = ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "textures/gui/brewing_guide/" + style + ".png");
            LOGGER.info("RePack: BrewingGuideMixin - Attempting to render brewing guide. Style: " + style + ", Path: " + guideTexture.toString());

            // Отримуємо позиції GUI на екрані
            // AbstractContainerScreen вже має поля leftPos та topPos, що є позиціями верхнього лівого кута GUI
            // imageWidth та imageHeight - це розміри самого GUI (176x166 для більшості ванільних екранів)
            int guiLeft = this.leftPos;
            int guiTop = this.topPos;

            // Визначаємо позицію для рендерингу гайда
            // Можна розмістити його поруч з GUI, наприклад, праворуч
            int renderX = guiLeft + this.imageWidth + 5; // 5 пікселів відступу від краю GUI
            int renderY = guiTop;

            // Задаємо розміри текстури гайда. Переконайтеся, що це відповідає розміру вашого PNG файлу!
            // Якщо ваш файл не 176x166, змініть ці значення.
            int textureWidth = 176;
            int textureHeight = 166;

            // Рендеримо зображення
            // Параметри blit: (текстура, x, y, u, v, width, height, textureWidth, textureHeight)
            // Для повного зображення, u=0, v=0, width=textureWidth, height=textureHeight
            p_283626_.blit(guideTexture, renderX, renderY, 0, 0, textureWidth, textureHeight);

            LOGGER.info("RePack: BrewingGuideMixin - Blit called for guide. Position: X=" + renderX + ", Y=" + renderY + ", Size: W=" + textureWidth + ", H=" + textureHeight);
        } else {
            LOGGER.debug("RePack: BrewingGuideMixin - Brewing guide is disabled in config.");
        }
    }
}