package dev.latvian.kubejs.world.gen.filter.biome;

import me.shedaniel.architectury.registry.BiomeModifications;

/**
 * @author MaxNeedsSnacks
 */
public class NotFilter implements BiomeFilter {
	public final BiomeFilter original;

	public NotFilter(BiomeFilter original) {
		this.original = original;
	}

	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return !original.test(ctx);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}

