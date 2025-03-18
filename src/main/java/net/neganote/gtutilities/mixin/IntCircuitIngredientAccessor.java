package net.neganote.gtutilities.mixin;

import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IntCircuitIngredient.class)
public interface IntCircuitIngredientAccessor {
    @Accessor
    int getConfiguration();
}
