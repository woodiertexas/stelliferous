package com.woodiertexas.planetarium.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.woodiertexas.planetarium.PlanetInfo;
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
import net.minecraft.util.math.Axis;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
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
	
	//@Unique
	//private static final Identifier MERCURY = new Identifier(Planetarium.MOD_ID, "textures/planets/mercury.png");
	
	@Unique
	private PlanetManager planetarium$planetManager;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void createPlanetManager(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityDispatcher, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
		this.planetarium$planetManager = new PlanetManager();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this.planetarium$planetManager);
	}
	
	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"))
	private void renderPlanets(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean skipRendering, Runnable preRender, CallbackInfo ci) {
		assert world != null;
		
		for (Map.Entry<Identifier, PlanetInfo> entry : planetarium$planetManager.getPlanets().entrySet()) {
			Planetarium.renderPlanet(matrices, entry.getKey(), entry.getValue(), tickDelta, world);
		}
	}
}
