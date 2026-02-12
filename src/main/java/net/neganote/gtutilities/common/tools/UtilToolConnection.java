package net.neganote.gtutilities.common.tools;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;

import net.neganote.gtutilities.config.UtilConfig;

/**
 * Custom tool type connection to base gtm tool tiers.
 */
public class UtilToolConnection {

    public static void modifyMaterials() {
        for (Material material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            ToolProperty toolProperty = material.getProperty(PropertyKey.TOOL);

            if (toolProperty == null) {
                continue;
            }

            // Custom MV Tools
            if (UtilConfig.INSTANCE.features.customMVToolsEnabled || GTCEu.isDataGen()) {
                if (toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_MV);
                }
                if (toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_MV);
                }
                if (toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_MV);
                }
                if (toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_MV);
                }
                if (toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_MV);
                }
            }

            // Custom HV Tools
            if (UtilConfig.INSTANCE.features.customHVToolsEnabled || GTCEu.isDataGen()) {
                if (toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_HV);
                }
            }

            // Custom EV Tools
            if (UtilConfig.INSTANCE.features.customEVToolsEnabled || GTCEu.isDataGen()) {
                if (toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_EV);
                }
                if (toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_EV);
                }
                if (toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_EV);
                }
                if (toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_EV);
                }
                if (toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_EV);
                }
            }

            // Custom IV Tools
            if (UtilConfig.INSTANCE.features.customIVToolsEnabled || GTCEu.isDataGen()) {
                if (toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_IV);
                }
            }

            // Custom LuV Tools
            if (UtilConfig.INSTANCE.features.customLuVToolsEnabled || GTCEu.isDataGen()) {
                if (toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_LuV);
                }
                if (toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_LuV);
                }
                if (toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_LuV);
                }
                if (toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_LuV);
                }
                if (toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_LuV);
                }
                if (toolProperty.hasType(GTToolType.DRILL_LV)) {
                    toolProperty.addTypes(UtilToolType.DRILL_LUV);
                }
            }

            // Custom ZPM Tools
            if (UtilConfig.INSTANCE.features.customZPMToolsEnabled || GTCEu.isDataGen()) {
                if (toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_ZPM);
                }
                if (toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_ZPM);
                }
                if (toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_ZPM);
                }
                if (toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_ZPM);
                }
                if (toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_ZPM);
                }
                if (toolProperty.hasType(GTToolType.DRILL_LV)) {
                    toolProperty.addTypes(UtilToolType.DRILL_ZPM);
                }
            }
        }
    }
}
