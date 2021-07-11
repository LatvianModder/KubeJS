package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockRegistryEventJS;
import dev.latvian.kubejs.block.KubeJSBlockEventHandler;
import dev.latvian.kubejs.client.KubeJSClient;
import dev.latvian.kubejs.docs.KubeJSDocs;
import dev.latvian.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.fluid.FluidRegistryEventJS;
import dev.latvian.kubejs.fluid.KubeJSFluidEventHandler;
import dev.latvian.kubejs.item.ItemRegistryEventJS;
import dev.latvian.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.kubejs.item.custom.ItemArmorTierEventJS;
import dev.latvian.kubejs.item.custom.ItemToolTierEventJS;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.kubejs.script.*;
import dev.latvian.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.KubeJSWorldEventHandler;
import dev.latvian.kubejs.world.gen.FlatChunkGeneratorKJS;
import dev.latvian.kubejs.world.gen.WorldgenAddEventJS;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.CreativeTabs;
import me.shedaniel.architectury.utils.EnvExecutor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public class KubeJS {
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static KubeJS instance;

	public static KubeJSCommon PROXY;
	public static boolean nextClientHasClientMod = false;
	public static CreativeModeTab tab = CreativeModeTab.TAB_MISC;

	public static ScriptManager startupScriptManager, clientScriptManager;

	public KubeJS() throws Throwable {
		instance = this;
		Locale.setDefault(Locale.US);

		if (Files.notExists(KubeJSPaths.README)) {
			UtilsJS.tryIO(() -> {
				List<String> list = new ArrayList<>();
				list.add("Find more info on the website: https://kubejs.com/");
				list.add("");
				list.add("Directory information:");
				list.add("");
				list.add("assets - Acts as a resource pack, you can put any client resources in here, like textures, models, etc. Example: assets/kubejs/textures/item/test_item.png");
				list.add("data - Acts as a datapack, you can put any server resources in here, like loot tables, functions, etc. Example: data/kubejs/loot_tables/blocks/test_block.json");
				list.add("");
				list.add("startup_scripts - Scripts that get loaded once during game startup - Used for adding items and other things that can only happen while the game is loading (Can be reloaded with /kubejs reload_startup_scripts, but it may not work!)");
				list.add("server_scripts - Scripts that get loaded every time server resources reload - Used for modifying recipes, tags, loot tables, and handling server events (Can be reloaded with /reload)");
				list.add("client_scripts - Scripts that get loaded every time client resources reload - Used for JEI events, tooltips and other client side things (Can be reloaded with F3+T)");
				list.add("");
				list.add("config - KubeJS config storage. This is also the only directory that scripts can access other than world directory");
				list.add("exported - Data dumps like texture atlases end up here");
				list.add("");
				list.add("You can find type-specific logs in logs/kubejs/ directory");
				Files.write(KubeJSPaths.README, list);
			});
		}

		PROXY = EnvExecutor.getEnvSpecific(() -> KubeJSClient::new, () -> KubeJSCommon::new);

		long now = System.currentTimeMillis();
		LOGGER.info("Looking for KubeJS plugins...");

		for (Mod mod : Platform.getMods()) {
			try {
				KubeJSPlugins.load(mod.getModId(), mod.getFilePath());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		LOGGER.info("Done in " + (System.currentTimeMillis() - now) / 1000L + " s");

		startupScriptManager = new ScriptManager(ScriptType.STARTUP, KubeJSPaths.STARTUP_SCRIPTS, "/data/kubejs/example_startup_script.js");
		clientScriptManager = new ScriptManager(ScriptType.CLIENT, KubeJSPaths.CLIENT_SCRIPTS, "/data/kubejs/example_client_script.js");

		KubeJSDocs.init();

		Path oldStartupFolder = KubeJSPaths.DIRECTORY.resolve("startup");

		if (Files.exists(oldStartupFolder)) {
			UtilsJS.tryIO(() -> Files.move(oldStartupFolder, KubeJSPaths.STARTUP_SCRIPTS));
		}

		for (KubeJSPlugin plugin : KubeJSPlugins.LIST) {
			plugin.init();
		}

		if (!CommonProperties.get().serverOnly) {
			tab = CreativeTabs.create(new ResourceLocation(KubeJS.MOD_ID, KubeJS.MOD_ID), () -> new ItemStack(Items.PURPLE_DYE));
		}

		startupScriptManager.unload();
		startupScriptManager.loadFromDirectory();
		startupScriptManager.load();

		new ItemToolTierEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_REGISTRY_TOOL_TIERS);
		new ItemArmorTierEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_REGISTRY_ARMOR_TIERS);

		new BlockRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.BLOCK_REGISTRY);
		new ItemRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_REGISTRY);
		new FluidRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.FLUID_REGISTRY);

		new WorldgenAddEventJS().post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_ADD);
		new WorldgenRemoveEventJS().post(ScriptType.STARTUP, KubeJSEvents.WORLDGEN_REMOVE);

		KubeJSOtherEventHandler.init();
		KubeJSWorldEventHandler.init();
		KubeJSPlayerEventHandler.init();
		KubeJSEntityEventHandler.init();
		KubeJSBlockEventHandler.init();
		KubeJSItemEventHandler.init();
		KubeJSRecipeEventHandler.init();
		KubeJSFluidEventHandler.init();
		KubeJSServerEventHandler.init();

		PROXY.init();
	}

	public static void loadScripts(ScriptPack pack, Path dir, String path) {
		if (!path.isEmpty() && !path.endsWith("/")) {
			path += "/";
		}

		final String pathPrefix = path;

		UtilsJS.tryIO(() -> Files.walk(dir, 10).filter(Files::isRegularFile).forEach(file -> {
			String fileName = dir.relativize(file).toString().replace(File.separatorChar, '/');

			if (fileName.endsWith(".js")) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, pathPrefix + fileName));
			}
		}));
	}

	public static String appendModId(String id) {
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}

	public static Path getGameDirectory() {
		return Platform.getGameFolder();
	}

	public static Path verifyFilePath(Path path) throws IOException {
		if (!path.normalize().toAbsolutePath().startsWith(getGameDirectory())) {
			throw new IOException("You can't access files outside Minecraft directory!");
		}

		return path;
	}

	public static void verifyFilePath(File file) throws IOException {
		verifyFilePath(file.toPath());
	}

	public void setup() {
		UtilsJS.init();
		KubeJSNet.init();
		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.INIT);
		Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(KubeJS.MOD_ID, "flat"), FlatChunkGeneratorKJS.CODEC);
		//KubeJSRegistries.chunkGenerators().register(new ResourceLocation(KubeJS.MOD_ID, "flat"), () -> FlatChunkGeneratorKJS.CODEC);
	}

	public void loadComplete() {
		for (KubeJSPlugin plugin : KubeJSPlugins.LIST) {
			plugin.afterInit();
		}

		ScriptsLoadedEvent.EVENT.invoker().run();
		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
		UtilsJS.postModificationEvents();
	}
}