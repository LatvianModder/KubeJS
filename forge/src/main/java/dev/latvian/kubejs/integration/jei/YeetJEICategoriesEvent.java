package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { JEIIntegration.JEI_YEET_CATEGORIES }
)
public class YeetJEICategoriesEvent extends EventJS {
	private final IJeiRuntime runtime;
	private final HashSet<ResourceLocation> categoriesYeeted;
	private final Collection<IRecipeCategory<?>> allCategories;

	public YeetJEICategoriesEvent(IJeiRuntime r) {
		runtime = r;
		categoriesYeeted = new HashSet<>();
		allCategories = runtime.getRecipeManager().getRecipeCategories();
	}

	public Collection<IRecipeCategory<?>> getCategories() {
		return allCategories;
	}

	public void yeet(String... categoriesToYeet) {
		for (String toYeet : categoriesToYeet) {
			categoriesYeeted.add(new ResourceLocation(toYeet));
		}
	}

	public void yeetIf(Predicate<IRecipeCategory<?>> filter) {
		allCategories.stream()
				.filter(filter)
				.map(IRecipeCategory::getUid)
				.map(ResourceLocation::toString)
				.forEach(this::yeet);
	}

	@Override
	protected void afterPosted(boolean result) {
		for (ResourceLocation category : categoriesYeeted) {
			try {
				runtime.getRecipeManager().hideRecipeCategory(category);
			} catch (Exception e) {
				KubeJS.LOGGER.warn("Failed to yeet recipe category {}!", category);
			}
		}
	}
}