package dev.latvian.kubejs.server;

import com.mojang.brigadier.ParseResults;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import me.shedaniel.architectury.event.events.CommandPerformEvent;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.COMMAND_RUN }
)
public class CommandEventJS extends ServerEventJS {
	private final CommandPerformEvent event;

	public CommandEventJS(CommandPerformEvent e) {
		event = e;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	public ParseResults<CommandSourceStack> getParseResults() {
		return event.getResults();
	}

	public void setParseResults(ParseResults<CommandSourceStack> parse) {
		event.setResults(parse);
	}

	public Throwable getException() {
		return event.getThrowable();
	}

	public void setException(Throwable exception) {
		event.setThrowable(exception);
	}
}