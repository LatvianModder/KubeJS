package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.SERVER_CUSTOM_COMMAND }
)
public class CustomCommandEventJS extends PlayerEventJS {
	private final Level level;
	private final Entity entity;
	private final BlockPos blockPos;
	private final String id;

	public CustomCommandEventJS(Level l, @Nullable Entity e, BlockPos p, String i) {
		level = l;
		entity = e;
		blockPos = p;
		id = i;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	public String getId() {
		return id;
	}

	@Override
	public WorldJS getWorld() {
		return worldOf(level);
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public BlockContainerJS getBlock() {
		return getWorld().getBlock(blockPos);
	}
}