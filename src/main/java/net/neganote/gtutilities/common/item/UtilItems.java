package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.config.UtilConfig;

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

    static {
        if (UtilConfig.INSTANCE.features.omnibreakerEnabled) {
            OMNIBREAKER = REGISTRATE
                    .item("omnibreaker", (p) -> OmniBreakerItem.create(p, OMNIBREAKER_TIER))
                    .lang("Omni-breaker")
                    .model((ctx, prov) -> {
                        ResourceLocation handheldItem = new ResourceLocation("item/handheld");
                        prov
                                .withExistingParent(ctx.getName(), handheldItem)
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
    }

    public static void init() {}

    // Copied from GTItems
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}
