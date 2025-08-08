package com.inasai.repack.mixin;

import com.inasai.repack.config.RePackConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.registries.BuiltInRegistries; // НОВИЙ ІМПОРТ

import java.util.List;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreen<EnchantmentMenu> {

    @Shadow(aliases = "f_39445_") private RandomSource random;
    @Shadow(aliases = {"f_39446_", "enchantClue"})
    public int[] enchantClue;
    @Shadow(aliases = {"f_39447_", "levelClue"})
    public int[] levelClue;

    public EnchantmentScreenMixin(EnchantmentMenu pMenu, Player pPlayer, Component pTitle) {
        super(pMenu, pPlayer.getInventory(), pTitle);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void repack$renderEnchantmentDetails(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (RePackConfig.guideConfig.ENCHANTMENT_GUIDES.isEmpty() || !RePackConfig.guideConfig.ENCHANTMENT_GUIDES.get(0).enableEnchantmentGuide.get()) {
            return;
        }

        ItemStack itemStack = this.menu.getSlot(0).getItem();
        if (itemStack.isEmpty()) {
            return;
        }

        int x = this.leftPos;
        int y = this.topPos;

        int enchantmentDisplayX = x + 120;
        int enchantmentDisplayY = y + 15;

        for (int slot = 0; slot < 3; ++slot) {
            if (this.menu.costs[slot] > 0) {
                int enchantmentId = this.enchantClue[slot];
                int enchantmentLevel = this.levelClue[slot];

                if (enchantmentId != -1) {
                    Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.byId(enchantmentId);

                    if (enchantment != null) {
                        int currentY = enchantmentDisplayY + slot * 36;

                        pGuiGraphics.drawString(this.font, "Slot " + (slot + 1) + ":", enchantmentDisplayX, currentY, 0x8B4513, false);

                        Component enchantmentName = enchantment.getFullname(enchantmentLevel);
                        pGuiGraphics.drawString(this.font, enchantmentName, enchantmentDisplayX + 5, currentY + 10, 0x4B0082, false);
                    }
                }
            }
        }
    }
}