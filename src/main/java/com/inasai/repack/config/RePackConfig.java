package com.inasai.repack.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;

public class RePackConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;

    // Death Category Config
    public static ForgeConfigSpec.BooleanValue enableDeathSounds;
    public static ForgeConfigSpec.IntValue specialDeathChance;
    public static ForgeConfigSpec.EnumValue<ScreenEffectType> specialDeathScreenEffect;

    // Guide Category Config
    public static ForgeConfigSpec.BooleanValue enableBrewingGuide;
    public static ForgeConfigSpec.ConfigValue<String> brewingGuideStyle; // Для вибору стилю PNG

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        // Death Category
        builder.push("Death Category");
        enableDeathSounds = builder
                .comment("Enable custom death sounds.")
                .define("enableDeathSounds", true);
        specialDeathChance = builder
                .comment("Chance for special death sound and effect (1/X, higher X means less frequent).")
                .defineInRange("specialDeathChance", 10, 1, 100);
        specialDeathScreenEffect = builder
                .comment("Choose the screen effect for special death.")
                .defineEnum("specialDeathScreenEffect", ScreenEffectType.SHAKE);
        builder.pop();

        // Guide Category
        builder.push("Guide Category");
        enableBrewingGuide = builder
                .comment("Enable the brewing guide in the brewing stand GUI.")
                .define("enableBrewingGuide", true);
        brewingGuideStyle = builder
                .comment("Choose the style of the brewing guide (e.g., 'default', 'simple').")
                .define("brewingGuideStyle", "default"); // Початкове значення
        builder.pop();

        CLIENT_SPEC = builder.build();
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(net.minecraft.network.chat.Component.translatable("repack.config.title")); // Локалізуйте назву

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Death Category
        builder.getOrCreateCategory(net.minecraft.network.chat.Component.translatable("repack.config.category.death")) // Використовуємо getOrCreateCategory з Component
                .addEntry(entryBuilder.startBooleanToggle(net.minecraft.network.chat.Component.translatable("repack.config.death.enableSounds"), enableDeathSounds.get())
                        .setDefaultValue(true)
                        .setTooltip(net.minecraft.network.chat.Component.translatable("repack.config.death.enableSounds.tooltip"))
                        .setSaveConsumer(enableDeathSounds::set)
                        .build())
                .addEntry(entryBuilder.startIntField(net.minecraft.network.chat.Component.translatable("repack.config.death.specialChance"), specialDeathChance.get())
                        .setDefaultValue(10)
                        .setMin(1).setMax(100)
                        .setTooltip(net.minecraft.network.chat.Component.translatable("repack.config.death.specialChance.tooltip"))
                        .setSaveConsumer(specialDeathChance::set)
                        .build())
                .addEntry(entryBuilder.startEnumSelector(net.minecraft.network.chat.Component.translatable("repack.config.death.screenEffect"), ScreenEffectType.class, specialDeathScreenEffect.get())
                        .setDefaultValue(ScreenEffectType.SHAKE)
                        .setTooltip(net.minecraft.network.chat.Component.translatable("repack.config.death.screenEffect.tooltip"))
                        .setSaveConsumer(specialDeathScreenEffect::set)
                        .build());

        // Guide Category
        builder.getOrCreateCategory(net.minecraft.network.chat.Component.translatable("repack.config.category.guide")) // Використовуємо getOrCreateCategory з Component
                .addEntry(entryBuilder.startBooleanToggle(net.minecraft.network.chat.Component.translatable("repack.config.guide.enableBrewingGuide"), enableBrewingGuide.get())
                        .setDefaultValue(true)
                        .setTooltip(net.minecraft.network.chat.Component.translatable("repack.config.guide.enableBrewingGuide.tooltip"))
                        .setSaveConsumer(enableBrewingGuide::set)
                        .build())
                .addEntry(entryBuilder.startStrField(net.minecraft.network.chat.Component.translatable("repack.config.guide.brewingGuideStyle"), brewingGuideStyle.get())
                        .setDefaultValue("default")
                        .setTooltip(net.minecraft.network.chat.Component.translatable("repack.config.guide.brewingGuideStyle.tooltip"))
                        // Якщо brewingGuideStyle - це ForgeConfigSpec.ConfigValue, то .setSaveConsumer(brewingGuideStyle::set) - це правильно
                        .setSaveConsumer(brewingGuideStyle::set)
                        .build());

        // Застосування змін при закритті екрану
        builder.setSavingRunnable(() -> {
            CLIENT_SPEC.save();
        });

        return builder.build();
    }

    // Enum для вибору ефекту екрану
    public enum ScreenEffectType {
        NONE, SHAKE, PARTICLES, GIF
    }
}