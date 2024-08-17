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

	/**
	 * @param matrices   The matrix stack for rendering.
	 * @param id         The id for the planet. (Ex: Mercury, Venus, Earth, and so on)
	 * @param planetInfo The planet data.
	 * @param tickDelta  Time between ticks.
	 * @param world      The client world to render in.
	 */

	public static void renderPlanet(MatrixStack matrices, Identifier id, PlanetInfo planetInfo, float tickDelta, ClientWorld world) {
		matrices.push();
		
		// First, line planet up where the sun is in the sky
		matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(90.0F));
		
		// Second, change the orbital tilt of the planet
		matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(planetInfo.tilt())); // tilt
		
		// Third, set the angle of the planet in the sky and offset it.
		matrices.rotate(Axis.X_POSITIVE.rotationDegrees(-world.getSkyAngle(tickDelta) * 360.0F + planetInfo.procession())); // procession
		
		matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(planetInfo.inclination())); // inclination
		
		// Finally, change the rotation of the planet texture
		matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(planetInfo.texture_rotation()));
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			Matrix4f matrix4f = matrices.peek().getModel();
			RenderSystem.setShaderTexture(0, planetInfo.getTexture(id));
			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.xyz(matrix4f, -planetInfo.size(), 99.0F, -planetInfo.size()).uv0(0.0F, 0.0F);
			bufferBuilder.xyz(matrix4f, planetInfo.size(), 99.0F, -planetInfo.size()).uv0(1.0F, 0.0F); // u: 1.0
			bufferBuilder.xyz(matrix4f, planetInfo.size(), 99.0F, planetInfo.size()).uv0(1.0F, 1.0F); // u: 1.0, v: 1.0
			bufferBuilder.xyz(matrix4f, -planetInfo.size(), 99.0F, planetInfo.size()).uv0(0.0F, 1.0F); // v: 1.0
			
			float rainGradient = 1.0f - world.getRainGradient(tickDelta);
			float transparency = 2 * world.getStarBrightness(tickDelta) * rainGradient;
			if (transparency > 0.0f) {
				RenderSystem.setShaderColor(transparency, transparency, transparency, transparency);
			}
			
			BufferRenderer.drawWithShader(bufferBuilder.endOrThrow());
		}
		matrices.pop();
	}
}
