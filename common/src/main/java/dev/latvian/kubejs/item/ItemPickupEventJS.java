package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { KubeJSEvents.ITEM_PICKUP },
		server = { KubeJSEvents.ITEM_PICKUP }
)
public class ItemPickupEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemEntity entity;
	private final ItemStack stack;

	public ItemPickupEventJS(Player player, ItemEntity entity, ItemStack stack) {
		this.player = player;
		this.entity = entity;
		this.stack = stack;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public EntityJS getItemEntity() {
		return getWorld().getEntity(entity);
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(stack);
	}
}