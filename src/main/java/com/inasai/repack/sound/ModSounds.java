package com.inasai.repack.sound;

import com.inasai.repack.RePack;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.slf4j.Logger;

public class ModSounds {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RePack.MOD_ID);

    public static final RegistryObject<SoundEvent> CUSTOM_DEATH_SOUND =
            SOUND_EVENTS.register("custom_death", () -> {
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "custom_death");
                LOGGER.info("RePack: Registering custom death sound: {}", loc);
                return SoundEvent.createVariableRangeEvent(loc);
            });

    public static final RegistryObject<SoundEvent> SPECIAL_DEATH_SOUND =
            SOUND_EVENTS.register("special_death", () -> {
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "special_death");
                LOGGER.info("RePack: Registering special death sound: {}", loc);
                return SoundEvent.createVariableRangeEvent(loc);
            });

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
        LOGGER.info("RePack: Mod Sounds registered to Event Bus.");
    }
}