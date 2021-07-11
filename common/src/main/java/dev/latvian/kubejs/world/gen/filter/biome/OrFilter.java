package dev.latvian.kubejs.world.gen.filter.biome;

import me.shedaniel.architectury.registry.BiomeModifications;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MaxNeedsSnacks
 */
public class OrFilter implements BiomeFilter {
	public final List<BiomeFilter> list = new ArrayList<>(2);

	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		for (BiomeFilter p : list) {
			if (p.test(ctx)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "OrFilter[" + list + ']';
	}
}
