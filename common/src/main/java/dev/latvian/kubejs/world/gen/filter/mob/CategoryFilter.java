package dev.latvian.kubejs.world.gen.filter.mob;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

/**
 * @author MaxNeedsSnacks
 */
public class CategoryFilter implements MobFilter {
	private final MobCategory category;

	public CategoryFilter(MobCategory cat) {
		category = cat;
	}

	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return cat == category;
	}

	@Override
	public String toString() {
		return "CategoryFilter{" +
				"category=" + category.getName() +
				'}';
	}
}
