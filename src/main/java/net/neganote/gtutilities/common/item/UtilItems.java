package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.config.UtilConfig;
import net.neganote.gtutilities.datagen.UtilModels;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

public class UtilItems {

    static {
        REGISTRATE.creativeModeTab(() -> GregTechModernUtilities.UTIL_CREATIVE_TAB);
    }

    @SuppressWarnings("unused")
    public static ItemEntry<OmniBreakerItem> OMNIBREAKER = null;
    public static int OMNIBREAKER_TIER = UtilConfig.INSTANCE.features.omnibreakerTier;

    public static ItemEntry<Item> ANCIENT_GOLD_COIN = null;
    public static ItemEntry<Item> CHOCOLATE_COIN = null;
    public static ItemEntry<Item> COPPER_CREDIT = null;
    public static ItemEntry<Item> CUPRONICKEL_CREDIT = null;
    public static ItemEntry<Item> DOGE_COIN = null;
    public static ItemEntry<Item> GOLD_CREDIT = null;
    public static ItemEntry<Item> NAQUADAH_CREDIT = null;
    public static ItemEntry<Item> NEUTRONIUM_CREDIT = null;
    public static ItemEntry<Item> OSMIUM_CREDIT = null;
    public static ItemEntry<Item> PLATINUM_CREDIT = null;
    public static ItemEntry<Item> SILVER_CREDIT = null;

    static {
        if (UtilConfig.INSTANCE.features.omnibreakerEnabled) {
            OMNIBREAKER = REGISTRATE
                    .item("omnibreaker", (p) -> OmniBreakerItem.create(p, OMNIBREAKER_TIER))
                    .lang("Omni-breaker")
                    .model((ctx, prov) -> {
                        prov.handheld(ctx)
                                .texture("layer0", GregTechModernUtilities.id("item/omnibreaker"));
                        // .override()
                        // .predicate(GregTechModernUtilities.id("omnibreaker_name"), 1)
                        // .model(prov.withExistingParent("monibreaker", handheldItem)
                        // .texture("layer0", GregTechModernUtilities.id("item/monibreaker")))
                        // .end()
                        // .override()
                        // .predicate(GregTechModernUtilities.id("omnibreaker_name"), 2)
                        // .model(prov.withExistingParent("meownibreaker", handheldItem)
                        // .texture("layer0", GregTechModernUtilities.id("item/meownibreaker")))
                        // .end();
                    })
                    .properties(p -> p.stacksTo(1).durability(0))
                    .tag(ItemTags.PICKAXES, ItemTags.AXES, ItemTags.SHOVELS, ItemTags.TOOLS)
                    .onRegister(attach(
                            ElectricStats.createElectricItem(UtilConfig.INSTANCE.features.omnibreakerEnergyCapacity,
                                    OMNIBREAKER_TIER),
                            new PrecisionBreakBehavior(OMNIBREAKER_TIER)))
                    .register();
        }

        if (UtilConfig.INSTANCE.features.coinsEnabled) {
            ANCIENT_GOLD_COIN = registerBasicItem("ancient_gold_coin");
            CHOCOLATE_COIN = registerBasicItem("chocolate_coin");
            COPPER_CREDIT = registerBasicItem("copper_credit");
            CUPRONICKEL_CREDIT = registerBasicItem("cupronickel_credit");
            DOGE_COIN = registerBasicItem("doge_coin");
            GOLD_CREDIT = registerBasicItem("gold_credit");
            NAQUADAH_CREDIT = registerBasicItem("naquadah_credit");
            NEUTRONIUM_CREDIT = registerBasicItem("neutronium_credit");
            OSMIUM_CREDIT = registerBasicItem("osmium_credit");
            PLATINUM_CREDIT = registerBasicItem("platinum_credit");
            SILVER_CREDIT = registerBasicItem("silver_credit");
        }
    }

    public static ItemEntry<Item> registerBasicItem(String id, String texturePath) {
        return REGISTRATE
                .item(id, Item::new)
                .initialProperties(Item.Properties::new)
                .model(UtilModels.basicItemModel(texturePath))
                .register();
    }

    public static ItemEntry<Item> registerBasicItem(String id) {
        return registerBasicItem(id, id);
    }

    public static void init() {}

    // Copied from GTItems
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}
