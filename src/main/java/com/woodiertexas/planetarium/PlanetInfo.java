package com.woodiertexas.planetarium;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

/**
 * @param procession       How far along in orbit the planet is.
 * @param tilt             The orbital tilt of the planet.
 * @param texture_rotation The rotation of the planet's texture.
 * @param size             The planet's size. (1.0 = very small, 20.0 = big)
 */
public record PlanetInfo(float procession, float tilt, float texture_rotation, float size) {
	public static final Codec<PlanetInfo> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.floatRange(-360.0f, 360.0f).fieldOf("procession").forGetter(PlanetInfo::procession),
			Codec.floatRange(-360.0f, 360.0f).fieldOf("tilt").forGetter(PlanetInfo::tilt),
			Codec.floatRange(-360.0f, 360.0f).fieldOf("texture_rotation").forGetter(PlanetInfo::texture_rotation),
			Codec.floatRange(0.1f, 750.0f).fieldOf("size").forGetter(PlanetInfo::size)
		).apply(instance, PlanetInfo::new)
	);

	public Identifier getTexture(Identifier id) {
		// Identifier.of(id.getNamespace, "textures/planets/" + id.getPath() + ".png");
		return id.withPrefix("textures/planets/").extendPath(".png");
	}
}
