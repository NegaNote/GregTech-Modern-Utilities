package net.neganote.gtutilities.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OmniBreakerItem extends ComponentItem {

    protected int tier;

    protected OmniBreakerItem(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public static OmniBreakerItem create(Item.Properties properties, int tier) {
        return new OmniBreakerItem(properties, tier);
    }

    // Should make it so it can harvest anything
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        var electricItem = Objects.requireNonNull(GTCapabilityHelper.getElectricItem(stack));

        // Only work if it has enough charge
        return electricItem.getCharge() >= GTValues.VEX[tier];
    }

    // Effectively insta-mines
    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        var electricItem = Objects.requireNonNull(GTCapabilityHelper.getElectricItem(pStack));

        // Only work if it has enough charge
        if (electricItem.getCharge() >= GTValues.VEX[tier]) {
            return 100_000.0F;
        } else {
            return 0.0F;
        }
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull BlockState pState,
                             @NotNull BlockPos pPos, @NotNull LivingEntity pMiningEntity) {
        super.mineBlock(pStack, pLevel, pState, pPos, pMiningEntity);

        var electricItem = Objects.requireNonNull(GTCapabilityHelper.getElectricItem(pStack));

        if (electricItem.getCharge() >= GTValues.VEX[tier]) {
            // Only discharge if possible to discharge the full amount
            electricItem.discharge(GTValues.VEX[tier], tier, true, false, false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        var electricItem = Objects.requireNonNull(GTCapabilityHelper.getElectricItem(stack));
        long charge = electricItem.getCharge();
        long maxCharge = electricItem.getMaxCharge();
        tooltipComponents.add(Component.translatable("tooltip.omnibreaker.charge_status",
                Component.translatable(FormattingUtil.formatNumbers(charge)).withStyle(ChatFormatting.GREEN),
                Component.translatable(FormattingUtil.formatNumbers(maxCharge)).withStyle(ChatFormatting.YELLOW)));
        tooltipComponents
                .add(Component.translatable("tooltip.omnibreaker.can_break_anything").withStyle(ChatFormatting.GRAY));
        tooltipComponents
                .add(Component.translatable("tooltip.omnibreaker.right_click_function").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
