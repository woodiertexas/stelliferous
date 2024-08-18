package com.woodiertexas.planetarium.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
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

import static com.woodiertexas.planetarium.Planetarium.MODID;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	private static final Identifier MERCURY = new Identifier(MODID, "textures/environment/mercury.png");
	
	@Unique
	private static final Identifier VENUS = new Identifier(MODID, "textures/environment/venus.png");
	
	@Unique
	private static final Identifier MARS = new Identifier(MODID, "textures/environment/mars.png");
	
	@Unique
	private static final Identifier JUPITER = new Identifier(MODID, "textures/environment/jupiter.png");
	
	@Unique
	private static final Identifier SATURN = new Identifier(MODID, "textures/environment/saturn.png");
	
	@Unique
	private static final Identifier URANUS = new Identifier(MODID, "textures/environment/uranus.png");
	
	@Unique
	private static final Identifier NEPTUNE = new Identifier(MODID, "textures/environment/neptune.png");
	
	@Unique
	private static final Identifier NORTH_STAR = new Identifier("minecraft", "textures/item/nether_star.png");
	
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
	
	
	/**
	 * Renders a planet texture in the Minecraft skybox
	 * @param planet the planet texture to use
	 * @param planetSize how big the planet is
	 * @param translateX the X position in the sky
	 * @param translateY the Y position in the sky
	 * @param matrices the MatrixStack
	 * @param world the World
	 * @param tickDelta time between ticks
	 * @param planetPhase 
	 * @param brightness how bright the planet is
	 */
	@Unique
	private void renderPlanet(Identifier planet, float planetSize, double translateX, double translateY, MatrixStack matrices, World world, float tickDelta, float planetPhase, float brightness) {
		matrices.push();
		matrices.multiply(Axis.X_POSITIVE.rotationDegrees(world.getSkyAngle(tickDelta) + planetPhase));
		matrices.translate(translateX, translateY, 0.0);
		Matrix4f matrix4f = matrices.peek().getModel();
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			RenderSystem.setShaderTexture(0, planet);
			
			buildBuffer(matrix4f, planetSize);
			fade(tickDelta, brightness);
			
			BufferRenderer.drawWithShader(bufferBuilder.end());
		} else {
			RenderSystem.setShaderTexture(0, 0);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			BufferRenderer.drawWithShader(bufferBuilder.end());
		}
		
		matrices.pop();
	}
	
	/**
	 * Renders a star in the Minecraft skybox.
	 * @param star the star texture to use (Example: nether star)
	 * @param starSize how big the star is
	 * @param matrices the MatrixStack
	 * @param tickDelta time between ticks
	 * @param brightness how bright the star is
	 */
	@Unique
	private void renderStar(Identifier star, float starSize, MatrixStack matrices, float tickDelta, float brightness) {
		matrices.push();
		matrices.multiply(Axis.Y_NEGATIVE.rotationDegrees(20.0f));
		matrices.multiply(Axis.Z_NEGATIVE.rotationDegrees(75.0f));
		Matrix4f matrix4f = matrices.peek().getModel();
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			RenderSystem.setShaderTexture(0, star);
			
			buildBuffer(matrix4f, starSize);
			fade(tickDelta, brightness);
			
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
		
		renderPlanet(MERCURY, 9.5f, -180.0, -160.0, matrices, world, tickDelta, 125.0f, 1.80f);
		renderPlanet(VENUS, 12.5f, -160.0, -140.0, matrices, world, tickDelta, 120.0f, 2.10f);
		renderPlanet(MARS, 9.5f, -35.0, -35.0, matrices, world, tickDelta, 60.0f, 1.80f);
		renderPlanet(JUPITER, 25.0f, 130.0, -80.0, matrices, world, tickDelta, -25.0f, 1.80f);
		renderPlanet(SATURN, 16.2f, 110.0, -100.0, matrices, world, tickDelta, -15.0f, 1.80f);
		renderPlanet(URANUS, 7.0f, -45.0, 0.0, matrices, world, tickDelta, -60.0f, 1.80f);
		renderPlanet(NEPTUNE, 7.0f, -20.0, 0.0, matrices, world, tickDelta, -80.0f, 1.80f);
		
		renderStar(NORTH_STAR, 1.1f, matrices, tickDelta, 1.50f);
	}
}
