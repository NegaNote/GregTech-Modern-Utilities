package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;

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
    public static ItemEntry<OmniToolItem> OMNITOOL = null;

    static {
        if (UtilConfig.INSTANCE.features.omnitoolEnabled) {
            OMNITOOL = REGISTRATE
                    .item("omnitool", (p) -> OmniToolItem.create(p, GTValues.IV))
                    .lang("Omnitool")
                    .defaultModel()
                    .properties(p -> p.stacksTo(1).durability(0))
                    .onRegister(attach(ElectricStats.createElectricItem(25_600_000L, GTValues.IV),
                            new PrecisionBreakBehavior(GTValues.IV)))
                    .register();
        }
    }

    public static void init() {}

    // Copied from GTItems
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}
