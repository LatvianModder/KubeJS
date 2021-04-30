package dev.latvian.kubejs.integration.rei;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.ListJS;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author shedaniel
 */
@KubeJSEvent(
		client = { REIIntegration.REI_ADD_ITEMS }
)
public class AddREIEventJS extends EventJS {
	private final EntryRegistry registry;
	private final Function<Object, Collection<EntryStack>> function;
	private final List<EntryStack> added = Lists.newArrayList();

	public AddREIEventJS(EntryRegistry registry, Function<Object, Collection<EntryStack>> function) {
		this.registry = registry;
		this.function = function;
	}

	public void add(Object o) {
		for (Object o1 : ListJS.orSelf(o)) {
			Collection<EntryStack> stacks = function.apply(o1);

			if (stacks != null && !stacks.isEmpty()) {
				for (EntryStack stack : stacks) {
					if (stack != null && !stack.isEmpty()) {
						added.add(stack);
					}
				}
			}
		}
	}

	@Override
	protected void afterPosted(boolean result) {
		if (!added.isEmpty()) {
			registry.registerEntries(added.toArray(new EntryStack[0]));
		}
	}
}
