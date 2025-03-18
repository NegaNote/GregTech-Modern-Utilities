package net.neganote.gtutilities.mixin;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.integration.jei.GTJEIPlugin;
import dev.latvian.mods.kubejs.integration.forge.jei.JEIEvents;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.IntPunchCardIngredient;
import net.neganote.gtutilities.common.item.UtilItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

@Mixin(GTRecipeBuilder.class)
public abstract class GTRecipeBuilderMixin {
//    @Inject(method = "circuitMeta", at = @At("HEAD"), cancellable = true, remap = false)
//    private void circuitInputMixin(int configuration, CallbackInfoReturnable<GTRecipeBuilder> cir) {
//        var self = (GTRecipeBuilder) (Object) this;
//        if (configuration < 0 || configuration > IntCircuitBehaviour.CIRCUIT_MAX) {
//            GTCEu.LOGGER.error("Circuit configuration must be in the bounds 0 - 32");
//        }

    @Shadow
    public abstract GTRecipeBuilder copy(String id);

    /// /        var nc = self.notConsumable(IntCircuitIngredient.circuitInput(configuration));
//        var ing = IntCircuitIngredient.circuitInput(configuration);
//        self.inputItems(ing);
//
//        cir.setReturnValue(self);
//    }

//    private <T> void input(GTRecipeBuilder self, RecipeCapability<T> capability, Content... objs) {
//        var t = (self.perTick ? self.tickInput : self.input);
//        if (t.get(capability) != null && t.get(capability).size() + objs.length > self.recipeType.getMaxInputs(capability)) {
//            GTCEu.LOGGER.warn("Trying to add more inputs than RecipeType can support, id: {}, Max {}{}Inputs: {}", self.id, (self.perTick ? "Tick " : ""), capability.name, self.recipeType.getMaxInputs(capability));
//        }
//        t.computeIfAbsent(capability, c -> new ArrayList<>())
//                .addAll(Arrays.stream(obj).map(capability::of)
//                        .map(o -> new Content(o, self.chance, self.maxChance, self.tierChanceBoost, self.slotName, self.uiName))
//                        .toList());
//    }
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
