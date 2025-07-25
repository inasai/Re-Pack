package com.inasai.repack.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.LivingEntity; // Важливо!

// Цей міксин буде "реалізовувати" методи для LivingEntity
@Mixin(LivingEntity.class)
public interface ILivingEntityAccessor {
    // Отримуємо "координати" через аксесори.
    // ForgeGradle автоматично знайде обфусковані імена для цих методів.
    @Accessor("f_20977_") // Це може бути обфусковане ім'я для getX()
    double getXCoord(); // Важливо: назва методу може бути будь-яка, але Accesor вказує на обфусковане ім'я

    @Accessor("f_20980_") // Це може бути обфусковане ім'я для getY()
    double getYCoord();

    @Accessor("f_20982_") // Це може бути обфусковане ім'я для getZ()
    double getZCoord();
}