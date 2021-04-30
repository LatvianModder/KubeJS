package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.WORLDGEN_REMOVE }
)
public class WorldgenRemoveEventJS extends EventJS {
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return true;
	}

	protected static boolean checkTree(ConfiguredFeature<?, ?> configuredFeature, Predicate<FeatureConfiguration> predicate) {
		return predicate.test(configuredFeature.config) || configuredFeature.config.getFeatures().anyMatch(cf -> checkTree(cf, predicate));
	}

	protected void removeFeature(GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
	}

	protected void removeSpawn(RemoveSpawnsByCategoryProperties properties) {
	}

	protected void removeSpawn(RemoveSpawnsByIDProperties properties) {
	}

	public void removeAllFeatures(String type) {
		removeFeature(GenerationStep.Decoration.valueOf(type.toUpperCase()), configuredFeature -> true);
	}

	public void removeAllFeatures() {
		for (GenerationStep.Decoration decoration : GenerationStep.Decoration.values()) {
			removeFeature(decoration, configuredFeature -> true);
		}
	}

	public void removeOres(Consumer<RemoveOresProperties> p) {
		RemoveOresProperties properties = new RemoveOresProperties();
		p.accept(properties);

		if (!verifyBiomes(properties.biomes)) {
			return;
		}

		removeFeature(properties._worldgenLayer, featureConfiguration -> {
			if (featureConfiguration instanceof OreConfiguration) {
				return properties._blocks.check(((OreConfiguration) featureConfiguration).state);
			} else if (featureConfiguration instanceof ReplaceBlockConfiguration) {
				return properties._blocks.check(((ReplaceBlockConfiguration) featureConfiguration).state);
			}

			return false;
		});
	}

	public void removeSpawnsByCategory(Consumer<RemoveSpawnsByCategoryProperties> p) {
		RemoveSpawnsByCategoryProperties properties = new RemoveSpawnsByCategoryProperties();
		p.accept(properties);

		if (verifyBiomes(properties.biomes)) {
			removeSpawn(properties);
		}
	}

	public void removeSpawnsByID(Consumer<RemoveSpawnsByIDProperties> p) {
		RemoveSpawnsByIDProperties properties = new RemoveSpawnsByIDProperties();
		p.accept(properties);

		if (verifyBiomes(properties.biomes)) {
			removeSpawn(properties);
		}
	}

	public void removeAllSpawns() {
	}
}