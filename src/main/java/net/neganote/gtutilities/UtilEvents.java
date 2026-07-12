package net.neganote.gtutilities;

import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.neganote.gtutilities.common.machine.multiblock.WEBHubMachine;

@Mod.EventBusSubscriber(modid = GregTechModernUtilities.MOD_ID)
public class UtilEvents {

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        WEBHubMachine.ENERGY_INPUTS.clear();
        WEBHubMachine.ENERGY_OUTPUTS.clear();
    }
}
