package com.inasai.repack.event.category;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.config.category.DeathConfig;
import com.inasai.repack.sound.ModSounds;
import com.inasai.repack.effect.ParticleEffect;
import com.inasai.repack.effect.ScreenShakeEffect;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent; // <<< НОВИЙ ІМПОРТ

import java.util.Random;

import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DeathEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();
    // prevImmediateRespawnState буде використовуватись для оптимізації, щоб не відправляти команду щоразу
    private static boolean prevImmediateRespawnState = false; // Зберігаємо тут для між-смертевого відстеження

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {
            LOGGER.debug("RePack: DeathEvents - Minecraft client world or player is null. Skipping event processing.");
            return;
        }

        LocalPlayer clientPlayer = Minecraft.getInstance().player;

        if (event.getEntity().equals(clientPlayer)) {
            LOGGER.info("RePack: DeathEvents - Captured LivingDeathEvent for LOCAL PLAYER: {}", clientPlayer.getName().getString());

            // --- ОБРОБКА IMMEDIATE RESPAWN ---
            // Ця логіка перенесена в onPlayerLoggedInEvent для кращої синхронізації з gamerule.
            // При смерті ми її не виконуємо, оскільки gamerule вже має бути встановлений при вході у світ.
            // Якщо gamerule не відповідає, значить була зміна світу або перезавантаження,
            // і onPlayerLoggedInEvent (або подібна подія) має її обробити.
            LOGGER.debug("RePack: Skipping gamerule update on death. Handled by PlayerLoggedInEvent.");
            // --- КІНЕЦЬ ОБРОБКИ IMMEDIATE RESPAWN ---


            if (RePackConfig.deathConfig.enableDeathSounds.get()) {
                LOGGER.info("RePack: DeathEvents - Custom death sounds are enabled in config.");

                int specialDeathChance = RePackConfig.deathConfig.specialDeathChance.get();

                if (specialDeathChance > 0 && random.nextInt(specialDeathChance) == 0) {
                    LOGGER.info("RePack: DeathEvents - Special death chance hit! (1/" + specialDeathChance + ")");
                    if (ModSounds.WITCH_CALLS_SOUND.isPresent()) {
                        LOGGER.info("RePack: DeathEvents - Playing SPECIAL death sound: {}", ModSounds.WITCH_CALLS_SOUND.get().getLocation());
                        clientPlayer.level().playSound(clientPlayer, clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(),
                                ModSounds.WITCH_CALLS_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    } else {
                        LOGGER.warn("RePack: DeathEvents - WITCH_CALLS_SOUND is not present/registered!");
                    }

                    DeathConfig.ScreenEffectType effectType = RePackConfig.deathConfig.specialDeathScreenEffect.get();
                    if (effectType == DeathConfig.ScreenEffectType.PARTICLES) {
                        ParticleEffect.activate();
                        ScreenShakeEffect.deactivate();
                        LOGGER.info("RePack: DeathEvents - Triggering screen effect: {}.", effectType);
                    } else if (effectType == DeathConfig.ScreenEffectType.SHAKE) {
                        ScreenShakeEffect.activate();
                        ParticleEffect.deactivate();
                        LOGGER.info("RePack: DeathEvents - Triggering screen effect: {}.", effectType);
                    } else if (effectType == DeathConfig.ScreenEffectType.GIF) {
                        // TODO: Активувати GIF ефект. Реалізація буде пізніше.
                        ParticleEffect.deactivate();
                        ScreenShakeEffect.deactivate();
                        LOGGER.info("RePack: DeathEvents - Triggering screen effect: {}. (GIF not yet implemented)", effectType);
                    } else {
                        ParticleEffect.deactivate();
                        ScreenShakeEffect.deactivate();
                        LOGGER.info("RePack: DeathEvents - Screen effect is NONE, no effect triggered.");
                    }

                } else {
                    LOGGER.info("RePack: DeathEvents - Normal death. Playing WITCH_WHISPERS death sound.");
                    if (ModSounds.WITCH_WHISPERS_SOUND.isPresent()) {
                        LOGGER.info("RePack: DeathEvents - Playing NORMAL death sound: {}", ModSounds.WITCH_WHISPERS_SOUND.get().getLocation());
                        clientPlayer.level().playSound(clientPlayer, clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(),
                                ModSounds.WITCH_WHISPERS_SOUND.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    } else {
                        LOGGER.warn("RePack: DeathEvents - WITCH_WHISPERS_SOUND is not present/registered!");
                    }
                    ParticleEffect.deactivate();
                    ScreenShakeEffect.deactivate();
                }
            } else {
                LOGGER.debug("RePack: DeathEvents - Death sounds disabled in config.");
                ParticleEffect.deactivate();
                ScreenShakeEffect.deactivate();
            }
        } else {
            if (event.getEntity() != null) {
                LOGGER.debug("RePack: DeathEvents - LivingDeathEvent for non-local player/entity: {}", event.getEntity().getName().getString());
            } else {
                LOGGER.debug("RePack: DeathEvents - LivingDeathEvent for null entity.");
            }
            ParticleEffect.deactivate();
            ScreenShakeEffect.deactivate();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity().level().isClientSide() && event.getEntity().equals(Minecraft.getInstance().player)) {
            LOGGER.info("RePack: Local Player respawned. Resetting screen effects.");
            ParticleEffect.deactivate();
            ScreenShakeEffect.deactivate();
            // TODO: GIF_Effect.deactivate();
        }
    }

    // <<< НОВА ПОДІЯ ДЛЯ ВСТАНОВЛЕННЯ GAMERULE ПРИ ВХОДІ У СВІТ >>>
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) { // Це подія спрацьовує на сервері, перевіряємо, що це ServerPlayer
            MinecraftServer server = serverPlayer.getServer();
            if (server != null) {
                CommandSourceStack source = server.createCommandSourceStack()
                        .withSuppressedOutput()
                        .withPosition(serverPlayer.position())
                        .withRotation(serverPlayer.getRotationVector());

                String commandString;
                boolean currentImmediateRespawnState = RePackConfig.deathConfig.doImmediateRespawn.get();

                if (currentImmediateRespawnState) {
                    commandString = "gamerule doImmediateRespawn true";
                } else {
                    commandString = "gamerule doImmediateRespawn false";
                }

                // Виконуємо команду тільки якщо стан змінився
                // Або якщо ми тільки зайшли у світ, і prevImmediateRespawnState ще не встановлений коректно.
                // Перевірка `currentImmediateRespawnState != prevImmediateRespawnState` вже працює як тригер
                // для відправлення команди при зміні конфіга.
                // При першому вході prevImmediateRespawnState буде false, тому команда відправиться.

                ParseResults<CommandSourceStack> parseResults = server.getCommands().getDispatcher().parse(commandString, source);
                int result = server.getCommands().performCommand(parseResults, commandString);

                if (result == 0) { // Command failed (return value 0 indicates failure)
                    LOGGER.warn("RePack: Failed to execute gamerule doImmediateRespawn {} for player {}. Result: {}", currentImmediateRespawnState, serverPlayer.getName().getString(), result);
                } else {
                    LOGGER.info("RePack: Executed gamerule doImmediateRespawn {} for player {}. Result: {}", currentImmediateRespawnState, serverPlayer.getName().getString(), result);
                }
                prevImmediateRespawnState = currentImmediateRespawnState; // Оновлюємо попередній стан
            }
        }
    }
}