package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.ITEM_SMELTED },
		client = { KubeJSEvents.ITEM_SMELTED }
)
public class ItemSmeltedEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemStack smelted;

	public ItemSmeltedEventJS(Player player, ItemStack smelted) {
		this.player = player;
		this.smelted = smelted;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(smelted);
	}
}