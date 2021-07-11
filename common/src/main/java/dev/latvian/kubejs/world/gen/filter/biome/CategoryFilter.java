package dev.latvian.kubejs.world.gen.filter.biome;

import me.shedaniel.architectury.registry.BiomeModifications;
import net.minecraft.world.level.biome.Biome;

/**
 * @author MaxNeedsSnacks
 */
public class CategoryFilter implements BiomeFilter {
	private final Biome.BiomeCategory category;

	public CategoryFilter(Biome.BiomeCategory cat) {
		category = cat;
	}

	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return ctx.getProperties().getCategory().equals(category);
	}

	@Override
	public String toString() {
		return "CategoryFilter{" +
				"category=" + category.getName() +
				'}';
	}
}
