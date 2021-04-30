package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration {
	public static final String GAMESTAGE_ADDED = "gamestage.added";
	public static final String GAMESTAGE_REMOVED = "gamestage.removed";

	public static void init() {
		MinecraftForge.EVENT_BUS.register(GameStagesIntegration.class);
		AttachPlayerDataEvent.EVENT.register(GameStagesIntegration::attachPlayerData);
	}

	// Just ignore when it says that it is not an Event
	public static void attachPlayerData(AttachPlayerDataEvent event) {
		event.add("gamestages", new GameStagesPlayerData(event.getParent()));
	}

	@SubscribeEvent
	public static void gameStageAdded(GameStageEvent.Added e) {
		new GameStageEventJS(e).post(GAMESTAGE_ADDED, e.getStageName());
	}

	@SubscribeEvent
	public static void gameStageRemoved(GameStageEvent.Removed e) {
		new GameStageEventJS(e).post(GAMESTAGE_REMOVED, e.getStageName());
	}
}