package net.neganote.gtutilities.recipe;

import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.gtutilities.config.UtilConfig;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.data.recipe.CraftingComponent.*;
import static net.neganote.gtutilities.common.machine.UtilMachines.ENERGY_CONVERTER_64A;

public class UtilRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        if (UtilConfig.INSTANCE.features.converters64aEnabled) {
            register64AConverterRecipes(provider);
        }
    }

    public static void register64AConverterRecipes(Consumer<FinishedRecipe> provider) {
        for (int tier : GTValues.tiersBetween(GTValues.ULV, GTValues.UXV)) {
            ASSEMBLER_RECIPES.recipeBuilder("converter_64a_" + GTValues.VN[tier])
                    .inputItems(HULL.getIngredient(tier))
                    .inputItems(CIRCUIT.getIngredient(tier + 2))
                    .inputItems(CABLE_HEX.getIngredient(0), 4)
                    .inputItems(CABLE_HEX.getIngredient(tier), 16)
                    .outputItems(ENERGY_CONVERTER_64A[tier])
                    .save(provider);
        }
    }
}
