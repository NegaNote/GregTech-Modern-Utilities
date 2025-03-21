package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.generators.ModelFile;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.config.UtilConfig;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import java.util.function.Function;

import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

public class UtilItems {

    static {
        REGISTRATE.creativeModeTab(() -> GregTechModernUtilities.UTIL_CREATIVE_TAB);
    }

    public static ItemEntry<OmniToolItem> OMNITOOL = null;
    public static ItemEntry<ComponentItem> PUNCH_CARD = null;

    public static ItemStack punchCard(int count, int encoded) {
        ItemStack stack = PUNCH_CARD.asStack(count);
        var tagCompound = stack.getOrCreateTag();
        tagCompound.putInt("Configuration", encoded);
        return stack;
    }


    public static int OMNITOOL_TIER = UtilConfig.INSTANCE.features.omnitoolTier;

    static {
        if (UtilConfig.INSTANCE.features.omnitoolEnabled) {
            OMNITOOL = REGISTRATE
                    .item("omnitool", (p) -> OmniToolItem.create(p, OMNITOOL_TIER))
                    .lang("Omnitool")
                    .defaultModel()
                    .properties(p -> p.stacksTo(1).durability(0))
                    .onRegister(attach(
                            ElectricStats.createElectricItem(
                                    (long) Math.pow(4.0, (double) OMNITOOL_TIER - 1) * 100_000L, OMNITOOL_TIER),
                            new PrecisionBreakBehavior(OMNITOOL_TIER)))
                    .register();
        }

        if (UtilConfig.INSTANCE.features.omnitoolEnabled) {
            PUNCH_CARD = REGISTRATE
                    .item("punch_card", ComponentItem::create)
                    .lang("Punch Card")
                    .model(overrideModel(GregTechModernUtilities.id("punch_card"), 33))
                    .onRegister(modelPredicate(GregTechModernUtilities.id("punch_card"),
                            (itemStack) -> IntCircuitBehaviour.getCircuitConfiguration(itemStack) / 100f))
                    .onRegister(attach(new IntCircuitBehaviour()))
                    .register();
        }
    }

    public static <
            T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> overrideModel(ResourceLocation predicate,
                                                                                                                  int modelNumber) {
        if (modelNumber <= 0) return NonNullBiConsumer.noop();
        return (ctx, prov) -> {
            var rootModel = prov.generated(ctx::getEntry, prov.modLoc("item/%s/1".formatted(prov.name(ctx))));
            for (int i = 0; i < modelNumber; i++) {
                var subModelBuilder = prov.getBuilder("item/" + prov.name(ctx::getEntry) + "/" + i)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"));
                subModelBuilder.texture("layer0", prov.modLoc("item/%s/%d".formatted(prov.name(ctx), i + 1)));

                rootModel = rootModel.override().predicate(predicate, i / 100f)
                        .model(new ModelFile.UncheckedModelFile(prov.modLoc("item/%s/%d".formatted(prov.name(ctx), i))))
                        .end();
            }
        };
    }

    public static <T extends Item> NonNullConsumer<T> modelPredicate(ResourceLocation predicate,
                                                                     Function<ItemStack, Float> property) {
        return item -> {
            if (GTCEu.isClientSide()) {
                ItemProperties.register(item, predicate, (itemStack, c, l, i) -> property.apply(itemStack));
            }
        };
    }

    public static void init() {
    }

    // Copied from GTItems
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}
