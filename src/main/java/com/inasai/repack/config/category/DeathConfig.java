package com.inasai.repack.config.category;

import net.minecraftforge.common.ForgeConfigSpec;

public class DeathConfig {
    public enum ScreenEffectType {
        NONE, SHAKE, PARTICLES
    }

    public final ForgeConfigSpec.BooleanValue enableDeathSounds;
    public final ForgeConfigSpec.IntValue specialDeathChance;
    public final ForgeConfigSpec.EnumValue<ScreenEffectType> specialDeathScreenEffect;
    public final ForgeConfigSpec.BooleanValue doImmediateRespawn;

    public DeathConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Death Category");

        enableDeathSounds = builder
                .comment("Enable custom death sounds.")
                .define("enableDeathSounds", true);
        specialDeathChance = builder
                .comment("Chance for special death sound and effect (1/X, higher X means less frequent).")
                .defineInRange("specialDeathChance", 5, 1, 100);
        specialDeathScreenEffect = builder
                .comment("Choose the screen effect for special death.")
                .defineEnum("specialDeathScreenEffect", ScreenEffectType.SHAKE);
        doImmediateRespawn = builder
                .comment("Enable immediate respawn after death (equivalent to /gamerule doImmediateRespawn true).")
                .define("doImmediateRespawn", false);
        builder.pop();
    }
}