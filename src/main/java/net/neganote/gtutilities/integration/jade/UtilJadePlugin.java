package net.neganote.gtutilities.integration.jade;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.gtutilities.integration.jade.provider.EnlargedMEStockingBusInformationProvider;
import net.neganote.gtutilities.integration.jade.provider.ExpandedMEPatternBufferProvider;
import net.neganote.gtutilities.integration.jade.provider.ExpandedMEPatternBufferProxyProvider;
import net.neganote.gtutilities.integration.jade.provider.PTERBInformationProvider;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@SuppressWarnings("unused")
@WailaPlugin
public class UtilJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new PTERBInformationProvider(), BlockEntity.class);
        if (GTCEu.Mods.isAE2Loaded()) {
            registration.registerBlockDataProvider(new EnlargedMEStockingBusInformationProvider(), BlockEntity.class);
            registration.registerBlockDataProvider(new ExpandedMEPatternBufferProvider(), BlockEntity.class);
            registration.registerBlockDataProvider(new ExpandedMEPatternBufferProxyProvider(), BlockEntity.class);
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new PTERBInformationProvider(), Block.class);
        if (GTCEu.Mods.isAE2Loaded()) {
            registration.registerBlockComponent(new EnlargedMEStockingBusInformationProvider(), Block.class);
            registration.registerBlockComponent(new ExpandedMEPatternBufferProvider(), Block.class);
            registration.registerBlockComponent(new ExpandedMEPatternBufferProxyProvider(), Block.class);
        }
    }
}
