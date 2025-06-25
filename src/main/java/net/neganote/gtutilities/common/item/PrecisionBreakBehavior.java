package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IToolable;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

public class PrecisionBreakBehavior implements IInteractionItem {

    public int tier;

    public PrecisionBreakBehavior(int tier) {
        this.tier = tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            var stack = player.getItemInHand(usedHand);
            var compound = stack.getOrCreateTag();

            if (!compound.contains("OmniModeTag")) {
                compound.putInt("OmniModeTag", 0);
            }

            var currentMode = compound.getInt("OmniModeTag");
            currentMode += 1;
            if (currentMode > 4) {
                currentMode = 0;
            }

            compound.putInt("OmniModeTag", currentMode);
            stack.setTag(compound);

            player.displayClientMessage(
                    Component.translatable("tooltip.omnibreaker.tool_mode", OmniBreakerItem.getToolMode(currentMode))
                            .withStyle(ChatFormatting.WHITE),
                    true);
        }

        return IInteractionItem.super.use(item, level, player, usedHand);
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

        CompoundTag tag = itemStack.getTag();
        assert tag != null;
        byte omniModeTag = tag.getByte("OmniModeTag");

        int unbreaking = context.getItemInHand().getItem().getAllEnchantments(itemStack)
                .getOrDefault(Enchantments.UNBREAKING, 0);
        double chance = 1.0 / (unbreaking + 1);
        double rand = Math.random();

        var electricItem = GTCapabilityHelper.getElectricItem(context.getItemInHand());

        assert electricItem != null;

        if (electricItem.getCharge() < GTValues.VEX[tier]) {
            return InteractionResult.PASS;
        }

        if (omniModeTag > 0) {
            var be = level.getBlockEntity(context.getClickedPos());
            var item = (OmniBreakerItem) itemStack.getItem();
            var set = item.getToolClasses(itemStack);

            InteractionResult toolResult;
            if (be instanceof IToolable toolable) {
                toolResult = toolable.onToolClick(set, itemStack, context).getSecond();
            } else if (be instanceof MetaMachineBlockEntity mmbe) {
                toolResult = mmbe.getMetaMachine().onToolClick(set, itemStack, context).getSecond();
            } else {
                return InteractionResult.PASS;
            }

            if (toolResult.consumesAction() && !level.isClientSide() && rand <= chance) {
                electricItem.discharge(GTValues.V[tier], tier, true, false, false);
            }
            return toolResult;
        } else if (!level.isClientSide()) {
            level.destroyBlock(pos, true);
            electricItem.discharge(GTValues.V[tier], tier, true, false, false);
        }

        return InteractionResult.SUCCESS;
    }
}
