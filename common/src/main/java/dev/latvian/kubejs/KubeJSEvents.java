package dev.latvian.kubejs;

/**
 * @author LatvianModder
 */
public class KubeJSEvents {
	public static final String INIT = "init";
	public static final String POSTINIT = "postinit";
	public static final String LOADED = "loaded";
	public static final String COMMAND_REGISTRY = "command.registry"; // TODO remove
	public static final String COMMAND_RUN = "command.run";

	public static final String CLIENT_INIT = "client.init";
	public static final String CLIENT_DEBUG_INFO_LEFT = "client.debug_info.left";
	public static final String CLIENT_DEBUG_INFO_RIGHT = "client.debug_info.right";
	public static final String CLIENT_LOGGED_IN = "client.logged_in";
	public static final String CLIENT_LOGGED_OUT = "client.logged_out";
	public static final String CLIENT_TICK = "client.tick";

	public static final String SERVER_LOAD = "server.load";
	public static final String SERVER_UNLOAD = "server.unload";
	public static final String SERVER_TICK = "server.tick";
	public static final String SERVER_DATAPACK_FIRST_PRIORITY = "server.datapack.first";
	public static final String SERVER_DATAPACK_HIGH_PRIORITY = "server.datapack.high_priority";
	public static final String SERVER_DATAPACK_LOW_PRIORITY = "server.datapack.low_priority";
	public static final String SERVER_DATAPACK_LAST_PRIORITY = "server.datapack.last";
	public static final String SERVER_CUSTOM_COMMAND = "server.custom_command";
	public static final String RECIPES = "recipes";
	public static final String RECIPES_SERIALIZER_SPECIAL_FLAG = "recipes.serializer.special.flag";
	public static final String RECIPES_COMPOSTABLES = "recipes.compostables";
	public static final String RECIPES_TYPE_REGISTRY = "recipes.type_registry";
	public static final String WORLDGEN_ADD = "worldgen.add";
	public static final String WORLDGEN_REMOVE = "worldgen.remove";

	public static final String WORLD_LOAD = "world.load";
	public static final String WORLD_UNLOAD = "world.unload";
	public static final String WORLD_TICK = "world.tick";
	public static final String WORLD_EXPLOSION_PRE = "world.explosion.pre";
	public static final String WORLD_EXPLOSION_POST = "world.explosion.post";

	public static final String PLAYER_LOGGED_IN = "player.logged_in";
	public static final String PLAYER_LOGGED_OUT = "player.logged_out";
	public static final String PLAYER_TICK = "player.tick";
	public static final String PLAYER_DATA_FROM_SERVER = "player.data_from_server";
	public static final String PLAYER_DATA_FROM_CLIENT = "player.data_from_client";
	public static final String PLAYER_CHAT = "player.chat";
	public static final String PLAYER_ADVANCEMENT = "player.advancement";
	public static final String PLAYER_INVENTORY_OPENED = "player.inventory.opened";
	public static final String PLAYER_INVENTORY_CLOSED = "player.inventory.closed";
	public static final String PLAYER_INVENTORY_CHANGED = "player.inventory.changed";
	public static final String PLAYER_CHEST_OPENED = "player.chest.opened";
	public static final String PLAYER_CHEST_CLOSED = "player.chest.closed";

	public static final String ENTITY_DEATH = "entity.death";
	public static final String ENTITY_ATTACK = "entity.attack";
	public static final String ENTITY_DROPS = "entity.drops";
	public static final String ENTITY_CHECK_SPAWN = "entity.check_spawn";
	public static final String ENTITY_SPAWNED = "entity.spawned";

	public static final String BLOCK_REGISTRY = "block.registry";
	public static final String BLOCK_MISSING_MAPPINGS = "block.missing_mappings";
	public static final String BLOCK_TAGS = "block.tags";
	public static final String BLOCK_RIGHT_CLICK = "block.right_click";
	public static final String BLOCK_LEFT_CLICK = "block.left_click";
	public static final String BLOCK_PLACE = "block.place";
	public static final String BLOCK_BREAK = "block.break";
	public static final String BLOCK_MODIFICATION = "block.modification";
	public static final String BLOCK_LOOT_TABLES = "block.loot_tables";

	public static final String ITEM_REGISTRY = "item.registry";
	public static final String ITEM_MISSING_MAPPINGS = "item.missing_mappings";
	public static final String ITEM_TAGS = "item.tags";
	public static final String ITEM_RIGHT_CLICK = "item.right_click";
	public static final String ITEM_RIGHT_CLICK_EMPTY = "item.right_click_empty";
	public static final String ITEM_LEFT_CLICK = "item.left_click";
	public static final String ITEM_ENTITY_INTERACT = "item.entity_interact";
	public static final String ITEM_PICKUP = "item.pickup";
	public static final String ITEM_TOSS = "item.toss";
	public static final String ITEM_CRAFTED = "item.crafted";
	public static final String ITEM_SMELTED = "item.smelted";
	public static final String ITEM_DESTROYED = "item.destroyed";
	public static final String ITEM_FOOD_EATEN = "item.food_eaten";
	public static final String ITEM_TOOLTIP = "item.tooltip";
	public static final String ITEM_MODIFICATION = "item.modification";

	public static final String FLUID_REGISTRY = "fluid.registry";
	public static final String FLUID_TAGS = "fluid.tags";

	public static final String ENTITY_TYPE_TAGS = "entity_type.tags";

	public static final String SOUND_REGISTRY = "sound.registry";
}