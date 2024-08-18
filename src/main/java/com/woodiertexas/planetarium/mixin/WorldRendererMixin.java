package com.woodiertexas.planetarium.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.woodiertexas.planetarium.PlanetInfo;
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

import static com.woodiertexas.planetarium.Planetarium.MOD_ID;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private @Nullable ClientWorld world;
	
	@Unique
	private static final Identifier MERCURY = new Identifier(MOD_ID, "textures/environment/mercury.png");
	
	@Unique
	private static final Identifier VENUS = new Identifier(MOD_ID, "textures/environment/venus.png");
	
	@Unique
	private static final Identifier MARS = new Identifier(MOD_ID, "textures/environment/mars.png");
	
	@Unique
	private static final Identifier JUPITER = new Identifier(MOD_ID, "textures/environment/jupiter.png");
	
	@Unique
	private static final Identifier SATURN = new Identifier(MOD_ID, "textures/environment/saturn.png");
	
	@Unique
	private static final Identifier URANUS = new Identifier(MOD_ID, "textures/environment/uranus.png");
	
	@Unique
	private static final Identifier NEPTUNE = new Identifier(MOD_ID, "textures/environment/neptune.png");
	
	@Unique
	private static final Identifier NORTH_STAR = new Identifier("minecraft", "textures/item/nether_star.png");
	
	
	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"))
	private void renderPlanets(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera preStep, boolean skipRendering, Runnable preRender, CallbackInfo ci) {
		assert world != null;
		
		Planetarium.renderPlanet(matrices, MERCURY, new PlanetInfo(0.0F, 0.0F, 0.0F, 0.0F, 10.0F, null), tickDelta, world);
	}
}
