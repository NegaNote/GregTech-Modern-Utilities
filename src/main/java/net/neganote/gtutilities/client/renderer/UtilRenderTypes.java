package net.neganote.gtutilities.client.renderer;

import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
                    .createCompositeState(false));
}
