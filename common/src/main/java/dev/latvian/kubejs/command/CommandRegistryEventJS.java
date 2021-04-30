package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * @author LatvianModder
 */
@Deprecated
@KubeJSEvent
public class CommandRegistryEventJS extends ServerEventJS {
	private final CommandDispatcher<CommandSourceStack> dispatcher;
	private final Commands.CommandSelection selection;

	public CommandRegistryEventJS(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
		this.dispatcher = dispatcher;
		this.selection = selection;
	}

	public boolean isSinglePlayer() {
		return selection == Commands.CommandSelection.ALL || selection == Commands.CommandSelection.INTEGRATED;
	}

	public CommandDispatcher<CommandSourceStack> getDispatcher() {
		return dispatcher;
	}
}