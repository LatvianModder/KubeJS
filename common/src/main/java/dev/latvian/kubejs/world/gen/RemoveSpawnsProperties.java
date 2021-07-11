package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.world.gen.filter.biome.BiomeFilter;
import dev.latvian.kubejs.world.gen.filter.mob.MobFilter;

/**
 * @author LatvianModder
 */
public class RemoveSpawnsProperties {
	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
	public MobFilter mobs = MobFilter.ALWAYS_TRUE;
}
