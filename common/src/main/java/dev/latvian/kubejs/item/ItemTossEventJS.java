package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.ITEM_TOSS },
		client = { KubeJSEvents.ITEM_TOSS }
)
public class ItemTossEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemEntity entity;

	public ItemTossEventJS(Player player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
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
		return ItemStackJS.of(entity.getItem());
	}
}