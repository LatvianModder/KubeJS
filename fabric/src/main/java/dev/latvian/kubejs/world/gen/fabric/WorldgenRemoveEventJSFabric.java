package dev.latvian.kubejs.world.gen.fabric;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByCategoryProperties;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByIDProperties;
import dev.latvian.kubejs.world.gen.WorldgenEntryList;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.lang.reflect.Field;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.WORLDGEN_REMOVE }
)
public class WorldgenRemoveEventJSFabric extends WorldgenRemoveEventJS {
	private final BiomeSelectionContext selectionContext;
	private final BiomeModificationContext modificationContext;
	private Registry<ConfiguredFeature<?, ?>> featureRegistry;

	public WorldgenRemoveEventJSFabric(BiomeSelectionContext s, BiomeModificationContext m) {
		selectionContext = s;
		modificationContext = m;
	}

	private Registry<ConfiguredFeature<?, ?>> getFeatureRegistry() {
		if (featureRegistry == null) {
			try {
				Class<?> c = Class.forName("net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl$GenerationSettingsContextImpl");
				Field field = c.getDeclaredField("features");
				field.setAccessible(true);
				featureRegistry = UtilsJS.cast(field.get(modificationContext.getGenerationSettings()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return featureRegistry;
	}

	@Override
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return biomes.verify(s -> {
			if (s.startsWith("#")) {
				return selectionContext.getBiome().getBiomeCategory() == Biome.BiomeCategory.byName(s.substring(1));
			}

			return selectionContext.getBiomeKey().location().equals(new ResourceLocation(s));
		});
	}

	@Override
	protected void removeFeature(GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
		for (ConfiguredFeature<?, ?> feature : getFeatureRegistry()) {
			if (checkTree(feature, predicate)) {
				getFeatureRegistry().getResourceKey(feature).ifPresent(key -> modificationContext.getGenerationSettings().removeFeature(decoration, key));
			}
		}
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByCategoryProperties p) {
		modificationContext.getSpawnSettings().removeSpawns((category, spawnerData) -> p.categories.verifyIgnoreCase(category.getName()));
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByIDProperties p) {
		modificationContext.getSpawnSettings().removeSpawns((category, spawnerData) -> p.entities.verify(spawnerData.type.getDescriptionId()));
	}

	@Override
	public void removeAllFeatures(String type) {
		GenerationStep.Decoration decoration = GenerationStep.Decoration.valueOf(type.toUpperCase());

		for (ResourceLocation key : getFeatureRegistry().keySet()) {
			modificationContext.getGenerationSettings().removeFeature(decoration, ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, key));
		}
	}

	@Override
	public void removeAllFeatures() {
		GenerationStep.Decoration[] decorations = GenerationStep.Decoration.values();

		for (ResourceLocation key : getFeatureRegistry().keySet()) {
			for (GenerationStep.Decoration decoration : decorations) {
				modificationContext.getGenerationSettings().removeFeature(decoration, ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, key));
			}
		}
	}

	@Override
	public void removeAllSpawns() {
		modificationContext.getSpawnSettings().clearSpawns();
	}
}
