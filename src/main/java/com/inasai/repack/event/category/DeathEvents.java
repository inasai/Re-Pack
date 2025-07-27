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

import java.util.Random;

import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DeathEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random random = new Random();
    private static boolean prevImmediateRespawnState = false;

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
            // ВИДАЛЕНО: if (RePackConfig.deathConfig.doImmediateRespawn.get()) {
            // LOGGER.info("RePack: DeathEvents - Immediate respawn is enabled."); // Цей лог тепер не потрібен тут
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                if (!serverPlayer.level().isClientSide()) {
                    MinecraftServer server = serverPlayer.getServer();
                    if (server != null) {
                        CommandSourceStack source = server.createCommandSourceStack()
                                .withSuppressedOutput()
                                .withPosition(serverPlayer.position())
                                .withRotation(serverPlayer.getRotationVector());

                        // Визначаємо команду залежно від конфігу
                        String commandString;
                        boolean currentImmediateRespawnState = RePackConfig.deathConfig.doImmediateRespawn.get();

                        if (currentImmediateRespawnState) {
                            commandString = "gamerule doImmediateRespawn true";
                        } else {
                            commandString = "gamerule doImmediateRespawn true"; // <-- ПОМИЛКА: Тут має бути false!
                        }

                        // Виконуємо команду тільки якщо стан змінився
                        if (currentImmediateRespawnState != prevImmediateRespawnState) {
                            ParseResults<CommandSourceStack> parseResults = server.getCommands().getDispatcher().parse(commandString, source);
                            server.getCommands().performCommand(parseResults, commandString);
                            LOGGER.info("RePack: Executed gamerule doImmediateRespawn {} for player {}.", currentImmediateRespawnState, serverPlayer.getName().getString());
                            prevImmediateRespawnState = currentImmediateRespawnState; // Оновлюємо попередній стан
                        } else {
                            LOGGER.debug("RePack: Gamerule doImmediateRespawn is already in desired state ({}). Skipping command execution.", currentImmediateRespawnState);
                        }
                    }
                }
            } else if (!clientPlayer.level().isClientSide()) { // Для одиночної гри, де clientPlayer також є ServerPlayer на задньому плані
                MinecraftServer server = clientPlayer.getServer();
                if (server != null) {
                    CommandSourceStack source = server.createCommandSourceStack()
                            .withSuppressedOutput()
                            .withPosition(clientPlayer.position())
                            .withRotation(clientPlayer.getRotationVector());

                    String commandString;
                    boolean currentImmediateRespawnState = RePackConfig.deathConfig.doImmediateRespawn.get();

                    if (currentImmediateRespawnState) {
                        commandString = "gamerule doImmediateRespawn true";
                    } else {
                        commandString = "gamerule doImmediateRespawn true"; // <-- ПОМИЛКА: Тут має бути false!
                    }

                    if (currentImmediateRespawnState != prevImmediateRespawnState) {
                        ParseResults<CommandSourceStack> parseResults = server.getCommands().getDispatcher().parse(commandString, source);
                        server.getCommands().performCommand(parseResults, commandString);
                        LOGGER.info("RePack: Executed gamerule doImmediateRespawn {} for player {} (non-ServerPlayer cast).", currentImmediateRespawnState, clientPlayer.getName().getString());
                        prevImmediateRespawnState = currentImmediateRespawnState;
                    } else {
                        LOGGER.debug("RePack: Gamerule doImmediateRespawn is already in desired state ({}). Skipping command execution.", currentImmediateRespawnState);
                    }
                }
            }
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
}