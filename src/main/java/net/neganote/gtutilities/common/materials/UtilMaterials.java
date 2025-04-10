package net.neganote.gtutilities.common.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.FluidState;

import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.config.UtilConfig;

public class UtilMaterials {

    public static Material QuantumCoolant;

    public static void register() {
        if (UtilConfig.coolantEnabled()) {
            QuantumCoolant = new Material.Builder(GregTechModernUtilities.id("quantum_coolant"))
                    .liquid(new FluidBuilder().state(FluidState.LIQUID).temperature(0))
                    .color(0x0040ef).secondaryColor(0x0030cf)
                    .buildAndRegister();
        }
    }
}
