package dev.latvian.kubejs.event;

import dev.latvian.kubejs.docs.KubeJSEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@KubeJSEvent
public class DataEvent extends EventJS {
	private final boolean canCancel;
	private final Object data;

	public DataEvent(boolean c, @Nullable Object d) {
		canCancel = c;
		data = d;
	}

	@Override
	public boolean canCancel() {
		return canCancel;
	}

	@Nullable
	public Object getData() {
		return data;
	}
}