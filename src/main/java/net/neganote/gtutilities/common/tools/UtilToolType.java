package net.neganote.gtutilities.common.tools;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.common.data.item.GTToolActions;
import com.gregtechceu.gtceu.common.item.tool.behavior.*;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Spider;
import net.minecraftforge.common.ToolActions;

/**
 * Custom ToolTypes
 */
public final class UtilToolType {

    /**
     * Custom Screwdrivers
     */

    public static final GTToolType SCREWDRIVER_MV = GTToolType.builder("mv_screwdriver")
            .idFormat("mv_%s_screwdriver")
            .toolTag(CustomTags.CRAFTING_SCREWDRIVERS)
            .toolTag(CustomTags.SCREWDRIVERS)
            .toolStats(b -> b.crafting().sneakBypassUse()
                    .attackDamage(-1.0F).attackSpeed(3.0F).durabilityMultiplier(2.0F)
                    .behaviors(new EntityDamageBehavior(3.0F, Spider.class))
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_MV))
            .sound(GTSoundEntries.SCREWDRIVER_TOOL)
            .electric(GTValues.MV)
            .toolClasses(GTToolType.SCREWDRIVER)
            .defaultActions(GTToolActions.DEFAULT_SCREWDRIVER_ACTIONS)
            .build();
    public static final GTToolType SCREWDRIVER_HV = GTToolType.builder("hv_screwdriver")
            .idFormat("hv_%s_screwdriver")
            .toolTag(CustomTags.CRAFTING_SCREWDRIVERS)
            .toolTag(CustomTags.SCREWDRIVERS)
            .toolStats(b -> b.crafting().sneakBypassUse()
                    .attackDamage(-1.0F).attackSpeed(3.0F).durabilityMultiplier(3.0F)
                    .behaviors(new EntityDamageBehavior(3.0F, Spider.class))
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_HV))
            .sound(GTSoundEntries.SCREWDRIVER_TOOL)
            .electric(GTValues.HV)
            .toolClasses(GTToolType.SCREWDRIVER)
            .defaultActions(GTToolActions.DEFAULT_SCREWDRIVER_ACTIONS)
            .build();
    public static final GTToolType SCREWDRIVER_EV = GTToolType.builder("ev_screwdriver")
            .idFormat("ev_%s_screwdriver")
            .toolTag(CustomTags.CRAFTING_SCREWDRIVERS)
            .toolTag(CustomTags.SCREWDRIVERS)
            .toolStats(b -> b.crafting().sneakBypassUse()
                    .attackDamage(-1.0F).attackSpeed(3.0F).durabilityMultiplier(4.0F)
                    .behaviors(new EntityDamageBehavior(3.0F, Spider.class))
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_EV))
            .sound(GTSoundEntries.SCREWDRIVER_TOOL)
            .electric(GTValues.EV)
            .toolClasses(GTToolType.SCREWDRIVER)
            .defaultActions(GTToolActions.DEFAULT_SCREWDRIVER_ACTIONS)
            .build();
    public static final GTToolType SCREWDRIVER_IV = GTToolType.builder("iv_screwdriver")
            .idFormat("iv_%s_screwdriver")
            .toolTag(CustomTags.CRAFTING_SCREWDRIVERS)
            .toolTag(CustomTags.SCREWDRIVERS)
            .toolStats(b -> b.crafting().sneakBypassUse()
                    .attackDamage(-1.0F).attackSpeed(3.0F).durabilityMultiplier(5.0F)
                    .behaviors(new EntityDamageBehavior(3.0F, Spider.class))
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_IV))
            .sound(GTSoundEntries.SCREWDRIVER_TOOL)
            .electric(GTValues.IV)
            .toolClasses(GTToolType.SCREWDRIVER)
            .defaultActions(GTToolActions.DEFAULT_SCREWDRIVER_ACTIONS)
            .build();
    public static final GTToolType SCREWDRIVER_LuV = GTToolType.builder("luv_screwdriver")
            .idFormat("luv_%s_screwdriver")
            .toolTag(CustomTags.CRAFTING_SCREWDRIVERS)
            .toolTag(CustomTags.SCREWDRIVERS)
            .toolStats(b -> b.crafting().sneakBypassUse()
                    .attackDamage(-1.0F).attackSpeed(3.0F).durabilityMultiplier(6.0F)
                    .behaviors(new EntityDamageBehavior(3.0F, Spider.class))
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_LUV))
            .sound(GTSoundEntries.SCREWDRIVER_TOOL)
            .electric(GTValues.LuV)
            .toolClasses(GTToolType.SCREWDRIVER)
            .defaultActions(GTToolActions.DEFAULT_SCREWDRIVER_ACTIONS)
            .build();
    public static final GTToolType SCREWDRIVER_ZPM = GTToolType.builder("zpm_screwdriver")
            .idFormat("zpm_%s_screwdriver")
            .toolTag(CustomTags.CRAFTING_SCREWDRIVERS)
            .toolTag(CustomTags.SCREWDRIVERS)
            .toolStats(b -> b.crafting().sneakBypassUse()
                    .attackDamage(-1.0F).attackSpeed(3.0F).durabilityMultiplier(7.0F)
                    .behaviors(new EntityDamageBehavior(3.0F, Spider.class))
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_ZPM))
            .sound(GTSoundEntries.SCREWDRIVER_TOOL)
            .electric(GTValues.ZPM)
            .toolClasses(GTToolType.SCREWDRIVER)
            .defaultActions(GTToolActions.DEFAULT_SCREWDRIVER_ACTIONS)
            .build();

    /**
     * Custom Chainsaws
     */

    public static final GTToolType CHAINSAW_MV = GTToolType.builder("mv_chainsaw")
            .idFormat("mv_%s_chainsaw")
            .toolTag(ItemTags.AXES)
            .toolTag(CustomTags.CHAINSAWS)
            .harvestTag(BlockTags.MINEABLE_WITH_AXE)
            .harvestTag(BlockTags.SWORD_EFFICIENT)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking()
                    .efficiencyMultiplier(3.0F)
                    .attackDamage(5.0F).attackSpeed(-3.2F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_MV)
                    .behaviors(HarvestIceBehavior.INSTANCE, DisableShieldBehavior.INSTANCE,
                            TreeFellingBehavior.INSTANCE))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.MV)
            .toolClasses(GTToolType.AXE)
            .defaultActions(ToolActions.AXE_DIG, ToolActions.SWORD_DIG, ToolActions.HOE_DIG,
                    GTToolActions.SAW_DIG)
            .build();
    public static final GTToolType CHAINSAW_HV = GTToolType.builder("hv_chainsaw")
            .idFormat("hv_%s_chainsaw")
            .toolTag(ItemTags.AXES)
            .toolTag(CustomTags.CHAINSAWS)
            .harvestTag(BlockTags.MINEABLE_WITH_AXE)
            .harvestTag(BlockTags.SWORD_EFFICIENT)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking()
                    .efficiencyMultiplier(4.0F)
                    .attackDamage(5.0F).attackSpeed(-3.2F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_HV)
                    .behaviors(HarvestIceBehavior.INSTANCE, DisableShieldBehavior.INSTANCE,
                            TreeFellingBehavior.INSTANCE))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.HV)
            .toolClasses(GTToolType.AXE)
            .defaultActions(ToolActions.AXE_DIG, ToolActions.SWORD_DIG, ToolActions.HOE_DIG,
                    GTToolActions.SAW_DIG)
            .build();
    public static final GTToolType CHAINSAW_EV = GTToolType.builder("ev_chainsaw")
            .idFormat("ev_%s_chainsaw")
            .toolTag(ItemTags.AXES)
            .toolTag(CustomTags.CHAINSAWS)
            .harvestTag(BlockTags.MINEABLE_WITH_AXE)
            .harvestTag(BlockTags.SWORD_EFFICIENT)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking()
                    .efficiencyMultiplier(5.0F)
                    .attackDamage(5.0F).attackSpeed(-3.2F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_EV)
                    .behaviors(HarvestIceBehavior.INSTANCE, DisableShieldBehavior.INSTANCE,
                            TreeFellingBehavior.INSTANCE))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.EV)
            .toolClasses(GTToolType.AXE)
            .defaultActions(ToolActions.AXE_DIG, ToolActions.SWORD_DIG, ToolActions.HOE_DIG,
                    GTToolActions.SAW_DIG)
            .build();
    public static final GTToolType CHAINSAW_IV = GTToolType.builder("iv_chainsaw")
            .idFormat("iv_%s_chainsaw")
            .toolTag(ItemTags.AXES)
            .toolTag(CustomTags.CHAINSAWS)
            .harvestTag(BlockTags.MINEABLE_WITH_AXE)
            .harvestTag(BlockTags.SWORD_EFFICIENT)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking()
                    .efficiencyMultiplier(6.0F)
                    .attackDamage(5.0F).attackSpeed(-3.2F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_IV)
                    .behaviors(HarvestIceBehavior.INSTANCE, DisableShieldBehavior.INSTANCE,
                            TreeFellingBehavior.INSTANCE))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.IV)
            .toolClasses(GTToolType.AXE)
            .defaultActions(ToolActions.AXE_DIG, ToolActions.SWORD_DIG, ToolActions.HOE_DIG,
                    GTToolActions.SAW_DIG)
            .build();
    public static final GTToolType CHAINSAW_LuV = GTToolType.builder("luv_chainsaw")
            .idFormat("luv_%s_chainsaw")
            .toolTag(ItemTags.AXES)
            .toolTag(CustomTags.CHAINSAWS)
            .harvestTag(BlockTags.MINEABLE_WITH_AXE)
            .harvestTag(BlockTags.SWORD_EFFICIENT)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking()
                    .efficiencyMultiplier(7.0F)
                    .attackDamage(5.0F).attackSpeed(-3.2F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_LUV)
                    .behaviors(HarvestIceBehavior.INSTANCE, DisableShieldBehavior.INSTANCE,
                            TreeFellingBehavior.INSTANCE))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.LuV)
            .toolClasses(GTToolType.AXE)
            .defaultActions(ToolActions.AXE_DIG, ToolActions.SWORD_DIG, ToolActions.HOE_DIG,
                    GTToolActions.SAW_DIG)
            .build();
    public static final GTToolType CHAINSAW_ZPM = GTToolType.builder("zpm_chainsaw")
            .idFormat("zpm_%s_chainsaw")
            .toolTag(ItemTags.AXES)
            .toolTag(CustomTags.CHAINSAWS)
            .harvestTag(BlockTags.MINEABLE_WITH_AXE)
            .harvestTag(BlockTags.SWORD_EFFICIENT)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking()
                    .efficiencyMultiplier(8.0F)
                    .attackDamage(5.0F).attackSpeed(-3.2F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_ZPM)
                    .behaviors(HarvestIceBehavior.INSTANCE, DisableShieldBehavior.INSTANCE,
                            TreeFellingBehavior.INSTANCE))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.ZPM)
            .toolClasses(GTToolType.AXE)
            .defaultActions(ToolActions.AXE_DIG, ToolActions.SWORD_DIG, ToolActions.HOE_DIG,
                    GTToolActions.SAW_DIG)
            .build();

    /**
     * Custom Drills
     */

    public static final GTToolType DRILL_LUV = GTToolType.builder("luv_drill")
            .idFormat("luv_%s_drill")
            .toolTag(CustomTags.DRILLS)
            .toolTag(ItemTags.PICKAXES)
            .toolTag(ItemTags.SHOVELS)
            .toolTag(ItemTags.HOES)
            .toolTag(ItemTags.CLUSTER_MAX_HARVESTABLES)
            .harvestTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .harvestTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking().aoe(5, 5, 10) // <--- THIS IS THE KEY CHANGE
                    .attackDamage(1.0F).attackSpeed(-3.2F).durabilityMultiplier(8.0F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_LUV)
                    .behaviors(AOEConfigUIBehavior.INSTANCE, TorchPlaceBehavior.INSTANCE))
            .sound(GTSoundEntries.DRILL_TOOL, true)
            .electric(GTValues.LuV)
            .toolClassNames("drill")
            .defaultActions(GTToolActions.DEFAULT_DRILL_ACTIONS)
            .build();
    public static final GTToolType DRILL_ZPM = GTToolType.builder("zpm_drill")
            .idFormat("zpm_%s_drill")
            .toolTag(CustomTags.DRILLS)
            .toolTag(ItemTags.PICKAXES)
            .toolTag(ItemTags.SHOVELS)
            .toolTag(ItemTags.HOES)
            .toolTag(ItemTags.CLUSTER_MAX_HARVESTABLES)
            .harvestTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .harvestTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .harvestTag(BlockTags.MINEABLE_WITH_HOE)
            .toolStats(b -> b.blockBreaking().aoe(6, 6, 12)
                    .attackDamage(1.0F).attackSpeed(-3.2F).durabilityMultiplier(9.0F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_ZPM)
                    .behaviors(AOEConfigUIBehavior.INSTANCE, TorchPlaceBehavior.INSTANCE))
            .sound(GTSoundEntries.DRILL_TOOL, true)
            .electric(GTValues.ZPM)
            .toolClassNames("drill")
            .defaultActions(GTToolActions.DEFAULT_DRILL_ACTIONS)
            .build();

    /**
     * Custom Wrenches
     */

    public static final GTToolType WRENCH_MV = GTToolType.builder("mv_wrench")
            .idFormat("mv_%s_wrench")
            .toolTag(CustomTags.CRAFTING_WRENCHES)
            .toolTag(CustomTags.WRENCHES)
            .toolTag(CustomTags.WRENCH)
            .harvestTag(CustomTags.MINEABLE_WITH_WRENCH)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .efficiencyMultiplier(3.0F)
                    .attackDamage(1.0F).attackSpeed(-2.8F)
                    .behaviors(BlockRotatingBehavior.INSTANCE, new EntityDamageBehavior(3.0F, IronGolem.class),
                            ToolModeSwitchBehavior.INSTANCE)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_MV))
            .sound(GTSoundEntries.WRENCH_TOOL, true)
            .electric(GTValues.MV)
            .toolClasses(GTToolType.WRENCH)
            .defaultActions(GTToolActions.WRENCH_DIG, GTToolActions.WRENCH_DISMANTLE, GTToolActions.WRENCH_CONNECT)
            .build();
    public static final GTToolType WRENCH_EV = GTToolType.builder("ev_wrench")
            .idFormat("ev_%s_wrench")
            .toolTag(CustomTags.CRAFTING_WRENCHES)
            .toolTag(CustomTags.WRENCHES)
            .toolTag(CustomTags.WRENCH)
            .harvestTag(CustomTags.MINEABLE_WITH_WRENCH)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .efficiencyMultiplier(5.0F)
                    .attackDamage(1.0F).attackSpeed(-2.8F)
                    .behaviors(BlockRotatingBehavior.INSTANCE, new EntityDamageBehavior(3.0F, IronGolem.class),
                            ToolModeSwitchBehavior.INSTANCE)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_EV))
            .sound(GTSoundEntries.WRENCH_TOOL, true)
            .electric(GTValues.EV)
            .toolClasses(GTToolType.WRENCH)
            .defaultActions(GTToolActions.WRENCH_DIG, GTToolActions.WRENCH_DISMANTLE, GTToolActions.WRENCH_CONNECT)
            .build();
    public static final GTToolType WRENCH_LuV = GTToolType.builder("luv_wrench")
            .idFormat("luv_%s_wrench")
            .toolTag(CustomTags.CRAFTING_WRENCHES)
            .toolTag(CustomTags.WRENCHES)
            .toolTag(CustomTags.WRENCH)
            .harvestTag(CustomTags.MINEABLE_WITH_WRENCH)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .efficiencyMultiplier(7.0F)
                    .attackDamage(1.0F).attackSpeed(-2.8F)
                    .behaviors(BlockRotatingBehavior.INSTANCE, new EntityDamageBehavior(3.0F, IronGolem.class),
                            ToolModeSwitchBehavior.INSTANCE)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_LUV))
            .sound(GTSoundEntries.WRENCH_TOOL, true)
            .electric(GTValues.LuV)
            .toolClasses(GTToolType.WRENCH)
            .defaultActions(GTToolActions.WRENCH_DIG, GTToolActions.WRENCH_DISMANTLE, GTToolActions.WRENCH_CONNECT)
            .build();
    public static final GTToolType WRENCH_ZPM = GTToolType.builder("zpm_wrench")
            .idFormat("zpm_%s_wrench")
            .toolTag(CustomTags.CRAFTING_WRENCHES)
            .toolTag(CustomTags.WRENCHES)
            .toolTag(CustomTags.WRENCH)
            .harvestTag(CustomTags.MINEABLE_WITH_WRENCH)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .efficiencyMultiplier(8.0F)
                    .attackDamage(1.0F).attackSpeed(-2.8F)
                    .behaviors(BlockRotatingBehavior.INSTANCE, new EntityDamageBehavior(3.0F, IronGolem.class),
                            ToolModeSwitchBehavior.INSTANCE)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_LV))
            .sound(GTSoundEntries.WRENCH_TOOL, true)
            .electric(GTValues.ZPM)
            .toolClasses(GTToolType.WRENCH)
            .defaultActions(GTToolActions.WRENCH_DIG, GTToolActions.WRENCH_DISMANTLE, GTToolActions.WRENCH_CONNECT)
            .build();

    /**
     * Custom Wire cutters
     */

    public static final GTToolType WIRE_CUTTER_MV = GTToolType.builder("mv_wirecutter")
            .idFormat("mv_%s_wire_cutter")
            .toolTag(CustomTags.CRAFTING_WIRE_CUTTERS)
            .toolTag(CustomTags.WIRE_CUTTERS)
            .harvestTag(CustomTags.MINEABLE_WITH_WIRE_CUTTER)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .damagePerCraftingAction(4).attackDamage(-1.0F).attackSpeed(-2.4F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_MV))
            .sound(GTSoundEntries.WIRECUTTER_TOOL, true)
            .electric(GTValues.MV)
            .toolClasses(GTToolType.WIRE_CUTTER)
            .defaultActions(GTToolActions.DEFAULT_WIRE_CUTTER_ACTIONS)
            .build();
    public static final GTToolType WIRE_CUTTER_EV = GTToolType.builder("ev_wirecutter")
            .idFormat("ev_%s_wire_cutter")
            .toolTag(CustomTags.CRAFTING_WIRE_CUTTERS)
            .toolTag(CustomTags.WIRE_CUTTERS)
            .harvestTag(CustomTags.MINEABLE_WITH_WIRE_CUTTER)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .damagePerCraftingAction(4).attackDamage(-1.0F).attackSpeed(-2.4F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_EV))
            .sound(GTSoundEntries.WIRECUTTER_TOOL, true)
            .electric(GTValues.EV)
            .toolClasses(GTToolType.WIRE_CUTTER)
            .defaultActions(GTToolActions.DEFAULT_WIRE_CUTTER_ACTIONS)
            .build();
    public static final GTToolType WIRE_CUTTER_LuV = GTToolType.builder("luv_wirecutter")
            .idFormat("luv_%s_wire_cutter")
            .toolTag(CustomTags.CRAFTING_WIRE_CUTTERS)
            .toolTag(CustomTags.WIRE_CUTTERS)
            .harvestTag(CustomTags.MINEABLE_WITH_WIRE_CUTTER)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .damagePerCraftingAction(4).attackDamage(-1.0F).attackSpeed(-2.4F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_LUV))
            .sound(GTSoundEntries.WIRECUTTER_TOOL, true)
            .electric(GTValues.LuV)
            .toolClasses(GTToolType.WIRE_CUTTER)
            .defaultActions(GTToolActions.DEFAULT_WIRE_CUTTER_ACTIONS)
            .build();
    public static final GTToolType WIRE_CUTTER_ZPM = GTToolType.builder("zpm_wirecutter")
            .idFormat("zpm_%s_wire_cutter")
            .toolTag(CustomTags.CRAFTING_WIRE_CUTTERS)
            .toolTag(CustomTags.WIRE_CUTTERS)
            .harvestTag(CustomTags.MINEABLE_WITH_WIRE_CUTTER)
            .toolStats(b -> b.blockBreaking().crafting().sneakBypassUse()
                    .damagePerCraftingAction(4).attackDamage(-1.0F).attackSpeed(-2.4F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_ZPM))
            .sound(GTSoundEntries.WIRECUTTER_TOOL, true)
            .electric(GTValues.ZPM)
            .toolClasses(GTToolType.WIRE_CUTTER)
            .defaultActions(GTToolActions.DEFAULT_WIRE_CUTTER_ACTIONS)
            .build();

    /**
     * Custom Buzzsaws
     */

    public static final GTToolType BUZZSAW_MV = GTToolType.builder("mv_buzzsaw")
            .toolTag(CustomTags.CRAFTING_SAWS)
            .idFormat("mv_%s_buzzsaw")
            .toolTag(CustomTags.SAWS)
            .toolTag(CustomTags.BUZZSAWS)
            .toolStats(b -> b.crafting().attackDamage(1.5F).attackSpeed(-3.2F).durabilityMultiplier(2.0F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_MV))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.MV)
            .toolClasses(GTToolType.SAW)
            .build();
    public static final GTToolType BUZZSAW_HV = GTToolType.builder("hv_buzzsaw")
            .toolTag(CustomTags.CRAFTING_SAWS)
            .idFormat("hv_%s_buzzsaw")
            .toolTag(CustomTags.SAWS)
            .toolTag(CustomTags.BUZZSAWS)
            .toolStats(b -> b.crafting().attackDamage(1.5F).attackSpeed(-3.2F).durabilityMultiplier(3.0F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_HV))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.HV)
            .toolClasses(GTToolType.SAW)
            .build();
    public static final GTToolType BUZZSAW_EV = GTToolType.builder("ev_buzzsaw")
            .toolTag(CustomTags.CRAFTING_SAWS)
            .idFormat("ev_%s_buzzsaw")
            .toolTag(CustomTags.SAWS)
            .toolTag(CustomTags.BUZZSAWS)
            .toolStats(b -> b.crafting().attackDamage(1.5F).attackSpeed(-3.2F).durabilityMultiplier(4.0F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_EV))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.EV)
            .toolClasses(GTToolType.SAW)
            .build();
    public static final GTToolType BUZZSAW_IV = GTToolType.builder("iv_buzzsaw")
            .toolTag(CustomTags.CRAFTING_SAWS)
            .idFormat("iv_%s_buzzsaw")
            .toolTag(CustomTags.SAWS)
            .toolTag(CustomTags.BUZZSAWS)
            .toolStats(b -> b.crafting().attackDamage(1.5F).attackSpeed(-3.2F).durabilityMultiplier(5.0F)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_IV))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.IV)
            .toolClasses(GTToolType.SAW)
            .build();
    public static final GTToolType BUZZSAW_LuV = GTToolType.builder("luv_buzzsaw")
            .toolTag(CustomTags.CRAFTING_SAWS)
            .idFormat("luv_%s_buzzsaw")
            .toolTag(CustomTags.SAWS)
            .toolTag(CustomTags.BUZZSAWS)
            .toolStats(b -> b.crafting().attackDamage(1.5F).attackSpeed(-3.2F).durabilityMultiplier(6.0F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_LUV))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.LuV)
            .toolClasses(GTToolType.SAW)
            .build();
    public static final GTToolType BUZZSAW_ZPM = GTToolType.builder("zpm_buzzsaw")
            .toolTag(CustomTags.CRAFTING_SAWS)
            .idFormat("zpm_%s_buzzsaw")
            .toolTag(CustomTags.SAWS)
            .toolTag(CustomTags.BUZZSAWS)
            .toolStats(b -> b.crafting().attackDamage(1.5F).attackSpeed(-3.2F).durabilityMultiplier(7.0F)
                    .brokenStack(UtilToolHelper.SUPPLY_POWER_UNIT_ZPM))
            .sound(GTSoundEntries.CHAINSAW_TOOL, true)
            .electric(GTValues.ZPM)
            .toolClasses(GTToolType.SAW)
            .build();
}
