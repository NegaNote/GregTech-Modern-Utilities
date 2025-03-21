package net.neganote.gtutilities;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.gtutilities.recipe.UtilRecipes;

import java.util.function.Consumer;

@SuppressWarnings("unused")
@GTAddon
public class GTMUtilitiesGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GregTechModernUtilities.REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return GregTechModernUtilities.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {
        // CustomTagPrefixes.init();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        UtilRecipes.init(provider);
    }
}
