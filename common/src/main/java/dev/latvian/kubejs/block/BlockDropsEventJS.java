package dev.latvian.kubejs.block;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;

/**
 * @author LatvianModder
 */
@Deprecated
@KubeJSEvent
public class BlockDropsEventJS extends PlayerEventJS {
	@Override
	public EntityJS getEntity() {
		return null;
	}
	
	/*
	public final BlockEvent.HarvestDropsEvent event;
	public List<ItemStackJS> dropList;

	public BlockDropsEventJS(BlockEvent.HarvestDropsEvent e)
	{
		event = e;
	}

	@Override
	public WorldJS getWorld()
	{
		return worldOf((World) event.getWorld());
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event.getHarvester());
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(event.getWorld(), event.getPos())
		{
			@Override
			public BlockState getBlockState()
			{
				return event.getState();
			}
		};
	}

	public int getFortuneLevel()
	{
		return event.getFortuneLevel();
	}

	public List<ItemStackJS> getDrops()
	{
		if (dropList == null)
		{
			dropList = new ArrayList<>();

			for (ItemStack stack : event.getDrops())
			{
				dropList.add(ItemStackJS.of(stack));
			}
		}

		return dropList;
	}

	public boolean isSilkTouching()
	{
		return event.isSilkTouching();
	}

	public float getDropChance()
	{
		return event.getDropChance();
	}

	public void setDropChance(float dropChance)
	{
		event.setDropChance(dropChance);
	}

	public void addDrop(Object item)
	{
		ItemStackJS i = ItemStackJS.of(item);

		if (!i.isEmpty())
		{
			getDrops().add(i);
		}
	}

	public void addDrop(Object item, float chance)
	{
		if (chance >= 1F || event.getWorld().getRandom().nextFloat() <= chance)
		{
			addDrop(item);
		}
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getHarvester().getHeldItem(Hand.MAIN_HAND));
	}

	public int getItemHarvestLevel(ToolType tool)
	{
		ItemStack stack = event.getHarvester().getHeldItem(Hand.MAIN_HAND);
		return stack.getItem().getHarvestLevel(stack, tool, event.getHarvester(), event.getState());
	}

	public int getPickaxeLevel()
	{
		return getItemHarvestLevel(ToolType.PICKAXE);
	}

	public int getAxeLevel()
	{
		return getItemHarvestLevel(ToolType.AXE);
	}

	public int getShovelLevel()
	{
		return getItemHarvestLevel(ToolType.SHOVEL);
	}
	 */
}