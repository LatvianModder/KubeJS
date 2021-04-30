package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.core.ItemKJS;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		startup = { KubeJSEvents.ITEM_MODIFICATION }
)
public class ItemModificationEventJS extends EventJS {
	public void modify(IngredientJS in, Consumer<ItemModificationProperties> c) {
		for (Item item : in.getVanillaItems()) {
			if (item instanceof ItemKJS) {
				c.accept(new ItemModificationProperties((ItemKJS) item));
			}
		}
	}
}
