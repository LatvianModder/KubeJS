package dev.latvian.kubejs.world.gen.filter.mob;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MaxNeedsSnacks
 */
public class OrFilter implements MobFilter {
	public final List<MobFilter> list = new ArrayList<>(2);

	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		for (MobFilter p : list) {
			if (p.test(cat, data)) {
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
