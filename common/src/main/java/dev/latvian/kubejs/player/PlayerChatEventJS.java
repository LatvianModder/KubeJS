package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.text.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.PLAYER_CHAT },
		client = { KubeJSEvents.PLAYER_CHAT }
)
public class PlayerChatEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final String message;
	public Component component;

	public PlayerChatEventJS(ServerPlayer player, String message, Component component) {
		this.player = player;
		this.message = message;
		this.component = component;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public String getUsername() {
		return player.getGameProfile().getName();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(Text text) {
		component = text.component();
	}
}