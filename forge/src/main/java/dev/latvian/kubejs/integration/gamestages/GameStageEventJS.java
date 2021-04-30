package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.darkhax.gamestages.event.GameStageEvent;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { GameStagesIntegration.GAMESTAGE_ADDED, GameStagesIntegration.GAMESTAGE_REMOVED },
		client = { GameStagesIntegration.GAMESTAGE_ADDED, GameStagesIntegration.GAMESTAGE_REMOVED }
)
public class GameStageEventJS extends PlayerEventJS {
	private final GameStageEvent event;

	public GameStageEventJS(GameStageEvent e) {
		event = e;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
	}

	public String getStage() {
		return event.getStageName();
	}
}