package dev.latvian.kubejs.world.gen.filter.biome;

import me.shedaniel.architectury.registry.BiomeModifications;
import net.minecraft.resources.ResourceLocation;

/**
 * @author MaxNeedsSnacks
 */
public class IDFilter implements BiomeFilter {
	private final ResourceLocation id;

	public IDFilter(ResourceLocation i) {
		id = i;
	}

	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return ctx.getKey().equals(id);
	}

	@Override
	public String toString() {
		return "IDFilter{" +
				"id=" + id +
				'}';
	}
}
