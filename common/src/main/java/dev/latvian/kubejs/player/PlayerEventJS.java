package dev.latvian.kubejs.player;

import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
@KubeJSEvent
public abstract class PlayerEventJS extends LivingEntityEventJS {
	@Nullable
	public PlayerJS getPlayer() {
		EntityJS e = getEntity();

		if (e instanceof PlayerJS) {
			return (PlayerJS) e;
		}

		return null;
	}

	// Helper methods for Game Stages

	public boolean hasGameStage(String stage) {
		if (getPlayer() != null && Platform.isModLoaded("gamestages")) {
			return hasStage(getPlayer().minecraftPlayer, stage);
		}

		return false;
	}

	public void addGameStage(String stage) {
		if (Platform.isModLoaded("gamestages")) {
			if (getPlayer() instanceof ServerPlayerJS) {
				addStage((ServerPlayer) getPlayer().minecraftPlayer, stage);
			}
		} else {
			getWorld().getSide().console.error("Can't add gamestage " + stage + ", GameStages mod isn't loaded!");
		}
	}

	public void removeGameStage(String stage) {
		if (Platform.isModLoaded("gamestages")) {
			if (getPlayer() instanceof ServerPlayerJS) {
				removeStage((ServerPlayer) getPlayer().minecraftPlayer, stage);
			}
		} else {
			getWorld().getSide().console.error("Can't remove gamestage " + stage + ", GameStages mod isn't loaded!");
		}
	}

	@ExpectPlatform
	private static boolean hasStage(Player player, String stage) {
		throw new AssertionError();
	}

	@ExpectPlatform
	private static void addStage(ServerPlayer player, String stage) {
		throw new AssertionError();
	}

	@ExpectPlatform
	private static void removeStage(ServerPlayer player, String stage) {
		throw new AssertionError();
	}
}