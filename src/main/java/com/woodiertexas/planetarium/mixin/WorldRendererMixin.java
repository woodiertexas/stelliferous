package com.woodiertexas.planetarium.mixin;

import com.woodiertexas.planetarium.Planetarium;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	private static final Identifier MERCURY = new Identifier(Planetarium.MOD_ID, "textures/planets/mercury.png");
	
	@Unique
	private static final Identifier VENUS = new Identifier(Planetarium.MOD_ID, "textures/planets/venus.png");
	
	@Unique
	private static final Identifier MARS = new Identifier(Planetarium.MOD_ID, "textures/planets/mars.png");
	
	@Unique
	private static final Identifier JUPITER = new Identifier(Planetarium.MOD_ID, "textures/planets/jupiter.png");
	
	@Unique
	private static final Identifier SATURN = new Identifier(Planetarium.MOD_ID, "textures/planets/saturn.png");
	
	@Unique
	private static final Identifier URANUS = new Identifier(Planetarium.MOD_ID, "textures/planets/uranus.png");
	
	@Unique
	private static final Identifier NEPTUNE = new Identifier(Planetarium.MOD_ID, "textures/planets/neptune.png");
	
	@Unique
	private static final Identifier NORTH_STAR = new Identifier("minecraft", "textures/item/nether_star.png");
	
	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"))
	private void renderPlanets(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean skipRendering, Runnable preRender, CallbackInfo ci) {
		assert world != null;
		
		Planetarium.renderPlanet(matrices, MERCURY, 140.00F, -30.00F, 0.03F, 5.00F, tickDelta, world);
		Planetarium.renderPlanet(matrices, VENUS, -177.00F, 20.00F, 2.64F, 6.50F, tickDelta, world);
		Planetarium.renderPlanet(matrices, MARS, 65.00F, 5.00F, 25.19F, 7.50F, tickDelta, world);
		Planetarium.renderPlanet(matrices, JUPITER, 15.45F, 50.00F, 3.13F, 11.00F, tickDelta, world);
		Planetarium.renderPlanet(matrices, SATURN, -28.57F, 30.00F, 26.73F, 10.40F, tickDelta, world);
		Planetarium.renderPlanet(matrices, URANUS, -67.90F, -43.20F, 82.23F, 6.40F, tickDelta, world);
		Planetarium.renderPlanet(matrices, NEPTUNE, -56.00F, -26.50F, 28.32F, 6.40F, tickDelta, world);
		Planetarium.renderPlanet(matrices, NORTH_STAR, 0.00F, -86.50F, 0.00F, 1.35F, tickDelta, world);
	}
	
	@Inject(method = "renderEndSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V"))
	private void renderPlanetsInEnd(MatrixStack matrices, CallbackInfo ci) {
		assert world != null;
		Planetarium.renderPlanet(matrices, MERCURY, 140.00F, -30.00F, 0.03F, 5.00F, world);
	}
	
	/*
	@ModifyVariable(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("STORE"), ordinal = 5)
	float changeSunAndMoonSize(float moonSize) {
		return 8.00F;
	}
	 */
}
