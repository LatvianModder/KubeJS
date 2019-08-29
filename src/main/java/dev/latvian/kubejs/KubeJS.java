package dev.latvian.kubejs;

import dev.latvian.kubejs.command.CommandKubeJS;
import dev.latvian.kubejs.command.CommandRegistryEventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.integration.IntegrationManager;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.KubeJSWorldEventHandler;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

/**
 * @author LatvianModder
 */
@Mod(
		modid = KubeJS.MOD_ID,
		name = KubeJS.MOD_NAME,
		version = KubeJS.VERSION,
		acceptableRemoteVersions = "*",
		dependencies = "required-after:forge@[0.0.0.forge,)"
)
public class KubeJS
{
	//Adds Nashorn to forge classloader, which for some reason isn't there.. Day was saved by modmuss50, again.
	static
	{
		try
		{
			Launch.classLoader.addURL(LaunchClassLoader.getSystemClassLoader().loadClass("jdk.nashorn.api.scripting.NashornScriptEngine").getProtectionDomain().getCodeSource().getLocation());
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}

	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final String VERSION = "0.0.0.kubejs";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static String appendModId(String id)
	{
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		UtilsJS.INSTANCE.init();
		IntegrationManager.preInit();
		ScriptManager.instance = new ScriptManager();
		ScriptManager.instance.load();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		EventsJS.post(KubeJSEvents.POSTINIT, new PostInitEventJS(new HashSet<>(Loader.instance().getIndexedModList().keySet())));
	}

	@Mod.EventHandler
	public void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		ServerJS.instance = null;
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		KubeJSWorldEventHandler.onServerStarting(event.getServer());
		event.registerServerCommand(new CommandKubeJS());
		EventsJS.post(KubeJSEvents.COMMAND_REGISTRY, new CommandRegistryEventJS(event::registerServerCommand));
	}

	@Mod.EventHandler
	public void onServerStopping(FMLServerStoppingEvent event)
	{
		KubeJSWorldEventHandler.onServerStopping();
	}
}