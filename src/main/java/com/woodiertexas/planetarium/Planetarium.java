package com.woodiertexas.planetarium;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Planetarium {
	public static final String MOD_ID = "planetarium";
	
	public static void renderPlanet(MatrixStack matrices, Identifier planetId, float procession, float offset, float textureRotation, float size, float tickDelta, ClientWorld world) {
		matrices.push();
		
		matrices.multiply(Axis.X_POSITIVE.rotationDegrees(world.getSkyAngle(tickDelta) + procession));
		
		matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(offset));
		
		matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(textureRotation));
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			Matrix4f matrix4f = matrices.peek().getModel();
			RenderSystem.setShaderTexture(0, planetId);
			
			final BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(matrix4f, -size, -100.00F, size).uv(0.00F, 0.00F).next();
			bufferBuilder.vertex(matrix4f, size, -100.00F, size).uv(1.00F, 0.00F).next(); // u: 1.0
			bufferBuilder.vertex(matrix4f, size, -100.00F, -size).uv(1.00F, 1.00F).next(); // u: 1.0, v: 1.0
			bufferBuilder.vertex(matrix4f, -size, -100.00F, -size).uv(0.00F, 1.00F).next(); // v: 1.0
			
			
			float rainGradient = 1.00F - world.getRainGradient(tickDelta);
			float transparency = 2.00F * world.getStarBrightness(tickDelta) * rainGradient;
			if (transparency > 0.00F) {
				RenderSystem.setShaderColor(transparency, transparency, transparency, transparency);
			}
			
			BufferRenderer.drawWithShader(bufferBuilder.end());
		}
		
		matrices.pop();
	}
}
