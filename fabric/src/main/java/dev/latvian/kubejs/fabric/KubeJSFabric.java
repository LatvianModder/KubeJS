package dev.latvian.kubejs.fabric;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.gen.WorldgenAddEventJS;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

public class KubeJSFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
	@Override
	public void onInitialize() {
		try {
			KubeJS.instance = new KubeJS();
			FabricLoader.getInstance().getEntrypoints("kubejs-init", KubeJSInitializer.class).forEach((it) -> {
				it.onKubeJSInitialization();
				KubeJS.LOGGER.log(Level.DEBUG, "[KubeJS] Initialized entrypoint " + it.getClass().getSimpleName() + ".");
			});
			KubeJS.instance.setup();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public void onInitializeClient() {
		KubeJS.instance.loadComplete();
	}

	@Override
	public void onInitializeServer() {
		KubeJS.instance.loadComplete();
	}
}
