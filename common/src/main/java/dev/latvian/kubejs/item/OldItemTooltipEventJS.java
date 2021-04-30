package dev.latvian.kubejs.item;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.text.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * @author LatvianModder
 */
@Deprecated
@KubeJSEvent
public class OldItemTooltipEventJS extends EventJS {
	private final ItemStack stack;
	private final List<Component> lines;
	private final boolean advanced;

	public OldItemTooltipEventJS(ItemStack stack, List<Component> lines, boolean a) {
		this.stack = stack;
		this.lines = lines;
		this.advanced = a;
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(stack);
	}

	public boolean isAdvanced() {
		return advanced;
	}

	public void add(Object text) {
		lines.add(Text.of(text).component());
	}
}