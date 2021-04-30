package dev.latvian.kubejs.integration.rei;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.utils.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Set;

@KubeJSEvent(
		client = { REIIntegration.REI_YEET_CATEGORIES }
)
public class YeetREICategoryEventJS extends EventJS {
	private final Set<ResourceLocation> categoriesYeeted;

	public YeetREICategoryEventJS(Set<ResourceLocation> categoriesYeeted) {
		this.categoriesYeeted = categoriesYeeted;
	}

	public Collection<String> getCategories() {
		return CollectionUtils.map(RecipeHelper.getInstance().getAllCategories(), category -> category.getIdentifier().toString());
	}

	public void yeet(String categoryToYeet) {
		yeet(new String[]{categoryToYeet});
	}

	public void yeet(String[] categoriesToYeet) {
		for (String toYeet : categoriesToYeet) {
			categoriesYeeted.add(new ResourceLocation(toYeet));
		}
	}
}
