package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { KubeJSEvents.CLIENT_DEBUG_INFO_LEFT, KubeJSEvents.CLIENT_DEBUG_INFO_RIGHT }
)
public class DebugInfoEventJS extends EventJS {
	private final List<String> lines;

	public DebugInfoEventJS(List<String> l) {
		lines = l;
	}

	public boolean getShowDebug() {
		return Minecraft.getInstance().options.renderDebug;
	}

	public List<String> getLines() {
		return lines;
	}
}