package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.BLOCK_REGISTRY }
)
public class BlockRegistryEventJS extends EventJS {
	public BlockBuilder create(String name) {
		BlockBuilder builder = new BlockBuilder(name);
		KubeJSObjects.BLOCKS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}

	public void addDetector(String id) {
		if (id.isEmpty() || !id.equals(id.toLowerCase()) || id.matches("\\W")) {
			throw new IllegalArgumentException("Detector ID can only contain a-z _ and 0-9!");
		}

		KubeJSObjects.DETECTORS.put(id, new DetectorInstance(id));
	}
}