package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.util.ListJS;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.plugins.jei.info.IngredientInfoRecipe;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { JEIIntegration.JEI_INFORMATION }
)
public class InformationJEIEventJS extends EventJS {
	private final List<IngredientInfoRecipe<?>> recipes;

	public InformationJEIEventJS(List<IngredientInfoRecipe<?>> l) {
		recipes = l;
	}

	public void add(Object o, Object s) {
		recipes.addAll(IngredientInfoRecipe.create(IngredientJS.of(o).getStacks().stream().map(ItemStackJS::getItemStack).collect(Collectors.toList()), VanillaTypes.ITEM, ListJS.orSelf(s).stream().map(String::valueOf).toArray(String[]::new)));
	}
}