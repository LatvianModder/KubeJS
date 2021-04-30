package dev.latvian.kubejs.integration.rei;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shedaniel
 */
@KubeJSEvent(
		client = { REIIntegration.REI_HIDE_ITEMS }
)
public class HideREIEventJS<T> extends EventJS {
	private final EntryRegistry registry;
	private final EntryStack.Type type;
	private final Function<Object, Collection<EntryStack>> serializer;
	private final LongSet hidden = new LongOpenHashSet();
	private boolean hideAll = false;

	public HideREIEventJS(EntryRegistry registry, EntryStack.Type type, Function<Object, Collection<EntryStack>> serializer) {
		this.registry = registry;
		this.type = type;
		this.serializer = serializer;
	}

	public Collection<T> getAllIngredients() {
		return (Collection<T>) registry.getEntryStacks().filter(this::filterType).map(EntryStack::getObject).collect(Collectors.toList());
	}

	private boolean filterType(EntryStack stack) {
		return stack.getType() == type;
	}

	public void hide(Object o) {
		if (!hideAll) {
			for (EntryStack stack : serializer.apply(o)) {
				hidden.add(stack.hashIgnoreAmount());
			}
		}
	}

	public void hideAll() {
		hideAll = true;
	}

	@Override
	protected void afterPosted(boolean result) {
		if (!hidden.isEmpty()) {
			registry.removeEntryIf(stack -> {
				if (filterType(stack)) {
					return hideAll || hidden.contains(stack.hashIgnoreAmount());
				}

				return false;
			});
		}
	}
}