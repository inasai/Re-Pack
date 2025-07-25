package com.inasai.repack.sound;

import com.inasai.repack.RePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RePack.MOD_ID);

    // Переконайтеся, що ідентифікатори тут точно відповідають тим, що в sounds.json
    public static final RegistryObject<SoundEvent> CUSTOM_DEATH_SOUND =
            SOUND_EVENTS.register("custom_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "custom_death")));

    public static final RegistryObject<SoundEvent> SPECIAL_DEATH_SOUND =
            SOUND_EVENTS.register("special_death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(RePack.MOD_ID, "special_death")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
