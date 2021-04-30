package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = {
				KubeJSEvents.SERVER_LOAD,
				KubeJSEvents.SERVER_UNLOAD,
				KubeJSEvents.SERVER_TICK
		}
)
public class ServerEventJS extends EventJS {
	@Nullable
	public ServerJS getServer() {
		return ServerJS.instance;
	}
}