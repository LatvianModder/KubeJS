package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { KubeJSEvents.PLAYER_INVENTORY_CHANGED },
		server = { KubeJSEvents.PLAYER_INVENTORY_CHANGED }
)
public class InventoryChangedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemStack item;
	private final int slot;

	public InventoryChangedEventJS(ServerPlayer p, ItemStack is, int s) {
		player = p;
		item = is;
		slot = s;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(item);
	}

	public int getSlot() {
		return slot;
	}
}