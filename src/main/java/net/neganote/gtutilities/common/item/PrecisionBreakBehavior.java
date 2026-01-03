package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

import static net.minecraft.world.level.block.Block.getDrops;

public class PrecisionBreakBehavior implements IInteractionItem {

    public int tier;

    public PrecisionBreakBehavior(int tier) {
        this.tier = tier;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);

        var itemStack = context.getItemInHand();

        float hardness = blockState.getBlock().defaultDestroyTime();
        if (!blockState.canHarvestBlock(level, pos, context.getPlayer()) || hardness < 0.0f) {
            return InteractionResult.PASS;
        }

        int unbreaking = context.getItemInHand().getItem().getAllEnchantments(itemStack)
                .getOrDefault(Enchantments.UNBREAKING, 0);
        double chance = 1.0 / (unbreaking + 1);
        double rand = Math.random();

        var electricItem = GTCapabilityHelper.getElectricItem(context.getItemInHand());

        assert electricItem != null;

        if (electricItem.getCharge() < GTValues.VEX[tier]) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            List<ItemStack> drops = new ObjectArrayList<>(
                    getDrops(blockState, (ServerLevel) level, pos, level.getBlockEntity(pos), null, itemStack));
            var player = context.getPlayer();
            assert player != null;
            level.destroyBlock(pos, false);
            drops.removeIf(player::addItem);
            for (var drop : drops) {
                var center = pos.getCenter();
                var entity = new ItemEntity(level, center.x(), center.y(), center.z(), drop);
                level.addFreshEntity(entity);
            }
            if (rand <= chance) {
                electricItem.discharge(GTValues.V[tier], tier, true, false, false);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
