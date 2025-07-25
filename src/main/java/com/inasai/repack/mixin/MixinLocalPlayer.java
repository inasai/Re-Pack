package com.inasai.repack.mixin;

import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.inasai.repack.mixin.accessor.ILivingEntityAccessor;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Random random = new Random();

    @Inject(method = "die", at = @At("RETURN"))
    private void repack_onLocalPlayerDie(DamageSource p_21009_, CallbackInfo ci) {
        if (RePackConfig.enableDeathSounds.get()) {
            LOGGER.info("RePack: LocalPlayerMixin - Player died! Custom death sound logic initiated.");
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                LOGGER.info("RePack: LocalPlayerMixin - Confirmed current local player death.");

                // Приводимо 'this' до нашого аксесор-інтерфейсу
                ILivingEntityAccessor accessor = (ILivingEntityAccessor) this;

                int specialDeathChance = RePackConfig.specialDeathChance.get();

                if (specialDeathChance > 0 && random.nextInt(specialDeathChance) == 0) {
                    LOGGER.info("RePack: LocalPlayerMixin - Special death chance hit! (" + specialDeathChance + ")");
                    if (ModSounds.SPECIAL_DEATH_SOUND.isPresent()) {
                        LOGGER.info("RePack: LocalPlayerMixin - Playing SPECIAL death sound: " + ModSounds.SPECIAL_DEATH_SOUND.get().getLocation());
                        // Використовуємо 'this' (який є LocalPlayer) як перший аргумент
                        mc.level.playSound((LocalPlayer)(Object)this, accessor.getXCoord(), accessor.getYCoord(), accessor.getZCoord(),
                                ModSounds.SPECIAL_DEATH_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    } else {
                        LOGGER.warn("RePack: LocalPlayerMixin - SPECIAL_DEATH_SOUND is not present/registered!");
                    }

                    if (RePackConfig.specialDeathScreenEffect.get() != RePackConfig.ScreenEffectType.NONE) {
                        LOGGER.info("RePack: LocalPlayerMixin - Triggering screen effect (requires separate implementation). Effect type: " + RePackConfig.specialDeathScreenEffect.get());
                    }
                } else {
                    LOGGER.info("RePack: LocalPlayerMixin - Normal death. Playing CUSTOM death sound.");
                    if (ModSounds.CUSTOM_DEATH_SOUND.isPresent()) {
                        LOGGER.info("RePack: LocalPlayerMixin - Playing CUSTOM death sound: " + ModSounds.CUSTOM_DEATH_SOUND.get().getLocation());
                        // Використовуємо 'this' (який є LocalPlayer) як перший аргумент
                        mc.level.playSound((LocalPlayer)(Object)this, accessor.getXCoord(), accessor.getYCoord(), accessor.getZCoord(),
                                ModSounds.CUSTOM_DEATH_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    } else {
                        LOGGER.warn("RePack: LocalPlayerMixin - CUSTOM_DEATH_SOUND is not present/registered!");
                    }
                }
            } else {
                LOGGER.debug("RePack: LocalPlayerMixin - Minecraft player instance is null. This should not happen on death.");
            }
        } else {
            LOGGER.debug("RePack: LocalPlayerMixin - Death sounds disabled in config.");
        }
    }
}