package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.ENTITY_DEATH },
		client = { KubeJSEvents.ENTITY_DEATH }
)
public class LivingEntityDeathEventJS extends LivingEntityEventJS {
	private final LivingEntity entity;
	private final DamageSource source;

	public LivingEntityDeathEventJS(LivingEntity entity, DamageSource source) {
		this.entity = entity;
		this.source = source;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public DamageSourceJS getSource() {
		return new DamageSourceJS(getWorld(), source);
	}
}