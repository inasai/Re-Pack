package com.inasai.repack.effect;

import com.inasai.repack.RePack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.NativeImage;

import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GifEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean isActive = false;
    private static List<GifFrame> frames = new ArrayList<>();
    private static int currentFrameIndex = 0;
    private static long frameDisplayStartTime = 0;

    private static final String FRAMES_PATH_PREFIX = "textures/gui/death_gif_frames/frame_";
    private static final int TOTAL_FRAMES = 6;
    private static final int DEFAULT_FRAME_DELAY_MS = 100;

    private static int loopCount = 0;
    private static final int MAX_LOOPS = 1;

    // Клас для представлення окремого кадру GIF
    private static class GifFrame {
        public final ResourceLocation textureLocation;
        public final int delayMs;
        public final int width;
        public final int height; // ТІЛЬКИ ОДНА ЗМІННА 'height'

        public GifFrame(ResourceLocation textureLocation, int delayMs, int width, int height) {
            this.textureLocation = textureLocation;
            this.delayMs = delayMs;
            this.width = width;
            this.height = height;
        }
    }

    public static void activate() {
        if (!isActive) {
            LOGGER.info("RePack: Activating GIF Effect.");
            isActive = true;
            loadGifFrames();
            currentFrameIndex = 0;
            frameDisplayStartTime = System.currentTimeMillis();
            loopCount = 0;
            if (frames.isEmpty()) {
                deactivate();
            }
        }
    }

    public static void deactivate() {
        if (isActive) {
            LOGGER.info("RePack: Deactivating GIF Effect.");
            isActive = false;
            for (GifFrame frame : frames) {
                Minecraft.getInstance().getTextureManager().release(frame.textureLocation);
            }
            frames.clear();
            currentFrameIndex = 0;
            loopCount = 0;
        }
    }

    private static void loadGifFrames() {
        if (!frames.isEmpty()) {
            LOGGER.debug("RePack: PNG frames already loaded. Skipping reload.");
            return;
        }

        LOGGER.info("RePack: Attempting to load GIF frames from PNG files.");
        try {
            for (int i = 0; i < TOTAL_FRAMES; i++) {
                ResourceLocation framePngLocation = new ResourceLocation(RePack.MOD_ID, FRAMES_PATH_PREFIX + String.format("%03d", i) + ".png");

                NativeImage nativeImage = null;
                try (InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(framePngLocation).get().open()) {
                    nativeImage = NativeImage.read(inputStream);
                }

                if (nativeImage == null) {
                    LOGGER.error("RePack: Failed to load NativeImage for frame: {}", framePngLocation);
                    deactivate();
                    return;
                }

                ResourceLocation frameTextureLocation = new ResourceLocation(RePack.MOD_ID, "dynamic_gif_frame_" + i);
                Minecraft.getInstance().getTextureManager().register(frameTextureLocation, new DynamicTexture(nativeImage));

                frames.add(new GifFrame(frameTextureLocation, DEFAULT_FRAME_DELAY_MS, nativeImage.getWidth(), nativeImage.getHeight()));
                LOGGER.debug("RePack: Loaded frame: {} ({}x{})", framePngLocation, nativeImage.getWidth(), nativeImage.getHeight());
            }
            LOGGER.info("RePack: Successfully loaded {} PNG frames.", frames.size());
        } catch (IOException e) {
            LOGGER.error("RePack: Failed to load PNG frame. Error: {}", e.getMessage(), e);
            deactivate();
        } catch (Exception e) {
            LOGGER.error("RePack: An unexpected error occurred while loading PNG frames. Error: {}", e.getMessage(), e);
            deactivate();
        }

        if (frames.isEmpty()) {
            LOGGER.warn("RePack: No PNG frames loaded. Deactivating effect.");
            deactivate();
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!isActive || frames.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        long currentTime = System.currentTimeMillis();
        if (currentTime - frameDisplayStartTime >= frames.get(currentFrameIndex).delayMs) {
            currentFrameIndex++;
            if (currentFrameIndex >= frames.size()) {
                currentFrameIndex = 0;
                loopCount++;
                LOGGER.debug("RePack: GIF loop finished. Current loop count: {}", loopCount);

                if (loopCount >= MAX_LOOPS) {
                    LOGGER.info("RePack: GIF reached max loops ({}). Deactivating effect.", MAX_LOOPS);
                    deactivate();
                    return;
                }
            }
            frameDisplayStartTime = currentTime;
        }

        GifFrame currentFrame = frames.get(currentFrameIndex);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, currentFrame.textureLocation);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        event.getGuiGraphics().blit(
                currentFrame.textureLocation,
                0, 0,
                screenWidth, screenHeight,
                0, 0,
                currentFrame.width, currentFrame.height,
                currentFrame.width, currentFrame.height
        );
    }
}