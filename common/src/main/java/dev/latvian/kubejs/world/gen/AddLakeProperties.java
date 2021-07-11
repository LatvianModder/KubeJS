package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.gen.filter.biome.BiomeFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * @author LatvianModder
 */
public class AddLakeProperties {
	public GenerationStep.Decoration _worldgenLayer = GenerationStep.Decoration.LAKES;
	public BlockState _block = Blocks.AIR.defaultBlockState();
	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
	public int chance = 20;
	public int retrogen = 0;
	public ResourceLocation id = null;

	public void setBlock(String id) {
		_block = UtilsJS.parseBlockState(id);
	}

	public void setWorldgenLayer(String id) {
		_worldgenLayer = GenerationStep.Decoration.valueOf(id.toUpperCase());
	}
}
