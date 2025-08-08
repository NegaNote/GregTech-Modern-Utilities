package net.neganote.gtutilities.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.neganote.gtutilities.common.machine.UtilMachines;
import net.neganote.gtutilities.config.UtilConfig;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTBlocks.LASER_PIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.*;
import static net.neganote.gtutilities.common.item.UtilItems.OMNIBREAKER;
import static net.neganote.gtutilities.common.machine.UtilMachines.ENERGY_CONVERTER_64A;

public class UtilRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        if (UtilConfig.INSTANCE.features.converters64aEnabled &&
                ConfigHolder.INSTANCE.compat.energy.enableFEConverters) {
            register64AConverterRecipes(provider);
        }

        if (UtilConfig.INSTANCE.features.omnibreakerEnabled) {
            registerOmnitoolRecipe(provider);
        }

        if (UtilConfig.INSTANCE.features.pterbEnabled) {
            ASSEMBLY_LINE_RECIPES.recipeBuilder("pterb")
                    .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER)
                    .inputItems(TagPrefix.plate, GTMaterials.Neutronium, 32)
                    .inputItems(SENSOR.get(GTValues.UV), 8)
                    .inputItems(EMITTER.get(GTValues.UV), 8)
                    .inputItems(FIELD_GENERATOR.get(GTValues.UV), 4)
                    .inputItems(CustomTags.UHV_CIRCUITS, 2)
                    .inputItems(TagPrefix.pipeLargeFluid, GTMaterials.Neutronium, 4)
                    .inputItems(CABLE_QUAD.get(GTValues.UV), 8)
                    .inputItems(LASER_PIPES[0], 8)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(GTValues.L * 32))
                    .EUt(1_600_000L)
                    .duration(1200)
                    .outputItems(UtilMachines.PTERB_MACHINE)
                    .stationResearch(b -> b
                            .researchStack(GTMultiMachines.ACTIVE_TRANSFORMER.asStack()).CWUt(16))
                    .save(provider);
        }
    }

    public static void register64AConverterRecipes(Consumer<FinishedRecipe> provider) {
        for (int tier : GTValues.tiersBetween(GTValues.ULV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV)) {
            ASSEMBLER_RECIPES.recipeBuilder("converter_64a_" + GTValues.VN[tier])
                    .inputItems(HULL.get(tier))
                    .inputItems(CIRCUIT.get(tier))
                    .inputItems(CABLE_HEX.get(0), 4)
                    .inputItems(CABLE_HEX.get(tier), 16)
                    .outputItems(ENERGY_CONVERTER_64A[tier])
                    .EUt(GTValues.VA[tier]).duration(40)
                    .save(provider);
        }
    }

    private static ItemStack getPowerUnit(int tier) {
        return switch (tier) {
            case GTValues.LV -> GTItems.POWER_UNIT_LV.asStack();
            case GTValues.MV -> GTItems.POWER_UNIT_MV.asStack();
            case GTValues.HV -> GTItems.POWER_UNIT_HV.asStack();
            case GTValues.EV -> GTItems.POWER_UNIT_EV.asStack();
            case GTValues.IV -> GTItems.POWER_UNIT_IV.asStack();
            default -> GTItems.POWER_UNIT_LV.asStack(); // just so there isn't an error if a tier is used that's not
            // LV through IV
        };
    }

    public static void registerOmnitoolRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("omnibreaker")
                .inputItems(getPowerUnit(UtilConfig.INSTANCE.features.omnibreakerTier))
                .inputItems(CIRCUIT.get(UtilConfig.INSTANCE.features.omnibreakerTier), 2)
                .inputItems(EMITTER.get(UtilConfig.INSTANCE.features.omnibreakerTier), 1)
                .inputItems(CABLE_QUAD.get(UtilConfig.INSTANCE.features.omnibreakerTier), 3)
                .inputItems(MOTOR.get(UtilConfig.INSTANCE.features.omnibreakerTier), 2)
                .outputItems(OMNIBREAKER)
                .EUt(GTValues.VA[UtilConfig.INSTANCE.features.omnibreakerTier], 2).duration(20 * 60)
                .save(provider);
    }
}
