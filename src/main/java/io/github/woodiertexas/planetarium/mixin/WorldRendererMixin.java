package io.github.woodiertexas.planetarium.mixin;

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

import static io.github.woodiertexas.planetarium.Planetarium.MODID;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	/*
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	private static final Identifier MERCURY = Identifier.of(MODID, "textures/environment/mercury.png");
	
	@Unique
	private static final Identifier VENUS = Identifier.of(MODID, "textures/environment/venus.png");
	
	@Unique
	private static final Identifier MARS = Identifier.of(MODID, "textures/environment/mars.png");
	
	@Unique
	private static final Identifier JUPITER = Identifier.of(MODID, "textures/environment/jupiter.png");
	
	@Unique
	private static final Identifier SATURN = Identifier.of(MODID, "textures/environment/saturn.png");
	
	@Unique
	private static final Identifier URANUS = Identifier.of(MODID, "textures/environment/uranus.png");
	
	@Unique
	private static final Identifier NEPTUNE = Identifier.of(MODID, "textures/environment/neptune.png");
	
	@Unique
	private static final Identifier NORTH_STAR = Identifier.of("minecraft", "textures/item/nether_star.png");
	
	@Unique
	Tessellator tessellator = Tessellator.getInstance();
	
	@Unique
	private final BufferBuilder bufferBuilder = tessellator.method_60827(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
	
	@Unique
	private void fade(float tickDelta, float brightness) {
		float rainGradient = 1.0f - world.getRainGradient(tickDelta);
		float transparency = this.world.getStarBrightness(tickDelta) * rainGradient * brightness;
		if (transparency > 0.0f) {
			RenderSystem.setShaderColor(transparency, transparency, transparency, transparency);
		}
	}
	
	private void buildBuffer(Matrix4f matrix4f, float size) {
		tessellator.method_60827(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.xyz(matrix4f, -size, -100.0F, size).uv0(0.0F, 0.0F);
		bufferBuilder.xyz(matrix4f, size, -100.0F, size).uv0(1.0F, 0.0F); // u: 1.0
		bufferBuilder.xyz(matrix4f, size, -100.0F, -size).uv0(1.0F, 1.0F); // u: 1.0, v: 1.0
		bufferBuilder.xyz(matrix4f, -size, -100.0F, -size).uv0(0.0F, 1.0F); // v: 1.0
	}
	
	

	@Unique
	private void renderPlanet(Identifier planet, float planetSize, double translateX, double translateY, MatrixStack matrices, World world, float tickDelta, float planetPhase, float brightness) {
		matrices.push();
		
		matrices.rotate(Axis.X_POSITIVE.rotationDegrees(world.getSkyAngle(tickDelta) + planetPhase));
		matrices.translate(translateX, translateY, 0.0);
		Matrix4f matrix4f = matrices.peek().getModel();
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			RenderSystem.setShaderTexture(0, planet);
			
			buildBuffer(matrix4f, planetSize);
			fade(tickDelta, brightness);
			
			BufferRenderer.drawWithShader(bufferBuilder.method_60800());
		} else {
			RenderSystem.setShaderTexture(0, 0);
			tessellator.method_60827(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			BufferRenderer.drawWithShader(bufferBuilder.method_60800());
		}
		
		matrices.pop();
	}
	
	
	@Unique
	private void renderStar(Identifier star, float starSize, MatrixStack matrices, float tickDelta, float brightness) {
		matrices.push();
		matrices.rotate(Axis.Y_NEGATIVE.rotationDegrees(20.0f));
		matrices.rotate(Axis.Z_NEGATIVE.rotationDegrees(75.0f));
		Matrix4f matrix4f = matrices.peek().getModel();
		
		if (world.getTimeOfDay() % 24000L >= 11800) {
			RenderSystem.setShaderTexture(0, star);
			
			buildBuffer(matrix4f, starSize);
			fade(tickDelta, brightness);
			
			BufferRenderer.drawWithShader(bufferBuilder.method_60800());
		} else {
			RenderSystem.setShaderTexture(0, 0);
			tessellator.method_60827(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			BufferRenderer.drawWithShader(bufferBuilder.method_60800());
		}
		
		matrices.pop();
	}
	*/
	
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	Tessellator tessellator = Tessellator.getInstance();
	
	@Unique
	private static final Identifier MARS = Identifier.of(MODID, "textures/environment/mars.png");
	
	@Unique
	private static final Identifier JUPITER = Identifier.of(MODID, "textures/environment/jupiter.png");
	
	@Unique
	private static final Identifier SATURN = Identifier.of(MODID, "textures/environment/saturn.png");
	
	@Unique
	private static final Identifier NORTH_STAR = Identifier.of("minecraft", "textures/item/nether_star.png");
	
	@Unique
	private void renderPlanet(MatrixStack matrices, Identifier planet, float xRot, float yRot, float zRot, float size, float tickDelta) {
		matrices.push();
		matrices.rotate(Axis.X_POSITIVE.rotationDegrees(xRot));
		matrices.rotate(Axis.Z_POSITIVE.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * 100 + zRot));
		matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(yRot));
		
		Matrix4f matrix4f = matrices.peek().getModel();
		RenderSystem.setShaderTexture(0, planet);
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.xyz(matrix4f, -size, -100.0F, size).uv0(0.0F, 0.0F);
		bufferBuilder.xyz(matrix4f, size, -100.0F, size).uv0(1.0F, 0.0F); // u: 1.0
		bufferBuilder.xyz(matrix4f, size, -100.0F, -size).uv0(1.0F, 1.0F); // u: 1.0, v: 1.0
		bufferBuilder.xyz(matrix4f, -size, -100.0F, -size).uv0(0.0F, 1.0F); // v: 1.0
		BufferRenderer.drawWithShader(bufferBuilder.endOrThrow());
		
		matrices.pop();
	}
	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"))
	private void renderCelestialObjects(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean skipRendering, Runnable preRender, CallbackInfo ci) {
		MatrixStack matrices = new MatrixStack();
		matrices.multiply(modelViewMatrix);
		
		renderPlanet(matrices, MARS, 0, 45, 0, 13.0f, tickDelta);
		renderPlanet(matrices, JUPITER, 55, 0, 0, 13.0f, tickDelta);
		renderPlanet(matrices, SATURN, 45, 0, 15, 13.0f, tickDelta);
	}
}
