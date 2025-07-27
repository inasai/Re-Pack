package com.inasai.repack.effect;

import com.inasai.repack.RePack;
import com.mojang.blaze3d.platform.NativeImage; // <<< НОВИЙ ІМПОРТ
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture; // <<< ДОДАТКОВИЙ ІМПОРТ, якщо ще немає
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream; // <<< НОВИЙ ІМПОРТ
import java.io.ByteArrayOutputStream; // <<< НОВИЙ ІМПОРТ
import java.io.IOException;
import java.io.InputStream; // <<< НОВИЙ ІМПОРТ
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node; // NamedNodeMap не використовується, його можна видалити, якщо IDE підкаже


@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GifEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean isActive = false;
    private static List<GifFrame> frames = new ArrayList<>();
    private static int currentFrameIndex = 0;
    private static long frameDisplayStartTime = 0;
    private static ResourceLocation gifLocation = new ResourceLocation(RePack.MOD_ID, "textures/gui/death_gif/test_gif.gif"); // Змініть на свій GIF

    // Клас для представлення окремого кадру GIF
    private static class GifFrame {
        public final ResourceLocation textureLocation;
        public final int delayMs; // Затримка в мілісекундах
        public final int width; // Ширина кадру
        public final int height; // Висота кадру

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
            loadGifFrames(); // Завантажуємо кадри при активації
            currentFrameIndex = 0;
            frameDisplayStartTime = System.currentTimeMillis();
            if (frames.isEmpty()) { // Якщо завантаження не вдалося, деактивуємо
                deactivate();
            }
        }
    }

    public static void deactivate() {
        if (isActive) {
            LOGGER.info("RePack: Deactivating GIF Effect.");
            isActive = false;
            // Очищаємо кадри, щоб звільнити пам'ять
            for (GifFrame frame : frames) {
                // Важливо: переконайтеся, що release не викликає помилок, якщо текстура вже була звільнена або не існує.
                // DynamicTexture автоматично видаляє себе при GC, але явне звільнення краще.
                Minecraft.getInstance().getTextureManager().release(frame.textureLocation);
            }
            frames.clear();
            currentFrameIndex = 0;
        }
    }

    private static void loadGifFrames() {
        if (!frames.isEmpty()) {
            LOGGER.debug("RePack: GIF frames already loaded. Skipping reload.");
            return;
        }

        try (InputStream resourceStream = Minecraft.getInstance().getResourceManager().getResource(gifLocation).get().open();
             ImageInputStream iis = ImageIO.createImageInputStream(resourceStream)) { // Використовуємо resourceStream для ImageInputStream

            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(iis);

            int numFrames = reader.getNumImages(true);
            LOGGER.info("RePack: Found {} frames in GIF: {}", numFrames, gifLocation);

            for (int i = 0; i < numFrames; i++) {
                BufferedImage image = reader.read(i);
                IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0");

                IIOMetadataNode graphicControlExtension = findNode(root, "GraphicControlExtension");
                int delay = 100;
                if (graphicControlExtension != null) {
                    String delayTime = graphicControlExtension.getAttribute("delayTime");
                    if (delayTime != null && !delayTime.isEmpty()) {
                        try {
                            delay = Integer.parseInt(delayTime) * 10;
                            if (delay <= 0) delay = 100;
                        } catch (NumberFormatException e) {
                            LOGGER.warn("RePack: Could not parse GIF frame delay time: {}. Using default 100ms.", delayTime);
                        }
                    }
                }

                // >>> КОНВЕРТАЦІЯ BufferedImage В NativeImage <<<
                NativeImage nativeImage;
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "PNG", baos); // Зберігаємо як PNG у пам'яті, бо NativeImage.read приймає InputStream
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                        nativeImage = NativeImage.read(bais);
                    }
                }

                ResourceLocation frameTextureLocation = new ResourceLocation(RePack.MOD_ID, "dynamic_gif_frame_" + i + "_" + System.currentTimeMillis());
                Minecraft.getInstance().getTextureManager().register(frameTextureLocation, new DynamicTexture(nativeImage));
                frames.add(new GifFrame(frameTextureLocation, delay, image.getWidth(), image.getHeight()));
            }
            LOGGER.info("RePack: Successfully loaded {} GIF frames.", frames.size());
        } catch (IOException e) {
            LOGGER.error("RePack: Failed to load GIF file: {}. Error: {}", gifLocation, e.getMessage(), e);
            deactivate();
        } catch (Exception e) {
            LOGGER.error("RePack: An unexpected error occurred while processing GIF: {}. Error: {}", gifLocation, e.getMessage(), e);
            deactivate();
        }
        if (frames.isEmpty()) {
            LOGGER.warn("RePack: No frames loaded for GIF {}. Deactivating effect.", gifLocation);
            deactivate();
        }
    }

    private static IIOMetadataNode findNode(IIOMetadataNode root, String nodeName) {
        if (root == null || !root.hasChildNodes()) {
            return null;
        }
        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            Node node = root.getChildNodes().item(i);
            if (node.getNodeName().equalsIgnoreCase(nodeName) && node instanceof IIOMetadataNode) {
                return (IIOMetadataNode) node;
            }
        }
        return null;
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
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
            frameDisplayStartTime = currentTime;
        }

        GifFrame currentFrame = frames.get(currentFrameIndex);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Повна прозорість (RGBA)
        RenderSystem.setShaderTexture(0, currentFrame.textureLocation);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // >>> ВИПРАВЛЕННЯ ДЛЯ GuiGraphics <<<
        // Використовуємо event.getGuiGraphics() замість event.getGraphics()
        // та відповідну сигнатуру blit для GuiGraphics
        event.getGuiGraphics().blit(
                currentFrame.textureLocation, // ResourceLocation текстури
                0, // x-координата (лівий верхній кут екрану)
                0, // y-координата (лівий верхній кут екрану)
                screenWidth, // Ширина для рендерингу (на весь екран)
                screenHeight, // Висота для рендерингу (на весь екран)
                0, // u-координата лівого верхнього кута текстури (0.0 для початку)
                0, // v-координата лівого верхнього кута текстури (0.0 для початку)
                currentFrame.width, // Ширина області текстури для відображення
                currentFrame.height, // Висота області текстури для відображення
                currentFrame.width, // Фактична ширина текстури
                currentFrame.height  // Фактична висота текстури
        );
    }
}