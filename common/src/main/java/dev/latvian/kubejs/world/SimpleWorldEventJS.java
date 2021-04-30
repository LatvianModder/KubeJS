package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.WORLD_UNLOAD, KubeJSEvents.WORLD_LOAD }
)
public class SimpleWorldEventJS extends WorldEventJS {
	private final WorldJS world;

	public SimpleWorldEventJS(WorldJS w) {
		world = w;
	}

	@Override
	public WorldJS getWorld() {
		return world;
	}
}