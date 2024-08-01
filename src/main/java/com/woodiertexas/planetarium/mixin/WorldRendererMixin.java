package com.woodiertexas.planetarium.mixin;

import com.woodiertexas.planetarium.Planet;
import com.woodiertexas.planetarium.PlanetManager;
import com.woodiertexas.planetarium.Planetarium;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	private PlanetManager planetarium$planetManager;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void createPlanetManager(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityDispatcher, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
		this.planetarium$planetManager = new PlanetManager();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this.planetarium$planetManager);
	}
	
	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"))
	private void renderCelestialObjects(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean skipRendering, Runnable preRender, CallbackInfo ci) {
		MatrixStack matrices = new MatrixStack();
		matrices.multiply(modelViewMatrix);
		
		assert world != null;
		for (Map.Entry<Identifier, Planet> planet : planetarium$planetManager.getPlanets().entrySet()) {
			Planet planetInfo = planet.getValue();
			Planetarium.renderPlanet(matrices, planet.getKey(), planetInfo.procession(), planetInfo.tilt(), planetInfo.texture_rotation(), planetInfo.size(), tickDelta, world);
		}
	}
}
