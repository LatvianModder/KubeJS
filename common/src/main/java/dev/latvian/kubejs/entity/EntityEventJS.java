package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.world.WorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.entity.Entity;

/**
 * @author LatvianModder
 */
@KubeJSEvent
public abstract class EntityEventJS extends WorldEventJS {
	private EntityJS cachedEntity;

	public abstract EntityJS getEntity();

	@Override
	public WorldJS getWorld() {
		return getEntity().getWorld();
	}

	protected EntityJS entityOf(Entity entity) {
		if (cachedEntity == null) {
			cachedEntity = worldOf(entity).getEntity(entity);

			if (cachedEntity == null) {
				throw new IllegalStateException("Entity can't be null!");
			}
		}

		return cachedEntity;
	}
}