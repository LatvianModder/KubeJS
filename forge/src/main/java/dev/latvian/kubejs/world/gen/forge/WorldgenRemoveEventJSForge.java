package dev.latvian.kubejs.world.gen.forge;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByCategoryProperties;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByIDProperties;
import dev.latvian.kubejs.world.gen.WorldgenEntryList;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.WORLDGEN_ADD }
)
public class WorldgenRemoveEventJSForge extends WorldgenRemoveEventJS {
	private final BiomeLoadingEvent event;

	public WorldgenRemoveEventJSForge(BiomeLoadingEvent e) {
		event = e;
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

	@Override
	protected void removeFeature(GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
		event.getGeneration().getFeatures(decoration).removeIf(configuredFeatureSupplier -> checkTree(configuredFeatureSupplier.get(), predicate));
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByCategoryProperties properties) {
		event.getSpawns().getSpawnerTypes().forEach(category -> {
			if (properties.categories.verifyIgnoreCase(category.getName())) {
				event.getSpawns().getSpawner(category).clear();
			}
		});
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByIDProperties properties) {
		event.getSpawns().getSpawnerTypes().forEach(category -> event.getSpawns().getSpawner(category).removeIf(spawner -> properties.entities.verify(spawner.type.getDescriptionId())));
	}

	@Override
	public void removeAllFeatures(String type) {
		event.getGeneration().getFeatures(GenerationStep.Decoration.valueOf(type.toUpperCase())).clear();
	}

	@Override
	public void removeAllFeatures() {
		for (GenerationStep.Decoration decoration : GenerationStep.Decoration.values()) {
			event.getGeneration().getFeatures(decoration).clear();
		}
	}

	@Override
	public void removeAllSpawns() {
		event.getSpawns().getSpawnerTypes().forEach(category -> event.getSpawns().getSpawner(category).clear());
	}
}
