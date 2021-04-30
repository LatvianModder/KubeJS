package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.SOUND_REGISTRY }
)
public class SoundRegistryEventJS extends EventJS {
	private final Consumer<ResourceLocation> registry;

	public SoundRegistryEventJS(Consumer<ResourceLocation> registry) {
		this.registry = registry;
	}

	public void register(String id) {
		ResourceLocation r = UtilsJS.getMCID(KubeJS.appendModId(id));
		registry.accept(r);
	}
}