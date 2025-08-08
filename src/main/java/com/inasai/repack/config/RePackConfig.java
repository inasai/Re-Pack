package com.inasai.repack.config;

import com.inasai.repack.config.category.DeathConfig;
import com.inasai.repack.config.category.GuideConfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
//import java.util.function.Function;

public class RePackConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static DeathConfig deathConfig;
    public static GuideConfig guideConfig;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        deathConfig = new DeathConfig(builder);
        guideConfig = new GuideConfig(builder);

        CLIENT_SPEC = builder.build();
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("repack.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        builder.getOrCreateCategory(Component.translatable("repack.config.category.death"))
                .addEntry(entryBuilder.startBooleanToggle(Component.translatable("repack.config.death.enableSounds"), deathConfig.enableDeathSounds.get())
                        .setDefaultValue(true)
                        .setTooltip(Component.translatable("repack.config.death.enableSounds.tooltip"))
                        .setSaveConsumer(deathConfig.enableDeathSounds::set)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(Component.translatable("repack.config.death.doImmediateRespawn"), deathConfig.doImmediateRespawn.get())
                        .setDefaultValue(false)
                        .setTooltip(Component.translatable("repack.config.death.doImmediateRespawn.tooltip"))
                        .setSaveConsumer(deathConfig.doImmediateRespawn::set)
                        .build())
                .addEntry(entryBuilder.startIntField(Component.translatable("repack.config.death.specialChance"), deathConfig.specialDeathChance.get())
                        .setDefaultValue(10)
                        .setMin(1).setMax(100)
                        .setTooltip(Component.translatable("repack.config.death.specialChance.tooltip"))
                        .setSaveConsumer(deathConfig.specialDeathChance::set)
                        .build())
                .addEntry(entryBuilder.startEnumSelector(Component.translatable("repack.config.death.screenEffect"), DeathConfig.ScreenEffectType.class, deathConfig.specialDeathScreenEffect.get())
                        .setDefaultValue(DeathConfig.ScreenEffectType.SHAKE)
                        .setTooltip(Component.translatable("repack.config.death.screenEffect.tooltip"))
                        .setSaveConsumer(deathConfig.specialDeathScreenEffect::set)
                        .build());

        var guideCategory = builder.getOrCreateCategory(Component.translatable("repack.config.category.guide"));

        for (int i = 0; i < GuideConfig.BREWING_GUIDES.size(); i++) {
            GuideConfig.BrewingGuideConfig guide = GuideConfig.BREWING_GUIDES.get(i);

            List<AbstractConfigListEntry> guideEntries = new ArrayList<>();

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
            guideEntries.add(entryBuilder.startEnumSelector(Component.translatable("repack.config.guide.brewingGuidePosition"), GuideConfig.GuidePosition.class, guide.brewingGuidePosition.get())
                    .setDefaultValue(guide.brewingGuidePosition.getDefault())
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuidePosition.tooltip"))
                    .setSaveConsumer(guide.brewingGuidePosition::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideOffsetX"), guide.brewingGuideOffsetX.get())
                    .setDefaultValue(guide.brewingGuideOffsetX.getDefault())
                    .setMin(-1000).setMax(1000)
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideOffsetX.tooltip"))
                    .setSaveConsumer(guide.brewingGuideOffsetX::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideOffsetY"), guide.brewingGuideOffsetY.get())
                    .setDefaultValue(guide.brewingGuideOffsetY.getDefault())
                    .setMin(-1000).setMax(1000)
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideOffsetY.tooltip"))
                    .setSaveConsumer(guide.brewingGuideOffsetY::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideWidth"), guide.brewingGuideWidth.get())
                    .setDefaultValue(guide.brewingGuideWidth.getDefault())
                    .setMin(1).setMax(1024)
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideWidth.tooltip"))
                    .setSaveConsumer(guide.brewingGuideWidth::set)
                    .build());
            guideEntries.add(entryBuilder.startIntField(Component.translatable("repack.config.guide.brewingGuideHeight"), guide.brewingGuideHeight.get())
                    .setDefaultValue(guide.brewingGuideHeight.getDefault())
                    .setMin(1).setMax(1024)
                    .setTooltip(Component.translatable("repack.config.guide.brewingGuideHeight.tooltip"))
                    .setSaveConsumer(guide.brewingGuideHeight::set)
                    .build());

            guideCategory.addEntry(entryBuilder.startSubCategory(
                    Component.translatable("repack.config.guide.subCategory." + guide.id),
                    guideEntries
            ).build());
        }

        for (int i = 0; i < GuideConfig.ENCHANTMENT_GUIDES.size(); i++) {
            GuideConfig.EnchantmentGuideConfig guide = GuideConfig.ENCHANTMENT_GUIDES.get(i);

            List<AbstractConfigListEntry> enchantmentGuideEntries = new ArrayList<>();

            enchantmentGuideEntries.add(entryBuilder.startBooleanToggle(Component.translatable("repack.config.guide.enableEnchantmentGuide"), guide.enableEnchantmentGuide.get())
                    .setDefaultValue(guide.enableEnchantmentGuide.getDefault())
                    .setTooltip(Component.translatable("repack.config.guide.enableEnchantmentGuide.tooltip"))
                    .setSaveConsumer(guide.enableEnchantmentGuide::set)
                    .build());

            guideCategory.addEntry(entryBuilder.startSubCategory(
                    Component.translatable("repack.config.guide.subCategory." + guide.id), // Це буде "enchantment"
                    enchantmentGuideEntries
            ).build());
        }

        builder.setSavingRunnable(() -> {
            // nothing
            CLIENT_SPEC.save();
        });

        return builder.build();
    }
}