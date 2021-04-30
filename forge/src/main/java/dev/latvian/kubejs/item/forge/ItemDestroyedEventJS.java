package dev.latvian.kubejs.item.forge;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import javax.annotation.Nullable;
@KubeJSEvent(
		server = { KubeJSEvents.ITEM_DESTROYED },
		client = { KubeJSEvents.ITEM_DESTROYED }
)
public class ItemDestroyedEventJS extends PlayerEventJS {
	private final PlayerDestroyItemEvent event;

	public ItemDestroyedEventJS(PlayerDestroyItemEvent e) {
		event = e;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
	}

	@Nullable
	public InteractionHand getHand() {
		return event.getHand();
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(event.getOriginal());
	}
}