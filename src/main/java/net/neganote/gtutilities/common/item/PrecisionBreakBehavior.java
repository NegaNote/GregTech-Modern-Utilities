package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class PrecisionBreakBehavior implements IInteractionItem {

    public int tier;

    public PrecisionBreakBehavior(int tier) {
        this.tier = tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide && player.isShiftKeyDown()) {
            var breaker = (OmniBreakerItem) item;
            breaker.mode += 1;
            if (breaker.mode > 3) {
                breaker.mode = 0;
            }
            player.displayClientMessage(MutableComponent.create(new LiteralContents(String.format("Mode: %d", breaker.mode))), true);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        return IInteractionItem.super.use(item, level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            BlockState blockState = level.getBlockState(pos);

            var itemStack = context.getItemInHand();
            var breaker = (OmniBreakerItem) itemStack.getItem();
            var meta = MetaMachine.getMachine(level, pos);
            if (breaker.mode > 0 && meta != null) {
                Set<GTToolType> set = new HashSet<>();
                set.add(breaker.getToolType());
                meta.onToolClick(set, itemStack, context);
                return InteractionResult.SUCCESS;
            }

            float hardness = blockState.getBlock().defaultDestroyTime();
            if (!blockState.canHarvestBlock(level, pos, context.getPlayer()) || hardness < 0.0f) {
                return InteractionResult.PASS;
            }

            int unbreaking = context.getItemInHand().getItem().getAllEnchantments(context.getItemInHand())
                    .getOrDefault(Enchantments.UNBREAKING, 0);
            double chance = 1.0f / (unbreaking + 1);
            double rand = Math.random();

            var electricItem = GTCapabilityHelper.getElectricItem(context.getItemInHand());

            if (electricItem != null) {
                if (electricItem.getCharge() >= GTValues.VEX[tier]) {
                    if (rand >= chance) {
                        electricItem.discharge(GTValues.VEX[tier], tier, true, false, false);
                    }
                } else {
                    return InteractionResult.PASS;
                }
            }

            level.destroyBlock(pos, true);
        }
        return InteractionResult.SUCCESS;
    }
}
