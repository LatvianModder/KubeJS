package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RecipeTypeRegistryEventJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptPackInfo;
import dev.latvian.kubejs.script.ScriptSource;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.DataPackEventJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ServerScriptManager {
	public static ServerScriptManager instance;

	public final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER, KubeJSPaths.SERVER_SCRIPTS, "/data/kubejs/example_server_script.js");

	public void init(ServerResources serverResources) {
		try {
			if (Files.notExists(KubeJSPaths.DATA)) {
				Files.createDirectories(KubeJSPaths.DATA);
			}
		} catch (Throwable ex) {
			throw new RuntimeException("KubeJS failed to register it's script loader!", ex);
		}
	}

	public List<PackResources> resourcePackList(List<PackResources> list0) {
		VirtualKubeJSDataPack virtualDataPackLow = new VirtualKubeJSDataPack(false);
		VirtualKubeJSDataPack virtualDataPackHigh = new VirtualKubeJSDataPack(true);
		List<PackResources> list = new ArrayList<>();
		list.add(virtualDataPackLow);
		list.addAll(list0);
		list.add(new KubeJSServerResourcePack());
		list.add(virtualDataPackHigh);

		SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(PackType.SERVER_DATA);

		for (PackResources p : list) {
			resourceManager.add(p);
		}

		scriptManager.unload();
		scriptManager.loadFromDirectory();

		Map<String, List<ResourceLocation>> packs = new HashMap<>();

		for (ResourceLocation resource : resourceManager.listResources("kubejs", s -> s.endsWith(".js"))) {
			packs.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (Map.Entry<String, List<ResourceLocation>> entry : packs.entrySet()) {
			ScriptPack pack = new ScriptPack(scriptManager, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (ResourceLocation id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (ScriptFileInfo fileInfo : pack.info.scripts) {
				ScriptSource.FromResource scriptSource = info -> resourceManager.getResource(info.id);
				Throwable error = fileInfo.preload(scriptSource);

				if (fileInfo.isIgnored()) {
					continue;
				}

				if (error == null) {
					pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
				} else {
					KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
				}
			}

			pack.scripts.sort(null);
			scriptManager.packs.put(pack.info.namespace, pack);
		}

		scriptManager.load();

		ScriptType.SERVER.console.setLineNumber(true);
		new DataPackEventJS(virtualDataPackLow).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LAST_PRIORITY);
		new DataPackEventJS(virtualDataPackLow).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LOW_PRIORITY);
		new DataPackEventJS(virtualDataPackHigh).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_FIRST_PRIORITY); // TODO swap order with high priority?
		new DataPackEventJS(virtualDataPackHigh).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_HIGH_PRIORITY);

		UtilsJS.postModificationEvents();

		ScriptType.SERVER.console.setLineNumber(false);

		ScriptType.SERVER.console.info("Scripts loaded");

		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		RegisterRecipeHandlersEvent.EVENT.invoker().accept(new RegisterRecipeHandlersEvent(typeMap));
		new RecipeTypeRegistryEventJS(typeMap).post(ScriptType.SERVER, KubeJSEvents.RECIPES_TYPE_REGISTRY);
		RecipeEventJS.instance = new RecipeEventJS(typeMap);
		return list;
	}
}
