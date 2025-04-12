package net.neganote.gtutilities.datagen;

import net.neganote.gtutilities.datagen.lang.UtilLangHandler;

import com.tterrag.registrate.providers.ProviderType;

import static net.neganote.gtutilities.GregTechModernUtilities.REGISTRATE;

public class UtilDatagen {

    public static void init() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, UtilLangHandler::init);
    }
}
