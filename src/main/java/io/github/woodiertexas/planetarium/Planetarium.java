package io.github.woodiertexas.planetarium;

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
	public static final String MODID = "planetarium";

	public static void renderPlanet(MatrixStack matrices, Identifier planet, float procession, float tilt, float rotation, float size, float tickDelta, ClientWorld world) {
		matrices.push();

		// First, line planet up where the sun is in the sky
		matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(90.0F));

		// Second, change the orbital tilt of the planet
		matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(tilt));
		
		// Third, set the angle of the planet in the sky and offset it.
		matrices.rotate(Axis.X_POSITIVE.rotationDegrees(-world.getSkyAngle(tickDelta) * 360.0F + procession));

		// Finally, change the rotation of the planet texture
		matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(rotation));

		Matrix4f matrix4f = matrices.peek().getModel();
		RenderSystem.setShaderTexture(0, planet);
		BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.xyz(matrix4f, -size, 99.0F, -size).uv0(0.0F, 0.0F);
		bufferBuilder.xyz(matrix4f, size, 99.0F, -size).uv0(1.0F, 0.0F); // u: 1.0
		bufferBuilder.xyz(matrix4f, size, 99.0F, size).uv0(1.0F, 1.0F); // u: 1.0, v: 1.0
		bufferBuilder.xyz(matrix4f, -size, 99.0F, size).uv0(0.0F, 1.0F); // v: 1.0
		BufferRenderer.drawWithShader(bufferBuilder.endOrThrow());
		
		matrices.pop();
	}
}
