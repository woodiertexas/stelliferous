package com.woodiertexas.planetarium;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

public class PlanetManager extends JsonDataLoader implements IdentifiableResourceReloader {
	private static final Gson GSON = new GsonBuilder().create();
	private Map<Identifier, PlanetInfo> planets;
	
	public PlanetManager() {
		super(GSON, Planetarium.MOD_ID + "/planets");
	}
	
	public Map<Identifier, PlanetInfo> getPlanets() {
		return planets;
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> cache, ResourceManager manager, Profiler profiler) {
		Map<Identifier, PlanetInfo> planets = new HashMap<>();
		
		profiler.push("Load Planets");
		for (Map.Entry<Identifier, JsonElement> resourceEntry : cache.entrySet()) {
			Identifier id = resourceEntry.getKey();
			DataResult<Pair<PlanetInfo, JsonElement>> result = PlanetInfo.CODEC.decode(JsonOps.INSTANCE, resourceEntry.getValue());
			
			if (result.error().isPresent()) {
				Planetarium.LOGGER.error(String.format("Could not parse planet file %s.\nReason: %s", id, result.error().get().message()));
				continue;
			}
			
			PlanetInfo planetInfo = result.result().get().getFirst();
			
			if (manager.getResource(planetInfo.getTexture(id)).isEmpty()) {
				Planetarium.LOGGER.error("No texture found for planet {}, skipping.", id);
				continue;
			}
			
			Planetarium.LOGGER.debug("Adding Planet {}: {}", id, planetInfo);
			planets.put(id, planetInfo);
		}
		
		profiler.pop();
		
		this.planets = Map.copyOf(planets);
	}
	
	@Override
	public @NotNull Identifier getQuiltId() {
		return new Identifier(Planetarium.MOD_ID, "planet_reloader");
	}
}
