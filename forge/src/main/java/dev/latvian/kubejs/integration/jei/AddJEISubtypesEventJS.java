package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		client = { JEIIntegration.JEI_SUBTYPES }
)
public class AddJEISubtypesEventJS extends EventJS {
	@FunctionalInterface
	public interface Interpreter extends Function<ItemStackJS, Object> {
	}

	private static class NBTKeyInterpreter implements ISubtypeInterpreter {
		private final String key;

		private NBTKeyInterpreter(String k) {
			key = k;
		}

		@Override
		public String apply(ItemStack stack) {
			CompoundTag nbt = stack.getTag();

			if (nbt == null || !nbt.contains(key)) {
				return "";
			}

			return String.valueOf(nbt.get(key));
		}
	}

	private final ISubtypeRegistration registration;

	public AddJEISubtypesEventJS(ISubtypeRegistration r) {
		registration = r;
	}

	public void registerInterpreter(Object id, Interpreter interpreter) {
		registration.registerSubtypeInterpreter(ItemStackJS.of(id).getItem(), stack -> {
			Object o = interpreter.apply(ItemStackJS.of(stack));
			return o == null ? "" : o.toString();
		});
	}

	public void useNBT(Object id) {
		registration.useNbtForSubtypes(ItemStackJS.of(id).getItem());
	}

	public void useNBTKey(Object id, String key) {
		registration.registerSubtypeInterpreter(ItemStackJS.of(id).getItem(), new NBTKeyInterpreter(key));
	}
}