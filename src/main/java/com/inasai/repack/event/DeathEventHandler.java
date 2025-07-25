package com.inasai.repack.event;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Random;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DeathEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // Перевіряємо, чи ми на клієнтській стороні
        if (event.getEntity().level().isClientSide()) {
            // Перевіряємо, чи померлий об'єкт є локальним гравцем Minecraft
            if (event.getEntity() instanceof LocalPlayer player) {
                // Додаємо більше логів, щоб переконатися, що цей блок коду виконується
                LOGGER.info("RePack: DeathEventHandler - Captured LivingDeathEvent for LocalPlayer: {}", player.getName().getString());

                Minecraft mc = Minecraft.getInstance();

                if (RePackConfig.enableDeathSounds.get()) {
                    LOGGER.info("RePack: DeathEventHandler - Custom death sounds are enabled in config.");

                    int specialDeathChance = RePackConfig.specialDeathChance.get();

                    if (specialDeathChance > 0 && random.nextInt(specialDeathChance) == 0) {
                        LOGGER.info("RePack: DeathEventHandler - Special death chance hit! (1/" + specialDeathChance + ")");
                        if (ModSounds.SPECIAL_DEATH_SOUND.isPresent()) {
                            LOGGER.info("RePack: DeathEventHandler - Playing SPECIAL death sound: {}", ModSounds.SPECIAL_DEATH_SOUND.get().getLocation());
                            // Можемо спробувати інший SoundSource, наприклад PLAYERS, або просто MASTER
                            mc.level.playSound(player, player.getX(), player.getY(), player.getZ(),
                                    ModSounds.SPECIAL_DEATH_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F); // Гучність 1.0F
                        } else {
                            LOGGER.warn("RePack: DeathEventHandler - SPECIAL_DEATH_SOUND is not present/registered!");
                        }

                        RePackConfig.ScreenEffectType effectType = RePackConfig.specialDeathScreenEffect.get();
                        if (effectType != RePackConfig.ScreenEffectType.NONE) {
                            LOGGER.info("RePack: DeathEventHandler - Triggering screen effect: {}. (Implementation needed)", effectType);
                            // TODO: Implement screen effects (shake, particles, GIF) here
                        }
                    } else {
                        LOGGER.info("RePack: DeathEventHandler - Normal death. Playing CUSTOM death sound.");
                        if (ModSounds.CUSTOM_DEATH_SOUND.isPresent()) {
                            LOGGER.info("RePack: DeathEventHandler - Playing CUSTOM death sound: {}", ModSounds.CUSTOM_DEATH_SOUND.get().getLocation());
                            mc.level.playSound(player, player.getX(), player.getY(), player.getZ(),
                                    ModSounds.CUSTOM_DEATH_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F); // Гучність 1.0F
                        } else {
                            LOGGER.warn("RePack: DeathEventHandler - CUSTOM_DEATH_SOUND is not present/registered!");
                        }
                    }
                } else {
                    LOGGER.debug("RePack: DeathEventHandler - Death sounds disabled in config.");
                }
            } else {
                LOGGER.debug("RePack: DeathEventHandler - LivingDeathEvent for non-LocalPlayer: {}", event.getEntity().getName().getString());
            }
        } else {
            LOGGER.debug("RePack: DeathEventHandler - LivingDeathEvent on server side for: {}", event.getEntity().getName().getString());
        }
    }
}