package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.world.gen.filter.biome.BiomeFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * @author LatvianModder
 */
public class AddSpawnProperties {
	public MobCategory _category = MobCategory.CREATURE;
	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
	public int weight = 10;
	public EntityType<?> _entity = null;
	public int minCount = 4;
	public int maxCount = 4;

	public void setCategory(String s) {
		_category = MobCategory.byName(s);
	}

	public void setEntity(String s) {
		_entity = EntityType.byString(s).orElse(null);
	}
}
