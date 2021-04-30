package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.block.BlockItemBuilder;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.ITEM_REGISTRY }
)
public class ItemRegistryEventJS extends EventJS {
	public ItemBuilder create(String name) {
		ItemBuilder builder = new ItemBuilder(name);
		KubeJSObjects.ITEMS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}

	@Deprecated
	public BlockItemBuilder createBlockItem(String name) {
		KubeJS.LOGGER.error("This method is deprecated! Replaced by block registry .item(function(item) { /*chained item functions here*/ }) or .noItem()");
		return new BlockItemBuilder(name);
	}

	public Supplier<FoodBuilder> createFood(Supplier<FoodBuilder> builder) {
		return builder;
	}
}