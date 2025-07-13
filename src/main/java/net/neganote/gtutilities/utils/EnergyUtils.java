package net.neganote.gtutilities.utils;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;

import java.util.ArrayList;
import java.util.List;

public class EnergyUtils {

    public static EnergyContainerList getEnergyListFromMultiParts(List<IMultiPart> parts) {
        List<IEnergyContainer> energyContainerList = new ArrayList<>();
        for (IMultiPart part : parts) {
            if (part instanceof EnergyHatchPartMachine hatch) {
                energyContainerList.add(hatch.energyContainer);
            }
            if (part instanceof LaserHatchPartMachine hatch) {
                // unfortunately the laser hatch's buffer is private, so I have to do this instead
                for (var handlerList : hatch.getRecipeHandlers()) {
                    var containers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                            .filter(IEnergyContainer.class::isInstance)
                            .map(IEnergyContainer.class::cast)
                            .toList();
                    energyContainerList.addAll(containers);
                }
            }
        }

        return new EnergyContainerList(energyContainerList);
    }
}
