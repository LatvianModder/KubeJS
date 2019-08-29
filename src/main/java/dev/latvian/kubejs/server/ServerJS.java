package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.text.TextUtilsJS;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.world.WorldCreatedEvent;
import dev.latvian.kubejs.world.WorldJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerJS implements MessageSender
{
	public static ServerJS instance;

	public final transient MinecraftServer server;
	public final transient List<ScheduledEvent> scheduledEvents;
	public final transient Int2ObjectOpenHashMap<WorldJS> worldMap;
	public final transient Map<UUID, PlayerDataJS> playerMap;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	@DocField("List of all currently loaded worlds")
	public final List<WorldJS> worlds;

	public final WorldJS overworld;
	public final GameRulesJS gameRules;

	public ServerJS(MinecraftServer ms, WorldServer w)
	{
		server = ms;
		scheduledEvents = new LinkedList<>();
		worldMap = new Int2ObjectOpenHashMap<>();
		playerMap = new HashMap<>();

		data = new HashMap<>();
		overworld = new WorldJS(this, w);
		worldMap.put(0, overworld);
		worlds = new ArrayList<>();
		worlds.add(overworld);
		gameRules = new GameRulesJS(w.getGameRules());
	}

	public void updateWorldList()
	{
		worlds.clear();
		worlds.addAll(worldMap.values());
	}

	public boolean isRunning()
	{
		return server.isServerRunning();
	}

	public boolean isHardcore()
	{
		return server.isHardcore();
	}

	public boolean isSinglePlayer()
	{
		return server.isSinglePlayer();
	}

	public boolean isDedicated()
	{
		return server.isDedicatedServer();
	}

	public String getMOTD()
	{
		return server.getMOTD();
	}

	public void setMOTD(String text)
	{
		server.setMOTD(text);
	}

	public void stop()
	{
		server.stopServer();
	}

	@Override
	public void tell(Object message)
	{
		ITextComponent component = TextUtilsJS.INSTANCE.of(message).component();
		KubeJS.LOGGER.info("Server: " + component.getUnformattedText());

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendMessage(component);
		}
	}

	@Override
	public void statusMessage(Object message)
	{
		ITextComponent component = TextUtilsJS.INSTANCE.of(message).component();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			player.sendStatusMessage(component, true);
		}
	}

	@Override
	public int runCommand(String command)
	{
		return server.getCommandManager().executeCommand(server, command);
	}

	public WorldJS world(int dimension)
	{
		if (dimension == 0)
		{
			return overworld;
		}

		WorldJS world = worldMap.get(dimension);

		if (world == null)
		{
			world = new WorldJS(this, server.getWorld(dimension));
			worldMap.put(dimension, world);
			updateWorldList();
			MinecraftForge.EVENT_BUS.post(new WorldCreatedEvent(world));
		}

		return world;
	}

	public WorldJS world(World world)
	{
		return world(world.provider.getDimension());
	}

	public PlayerJS player(UUID uuid)
	{
		PlayerDataJS p = playerMap.get(uuid);

		if (p == null)
		{
			throw new NullPointerException("Player from UUID " + uuid + " not found!");
		}

		return p.player();
	}

	public PlayerJS player(String name)
	{
		name = name.trim().toLowerCase();

		if (name.isEmpty())
		{
			throw new NullPointerException("Player can't have empty name!");
		}

		UUID uuid = UUIDUtilsJS.INSTANCE.fromString(name);

		if (uuid != null)
		{
			return player(uuid);
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.name.equalsIgnoreCase(name))
			{
				return p.player();
			}
		}

		for (PlayerDataJS p : playerMap.values())
		{
			if (p.name.toLowerCase().contains(name))
			{
				return p.player();
			}
		}

		throw new NullPointerException("Player from name " + name + " not found!");
	}

	@Nullable
	public EntityJS entity(@Nullable Entity entity)
	{
		if (entity == null)
		{
			return null;
		}
		else if (entity instanceof EntityPlayerMP)
		{
			PlayerDataJS data = playerMap.get(entity.getUniqueID());

			if (data == null)
			{
				throw new NullPointerException("Player from UUID " + entity.getUniqueID() + " not found!");
			}

			return new PlayerJS(data, (EntityPlayerMP) entity);
		}
		else if (entity instanceof EntityLivingBase)
		{
			return new LivingEntityJS(this, (EntityLivingBase) entity);
		}

		return new EntityJS(this, entity);
	}

	public EntityArrayList entities(Collection<? extends Entity> entities)
	{
		return new EntityArrayList(this, entities);
	}

	public EntityArrayList players()
	{
		return entities(server.getPlayerList().getPlayers());
	}

	public EntityArrayList entities()
	{
		EntityArrayList list = new EntityArrayList(this, overworld.world.loadedEntityList.size());

		for (WorldJS world : worlds)
		{
			for (Entity entity : world.world.loadedEntityList)
			{
				list.add(entity(entity));
			}
		}

		return list;
	}

	public EntityArrayList entities(String filter)
	{
		try
		{
			EntityArrayList list = new EntityArrayList(this, overworld.world.loadedEntityList.size());

			for (WorldJS world : worlds)
			{
				for (Entity entity : EntitySelector.matchEntities(world, filter, Entity.class))
				{
					list.add(entity(entity));
				}
			}

			return list;
		}
		catch (CommandException e)
		{
			return new EntityArrayList(this, 0);
		}
	}

	public ScheduledEvent schedule(long timer, IScheduledEventCallback event)
	{
		ScheduledEvent e = new ScheduledEvent(this, timer, event);
		scheduledEvents.add(e);
		return e;
	}
}