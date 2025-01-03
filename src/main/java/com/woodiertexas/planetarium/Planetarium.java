package com.woodiertexas.planetarium;

import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;

public class Planetarium {
	public static final Logger LOGGER = LoggerFactory.getLogger("Planetarium");
	public static final String MOD_ID = "planetarium";
	
	public static void renderPlanet(MatrixStack matrices, Identifier id, PlanetInfo planetInfo, float tickDelta, ClientWorld world) {
		matrices.push();
		
		// First, line planet up where the sun is in the sky
		matrices.multiply(Axis.Y_NEGATIVE.rotationDegrees(0.0F));
		
		// Second, change the orbital tilt of the planet
		matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(planetInfo.tilt())); // tilt
		
		// Third, set the angle of the planet in the sky and offset it.
		matrices.multiply(Axis.X_NEGATIVE.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F + planetInfo.procession())); // procession
		
		matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(planetInfo.inclination())); // inclination
		
		// Finally, change the rotation of the planet texture
		matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(planetInfo.texture_rotation()));
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			Matrix4f matrix4f = matrices.peek().getModel();
			RenderSystem.setShaderTexture(0, planetInfo.getTexture(id));
			
			final BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			float finalPlanetSize = planetInfo.size() * 0.75F;
			bufferBuilder.vertex(matrix4f, -finalPlanetSize, -100.0F, finalPlanetSize).uv(0.0F, 0.0F).next();
			bufferBuilder.vertex(matrix4f, finalPlanetSize, -100.0F, finalPlanetSize).uv(1.0F, 0.0F).next(); // u: 1.0
			bufferBuilder.vertex(matrix4f, finalPlanetSize, -100.0F, -finalPlanetSize).uv(1.0F, 1.0F).next(); // u: 1.0, v: 1.0
			bufferBuilder.vertex(matrix4f, -finalPlanetSize, -100.0F, -finalPlanetSize).uv(0.0F, 1.0F).next(); // v: 1.0
			
			
			float rainGradient = 1.0f - world.getRainGradient(tickDelta);
			float transparency = 2 * world.getStarBrightness(tickDelta) * rainGradient;
			if (transparency > 0.0f) {
				RenderSystem.setShaderColor(transparency, transparency, transparency, transparency);
			}
			
			BufferRenderer.drawWithShader(bufferBuilder.end());
		}
		
		matrices.pop();
	}
}
