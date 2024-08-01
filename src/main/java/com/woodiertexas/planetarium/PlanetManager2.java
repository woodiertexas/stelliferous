package com.woodiertexas.planetarium;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlanetManager2 extends JsonDataLoader {
	private Map<Identifier, Planet> planets;
	
	public PlanetManager2(Gson gson, String dataType) {
		super(gson, dataType);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> cache, ResourceManager manager, Profiler profiler) {
		
	}
	private final Map<Identifier, Planet> planets = new HashMap<>();
	
	public PlanetLoader(ResourceManager manager, Profiler profile) {
		Map<Identifier, Planet> planets = loader.loadPlanets();
	}
	
	private void loadPlanets() {
		this.profiler.push("Load Planets");
		Map<Identifier, Resource> resources = this.manager.findResources("planetarium/planets", id -> id.getPath().endsWith(".json"));
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
		
		String path = id.getPath().substring("planetarium/planets/".length());
		Identifier planetId = Identifier.of(id.getNamespace(), path.substring(0, path.length() - 5));
		this.planets.put(planetId, result.result().get().getFirst());
	}
	
	public Map<Identifier, Planet> getPlanets() {
		return this.planets;
	}
}
