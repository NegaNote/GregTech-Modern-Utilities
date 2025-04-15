package net.neganote.gtutilities.client.renderer.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.common.machine.multiblock.PTERBMachine;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Consumer;

public class PTERBRenderer extends WorkableCasingMachineRenderer {

    public static final ResourceLocation TEXTURE = GTCEu.id("block/casings/hpca/high_power_casing");
    // TODO: replace this texture with custom casing
    public static final ResourceLocation OVERLAY_MODEL_TEXTURES = GTCEu.id("block/multiblock/data_bank");

    public static final ResourceLocation SPHERE = GregTechModernUtilities.id("render/sphere");

    public PTERBRenderer() {
        super(TEXTURE, OVERLAY_MODEL_TEXTURES);
    }

    @Override
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof MetaMachineBlockEntity mmbe && mmbe.getMetaMachine() instanceof PTERBMachine pterb &&
                pterb.isFormed() && pterb.isActive()) {
            var level = pterb.getLevel();
            var upwards = pterb.getUpwardsFacing();
            float tick = level.getGameTime() + partialTicks;

            renderWormhole(stack, buffer, upwards, tick, combinedLight, combinedOverlay);
        }
    }

    private void renderWormhole(PoseStack stack, MultiBufferSource buffer, Direction upwards, float tick,
                                int combinedLight, int combinedOverlay) {
        // TODO: do the thing
    }

    @Override
    public boolean hasTESR(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean isGlobalRenderer(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public void onAdditionalModel(Consumer<ResourceLocation> registry) {
        super.onAdditionalModel(registry);
        registry.accept(SPHERE);
    }
}
