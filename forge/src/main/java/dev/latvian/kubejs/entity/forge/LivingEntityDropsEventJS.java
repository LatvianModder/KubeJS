package dev.latvian.kubejs.entity.forge;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.DamageSourceJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.ItemEntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.ENTITY_DROPS },
		client = { KubeJSEvents.ENTITY_DROPS }
)
public class LivingEntityDropsEventJS extends LivingEntityEventJS {
	private final LivingDropsEvent event;
	public List<ItemEntityJS> eventDrops;

	public LivingEntityDropsEventJS(LivingDropsEvent e) {
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

	public DamageSourceJS getSource() {
		return new DamageSourceJS(getWorld(), event.getSource());
	}

	public int getLootingLevel() {
		return event.getLootingLevel();
	}

	public boolean isRecentlyHit() {
		return event.isRecentlyHit();
	}

	public List<ItemEntityJS> getDrops() {
		if (eventDrops == null) {
			eventDrops = new ArrayList<>();

			for (ItemEntity entity : event.getDrops()) {
				eventDrops.add(new ItemEntityJS(getWorld(), entity));
			}
		}

		return eventDrops;
	}

	@Nullable
	public ItemEntityJS addDrop(Object item) {
		ItemStack i = ItemStackJS.of(item).getItemStack();

		if (!i.isEmpty()) {
			Entity e = event.getEntity();
			ItemEntity ei = new ItemEntity(e.level, e.getX(), e.getY(), e.getZ(), i);
			ei.setPickUpDelay(10);
			ItemEntityJS ie = new ItemEntityJS(getWorld(), ei);
			getDrops().add(ie);
			return ie;
		}

		return null;
	}

	@Nullable
	public ItemEntityJS addDrop(Object item, float chance) {
		if (chance >= 1F || event.getEntity().level.random.nextFloat() <= chance) {
			return addDrop(item);
		}

		return null;
	}
}