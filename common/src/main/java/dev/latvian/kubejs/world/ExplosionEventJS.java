package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import me.shedaniel.architectury.hooks.ExplosionHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class ExplosionEventJS extends WorldEventJS {
	protected final Level world;
	protected final Explosion explosion;

	public ExplosionEventJS(Level world, Explosion explosion) {
		this.world = world;
		this.explosion = explosion;
	}

	@Override
	public WorldJS getWorld() {
		return worldOf(world);
	}

	public Vec3 getPosition() {
		return ExplosionHooks.getPosition(explosion);
	}

	public double getX() {
		return getPosition().x;
	}

	public double getY() {
		return getPosition().y;
	}

	public double getZ() {
		return getPosition().z;
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(world, new BlockPos(getPosition()));
	}

	@Nullable
	public LivingEntityJS getExploder() {
		return getWorld().getLivingEntity(explosion.getSourceMob());
	}

	@KubeJSEvent(
			server = { KubeJSEvents.WORLD_EXPLOSION_PRE },
			client = { KubeJSEvents.WORLD_EXPLOSION_PRE }
	)
	public static class Pre extends ExplosionEventJS {
		public Pre(Level world, Explosion explosion) {
			super(world, explosion);
		}

		@Override
		public boolean canCancel() {
			return true;
		}

		public float getSize() {
			return ExplosionHooks.getRadius(explosion);
		}

		public void setSize(float s) {
			ExplosionHooks.setRadius(explosion, s);
		}
	}

	@KubeJSEvent(
			server = { KubeJSEvents.WORLD_EXPLOSION_POST },
			client = { KubeJSEvents.WORLD_EXPLOSION_POST }
	)
	public static class Post extends ExplosionEventJS {
		private final List<Entity> affectedEntities;

		public Post(Level world, Explosion explosion, List<Entity> affectedEntities) {
			super(world, explosion);
			this.affectedEntities = affectedEntities;
		}

		public EntityArrayList getAffectedEntities() {
			return new EntityArrayList(getWorld(), affectedEntities);
		}

		public void removeAffectedEntity(EntityJS entity) {
			affectedEntities.remove(entity.minecraftEntity);
		}

		public void removeAllAffectedEntities() {
			affectedEntities.clear();
		}

		public List<BlockContainerJS> getAffectedBlocks() {
			List<BlockContainerJS> list = new ArrayList<>(explosion.getToBlow().size());

			for (BlockPos pos : explosion.getToBlow()) {
				list.add(new BlockContainerJS(world, pos));
			}

			return list;
		}

		public void removeAffectedBlock(BlockContainerJS block) {
			explosion.getToBlow().remove(block.getPos());
		}

		public void removeAllAffectedBlocks() {
			explosion.getToBlow().clear();
		}

		public void removeKnockback() {
			explosion.getHitPlayers().clear();
		}
	}
}