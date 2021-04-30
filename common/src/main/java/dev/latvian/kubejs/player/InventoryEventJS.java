package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.PLAYER_INVENTORY_OPENED, KubeJSEvents.PLAYER_INVENTORY_CLOSED },
		client = { KubeJSEvents.PLAYER_INVENTORY_OPENED, KubeJSEvents.PLAYER_INVENTORY_CLOSED }
)
public class InventoryEventJS extends PlayerEventJS {
	private final Player player;
	private final AbstractContainerMenu menu;

	public InventoryEventJS(Player player, AbstractContainerMenu menu) {
		this.player = player;
		this.menu = menu;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	@MinecraftClass
	public AbstractContainerMenu getInventoryContainer() {
		return menu;
	}
}