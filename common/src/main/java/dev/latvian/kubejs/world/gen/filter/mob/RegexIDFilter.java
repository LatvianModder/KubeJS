package dev.latvian.kubejs.world.gen.filter.mob;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.regex.Pattern;

/**
 * @author MaxNeedsSnacks
 */
public class RegexIDFilter implements MobFilter {
	private final Pattern pattern;

	public RegexIDFilter(Pattern i) {
		pattern = i;
	}

	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return pattern.matcher(Registry.ENTITY_TYPE.getKey(data.type).toString()).find();
	}

	@Override
	public String toString() {
		return "RegexIDFilter{" +
				"pattern=" + pattern +
				'}';
	}
}
