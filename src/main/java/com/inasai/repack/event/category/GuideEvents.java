package com.inasai.repack.event.category;

import com.inasai.repack.RePack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent; // Поки не використовується, але може знадобитися

// Це місце для обробників подій, пов'язаних з категорією "Посібник", якщо вони з'являться.
// Наразі весь функціонал гайду обробляється в MixinBrewingStandScreen.
@Mod.EventBusSubscriber(modid = RePack.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GuideEvents {
    // Тут можуть бути @SubscribeEvent методи для подій, що стосуються гайдів.
    // Наприклад, якщо ви захочете додати кнопку, яка відкриває гайд,
    // або якщо гайд буде реагувати на певні дії гравця.
}