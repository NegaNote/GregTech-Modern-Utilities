package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;

import net.neganote.gtutilities.GregTechUtilities;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import static net.neganote.gtutilities.GregTechUtilities.REGISTRATE;

public class UtilItems {

    static {
        REGISTRATE.creativeModeTab(() -> GregTechUtilities.UTIL_CREATIVE_TAB);
    }

    @SuppressWarnings("unused")
    public static ItemEntry<OmniToolItem> OMNITOOL = REGISTRATE
            .item("omnitool", (p) -> OmniToolItem.create(p, GTValues.IV))
            .lang("Omnitool")
            .defaultModel()
            .properties(p -> p.stacksTo(1).durability(0))
            .onRegister(attach(ElectricStats.createElectricItem(25_600_000L, GTValues.IV),
                    new PrecisionBreakBehavior(GTValues.IV)))
            .register();

    public static void init() {}

    // Copied from GTItems
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}
