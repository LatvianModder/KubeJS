package dev.latvian.kubejs.integration.jei;


import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.ListJS;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { JEIIntegration.JEI_ADD_ITEMS, JEIIntegration.JEI_ADD_FLUIDS }
)
public class AddJEIEventJS<T> extends EventJS {
	private final IJeiRuntime runtime;
	private final IIngredientType<T> type;
	private final Function<Object, T> function;
	private final Collection<T> added;

	public AddJEIEventJS(IJeiRuntime r, IIngredientType<T> t, Function<Object, T> f) {
		runtime = r;
		type = t;
		function = f;
		added = new ArrayList<>();
	}

	public void add(Object o) {
		for (Object o1 : ListJS.orSelf(o)) {
			T t = function.apply(o1);

			if (t != null) {
				added.add(t);
			}
		}
	}

	@Override
	protected void afterPosted(boolean result) {
		if (!added.isEmpty()) {
			runtime.getIngredientManager().addIngredientsAtRuntime(type, added);
		}
	}
}