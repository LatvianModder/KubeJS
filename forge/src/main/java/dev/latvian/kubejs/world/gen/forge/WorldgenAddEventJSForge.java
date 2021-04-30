package dev.latvian.kubejs.world.gen.forge;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.world.gen.WorldgenAddEventJS;
import dev.latvian.kubejs.world.gen.WorldgenEntryList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.WORLDGEN_ADD }
)
public class WorldgenAddEventJSForge extends WorldgenAddEventJS {
	private final BiomeLoadingEvent event;

	public WorldgenAddEventJSForge(BiomeLoadingEvent e) {
		event = e;
	}

	@Override
	protected void addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> configuredFeature) {
		event.getGeneration().addFeature(decoration, configuredFeature);
	}

	@Override
	protected void addEntitySpawn(MobCategory category, MobSpawnSettings.SpawnerData spawnerData) {
		event.getSpawns().addSpawn(category, spawnerData);
	}

	@Override
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return biomes.verify(s -> {
			if (s.startsWith("#")) {
				return event.getCategory() == Biome.BiomeCategory.byName(s.substring(1));
			}

			return new ResourceLocation(s).equals(event.getName());
		});
	}
}
