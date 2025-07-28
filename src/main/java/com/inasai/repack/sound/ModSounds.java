package com.inasai.repack.sound;

import com.inasai.repack.RePack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

public class ModSounds {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RePack.MOD_ID);

    public static final RegistryObject<SoundEvent> WITCH_WHISPERS_SOUND =
            SOUND_EVENTS.register("witch_whispers", () -> {
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "witch_whispers");
                LOGGER.info("RePack: Registering custom sound: '{}'", loc.getPath());
                return SoundEvent.createVariableRangeEvent(loc);
            });

    public static final RegistryObject<SoundEvent> WITCH_CALLS_SOUND =
            SOUND_EVENTS.register("witch_calls", () -> {
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "witch_calls");
                LOGGER.info("RePack: Registering special sound: '{}'", loc.getPath());
                return SoundEvent.createVariableRangeEvent(loc);
            });

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
        LOGGER.info("RePack: Mod Sounds registered to Event Bus.");
    }
}