package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { KubeJSEvents.PLAYER_CHEST_OPENED, KubeJSEvents.PLAYER_CHEST_CLOSED },
		server = { KubeJSEvents.PLAYER_CHEST_OPENED, KubeJSEvents.PLAYER_CHEST_CLOSED }
)
public class ChestEventJS extends InventoryEventJS {
	private InventoryJS inventory;

	public ChestEventJS(Player player, AbstractContainerMenu menu) {
		super(player, menu);
	}

	@MinecraftClass
	public Container getWrappedInventory() {
		return ((ChestMenu) getInventoryContainer()).getContainer();
	}

	public InventoryJS getInventory() {
		if (inventory == null) {
			inventory = new InventoryJS(getWrappedInventory());
		}

		return inventory;
	}

	@Nullable
	public BlockContainerJS getBlock() {
		if (getWrappedInventory() instanceof BlockEntity) {
			return getWorld().getBlock((BlockEntity) getWrappedInventory());
		}

		return null;
	}
}