package dev.latvian.kubejs.player;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatsCounter;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS {
	private final PlayerJS<?> player;
	private final StatsCounter statFile;

	public PlayerStatsJS(PlayerJS<?> p, StatsCounter s) {
		player = p;
		statFile = s;
	}

	public PlayerJS<?> getPlayer() {
		return player;
	}

	public int get(ResourceLocation id) {
		return statFile.getValue(UtilsJS.getStat(id));
	}

	public void set(ResourceLocation id, int value) {
		statFile.setValue(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}

	public void add(ResourceLocation id, int value) {
		statFile.increment(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}
}