package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { KubeJSEvents.BLOCK_RIGHT_CLICK },
		server = { KubeJSEvents.BLOCK_RIGHT_CLICK }
)
public class BlockRightClickEventJS extends PlayerEventJS {
	private final Player player;
	private final InteractionHand hand;
	private final BlockPos pos;
	private final Direction direction;
	private BlockContainerJS block;
	private ItemStackJS item;

	public BlockRightClickEventJS(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		this.player = player;
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public BlockContainerJS getBlock() {
		if (block == null) {
			block = new BlockContainerJS(player.level, pos);
		}

		return block;
	}

	public InteractionHand getHand() {
		return hand;
	}

	public ItemStackJS getItem() {
		if (item == null) {
			item = ItemStackJS.of(player.getItemInHand(hand));
		}

		return item;
	}

	public Direction getFacing() {
		return direction;
	}
}