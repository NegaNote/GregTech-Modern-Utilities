package net.neganote.gtutilities.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.common.machine.multiblock.PTERBMachine;
import net.neganote.gtutilities.common.materials.UtilMaterials;
import net.neganote.gtutilities.config.UtilConfig;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class PTERBInformationProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity be = blockAccessor.getBlockEntity();
        if (be instanceof MetaMachineBlockEntity mmbe && mmbe.getMetaMachine() instanceof PTERBMachine pterb) {
            CompoundTag data = blockAccessor.getServerData().getCompound(getUid().toString());
            if (data.contains("pterbData")) {
                var tag = data.getCompound("pterbData");
                iTooltip.add(Component.translatable("gtmutils.pterb.current_frequency",
                        FormattingUtil.formatNumbers(tag.getInt("currentFrequency"))));
                if (tag.contains("coolantDrain") && UtilConfig.coolantEnabled() && pterb.isFormed()) {
                    iTooltip.add(Component.translatable("gtmutils.multiblock.pterb_machine.coolant_usage",
                            FormattingUtil.formatNumbers(tag.getInt("coolantDrain")),
                            UtilMaterials.QuantumCoolant.getLocalizedName()));
                }
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        CompoundTag data = compoundTag.getCompound(getUid().toString());
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity mmbe &&
                mmbe.getMetaMachine() instanceof PTERBMachine pterb) {
            CompoundTag pterbData = new CompoundTag();
            pterbData.putInt("currentFrequency", pterb.getFrequency());
            if (UtilConfig.coolantEnabled() && pterb.isFormed()) {
                int coolantDrain = pterb.getCoolantDrain();
                pterbData.putInt("coolantDrain", coolantDrain);
            }
            data.put("pterbData", pterbData);
        }
        compoundTag.put(getUid().toString(), data);
    }

    @Override
    public ResourceLocation getUid() {
        return GregTechModernUtilities.id("pterb_info");
    }
}
