//package net.neganote.gtutilities;
//
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.registration.IRecipeRegistration;
//import mezz.jei.api.registration.IRuntimeRegistration;
//import net.minecraft.resources.ResourceLocation;
//
//import java.util.stream.Stream;
//
//
//
//
//
//@JeiPlugin
//public class GTMUtilitiesJEIAddon implements IModPlugin {
//    @Override
//    public ResourceLocation getPluginUid() {
//        return new ResourceLocation(GregTechModernUtilities.MOD_ID);
//    }
//
//    // Hide all recipies ending with _punch_card
//    @Override
//    public void registerRecipes(IRecipeRegistration registration) {
//    }
//    @Override
//    public void registerRuntime(IRuntimeRegistration registration) {
//        var manager = registration.getRecipeManager();
//        var helper = registration.getJeiHelpers();
//
//        Stream<RecipeType<?>> rt = helper.getAllRecipeTypes();
//
//        registration.get
//
//        // Hide all recipies ending with _punch_card
//        rt.forEach(recipeType -> {
//            var recipes = manager.hideRecipes(recipeType,
//        });
//    }
//}
