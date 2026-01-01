package net.neganote.gtutilities.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.neganote.gtutilities.common.machine.UtilAEMachines;
import net.neganote.gtutilities.common.machine.UtilMachines;
import net.neganote.gtutilities.config.UtilConfig;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireFine;
import static com.gregtechceu.gtceu.common.data.GTBlocks.LASER_PIPES;
import static com.gregtechceu.gtceu.common.data.GTItems.*;
import static com.gregtechceu.gtceu.common.data.GTMachines.DUAL_IMPORT_HATCH;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.*;
import static net.neganote.gtutilities.common.item.UtilItems.OMNIBREAKER;
import static net.neganote.gtutilities.common.machine.UtilMachines.AUTO_CHARGER_4;
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
                    .addMaterialInfo(true)
                    .stationResearch(b -> b
                            .researchStack(GTMultiMachines.ACTIVE_TRANSFORMER.asStack()).CWUt(16))
                    .save(provider);
        }

        if (UtilConfig.INSTANCE.features.aeMachinesEnabled) {
            ASSEMBLY_LINE_RECIPES.recipeBuilder("expanded_me_pattern_buffer")
                    .inputItems(DUAL_IMPORT_HATCH[ZPM], 1)
                    .inputItems(EMITTER_ZPM, 1)
                    .inputItems(CustomTags.ZPM_CIRCUITS, 4)
                    .inputItems(AEBlocks.PATTERN_PROVIDER.asItem(), 4)
                    .inputItems(AEBlocks.INTERFACE.asItem(), 4)
                    .inputItems(AEItems.SPEED_CARD.asItem(), 8)
                    .inputItems(AEItems.CAPACITY_CARD.asItem(), 4)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputFluids(SolderingAlloy, L * 8)
                    .inputFluids(Lubricant, 4000)
                    .outputItems(UtilAEMachines.EXPANDED_ME_PATTERN_BUFFER)
                    .stationResearch(b -> b.researchStack(GTAEMachines.ME_PATTERN_BUFFER.asStack())
                            .CWUt(16, 32000))
                    .duration(4000).EUt(VA[ZPM]).save(provider);
            ASSEMBLY_LINE_RECIPES.recipeBuilder("me_pattern_buffer_proxy")
                    .inputItems(GTMachines.HULL[ZPM], 1)
                    .inputItems(SENSOR_ZPM, 4)
                    .inputItems(CustomTags.ZPM_CIRCUITS, 2)
                    .inputItems(AEBlocks.QUANTUM_LINK.asItem(), 2)
                    .inputItems(AEBlocks.QUANTUM_RING.asItem(), 4)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputItems(wireFine, UraniumRhodiumDinaquadide, 48)
                    .inputFluids(SolderingAlloy, L * 8)
                    .inputFluids(Lubricant, 2000)
                    .outputItems(UtilAEMachines.EXPANDED_ME_PATTERN_BUFFER_PROXY)
                    .stationResearch(b -> b.researchStack(UtilAEMachines.EXPANDED_ME_PATTERN_BUFFER.asStack())
                            .CWUt(32))
                    .duration(600).EUt(VA[ZPM]).save(provider);
        }

        if (UtilConfig.INSTANCE.features.autoChargersEnabled) {
            for (MachineDefinition autoChargerDef : AUTO_CHARGER_4) {
                if (autoChargerDef == null) {
                    continue;
                }
                var tier = autoChargerDef.getTier();
                VanillaRecipeHelper.addShapedRecipe(provider, true, autoChargerDef.getId(), autoChargerDef.asStack(),
                        "WRW", "WCW", "WRW",
                        'W', WIRE_QUAD.get(tier),
                        'R', ROBOT_ARM.get(tier),
                        'C', GTMachines.CHARGER_4[tier].asStack());
            }
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
                    .addMaterialInfo(true)
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
                .addMaterialInfo(true)
                .save(provider);
    }
}
