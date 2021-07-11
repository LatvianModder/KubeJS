package dev.latvian.kubejs.world.gen.filter.mob;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

/**
 * @author MaxNeedsSnacks
 */
public class IDFilter implements MobFilter {
	private final ResourceLocation id;

	public IDFilter(ResourceLocation i) {
		id = i;
	}

	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return Registry.ENTITY_TYPE.getKey(data.type).equals(id);
	}

	@Override
	public String toString() {
		return "IDFilter{" +
				"id=" + id +
				'}';
	}
}
