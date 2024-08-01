package com.woodiertexas.planetarium.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import com.woodiertexas.planetarium.Planetarium;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.woodiertexas.planetarium.Planetarium.MOD_ID;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	private static final Identifier MERCURY = Identifier.of(MOD_ID, "textures/environment/mercury.png");
	
	@Unique
	private static final Identifier VENUS = Identifier.of(MOD_ID, "textures/environment/venus.png");
	
	@Unique
	private static final Identifier MARS = Identifier.of(MOD_ID, "textures/environment/mars.png");
	
	@Unique
	private static final Identifier JUPITER = Identifier.of(MOD_ID, "textures/environment/jupiter.png");
	
	@Unique
	private static final Identifier SATURN = Identifier.of(MOD_ID, "textures/environment/saturn.png");
	
	@Unique
	private static final Identifier URANUS = Identifier.of(MOD_ID, "textures/environment/uranus.png");
	
	@Unique
	private static final Identifier NEPTUNE = Identifier.of(MOD_ID, "textures/environment/neptune.png");
	
	@Unique
	private static final Identifier NORTH_STAR = Identifier.of("minecraft", "textures/item/nether_star.png");
	
	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"))
	private void renderCelestialObjects(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean skipRendering, Runnable preRender, CallbackInfo ci) {
		MatrixStack matrices = new MatrixStack();
		matrices.multiply(modelViewMatrix);
		
		assert world != null;
		Planetarium.renderPlanet(matrices, MARS, 0, 45, 25.2f, 13.0f, tickDelta, world);
		Planetarium.renderPlanet(matrices, JUPITER, 0, -45, 0, 13.0f, tickDelta, world);
		Planetarium.renderPlanet(matrices, SATURN, 0, 15, 15, 13.0f, tickDelta, world);
	}
}
