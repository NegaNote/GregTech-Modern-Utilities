package net.neganote.gtutilities.mixin;

import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.recipe.IntPunchCardIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.function.Consumer;

@Mixin(GTRecipeBuilder.class)
public abstract class GTRecipeBuilderMixin {

    @Shadow
    public abstract GTRecipeBuilder copy(String id);

    @Inject(method = "save", at = @At("RETURN"), cancellable = true, remap = false)
    public void saveMixin(Consumer<FinishedRecipe> consumer, CallbackInfo ci) {
        var self = (GTRecipeBuilder) (Object) this;

        if (self.id.toString().endsWith("_punch_card")) {
            return;
        }

        if (self.input.values().stream()
                .anyMatch(x -> x.stream().anyMatch(y -> y.content instanceof IntCircuitIngredient))) {

            var n = self.copy(self.id.getPath() + "_punch_card");
            n.input.replaceAll((k, v) -> {
                var l = new ArrayList<Content>();
                v.forEach(c -> {
                    if (c.content instanceof IntCircuitIngredient) {
                        var ic = (IntCircuitIngredient) c.content;
                        int configuration = ((IntCircuitIngredientAccessor) c.content).getConfiguration();
                        GregTechModernUtilities.LOGGER.info("Recipe has IntCircuitIngredient " + configuration);
                        var i = IntPunchCardIngredient.circuitInput(configuration);
                        l.add(new Content(i, ChanceLogic.getMaxChancedValue(), c.maxChance, c.tierChanceBoost, c.slotName, c.uiName));
                    } else {
                        l.add(c);
                    }
                });
                return l;
            });

            n.save(consumer);
        }
    }
}
