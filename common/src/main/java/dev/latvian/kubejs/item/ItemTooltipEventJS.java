package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { KubeJSEvents.ITEM_TOOLTIP }
)
public class ItemTooltipEventJS extends EventJS {
	@FunctionalInterface
	public interface StaticTooltipHandler {
		void tooltip(ItemStack stack, boolean advanced, List<Component> components);
	}

	@FunctionalInterface
	public interface StaticTooltipHandlerFromJS {
		void accept(ItemStackJS stack, boolean advanced, List<Object> text);
	}

	public static class StaticTooltipHandlerFromLines implements StaticTooltipHandler {
		public final List<Component> lines;

		public StaticTooltipHandlerFromLines(List<Component> l) {
			lines = l;
		}

		public StaticTooltipHandlerFromLines(Object o) {
			lines = new ArrayList<>();

			for (Object o1 : ListJS.orSelf(o)) {
				lines.add(Text.of(o1).component());
			}
		}

		@Override
		public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
			components.addAll(lines);
		}
	}

	public static class StaticTooltipHandlerFromJSWrapper implements StaticTooltipHandler {
		private final StaticTooltipHandlerFromJS handler;

		public StaticTooltipHandlerFromJSWrapper(StaticTooltipHandlerFromJS h) {
			handler = h;
		}

		@Override
		public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
			List<Object> text = new ArrayList<>();
			handler.accept(ItemStackJS.of(stack), advanced, text);

			for (Object o : text) {
				components.add(Text.of(o).component());
			}
		}
	}

	private final Map<Item, List<StaticTooltipHandler>> map;

	public ItemTooltipEventJS(Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> m) {
		map = m;
	}

	public void add(Object item, Object text) {
		if ("*".equals(item)) {
			addToAll(text);
			return;
		}

		StaticTooltipHandlerFromLines l = new StaticTooltipHandlerFromLines(text);

		if (!l.lines.isEmpty()) {
			for (Item i : IngredientJS.of(item).getVanillaItems()) {
				if (i != Items.AIR) {
					map.computeIfAbsent(i, k -> new ArrayList<>()).add(l);
				}
			}
		}
	}

	public void addToAll(Object text) {
		StaticTooltipHandlerFromLines l = new StaticTooltipHandlerFromLines(text);

		if (!l.lines.isEmpty()) {
			map.computeIfAbsent(Items.AIR, k -> new ArrayList<>()).add(l);
		}
	}

	public void addAdvanced(Object item, StaticTooltipHandlerFromJS handler) {
		if ("*".equals(item)) {
			addAdvancedToAll(handler);
			return;
		}

		StaticTooltipHandlerFromJSWrapper l = new StaticTooltipHandlerFromJSWrapper(handler);

		for (Item i : IngredientJS.of(item).getVanillaItems()) {
			if (i != Items.AIR) {
				map.computeIfAbsent(i, k -> new ArrayList<>()).add(l);
			}
		}
	}

	public void addAdvancedToAll(StaticTooltipHandlerFromJS handler) {
		map.computeIfAbsent(Items.AIR, k -> new ArrayList<>()).add(new StaticTooltipHandlerFromJSWrapper(handler));
	}
}