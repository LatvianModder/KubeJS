package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.ITEM_ENTITY_INTERACT },
		client = { KubeJSEvents.ITEM_ENTITY_INTERACT }
)
public class ItemEntityInteractEventJS extends PlayerEventJS {
	private final Player player;
	private final Entity entity;
	private final InteractionHand hand;

	public ItemEntityInteractEventJS(Player player, Entity entity, InteractionHand hand) {

		this.player = player;
		this.entity = entity;
		this.hand = hand;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public InteractionHand getHand() {
		return hand;
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(player.getItemInHand(hand));
	}

	public EntityJS getTarget() {
		return getWorld().getEntity(entity);
	}
}