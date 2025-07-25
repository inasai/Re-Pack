package com.inasai.repack.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RePackConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static ForgeConfigSpec.BooleanValue enableDeathSounds;
    public static ForgeConfigSpec.IntValue specialDeathChance;
    public static ForgeConfigSpec.EnumValue<ScreenEffectType> specialDeathScreenEffect;

    public static final List<BrewingGuideConfig> BREWING_GUIDES = new ArrayList<>();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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

        // Ініціалізуємо конфігурацію для перших двох частин гайду
        // Ви можете додати більше BrewingGuideConfig.define() для додаткових частин
        BREWING_GUIDES.add(BrewingGuideConfig.define(builder, "part1", true, "guide1", GuidePosition.RIGHT, 5, 0, 170, 166));
        BREWING_GUIDES.add(BrewingGuideConfig.define(builder, "part2", false, "guide2", GuidePosition.RIGHT, 5, 130, 186, 193));

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
        var guideCategory = builder.getOrCreateCategory(Component.translatable("repack.config.category.guide"));

        // Додаємо підкатегорії для кожного гайду
        for (int i = 0; i < BREWING_GUIDES.size(); i++) {
            BrewingGuideConfig guide = BREWING_GUIDES.get(i);

            // ЗМІНА ТИПУ СПИСКУ:
            List<AbstractConfigListEntry> guideEntries = new ArrayList<>(); // Змінено з 'AbstractConfigListEntry<?>'

            guideEntries.add(entryBuilder.startBooleanToggle(Component.translatable("repack.config.guide.enableBrewingGuide"), guide.enableBrewingGuide.get())
                    .setDefaultValue(guide.enableBrewingGuide.getDefault())
                    .setTooltip(Component.translatable("repack.config.guide.enableBrewingGuide.tooltip"))
                    .setSaveConsumer(guide.enableBrewingGuide::set)
                    .build());
            guideEntries.add(entryBuilder.startStrField(Component.translatable("repack.config.guide.brewingGuideStyle"), guide.brewingGuideStyle.get())
                    .setDefaultValue(guide.brewingGuideStyle.getDefault())
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideStyle.tooltip"))
                    .setSaveConsumer(guide.brewingGuideStyle::set)
                    .build());
            guideEntries.add(entryBuilder.startEnumSelector(Component.translatable("repack.config.guide.brewingGuidePosition"), GuidePosition.class, guide.brewingGuidePosition.get())
                    .setDefaultValue(guide.brewingGuidePosition.getDefault())
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuidePosition.tooltip"))
                    .setSaveConsumer(guide.brewingGuidePosition::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideOffsetX"), guide.brewingGuideOffsetX.get())
                    .setDefaultValue(guide.brewingGuideOffsetX.getDefault())
                    .setMin(-1000).setMax(1000) // Збільшуємо діапазон
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideOffsetX.tooltip"))
                    .setSaveConsumer(guide.brewingGuideOffsetX::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideOffsetY"), guide.brewingGuideOffsetY.get())
                    .setDefaultValue(guide.brewingGuideOffsetY.getDefault())
                    .setMin(-1000).setMax(1000) // Збільшуємо діапазон
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideOffsetY.tooltip"))
                    .setSaveConsumer(guide.brewingGuideOffsetY::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideWidth"), guide.brewingGuideWidth.get())
                    .setDefaultValue(guide.brewingGuideWidth.getDefault())
                    .setMin(1).setMax(1024) // Збільшуємо максимальний розмір
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideWidth.tooltip"))
                    .setSaveConsumer(guide.brewingGuideWidth::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideHeight"), guide.brewingGuideHeight.get())
                    .setDefaultValue(guide.brewingGuideHeight.getDefault())
                    .setMin(1).setMax(1024) // Збільшуємо максимальний розмір
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideHeight.tooltip"))
                    .setSaveConsumer(guide.brewingGuideHeight::set)
                    .build());

            // Тепер передаємо назву підкатегорії та список елементів
            guideCategory.addEntry(entryBuilder.startSubCategory(
                    Component.translatable("repack.config.guide.subCategory." + guide.id),
                    guideEntries
            ).build());
        }


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

    // НОВИЙ КЛАС ДЛЯ КОНФІГУРАЦІЇ КОЖНОГО ПОСІБНИКА
    public static class BrewingGuideConfig {
        public final String id;
        public final ForgeConfigSpec.BooleanValue enableBrewingGuide;
        public final ForgeConfigSpec.ConfigValue<String> brewingGuideStyle;
        public final ForgeConfigSpec.EnumValue<GuidePosition> brewingGuidePosition;
        public final ForgeConfigSpec.IntValue brewingGuideOffsetX;
        public final ForgeConfigSpec.IntValue brewingGuideOffsetY;
        public final ForgeConfigSpec.IntValue brewingGuideWidth;
        public final ForgeConfigSpec.IntValue brewingGuideHeight;

        private BrewingGuideConfig(String id, ForgeConfigSpec.BooleanValue enableBrewingGuide,
                                   ForgeConfigSpec.ConfigValue<String> brewingGuideStyle,
                                   ForgeConfigSpec.EnumValue<GuidePosition> brewingGuidePosition,
                                   ForgeConfigSpec.IntValue brewingGuideOffsetX,
                                   ForgeConfigSpec.IntValue brewingGuideOffsetY,
                                   ForgeConfigSpec.IntValue brewingGuideWidth,
                                   ForgeConfigSpec.IntValue brewingGuideHeight) {
            this.id = id;
            this.enableBrewingGuide = enableBrewingGuide;
            this.brewingGuideStyle = brewingGuideStyle;
            this.brewingGuidePosition = brewingGuidePosition;
            this.brewingGuideOffsetX = brewingGuideOffsetX;
            this.brewingGuideOffsetY = brewingGuideOffsetY;
            this.brewingGuideWidth = brewingGuideWidth;
            this.brewingGuideHeight = brewingGuideHeight;
        }

        public static BrewingGuideConfig define(ForgeConfigSpec.Builder builder, String id,
                                                boolean defaultEnable, String defaultStyle,
                                                GuidePosition defaultPosition, int defaultOffsetX, int defaultOffsetY,
                                                int defaultWidth, int defaultHeight) {
            builder.push("BrewingGuide_" + id); // Кожна частина гайду матиме свій унікальний блок в конфіг файлі
            ForgeConfigSpec.BooleanValue enable = builder
                    .comment("Enable this brewing guide part.")
                    .define("enable", defaultEnable);
            ForgeConfigSpec.ConfigValue<String> style = builder
                    .comment("Style for this brewing guide part (e.g., 'default', 'simple').")
                    .define("style", defaultStyle);
            ForgeConfigSpec.EnumValue<GuidePosition> position = builder
                    .comment("Position of this brewing guide part relative to the GUI.")
                    .defineEnum("position", defaultPosition);
            ForgeConfigSpec.IntValue offsetX = builder
                    .comment("X offset for this brewing guide part (pixels).")
                    .defineInRange("offsetX", defaultOffsetX, -1000, 1000);
            ForgeConfigSpec.IntValue offsetY = builder
                    .comment("Y offset for this brewing guide part (pixels).")
                    .defineInRange("offsetY", defaultOffsetY, -1000, 1000);
            ForgeConfigSpec.IntValue width = builder
                    .comment("Width of this brewing guide part image (pixels).")
                    .defineInRange("width", defaultWidth, 1, 1024);
            ForgeConfigSpec.IntValue height = builder
                    .comment("Height of this brewing guide part image (pixels).")
                    .defineInRange("height", defaultHeight, 1, 1024);
            builder.pop();
            return new BrewingGuideConfig(id, enable, style, position, offsetX, offsetY, width, height);
        }
    }
}