package com.inasai.repack.event;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.sound.ModSounds;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DeathEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();

    public static boolean isSpecialDeathEffectActive = false;
    private static long effectStartTime = 0;
    private static final long EFFECT_DURATION_MS = 2000;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {
            LOGGER.debug("RePack: DeathEventHandler - Minecraft client world or player is null. Skipping event processing.");
            return;
        }

        LocalPlayer clientPlayer = Minecraft.getInstance().player;

        if (event.getEntity().equals(clientPlayer)) {
            LOGGER.info("RePack: DeathEventHandler - Captured LivingDeathEvent for LOCAL PLAYER: {}", clientPlayer.getName().getString());

            if (RePackConfig.enableDeathSounds.get()) {
                LOGGER.info("RePack: DeathEventHandler - Custom death sounds are enabled in config.");

                int specialDeathChance = RePackConfig.specialDeathChance.get();

                if (specialDeathChance > 0 && random.nextInt(specialDeathChance) == 0) {
                    LOGGER.info("RePack: DeathEventHandler - Special death chance hit! (1/" + specialDeathChance + ")");
                    if (ModSounds.SPECIAL_DEATH_SOUND.isPresent()) {
                        LOGGER.info("RePack: DeathEventHandler - Playing SPECIAL death sound: {}", ModSounds.SPECIAL_DEATH_SOUND.get().getLocation());
                        clientPlayer.level().playSound(clientPlayer, clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(),
                                ModSounds.SPECIAL_DEATH_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    } else {
                        LOGGER.warn("RePack: DeathEventHandler - SPECIAL_DEATH_SOUND is not present/registered!");
                    }

                    RePackConfig.ScreenEffectType effectType = RePackConfig.specialDeathScreenEffect.get();
                    if (effectType == RePackConfig.ScreenEffectType.PARTICLES) {
                        // Логіка для частинок залишається тут, якщо вона не винесена
                        SpecialScreenEffects.isShakeEffectActive = false; // Переконайтеся, що тряска не активна, якщо обрано частинки
                        DeathEventHandler.isSpecialDeathEffectActive = true; // Для частинок
                        DeathEventHandler.effectStartTime = System.currentTimeMillis(); // Для частинок
                        LOGGER.info("RePack: DeathEventHandler - Triggering screen effect: {}.", effectType);
                    } else if (effectType == RePackConfig.ScreenEffectType.SHAKE) {
                        SpecialScreenEffects.activateShakeEffect();
                        DeathEventHandler.isSpecialDeathEffectActive = false; // Для частинок
                        LOGGER.info("RePack: DeathEventHandler - Triggering screen effect: {}.", effectType);
                    } else if (effectType == RePackConfig.ScreenEffectType.GIF) {
                        // TODO: Активувати GIF ефект. Реалізація буде пізніше.
                        SpecialScreenEffects.isShakeEffectActive = false; // Переконайтеся, що тряска не активна
                        DeathEventHandler.isSpecialDeathEffectActive = false; // Для частинок
                        LOGGER.info("RePack: DeathEventHandler - Triggering screen effect: {}.", effectType);
                    } else {
                        SpecialScreenEffects.isShakeEffectActive = false;
                        DeathEventHandler.isSpecialDeathEffectActive = false;
                        LOGGER.info("RePack: DeathEventHandler - Screen effect is NONE, no effect triggered.");
                    }

                } else {
                    LOGGER.info("RePack: DeathEventHandler - Normal death. Playing CUSTOM death sound.");
                    if (ModSounds.CUSTOM_DEATH_SOUND.isPresent()) {
                        LOGGER.info("RePack: DeathEventHandler - Playing CUSTOM death sound: {}", ModSounds.CUSTOM_DEATH_SOUND.get().getLocation());
                        clientPlayer.level().playSound(clientPlayer, clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(),
                                ModSounds.CUSTOM_DEATH_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    } else {
                        LOGGER.warn("RePack: DeathEventHandler - CUSTOM_DEATH_SOUND is not present/registered!");
                    }
                    isSpecialDeathEffectActive = false;
                }
            } else {
                LOGGER.debug("RePack: DeathEventHandler - Death sounds disabled in config.");
                isSpecialDeathEffectActive = false;
            }
        } else {
            if (event.getEntity() != null) {
                LOGGER.debug("RePack: DeathEventHandler - LivingDeathEvent for non-local player/entity: {}", event.getEntity().getName().getString());
            } else {
                LOGGER.debug("RePack: DeathEventHandler - LivingDeathEvent for null entity.");
            }
            isSpecialDeathEffectActive = false;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            if (isSpecialDeathEffectActive && RePackConfig.specialDeathScreenEffect.get() == RePackConfig.ScreenEffectType.PARTICLES) {
                if (System.currentTimeMillis() - effectStartTime < EFFECT_DURATION_MS) { // Використовуємо EFFECT_DURATION_MS для частинок
                    for (int i = 0; i < 10; i++) {
                        Minecraft.getInstance().level.addParticle(
                                ParticleTypes.SMOKE, // Тип частинки (диму)
                                Minecraft.getInstance().player.getX() + (random.nextDouble() - 0.5) * 1.0,
                                Minecraft.getInstance().player.getY() + 0.5 + (random.nextDouble() - 0.5) * 1.0,
                                Minecraft.getInstance().player.getZ() + (random.nextDouble() - 0.5) * 1.0,
                                (random.nextDouble() - 0.5) * 0.05,
                                0.05 + random.nextDouble() * 0.05,
                                (random.nextDouble() - 0.5) * 0.05
                        );
                    }
                } else {
                    isSpecialDeathEffectActive = false;
                    LOGGER.info("RePack: Particle effect duration ended. Deactivating.");
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity().level().isClientSide() && event.getEntity().equals(Minecraft.getInstance().player)) {
            LOGGER.info("RePack: Local Player respawned. Resetting special death effects.");
            isSpecialDeathEffectActive = false;
        }
    }
}