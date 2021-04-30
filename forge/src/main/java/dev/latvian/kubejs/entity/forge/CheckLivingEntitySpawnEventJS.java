package dev.latvian.kubejs.entity.forge;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.ENTITY_CHECK_SPAWN }
)
public class CheckLivingEntitySpawnEventJS extends LivingEntityEventJS {
	private final LivingSpawnEvent.CheckSpawn event;

	public CheckLivingEntitySpawnEventJS(LivingSpawnEvent.CheckSpawn e) {
		event = e;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public WorldJS getWorld() {
		return worldOf((Level) event.getWorld());
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
	}

	public double getX() {
		return event.getX();
	}

	public double getY() {
		return event.getY();
	}

	public double getZ() {
		return event.getZ();
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(event.getWorld(), new BlockPos(getX(), getY(), getZ()));
	}
}