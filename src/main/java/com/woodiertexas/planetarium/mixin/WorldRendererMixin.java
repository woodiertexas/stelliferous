package com.woodiertexas.planetarium.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.woodiertexas.planetarium.Planetarium;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	private static final Identifier MERCURY = new Identifier(Planetarium.MOD_ID, "textures/environment/mercury.png");
	
	@Unique
	private final BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
	
	@Unique
	private void fade(float tickDelta, float brightness) {
		float rainGradient = 1.0f - world.getRainGradient(tickDelta);
		float transparency = this.world.getStarBrightness(tickDelta) * rainGradient * brightness;
		if (transparency > 0.0f) {
			RenderSystem.setShaderColor(transparency, transparency, transparency, transparency);
		}
	}
	
	private void buildBuffer(Matrix4f matrix4f, float size) {
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, -size, -100.0F, size).uv(0.0F, 0.0F).next();
		bufferBuilder.vertex(matrix4f, size, -100.0F, size).uv(1.0F, 0.0F).next(); // u: 1.0
		bufferBuilder.vertex(matrix4f, size, -100.0F, -size).uv(1.0F, 1.0F).next(); // u: 1.0, v: 1.0
		bufferBuilder.vertex(matrix4f, -size, -100.0F, -size).uv(0.0F, 1.0F).next(); // v: 1.0
	}
	
	@Unique
	private void renderPlanet(Identifier planet, float tilt, float procession, float inclination, float rotation, float planetSize, MatrixStack matrices, World world, float tickDelta) {
		matrices.push();
		
		// First, line planet up where the sun is in the sky
		matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(90.0F));
		
		// Second, change the orbital tilt of the planet
		matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(tilt)); // tilt
		
		// Third, set the angle of the planet in the sky and offset it.
		matrices.multiply(Axis.X_POSITIVE.rotationDegrees(-world.getSkyAngle(tickDelta) * 360.0F + procession)); // procession
		
		matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(inclination)); // inclination
		
		// Finally, change the rotation of the planet texture
		matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(rotation));
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			Matrix4f matrix4f = matrices.peek().getModel();
			RenderSystem.setShaderTexture(0, planet);
			
			buildBuffer(matrix4f, planetSize);
			fade(tickDelta, 1.0F);
			
			BufferRenderer.drawWithShader(bufferBuilder.end());
		} else {
			RenderSystem.setShaderTexture(0, 0);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			BufferRenderer.drawWithShader(bufferBuilder.end());
		}
		
		matrices.pop();
	}
	
	
	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"))
	private void renderPlanets(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean skipRendering, Runnable preRender, CallbackInfo ci) {
		assert world != null;
		
		renderPlanet(MERCURY, 0F, 35.0F, -35.0F, 0.03F, 5.00F, matrices, world, tickDelta);
	}
}
