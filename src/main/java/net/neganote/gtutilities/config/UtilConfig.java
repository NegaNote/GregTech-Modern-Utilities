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

        @Configurable
        @Configurable.Comment({ "Base amount of QAT coolant to drain every tick.",
                "(Setting both this amount and the IO multiplier to 0 disables the coolant mechanic.)" })
        public int qatCoolantBaseDrain = 4;

        @Configurable
        @Configurable.Comment({ "Multiplier over IO amount for additional coolant drain.",
                "(Setting both this and the base drain amount to 0 disables the coolant mechanic.)" })
        public float qatCoolantIOMultiplier = 0.005f;
    }

    public static boolean coolantEnabled() {
        return UtilConfig.INSTANCE.features.qatCoolantBaseDrain != 0 &&
                UtilConfig.INSTANCE.features.qatCoolantIOMultiplier != 0.0f;
    }
}
