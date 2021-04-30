package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.ITEM_FOOD_EATEN }
)
public class ItemFoodEatenEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemStackJS item;

	public ItemFoodEatenEventJS(ServerPlayer p, ItemStack is) {
		player = p;
		item = ItemStackJS.of(is);
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public ItemStackJS getItem() {
		return item;
	}
}