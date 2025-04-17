package net.neganote.gtutilities.client.renderer;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

@OnlyIn(Dist.CLIENT)
public class UtilRenderTypes {

    public static final ShaderStateShard WORMHOLE_SHADER_SHARD = new ShaderStateShard(
            () -> UtilShaders.WORMHOLE_SHADER);

    public static RenderType WORMHOLE = RenderType.create("wormhole", DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS, 131072, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(WORMHOLE_SHADER_SHARD)
                    // I would just use RenderStateShard.ADDITIVE_TRANSPARENCY, but that's protected for some reason
                    // So instead I'm just copying it directly
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .createCompositeState(false));
}
