package net.neganote.gtutilities.common.tools.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neganote.gtutilities.common.item.UtilToolItems;
import net.neganote.gtutilities.common.tools.UtilToolType;

import com.tterrag.registrate.util.entry.ItemEntry;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;

import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;

/**
 * Recipes for custom tools.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UtilToolRecipeHelper {

    public static final Int2ReferenceMap<ItemEntry<? extends Item>> powerUnitItems = new Int2ReferenceArrayMap<>(
            GTValues.tiersBetween(GTValues.LV, GTValues.ZPM),
            new ItemEntry[] { GTItems.POWER_UNIT_LV, GTItems.POWER_UNIT_MV, GTItems.POWER_UNIT_HV,
                    GTItems.POWER_UNIT_EV, GTItems.POWER_UNIT_IV, UtilToolItems.POWER_UNIT_LUV,
                    UtilToolItems.POWER_UNIT_ZPM });

    private UtilToolRecipeHelper() {}

    public static void run(Consumer<FinishedRecipe> provider, Material material) {
        ToolProperty property = material.getProperty(PropertyKey.TOOL);
        if (property == null) {
            return;
        }

        processElectricTool(provider, property, material);
    }

    private static void processElectricTool(Consumer<FinishedRecipe> provider, ToolProperty property,
                                            Material material) {
        if (!material.shouldGenerateRecipesFor(plate)) {
            return;
        }

        final int voltageMultiplier = material.getBlastTemperature() > 2800 ? GTValues.VA[GTValues.LV] :
                GTValues.VA[GTValues.ULV];
        TagPrefix toolPrefix;

        if (material.hasFlag(GENERATE_PLATE)) {
            final MaterialEntry plate = new MaterialEntry(TagPrefix.plate, material);
            final MaterialEntry steelPlate = new MaterialEntry(TagPrefix.plate, GTMaterials.Steel);
            final MaterialEntry steelRing = new MaterialEntry(TagPrefix.ring, GTMaterials.Steel);

            // chainsaw
            if (property.hasType(GTToolType.CHAINSAW_LV)) {
                toolPrefix = TagPrefix.toolHeadChainsaw;
                VanillaRecipeHelper.addShapedRecipe(provider, String.format("chainsaw_head_%s", material.getName()),
                        ChemicalHelper.get(toolPrefix, material),
                        "SRS", "XhX", "SRS",
                        'X', plate,
                        'S', steelPlate,
                        'R', steelRing);

                addElectricToolRecipe(provider, toolPrefix,
                        new GTToolType[] { UtilToolType.CHAINSAW_MV, UtilToolType.CHAINSAW_HV,
                                UtilToolType.CHAINSAW_EV, UtilToolType.CHAINSAW_IV, UtilToolType.CHAINSAW_LuV,
                                UtilToolType.CHAINSAW_ZPM, },
                        material);
            }

            // drill
            if (property.hasType(GTToolType.DRILL_LV)) {
                toolPrefix = TagPrefix.toolHeadDrill;
                VanillaRecipeHelper.addShapedRecipe(provider, String.format("drill_head_%s", material.getName()),
                        ChemicalHelper.get(toolPrefix, material),
                        "XSX", "XSX", "ShS",
                        'X', plate,
                        'S', steelPlate);

                addElectricToolRecipe(provider, toolPrefix,
                        new GTToolType[] { UtilToolType.DRILL_LUV, UtilToolType.DRILL_ZPM }, material);
            }

            // electric wire cutters
            if (property.hasType(GTToolType.WIRE_CUTTER_LV)) {
                toolPrefix = toolHeadWireCutter;
                addElectricToolRecipe(provider, toolPrefix,
                        new GTToolType[] { UtilToolType.WIRE_CUTTER_MV, UtilToolType.WIRE_CUTTER_EV,
                                UtilToolType.WIRE_CUTTER_LuV, UtilToolType.WIRE_CUTTER_ZPM },
                        material);

                VanillaRecipeHelper.addShapedRecipe(provider, String.format("wirecutter_head_%s", material.getName()),
                        ChemicalHelper.get(toolPrefix, material),
                        "XfX", "X X", "SRS",
                        'X', plate,
                        'R', steelRing,
                        'S', new MaterialEntry(screw, GTMaterials.Steel));
            }

            // buzzsaw
            if (property.hasType(GTToolType.BUZZSAW)) {
                toolPrefix = TagPrefix.toolHeadBuzzSaw;
                addElectricToolRecipe(provider, toolPrefix,
                        new GTToolType[] { UtilToolType.BUZZSAW_MV, UtilToolType.BUZZSAW_HV,
                                UtilToolType.BUZZSAW_EV, UtilToolType.BUZZSAW_IV, UtilToolType.BUZZSAW_LuV,
                                UtilToolType.BUZZSAW_ZPM, },
                        material);

                VanillaRecipeHelper.addShapedRecipe(provider, String.format("buzzsaw_blade_%s", material.getName()),
                        ChemicalHelper.get(toolPrefix, material),
                        "sXh", "X X", "fXx",
                        'X', plate);
            }
            // wrench
            if (property.hasType(GTToolType.WRENCH_LV)) {
                toolPrefix = TagPrefix.toolHeadWrench;
                addElectricToolRecipe(provider, toolPrefix,
                        new GTToolType[] { UtilToolType.WRENCH_MV, UtilToolType.WRENCH_EV,
                                UtilToolType.WRENCH_LuV, UtilToolType.WRENCH_ZPM },
                        material);

                VanillaRecipeHelper.addShapedRecipe(provider, String.format("wrench_head_%s", material.getName()),
                        ChemicalHelper.get(toolPrefix, material),
                        "hXW", "XRX", "WXd",
                        'X', plate,
                        'R', steelRing,
                        'W', new MaterialEntry(TagPrefix.screw, GTMaterials.Steel));
            }

        }

        // screwdriver
        if (property.hasType(GTToolType.SCREWDRIVER_LV)) {

            if (material.hasFlag(GENERATE_LONG_ROD)) {
                toolPrefix = TagPrefix.toolHeadScrewdriver;
                addElectricToolRecipe(provider, toolPrefix,
                        new GTToolType[] { UtilToolType.SCREWDRIVER_MV, UtilToolType.SCREWDRIVER_HV,
                                UtilToolType.SCREWDRIVER_EV, UtilToolType.SCREWDRIVER_IV,
                                UtilToolType.SCREWDRIVER_LuV, UtilToolType.SCREWDRIVER_ZPM, },
                        material);

                VanillaRecipeHelper.addShapedRecipe(provider, String.format("screwdriver_tip_%s", material.getName()),
                        ChemicalHelper.get(toolPrefix, material),
                        "fR", " h",
                        'R', new MaterialEntry(TagPrefix.rodLong, material));
            }

        }
    }

    private static void addElectricToolRecipe(Consumer<FinishedRecipe> provider, TagPrefix toolHead,
                                              GTToolType[] toolItems,
                                              Material material) {
        for (GTToolType toolType : toolItems) {
            if (!material.getProperty(PropertyKey.TOOL).hasType(toolType)) continue;

            int tier = toolType.electricTier;
            ItemStack powerUnitStack = powerUnitItems.get(tier).asStack();
            IElectricItem powerUnit = GTCapabilityHelper.getElectricItem(powerUnitStack);
            ItemStack tool = GTMaterialItems.TOOL_ITEMS.get(material, toolType).get().get(0, powerUnit.getMaxCharge());
            VanillaRecipeHelper.addShapedEnergyTransferRecipe(provider,
                    true, true, true,
                    String.format("%s_%s", material.getName(), toolType.name),
                    Ingredient.of(powerUnitStack),
                    tool,
                    "wHd", " U ",
                    'H', new MaterialEntry(toolHead, material),
                    'U', powerUnitStack);
        }
    }
}
