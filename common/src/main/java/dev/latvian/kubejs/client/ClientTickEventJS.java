package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.player.PlayerEventJS;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = KubeJSEvents.CLIENT_TICK
)
public class ClientTickEventJS extends PlayerEventJS {
	private final ClientPlayerJS player;

	public ClientTickEventJS(ClientPlayerJS p) {
		player = p;
	}

	@Override
	public EntityJS getEntity() {
		return player;
	}
}