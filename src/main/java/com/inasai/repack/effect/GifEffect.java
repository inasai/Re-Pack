package com.inasai.repack.effect;

import com.inasai.repack.RePack;
import com.inasai.repack.config.RePackConfig; // Додаємо імпорт конфігурації
import com.inasai.repack.config.category.DeathConfig; // Додаємо імпорт DeathConfig

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.opengl.GL11; // GL11 is good for blendFunc, but generally prefer RenderSystem wrappers

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
    private static final int MAX_LOOPS = 2; // Збільшимо кількість циклів для кращого спостереження

    // Зберігаємо DynamicTexture, щоб мати доступ до її ідентифікатора OpenGL для звільнення
    private static class GifFrame {
        public final ResourceLocation textureLocation;
        public final int delayMs;
        public final int width;
        public final int height;
        public final DynamicTexture dynamicTexture; // Додаємо посилання на DynamicTexture

        public GifFrame(ResourceLocation textureLocation, int delayMs, int width, int height, DynamicTexture dynamicTexture) {
            this.textureLocation = textureLocation;
            this.delayMs = delayMs;
            this.width = width;
            this.height = height;
            this.dynamicTexture = dynamicTexture;
        }
    }

    public static void activate() {
        if (!isActive) {
            LOGGER.info("RePack: Activating GIF Effect.");
            isActive = true;
            // Завантажуємо кадри лише якщо їх ще немає, або якщо вони були звільнені
            if (frames.isEmpty()) {
                loadGifFrames();
            } else {
                LOGGER.debug("RePack: GIF frames already loaded. Reusing existing textures.");
            }
            currentFrameIndex = 0;
            frameDisplayStartTime = System.currentTimeMillis();
            loopCount = 0;
            if (frames.isEmpty()) {
                LOGGER.warn("RePack: No GIF frames available after activation attempt. Deactivating.");
                deactivate(); // Деактивуємо, якщо не вдалося завантажити кадри
            }
        }
    }

    public static void deactivate() {
        if (isActive) {
            LOGGER.info("RePack: Deactivating GIF Effect.");
            isActive = false;
            // Звільняємо всі текстури з менеджера текстур Minecraft та пам'яті GPU
            for (GifFrame frame : frames) {
                if (frame.dynamicTexture != null) {
                    frame.dynamicTexture.close(); // Це звільняє NativeImage і GL текстуру
                    LOGGER.debug("RePack: Released dynamic texture for: {}", frame.textureLocation);
                }
                // Нам не потрібно явно викликати release на TextureManager, оскільки close() DynamicTexture робить це за нас.
                // Minecraft.getInstance().getTextureManager().release(frame.textureLocation);
            }
            frames.clear();
            currentFrameIndex = 0;
            loopCount = 0;
            LOGGER.debug("RePack: GIF frames and resources cleared.");
        }
    }

    private static void loadGifFrames() {
        // Перевірка, щоб уникнути подвійного завантаження, якщо activate() викликається кілька разів поспіль
        // без проміжної деактивації. Але основна логіка вже є в activate().
        if (!frames.isEmpty()) {
            return;
        }

        LOGGER.info("RePack: Attempting to load GIF frames from PNG files.");
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            ResourceLocation framePngLocation = new ResourceLocation(RePack.MOD_ID, FRAMES_PATH_PREFIX + String.format("%03d", i) + ".png");
            NativeImage nativeImage = null;
            DynamicTexture dynamicTexture = null;

            try (InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(framePngLocation).get().open()) {
                nativeImage = NativeImage.read(inputStream);

                if (nativeImage == null) {
                    LOGGER.error("RePack: Failed to read NativeImage from input stream for frame: {}", framePngLocation);
                    continue; // Переходимо до наступного кадру
                }

                // Реєструємо DynamicTexture
                ResourceLocation frameTextureLocation = new ResourceLocation(RePack.MOD_ID, "dynamic_gif_frame_" + i);
                dynamicTexture = new DynamicTexture(nativeImage); // NativeImage передається в DynamicTexture
                Minecraft.getInstance().getTextureManager().register(frameTextureLocation, dynamicTexture);

                // Додаємо новий фрейм з посиланням на DynamicTexture
                frames.add(new GifFrame(frameTextureLocation, DEFAULT_FRAME_DELAY_MS, nativeImage.getWidth(), nativeImage.getHeight(), dynamicTexture));
                LOGGER.debug("RePack: Loaded frame: {} ({}x{}) with texture ID: {}", framePngLocation, nativeImage.getWidth(), nativeImage.getHeight(), frameTextureLocation);

            } catch (IOException e) {
                LOGGER.error("RePack: Failed to load PNG frame '{}'. Error: {}", framePngLocation, e.getMessage());
            } catch (Exception e) { // Ловимо ширший клас винятків на випадок інших проблем
                LOGGER.error("RePack: An unexpected error occurred while loading PNG frame '{}'. Error: {}", framePngLocation, e.getMessage(), e);
            } finally {
                // NativeImage буде закрита DynamicTexture, коли DynamicTexture.close() буде викликано.
                // Тут її не потрібно закривати вручну, якщо вона успішно передана в DynamicTexture.
                // Якщо була помилка до передачі, nativeImage все одно має бути закритою.
                // Однак, NativeImage.read(InputStream) сам закриває InputStream, тому тут нічого не робимо.
                // Додатково: if (nativeImage != null && dynamicTexture == null) { nativeImage.close(); }
            }
        }

        if (frames.isEmpty()) {
            LOGGER.warn("RePack: No PNG frames were successfully loaded. GIF effect will not function.");
            // Не викликаємо deactivate тут, оскільки activate() вже перевірить frames.isEmpty()
            // і деактивує, якщо необхідно.
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // Перевіряємо, чи має ефект відображатися зараз, і чи активований він в конфігурації
        if (!isActive || frames.isEmpty() || RePackConfig.deathConfig.specialDeathScreenEffect.get() != DeathConfig.ScreenEffectType.GIF) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) {
            LOGGER.warn("RePack: Minecraft window is null during GIF render. Deactivating.");
            deactivate();
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        long currentTime = System.currentTimeMillis();
        GifFrame currentFrame = frames.get(currentFrameIndex); // Отримуємо поточний кадр до перевірки часу

        if (currentTime - frameDisplayStartTime >= currentFrame.delayMs) {
            currentFrameIndex++;
            if (currentFrameIndex >= frames.size()) {
                currentFrameIndex = 0;
                loopCount++;
                LOGGER.debug("RePack: GIF loop finished. Current loop count: {}", loopCount);

                if (loopCount >= MAX_LOOPS) {
                    LOGGER.info("RePack: GIF reached max loops ({}). Deactivating effect.", MAX_LOOPS);
                    deactivate();
                    return; // Важливо вийти після деактивації
                }
            }
            frameDisplayStartTime = currentTime;
            // Оновлюємо currentFrame після зміни currentFrameIndex
            currentFrame = frames.get(currentFrameIndex);
        }

        // RenderSystem.setShader(GameRenderer::getPositionTexShader); // Цей шейдер може бути несумісним
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, currentFrame.textureLocation);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // Використовуйте стандартну функцію змішування Minecraft
        // RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Це те саме, що defaultBlendFunc

        try {
            // Малюємо поточний кадр на весь екран
            event.getGuiGraphics().blit(
                    currentFrame.textureLocation,
                    0, 0, // Позиція (x, y) верхнього лівого кута на екрані
                    screenWidth, screenHeight, // Ширина та висота, на яку малюємо на екрані
                    0, 0, // UV координати початку текстури (верхній лівий кут текстури)
                    currentFrame.width, currentFrame.height, // UV координати кінця текстури (нижній правий кут текстури)
                    currentFrame.width, currentFrame.height // Оригінальні розміри текстури
            );
        } catch (Exception e) {
            LOGGER.error("RePack: Error rendering GIF frame: {}", e.getMessage(), e);
            deactivate(); // Деактивуємо ефект при помилці рендерингу
        }

        RenderSystem.disableBlend();
    }
}