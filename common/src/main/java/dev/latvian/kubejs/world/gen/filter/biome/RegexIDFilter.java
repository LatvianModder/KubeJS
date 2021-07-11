package dev.latvian.kubejs.world.gen.filter.biome;

import me.shedaniel.architectury.registry.BiomeModifications;

import java.util.regex.Pattern;

/**
 * @author MaxNeedsSnacks
 */
public class RegexIDFilter implements BiomeFilter {
	private final Pattern pattern;

	public RegexIDFilter(Pattern i) {
		pattern = i;
	}

	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return pattern.matcher(ctx.getKey().toString()).find();
	}

	@Override
	public String toString() {
		return "RegexIDFilter{" +
				"pattern=" + pattern +
				'}';
	}
}
