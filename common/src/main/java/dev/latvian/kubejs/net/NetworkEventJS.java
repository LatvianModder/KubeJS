package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.PLAYER_DATA_FROM_CLIENT },
		client = { KubeJSEvents.PLAYER_DATA_FROM_SERVER }
)
public class NetworkEventJS extends PlayerEventJS {
	private final Player player;
	private final String channel;
	private final MapJS data;

	public NetworkEventJS(Player p, String c, @Nullable MapJS d) {
		player = p;
		channel = c;
		data = d;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public String getChannel() {
		return channel;
	}

	@Nullable
	public MapJS getData() {
		return data;
	}
}