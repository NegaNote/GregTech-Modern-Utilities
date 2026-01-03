package net.neganote.gtutilities.config;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;

import net.neganote.gtutilities.GregTechModernUtilities;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = GregTechModernUtilities.MOD_ID)
public class UtilConfig {

    public static UtilConfig INSTANCE;

    public static ConfigHolder<UtilConfig> CONFIG_HOLDER;

    public static void init() {
        CONFIG_HOLDER = Configuration.registerConfig(UtilConfig.class, ConfigFormats.yaml());
        INSTANCE = CONFIG_HOLDER.getConfigInstance();
    }

    @Configurable
    public FeatureConfigs features = new FeatureConfigs();

    public static class FeatureConfigs {

        @Configurable
        @Configurable.Comment({ "Whether the Sterile Cleaning Maintenance Hatch is enabled." })
        public boolean sterileHatchEnabled = false;
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
        @Configurable.Comment({ "Whether the Wireless Active Transformer is enabled." })
        public boolean pterbEnabled = true;

        @Configurable
        @Configurable.Comment({ "Base amount of WAT coolant to drain every second.",
                "(Setting both this amount and the IO multiplier to 0 disables the coolant mechanic.)" })
        public int pterbCoolantBaseDrain = 0;

        @Configurable
        @Configurable.Comment({ "Multiplier over IO amount for additional coolant drain.",
                "(Setting both this and the base drain amount to 0 disables the coolant mechanic.)" })
        public float pterbCoolantIOMultiplier = 0;

        @Configurable
        @Configurable.Comment({ "Whether the coins/credits are enabled." })
        public boolean coinsEnabled = false;

        @Configurable
        @Configurable.Comment({ "Whether the custom MV tools are enabled." })
        public boolean customMVToolsEnabled = false;

        @Configurable
        @Configurable.Comment({ "Whether the custom HV tools are enabled." })
        public boolean customHVToolsEnabled = false;

        @Configurable
        @Configurable.Comment({ "Whether the custom EV tools are enabled." })
        public boolean customEVToolsEnabled = false;

        @Configurable
        @Configurable.Comment({ "Whether the custom IV tools are enabled." })
        public boolean customIVToolsEnabled = false;

        @Configurable
        @Configurable.Comment({ "Whether the custom LuV tools are enabled." })
        public boolean customLuVToolsEnabled = false;

        @Configurable
        @Configurable.Comment({ "Whether the custom ZPM tools are enabled." })
        public boolean customZPMToolsEnabled = false;

        @Configurable
        @Configurable.Comment({ "Whether the Auto Turbo Chargers are enabled." })
        public boolean autoChargersEnabled = true;

        @Configurable
        @Configurable.Comment({ "Whether the Expanded Pattern Buffer and Expanded Pattern Buffer Proxy are enabled. If AE2 is not loaded, this config will not load the machines regardless. " })
        public boolean expandedBuffersEnabled = false;
    }

    public static boolean coolantEnabled() {
        return UtilConfig.INSTANCE.features.pterbCoolantBaseDrain != 0 ||
                UtilConfig.INSTANCE.features.pterbCoolantIOMultiplier != 0.0f || GTCEu.isDataGen();
    }
}
