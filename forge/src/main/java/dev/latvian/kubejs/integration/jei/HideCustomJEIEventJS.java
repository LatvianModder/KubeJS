package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { JEIIntegration.JEI_HIDE_CUSTOM }
)
public class HideCustomJEIEventJS extends EventJS {
	private final IJeiRuntime runtime;
	private final HashMap<IIngredientType<?>, HideJEIEventJS<?>> events;

	public HideCustomJEIEventJS(IJeiRuntime r) {
		runtime = r;
		events = new HashMap<>();
	}

	@SuppressWarnings("all")
	public HideJEIEventJS get(IIngredientType s) {
		return events.computeIfAbsent(s, type -> {
			return new HideJEIEventJS(runtime, type, o -> {
				List list = new ArrayList();

				for (Object o1 : ListJS.orSelf(o)) {
					list.add(UtilsJS.cast(o1));
				}

				return list;
			}, o -> true);
		});
	}

	@Override
	protected void afterPosted(boolean result) {
		for (HideJEIEventJS<?> eventJS : events.values()) {
			eventJS.afterPosted(result);
		}
	}
}