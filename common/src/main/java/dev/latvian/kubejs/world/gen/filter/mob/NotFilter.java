package dev.latvian.kubejs.world.gen.filter.mob;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

/**
 * @author MaxNeedsSnacks
 */
public class NotFilter implements MobFilter {
	public final MobFilter original;

	public NotFilter(MobFilter original) {
		this.original = original;
	}

	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return !original.test(cat, data);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}

