package net.neganote.gtutilities.data;

import net.neganote.gtutilities.data.lang.UtilLangHandler;

import com.tterrag.registrate.providers.ProviderType;

import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

public class UtilDatagen {

    public static void init() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, UtilLangHandler::init);
    }
}
