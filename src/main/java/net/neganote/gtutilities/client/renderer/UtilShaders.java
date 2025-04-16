package net.neganote.gtutilities.client.renderer;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neganote.gtutilities.GregTechModernUtilities;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import java.io.IOException;

public class UtilShaders {

    public static ShaderInstance WORMHOLE_SHADER;

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    GregTechModernUtilities.id("rendertype_wormhole"), DefaultVertexFormat.POSITION),
                    (shaderInstance -> WORMHOLE_SHADER = shaderInstance));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
