package com.inasai.repack.event;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes; // <-- НОВИЙ ІМПОРТ
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent; // <-- НОВИЙ ІМПОРТ для PlayerRespawnEvent
import net.minecraftforge.event.TickEvent; // <-- НОВИЙ ІМПОРТ для TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Random;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DeathEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();

    // НОВІ ЗМІННІ ДЛЯ КЕРУВАННЯ ЕФЕКТАМИ
    public static boolean isSpecialDeathEffectActive = false; // Використовуватимемо для активації/деактивації ефекту
    private static long effectStartTime = 0; // Для відстеження часу початку ефекту
    private static final long EFFECT_DURATION_MS = 2000; // Тривалість ефекту в мілісекундах (2 секунди)

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
                    if (effectType != RePackConfig.ScreenEffectType.NONE) {
                        isSpecialDeathEffectActive = true; // АКТИВУЄМО ЕФЕКТ
                        effectStartTime = System.currentTimeMillis(); // ЗБЕРІГАЄМО ЧАС ПОЧАТКУ
                        LOGGER.info("RePack: DeathEventHandler - Triggering screen effect: {}.", effectType);
                    } else {
                        isSpecialDeathEffectActive = false; // ВИКЛЮЧАЄМО, ЯКЩО NONE
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
                    isSpecialDeathEffectActive = false; // Виключаємо ефект, якщо це не особлива смерть
                }
            } else {
                LOGGER.debug("RePack: DeathEventHandler - Death sounds disabled in config.");
                isSpecialDeathEffectActive = false; // Виключаємо ефект, якщо звуки вимкнені
            }
        } else {
            if (event.getEntity() != null) {
                LOGGER.debug("RePack: DeathEventHandler - LivingDeathEvent for non-local player/entity: {}", event.getEntity().getName().getString());
            } else {
                LOGGER.debug("RePack: DeathEventHandler - LivingDeathEvent for null entity.");
            }
            isSpecialDeathEffectActive = false; // Виключаємо ефект, якщо це не локальний гравець
        }
    }

    // НОВИЙ МЕТОД: Обробка події тіку на клієнті
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // Перевіряємо, що ми на клієнтській стороні і це кінець тіку (фаза END)
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            // Перевіряємо, чи активний ефект і чи обрано тип PARTICLES
            if (isSpecialDeathEffectActive && RePackConfig.specialDeathScreenEffect.get() == RePackConfig.ScreenEffectType.PARTICLES) {
                // Перевіряємо тривалість ефекту
                if (System.currentTimeMillis() - effectStartTime < EFFECT_DURATION_MS) {
                    // Додаємо кілька темних частинок навколо гравця
                    for (int i = 0; i < 10; i++) { // Збільшимо кількість частинок за тік для кращої помітності
                        Minecraft.getInstance().level.addParticle(
                                ParticleTypes.SMOKE, // Тип частинки (диму)
                                Minecraft.getInstance().player.getX() + (random.nextDouble() - 0.5) * 1.0, // X координата з невеликим зміщенням
                                Minecraft.getInstance().player.getY() + 0.5 + (random.nextDouble() - 0.5) * 1.0, // Y координата (біля гравця)
                                Minecraft.getInstance().player.getZ() + (random.nextDouble() - 0.5) * 1.0, // Z координата з невеликим зміщенням
                                (random.nextDouble() - 0.5) * 0.05, // Швидкість по X
                                0.05 + random.nextDouble() * 0.05, // Швидкість по Y (частинки піднімаються)
                                (random.nextDouble() - 0.5) * 0.05 // Швидкість по Z
                        );
                    }
                } else {
                    // Якщо час ефекту минув, деактивуємо його
                    isSpecialDeathEffectActive = false;
                    LOGGER.info("RePack: Particle effect duration ended. Deactivating.");
                }
            }
        }
    }

    // НОВИЙ МЕТОД: Обробка події відродження гравця для скидання ефекту
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        // Перевіряємо, що це клієнтська сторона і відродився саме локальний гравець
        if (event.getEntity().level().isClientSide() && event.getEntity().equals(Minecraft.getInstance().player)) {
            LOGGER.info("RePack: Local Player respawned. Resetting special death effects.");
            isSpecialDeathEffectActive = false; // Скидаємо прапорець активації
        }
    }
}