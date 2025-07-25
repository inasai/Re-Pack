package com.inasai.repack.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraft.network.chat.Component; // Додано імпорт Component

public class RePackConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;

    // Death Category Config
    public static ForgeConfigSpec.BooleanValue enableDeathSounds;
    public static ForgeConfigSpec.IntValue specialDeathChance;
    public static ForgeConfigSpec.EnumValue<ScreenEffectType> specialDeathScreenEffect;

    // Guide Category Config
    public static ForgeConfigSpec.BooleanValue enableBrewingGuide;
    public static ForgeConfigSpec.ConfigValue<String> brewingGuideStyle;
    public static ForgeConfigSpec.EnumValue<GuidePosition> brewingGuidePosition; // НОВЕ: Позиція гайду
    public static ForgeConfigSpec.IntValue brewingGuideOffsetX; // НОВЕ: Зміщення по X
    public static ForgeConfigSpec.IntValue brewingGuideOffsetY; // НОВЕ: Зміщення по Y
    public static ForgeConfigSpec.IntValue brewingGuideWidth; // НОВЕ: Ширина гайду
    public static ForgeConfigSpec.IntValue brewingGuideHeight; // НОВЕ: Висота гайду


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
                .define("brewingGuideStyle", "default");
        // НОВІ ОПЦІЇ КОНФІГУРАЦІЇ
        brewingGuidePosition = builder
                .comment("Position of the brewing guide relative to the GUI.")
                .defineEnum("brewingGuidePosition", GuidePosition.RIGHT); // За замовчуванням праворуч
        brewingGuideOffsetX = builder
                .comment("X offset for the brewing guide (pixels).")
                .defineInRange("brewingGuideOffsetX", 5, -500, 500); // За замовчуванням 5px
        brewingGuideOffsetY = builder
                .comment("Y offset for the brewing guide (pixels).")
                .defineInRange("brewingGuideOffsetY", 0, -500, 500); // За замовчуванням 0px
        brewingGuideWidth = builder
                .comment("Width of the brewing guide image (pixels).")
                .defineInRange("brewingGuideWidth", 128, 1, 512); // Приклад розміру. Встановіть реальний розмір вашого PNG!
        brewingGuideHeight = builder
                .comment("Height of the brewing guide image (pixels).")
                .defineInRange("brewingGuideHeight", 128, 1, 512); // Приклад розміру. Встановіть реальний розмір вашого PNG!
        builder.pop();

        CLIENT_SPEC = builder.build();
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("repack.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Death Category
        builder.getOrCreateCategory(Component.translatable("repack.config.category.death"))
                .addEntry(entryBuilder.startBooleanToggle(Component.translatable("repack.config.death.enableSounds"), enableDeathSounds.get())
                        .setDefaultValue(true)
                        .setTooltip(Component.translatable("repack.config.death.enableSounds.tooltip"))
                        .setSaveConsumer(enableDeathSounds::set)
                        .build())
                .addEntry(entryBuilder.startIntField(Component.translatable("repack.config.death.specialChance"), specialDeathChance.get())
                        .setDefaultValue(10)
                        .setMin(1).setMax(100)
                        .setTooltip(Component.translatable("repack.config.death.specialChance.tooltip"))
                        .setSaveConsumer(specialDeathChance::set)
                        .build())
                .addEntry(entryBuilder.startEnumSelector(Component.translatable("repack.config.death.screenEffect"), ScreenEffectType.class, specialDeathScreenEffect.get())
                        .setDefaultValue(ScreenEffectType.SHAKE)
                        .setTooltip(Component.translatable("repack.config.death.screenEffect.tooltip"))
                        .setSaveConsumer(specialDeathScreenEffect::set)
                        .build());

        // Guide Category
        builder.getOrCreateCategory(Component.translatable("repack.config.category.guide"))
                .addEntry(entryBuilder.startBooleanToggle(Component.translatable("repack.config.guide.enableBrewingGuide"), enableBrewingGuide.get())
                        .setDefaultValue(true)
                        .setTooltip(Component.translatable("repack.config.guide.enableBrewingGuide.tooltip"))
                        .setSaveConsumer(enableBrewingGuide::set)
                        .build())
                .addEntry(entryBuilder.startStrField(Component.translatable("repack.config.guide.brewingGuideStyle"), brewingGuideStyle.get())
                        .setDefaultValue("default")
                        .setTooltip(Component.translatable("repack.config.guide.brewingGuideStyle.tooltip"))
                        .setSaveConsumer(brewingGuideStyle::set)
                        .build())
                // НОВІ ЕЛЕМЕНТИ КОНФІГУРАЦІЇ
                .addEntry(entryBuilder.startEnumSelector(Component.translatable("repack.config.guide.brewingGuidePosition"), GuidePosition.class, brewingGuidePosition.get())
                        .setDefaultValue(GuidePosition.RIGHT)
                        .setTooltip(Component.translatable("repack.config.guide.brewingGuidePosition.tooltip"))
                        .setSaveConsumer(brewingGuidePosition::set)
                        .build())
                .addEntry(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideOffsetX"), brewingGuideOffsetX.get())
                        .setDefaultValue(5)
                        .setMin(-500).setMax(500) // Дозволяємо широкі діапазони для гнучкості
                        .setTooltip(Component.translatable("repack.config.guide.brewingGuideOffsetX.tooltip"))
                        .setSaveConsumer(brewingGuideOffsetX::set)
                        .build())
                .addEntry(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideOffsetY"), brewingGuideOffsetY.get())
                        .setDefaultValue(0)
                        .setMin(-500).setMax(500)
                        .setTooltip(Component.translatable("repack.config.guide.brewingGuideOffsetY.tooltip"))
                        .setSaveConsumer(brewingGuideOffsetY::set)
                        .build())
                .addEntry(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideWidth"), brewingGuideWidth.get())
                        .setDefaultValue(128) // Важливо: встановіть реальний розмір вашого PNG
                        .setMin(1).setMax(512)
                        .setTooltip(Component.translatable("repack.config.guide.brewingGuideWidth.tooltip"))
                        .setSaveConsumer(brewingGuideWidth::set)
                        .build())
                .addEntry(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideHeight"), brewingGuideHeight.get())
                        .setDefaultValue(128) // Важливо: встановіть реальний розмір вашого PNG
                        .setMin(1).setMax(512)
                        .setTooltip(Component.translatable("repack.config.guide.brewingGuideHeight.tooltip"))
                        .setSaveConsumer(brewingGuideHeight::set)
                        .build());


        builder.setSavingRunnable(() -> {
            CLIENT_SPEC.save();
        });

        return builder.build();
    }

    // Enum для вибору ефекту екрану
    public enum ScreenEffectType {
        NONE, SHAKE, PARTICLES, GIF
    }

    // НОВИЙ ENUM для вибору позиції гайду
    public enum GuidePosition {
        LEFT, RIGHT, TOP, BOTTOM
    }
}