package com.inasai.repack.config.category;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class GuideConfig {
    public enum GuidePosition {
        LEFT, RIGHT, TOP, BOTTOM
    }

    public static final List<BrewingGuideConfig> BREWING_GUIDES = new ArrayList<>();

    public GuideConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Guide Category");

        BREWING_GUIDES.add(BrewingGuideConfig.define(builder, "part1", true, "guide_1", GuidePosition.LEFT, 5, 0, 145, 160));
        BREWING_GUIDES.add(BrewingGuideConfig.define(builder, "part2", true, "guide_2", GuidePosition.RIGHT, 5, 0, 186, 193));

        builder.pop();
    }

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
            builder.push("BrewingGuide_" + id);
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