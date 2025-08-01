package com.inasai.repack.event.category;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig;
import com.inasai.repack.config.category.DeathConfig;
import com.inasai.repack.sound.ModSounds;
import com.inasai.repack.effect.ParticleEffect;
import com.inasai.repack.effect.ScreenShakeEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

import com.mojang.logging.LogUtils;
import com.mojang.brigadier.ParseResults;

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

            LOGGER.debug("RePack: Skipping gamerule update on death. Handled by PlayerLoggedInEvent.");

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
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
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

                ParseResults<CommandSourceStack> parseResults = server.getCommands().getDispatcher().parse(commandString, source);
                int result = server.getCommands().performCommand(parseResults, commandString);

                if (result == 0) {
                    LOGGER.warn("RePack: Failed to execute gamerule doImmediateRespawn {} for player {}. Result: {}", currentImmediateRespawnState, serverPlayer.getName().getString(), result);
                } else {
                    LOGGER.info("RePack: Executed gamerule doImmediateRespawn {} for player {}. Result: {}", currentImmediateRespawnState, serverPlayer.getName().getString(), result);
                }
                prevImmediateRespawnState = currentImmediateRespawnState;
            }
        }
    }
}