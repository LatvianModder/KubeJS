package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */

@KubeJSEvent(
		client = { KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY },
		server = { KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY }
)
public class ItemRightClickEmptyEventJS extends PlayerEventJS {
	private final Player player;
	private final InteractionHand hand;

	public ItemRightClickEmptyEventJS(Player player, InteractionHand hand) {
		this.player = player;
		this.hand = hand;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public InteractionHand getHand() {
		return hand;
	}

	public ItemStackJS getItem() {
		return EmptyItemStackJS.INSTANCE;
	}
}