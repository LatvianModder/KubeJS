package dev.latvian.kubejs.core;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * @author LatvianModder
 */
public interface ItemKJS {
	void setMaxStackSizeKJS(int i);

	void setMaxDamageKJS(int i);

	void setCraftingReminderKJS(Item i);

	void setFireResistantKJS(boolean b);

	void setRarityKJS(Rarity r);
	void setBurnTimeKJS(int i);

	void setFoodPropertiesKJS(FoodProperties properties);
}
