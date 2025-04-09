package net.neganote.gtutilities.config;

import com.gregtechceu.gtceu.api.GTValues;

import net.neganote.gtutilities.GregTechModernUtilities;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = GregTechModernUtilities.MOD_ID)
public class UtilConfig {

    public static UtilConfig INSTANCE;

    public static void init() {
        INSTANCE = Configuration.registerConfig(UtilConfig.class, ConfigFormats.yaml()).getConfigInstance();
    }

    @Configurable
    public FeatureConfigs features = new FeatureConfigs();

    public static class FeatureConfigs {

        @Configurable
        @Configurable.Comment({ "Whether the Sterile Cleaning Maintenance Hatch is enabled." })
        public boolean sterileHatchEnabled = true;
        @Configurable
        @Configurable.Comment({ "Whether the 64A energy converters are enabled." })
        public boolean converters64aEnabled = true;
        @Configurable
        @Configurable.Comment({ "Whether the Omni-breaker is enabled." })
        public boolean omnibreakerEnabled = true;
        @Configurable
        @Configurable.Comment({ "What tier the Omni-breaker is, if enabled. (ULV = 0, LV = 1, MV = 2, ...)",
                "(Unless the default recipe is overridden, can only support LV to IV!)" })
        public int omnibreakerTier = GTValues.IV;
        @Configurable
        @Configurable.Comment("The energy capacity of the Omni-breaker.")
        public long omnibreakerEnergyCapacity = 40_960_000L;

        @Configurable
        @Configurable.Comment({ "Whether the Quantum Active Transformer is enabled." })
        public boolean quantumActiveTransformerEnabled = true;
    }
}
