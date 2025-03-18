package net.neganote.gtutilities.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class UtilLangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("item.gtmutils.omnitool", "Omnitool");
        provider.add("item.gtmutils.punch_card", "Punched Card");
        provider.add("tooltip.omnitool.can_break_anything", "The omnitool can break ANYTHING INSTANTLY!");
        provider.add("tooltip.omnitool.charge_status", "Energy: %s EU / %s EU");
        provider.add("tooltip.omnitool.right_click_function", "Break individual blocks with right-click!");
    }
}
