package dev.latvian.kubejs.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@KubeJSEvent
public abstract class LootEventJS<LB extends LootBuilder<?, ?>> extends EventJS {
	private final Map<ResourceLocation, JsonElement> lootMap;
	final Gson gsonConditions;
	final Gson gsonFunctions;
	final Gson gsonLootTables;

	public LootEventJS(Map<ResourceLocation, JsonElement> c) {
		lootMap = c;
		gsonConditions = Deserializers.createConditionSerializer().create();
		gsonFunctions = Deserializers.createFunctionSerializer().create();
		gsonLootTables = Deserializers.createLootTableSerializer().create();
	}

	public abstract LB newLootBuilder();

	public void addJson(ResourceLocation id, Object json) {
		lootMap.put(id, MapJS.json(json));
	}

	public void build(ResourceLocation id, Consumer<LB> lb) {
		LB lootBuilder = newLootBuilder();
		lb.accept(lootBuilder);
		addJson(id, lootBuilder.toJson(this));
	}
}
