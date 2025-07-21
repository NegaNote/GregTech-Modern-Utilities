package net.neganote.gtutilities.datagen;

import net.minecraft.world.item.Item;
import net.neganote.gtutilities.GregTechModernUtilities;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

public class UtilModels {

    public static NonNullBiConsumer<DataGenContext<Item, Item>, RegistrateItemModelProvider> basicItemModel(String texturePath) {
        return (ctx, prov) -> prov.generated(ctx).texture("layer0", GregTechModernUtilities.id("item/" + texturePath));
    }
}
