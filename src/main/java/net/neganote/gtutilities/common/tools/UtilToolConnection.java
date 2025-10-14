package net.neganote.gtutilities.common.tools;

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

            if (UtilConfig.INSTANCE.features.customMVToolsEnabled) {
                if (toolProperty != null && toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_MV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_MV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_MV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_MV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_MV);
                }
            }

            if (UtilConfig.INSTANCE.features.customHVToolsEnabled) {
                if (toolProperty != null && toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_HV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_HV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_HV);
                }
            }

            if (UtilConfig.INSTANCE.features.customEVToolsEnabled) {
                if (toolProperty != null && toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_EV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_EV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_EV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_EV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_EV);
                }
            }

            if (UtilConfig.INSTANCE.features.customIVToolsEnabled) {
                if (toolProperty != null && toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_IV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_IV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_IV);
                }
            }

            if (UtilConfig.INSTANCE.features.customLuVToolsEnabled) {
                if (toolProperty != null && toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_LuV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_LuV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_LuV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_LuV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_LuV);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.DRILL_LV)) {
                    toolProperty.addTypes(UtilToolType.DRILL_LUV);
                }
            }

            if (UtilConfig.INSTANCE.features.customZPMToolsEnabled) {
                if (toolProperty != null && toolProperty.hasType(GTToolType.SCREWDRIVER_LV)) {
                    toolProperty.addTypes(UtilToolType.SCREWDRIVER_ZPM);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.BUZZSAW)) {
                    toolProperty.addTypes(UtilToolType.BUZZSAW_ZPM);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.CHAINSAW_LV)) {
                    toolProperty.addTypes(UtilToolType.CHAINSAW_ZPM);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WIRE_CUTTER_LV)) {
                    toolProperty.addTypes(UtilToolType.WIRE_CUTTER_ZPM);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.WRENCH_LV)) {
                    toolProperty.addTypes(UtilToolType.WRENCH_ZPM);
                }
                if (toolProperty != null && toolProperty.hasType(GTToolType.DRILL_LV)) {
                    toolProperty.addTypes(UtilToolType.DRILL_ZPM);
                }
            }

        }
    }
}
