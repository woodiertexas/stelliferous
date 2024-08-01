package io.github.woodiertexas.planetarium;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.client.model.Texture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlanetManager{
	private static final Logger LOGGER = LoggerFactory.getLogger("Planetarium Planet Manager");
	// "texture" is a placeholder
	private Map<Identifier, Planet> planets;
	
	public @Nullable Texture getPlanet(Identifier id) {
		this.planets.get(id);
	}
	
	public static class PlanetLoader {
		private final ResourceManager manager;
		private final Profiler profiler;
		private final Map<Identifier, Planet> planets = new HashMap<>();
		
		public PlanetLoader(ResourceManager manager, Profiler profile) {
			this.manager = manager;
			this.profiler = profile;
		}
		
		private void loadPlanets() {
			this.profiler.push("Load Planets");
			Map<Identifier, Resource> resources = this.manager.findResources("planets", id -> id.getPath().endsWith(".json"));
			for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {
				
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
				LOGGER.error(String.format("Could not parse animation file %s.\nReason: %s", id, result.error().get().message()));
				return;
			}
			
			Identifier animationId = new Identifier(id.getNamespace(), id.getPath().substring("animations/".length()));
			this.planets.put(animationId, result.result().get().getFirst());
		}
		
		public Map<Identifier, Texture> getPlanets() {
			return this.planets;
		}
	}
}
