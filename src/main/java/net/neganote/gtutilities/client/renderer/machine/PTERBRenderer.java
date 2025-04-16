package net.neganote.gtutilities.client.renderer.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.gtutilities.GregTechModernUtilities;
import net.neganote.gtutilities.client.renderer.UtilRenderTypes;
import net.neganote.gtutilities.common.machine.multiblock.PTERBMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.List;
import java.util.function.Consumer;

public class PTERBRenderer extends WorkableCasingMachineRenderer {

    public static final ResourceLocation TEXTURE = GTCEu.id("block/casings/hpca/high_power_casing");
    // TODO: replace this texture with custom casing
    public static final ResourceLocation OVERLAY_MODEL_TEXTURES = GTCEu.id("block/multiblock/data_bank");

    public static final ResourceLocation CUBE = GregTechModernUtilities.id("render/cube");

    public PTERBRenderer() {
        super(TEXTURE, OVERLAY_MODEL_TEXTURES);
    }

    @Override
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof MetaMachineBlockEntity mmbe && mmbe.getMetaMachine() instanceof PTERBMachine pterb &&
                pterb.isFormed() && pterb.isActive()) {
            var upwards = pterb.getUpwardsFacing();

            renderWormhole(stack, buffer, upwards, combinedLight, combinedOverlay);
        }
    }

    @SuppressWarnings("deprecation")
    private void renderWormhole(PoseStack stack, MultiBufferSource bufferSource, Direction upwards,
                                int combinedLight, int combinedOverlay) {
        stack.pushPose();
        var modelManager = Minecraft.getInstance().getModelManager();
        BakedModel sphere = modelManager.getModel(CUBE);
        Vec3i movement = upwards.getNormal();
        stack.translate(movement.getX() * 8 + 0.5f, movement.getY() * 8 + 0.5f, movement.getZ() * 8 + 0.5f);
        PoseStack.Pose pose = stack.last();
        VertexConsumer consumer = bufferSource.getBuffer(UtilRenderTypes.WORMHOLE);
        List<BakedQuad> quads = sphere.getQuads(null, null, GTValues.RNG);
        for (BakedQuad quad : quads) {
            consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, combinedLight, combinedOverlay);
        }
        stack.popPose();
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
        registry.accept(CUBE);
    }
}
