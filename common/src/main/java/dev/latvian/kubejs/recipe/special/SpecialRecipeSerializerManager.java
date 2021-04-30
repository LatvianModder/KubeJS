package dev.latvian.kubejs.recipe.special;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.HashMap;
import java.util.Map;

@KubeJSEvent(
		server = { KubeJSEvents.RECIPES_SERIALIZER_SPECIAL_FLAG }
)
public class SpecialRecipeSerializerManager extends EventJS {
	public static final SpecialRecipeSerializerManager INSTANCE = new SpecialRecipeSerializerManager();
	public static final Event<Runnable> EVENT = EventFactory.createLoop();
	private final Map<ResourceLocation, Boolean> data = new HashMap<>();

	public void reset() {
		synchronized (data) {
			data.clear();
		}
	}

	@Override
	protected void afterPosted(boolean result) {
		super.afterPosted(result);
		EVENT.invoker().run();
	}

	public boolean isSpecial(Recipe<?> recipe) {
		return data.getOrDefault(Registries.getId(recipe.getSerializer(), Registry.RECIPE_SERIALIZER_REGISTRY), recipe.isSpecial());
	}

	public void ignoreSpecialFlag(String id) {
		synchronized (data) {
			data.put(new ResourceLocation(id), false);
		}
	}

	public void addSpecialFlag(String id) {
		synchronized (data) {
			data.put(new ResourceLocation(id), true);
		}
	}
}
