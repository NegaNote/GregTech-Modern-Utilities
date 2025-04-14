package net.neganote.gtutilities.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.common.machine.multiblock.PowerWormholeMachine;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class PTERBFrequencyProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity be = blockAccessor.getBlockEntity();
        if (be instanceof MetaMachineBlockEntity mmbe && mmbe.getMetaMachine() instanceof PowerWormholeMachine) {
            CompoundTag data = blockAccessor.getServerData().getCompound(getUid().toString());
            if (data.contains("frequencyData")) {
                var tag = data.getCompound("frequencyData");
                iTooltip.add(Component.translatable("gtmutils.pterb.current_frequency",
                        FormattingUtil.formatNumbers(tag.getInt("currentFrequency"))));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        CompoundTag data = compoundTag.getCompound(getUid().toString());
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity mmbe &&
                mmbe.getMetaMachine() instanceof PowerWormholeMachine pterb) {
            CompoundTag freqData = new CompoundTag();
            freqData.putInt("currentFrequency", pterb.getFrequency());
            data.put("frequencyData", freqData);
        }
        compoundTag.put(getUid().toString(), data);
    }

    @Override
    public ResourceLocation getUid() {
        return GregTechModernUtilities.id("frequency_info");
    }
}
