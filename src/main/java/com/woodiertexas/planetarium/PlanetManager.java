package com.woodiertexas.planetarium;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.woodiertexas.planetarium.Planetarium.MOD_ID;

public class PlanetManager implements SimpleResourceReloadListener<PlanetManager.PlanetLoader> {
	private static final Logger LOGGER = LoggerFactory.getLogger("Planetarium Planet Manager");
	private Map<Identifier, Planet> planets;
	
	public Map<Identifier, Planet> getPlanets() {
		return planets;
	}
	
	public CompletableFuture<PlanetLoader> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> new PlanetLoader(manager, profiler), executor);
	}
	
	public CompletableFuture<Void> apply(PlanetLoader prepared, ResourceManager manager, Profiler profiler, Executor executor) {
		this.planets = prepared.getPlanets();
		return CompletableFuture.runAsync(() -> {});
	}
	
	@Override
	public Identifier getFabricId() {
		return Identifier.of("planetarium", "planet_reloader");
	}
	
	public static class PlanetLoader {
		private final ResourceManager manager;
		private final Profiler profiler;
		private final Map<Identifier, Planet> planets = new HashMap<>();
		
		public PlanetLoader(ResourceManager manager, Profiler profile) {
			this.manager = manager;
			this.profiler = profile;
			this.loadPlanets();
		}
		
		private void loadPlanets() {
			this.profiler.push("Load Planets");
			Map<Identifier, Resource> resources = this.manager.findResources("planets", id -> id.getPath().endsWith(".json"));
			for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {
				this.addPlanet(resourceEntry.getKey(), resourceEntry.getValue());
			}
			
			this.profiler.pop();
		}
		
		private void addPlanet(Identifier id, Resource resource) {
			BufferedReader reader;
			
			try {
				reader = resource.openBufferedReader();
			} catch (IOException exception) {
				LOGGER.error(String.format("Could not open buffered reader for id \"%s\"", id), exception);
				return;
			}
			
			JsonObject json = JsonHelper.deserialize(reader);
			DataResult<Pair<Planet, JsonElement>> result = Planetarium.PLANET.decode(JsonOps.INSTANCE, json);
			
			if (result.error().isPresent()) {
				LOGGER.error(String.format("Could not parse planet file %s.\nReason: %s", id, result.error().get().message()));
				return;
			}
			
			Identifier planetId = Identifier.of(MOD_ID, id.getPath().substring("planets/".length()));
			this.planets.put(planetId, result.result().get().getFirst());
		}
		
		public Map<Identifier, Planet> getPlanets() {
			return this.planets;
		}
	}
}
