package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neganote.gtutilities.GregTechModernUtilities;

public class PrecisionBreakBehavior implements IInteractionItem {

    public int tier;

    public PrecisionBreakBehavior(int tier) {
        this.tier = tier;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            BlockState blockState = level.getBlockState(pos);
            if (!blockState.canHarvestBlock(level, pos, context.getPlayer())) {
                return InteractionResult.PASS;
            }

            var electricItem = GTCapabilityHelper.getElectricItem(context.getItemInHand());

            if (electricItem != null) {
                if (electricItem.discharge(GTValues.VEX[tier], tier, true, false, true) == GTValues.VEX[tier]) {
                    // Only discharge if possible to discharge the full amount
                    electricItem.discharge(GTValues.VEX[tier], tier, true, false, false);
                } else {
                    return InteractionResult.PASS;
                }
            }

            level.destroyBlock(pos, true, context.getPlayer());
        }
        return InteractionResult.SUCCESS;
    }
}
