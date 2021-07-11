package dev.latvian.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.bindings.BlockWrapper;
import dev.latvian.kubejs.bindings.FacingWrapper;
import dev.latvian.kubejs.bindings.IngredientWrapper;
import dev.latvian.kubejs.bindings.ItemWrapper;
import dev.latvian.kubejs.bindings.JsonWrapper;
import dev.latvian.kubejs.bindings.NBTWrapper;
import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.bindings.ScriptEventsWrapper;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.bindings.UUIDWrapper;
import dev.latvian.kubejs.bindings.UtilsWrapper;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.event.IEventHandler;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.fluid.FluidWrapper;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.PlatformWrapper;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.util.ClassFilter;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UUIDUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.gen.filter.biome.BiomeFilter;
import dev.latvian.kubejs.world.gen.filter.mob.MobFilter;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import me.shedaniel.architectury.registry.Registry;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinKubeJSPlugin extends KubeJSPlugin {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	@Override
	public void addClasses(ScriptType type, ClassFilter filter) {
		filter.allow("java.lang.Number"); // java.lang
		filter.allow("java.lang.String");
		filter.allow("java.lang.Character");
		filter.allow("java.lang.Byte");
		filter.allow("java.lang.Short");
		filter.allow("java.lang.Integer");
		filter.allow("java.lang.Long");
		filter.allow("java.lang.Float");
		filter.allow("java.lang.Double");
		filter.allow("java.lang.Boolean");
		filter.allow("java.lang.Runnable");
		filter.allow("java.lang.Iterable");
		filter.allow("java.lang.Comparable");
		filter.allow("java.lang.CharSequence");

		filter.allow("java.math.BigInteger"); // java.math
		filter.allow("java.math.BigDecimal");

		filter.deny("java.io"); // IO
		filter.allow("java.io.Closeable");
		filter.allow("java.io.Serializable");

		filter.deny("java.nio"); // NIO
		filter.allow("java.nio.ByteOrder");

		filter.allow("java.util"); // Utils
		filter.deny("java.util.jar");
		filter.deny("java.util.zip");

		filter.allow("it.unimi.dsi.fastutil"); // FastUtil

		filter.allow("dev.latvian.kubejs"); // KubeJS
		filter.deny("dev.latvian.kubejs.script");
		filter.deny("dev.latvian.kubejs.mixin");

		filter.allow("net.minecraft"); // Minecraft
		filter.allow("com.mojang.authlib.GameProfile");
		filter.allow("com.mojang.util.UUIDTypeAdapter");
		filter.allow("com.mojang.brigadier");

		filter.allow("me.shedaniel.architectury"); // Architectury

		// Misc
		filter.deny("java.net"); // Networks
		filter.deny("sun"); // Sun
		filter.deny("com.sun"); // Sun
		filter.deny("io.netty"); // Netty
		filter.deny("org.objectweb.asm"); // ASM
		filter.deny("org.spongepowered.asm"); // Sponge ASM
		filter.deny("org.openjdk.nashorn"); // Nashorn
		filter.deny("jdk.nashorn"); // Nashorn
	}

	@Override
	public void addBindings(BindingsEvent event) {
		event.add("global", GLOBAL);

		if (event.type == ScriptType.SERVER) {
			ServerSettings.instance = new ServerSettings();
			event.add("settings", ServerSettings.instance);
		}

		event.addClass("Platform", PlatformWrapper.class);
		event.add("console", event.type.console);
		event.add("events", new ScriptEventsWrapper(event.type.manager.get().events));

		event.addFunction("onEvent", args -> onEvent(event, args), null, IEventHandler.class);
		event.addFunction("java", args -> event.manager.loadJavaClass(event.scope, args), new Class[]{null});

		event.addClass("Utils", UtilsWrapper.class);
		event.addClass("utils", UtilsWrapper.class);
		event.addClass("Text", TextWrapper.class);
		event.addClass("text", TextWrapper.class);
		event.addClass("uuid", UUIDWrapper.class);
		event.addClass("json", JsonWrapper.class);
		event.addClass("Block", BlockWrapper.class);
		event.addClass("block", BlockWrapper.class);
		event.addClass("Item", ItemWrapper.class);
		event.addClass("item", ItemWrapper.class);
		event.addClass("Ingredient", IngredientWrapper.class);
		event.addClass("ingredient", IngredientWrapper.class);
		event.addClass("NBT", NBTWrapper.class);
		event.add("Facing", new FacingWrapper());

		event.addClass("Fluid", FluidWrapper.class);
		event.addClass("fluid", FluidWrapper.class);

		event.addConstant("SECOND", 1000L);
		event.addConstant("MINUTE", 60000L);
		event.addConstant("HOUR", 3600000L);

		event.add("TextColor", TextColor.class);

		for (TextColor color : TextColor.MAP.values()) {
			event.addConstant(color.name.toUpperCase(), color);
		}

		event.add("EquipmentSlot", EquipmentSlot.class);
		event.addConstant("SLOT_MAINHAND", EquipmentSlot.MAINHAND);
		event.addConstant("SLOT_OFFHAND", EquipmentSlot.OFFHAND);
		event.addConstant("SLOT_FEET", EquipmentSlot.FEET);
		event.addConstant("SLOT_LEGS", EquipmentSlot.LEGS);
		event.addConstant("SLOT_CHEST", EquipmentSlot.CHEST);
		event.addConstant("SLOT_HEAD", EquipmentSlot.HEAD);

		event.add("Rarity", RarityWrapper.class);
		event.addConstant("RARITY_COMMON", RarityWrapper.COMMON);
		event.addConstant("RARITY_UNCOMMON", RarityWrapper.UNCOMMON);
		event.addConstant("RARITY_RARE", RarityWrapper.RARE);
		event.addConstant("RARITY_EPIC", RarityWrapper.EPIC);

		event.addConstant("AIR_ITEM", Items.AIR);
		event.addConstant("AIR_BLOCK", Blocks.AIR);

		event.add("ToolType", ToolType.class);
		event.addConstant("TOOL_TYPE_AXE", ToolType.AXE);
		event.addConstant("TOOL_TYPE_PICKAXE", ToolType.PICKAXE);
		event.addConstant("TOOL_TYPE_SHOVEL", ToolType.SHOVEL);
		event.addConstant("TOOL_TYPE_HOE", ToolType.HOE);

		event.add("Hand", InteractionHand.class);
		event.addConstant("MAIN_HAND", InteractionHand.MAIN_HAND);
		event.addConstant("OFF_HAND", InteractionHand.OFF_HAND);

		event.add("DecorationGenerationStep", GenerationStep.Decoration.class);
		event.add("CarvingGenerationStep", GenerationStep.Carving.class);

		KubeJS.PROXY.clientBindings(event);
	}

	private static Object onEvent(BindingsEvent event, Object[] args) {
		for (Object o : ListJS.orSelf(args[0])) {
			event.type.manager.get().events.listen(String.valueOf(o), (IEventHandler) args[1]);
		}

		return null;
	}

	@Override
	public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		// Java / Minecraft //
		typeWrappers.register(String.class, String::valueOf);
		typeWrappers.register(CharSequence.class, String::valueOf);
		typeWrappers.register(ResourceLocation.class, o -> UtilsJS.getMCID(o == null ? null : o.toString()));
		typeWrappers.register(JsonObject.class, MapJS::json);
		typeWrappers.register(JsonArray.class, ListJS::json);
		typeWrappers.register(ItemStack.class, o -> ItemStackJS.of(o).getItemStack());
		typeWrappers.register(CompoundTag.class, MapJS::nbt);
		typeWrappers.register(CollectionTag.class, ListJS::nbt);
		typeWrappers.register(ListTag.class, o -> (ListTag) ListJS.nbt(o));
		typeWrappers.register(UUID.class, UUIDUtilsJS::fromString);
		typeWrappers.register(Pattern.class, UtilsJS::parseRegex);
		typeWrappers.register(Component.class, Text::componentOfObject);
		typeWrappers.register(MutableComponent.class, o -> new TextComponent("").append(Text.componentOfObject(o)));
		typeWrappers.register(BlockPos.class, o -> {
			if (o instanceof BlockPos) {
				return (BlockPos) o;
			} else if (o instanceof List && ((List<?>) o).size() >= 3) {
				return new BlockPos(((Number) ((List<?>) o).get(0)).intValue(), ((Number) ((List<?>) o).get(1)).intValue(), ((Number) ((List<?>) o).get(2)).intValue());
			}

			return BlockPos.ZERO;
		});

		typeWrappers.register(Item.class, o -> ItemStackJS.of(o).getItem());
		wrapRegistry(typeWrappers, Block.class, KubeJSRegistries.blocks());
		wrapRegistry(typeWrappers, Fluid.class, KubeJSRegistries.fluids());
		wrapRegistry(typeWrappers, SoundEvent.class, KubeJSRegistries.soundEvents());

		// KubeJS //
		typeWrappers.register(MapJS.class, MapJS::of);
		typeWrappers.register(ListJS.class, ListJS::of);
		typeWrappers.register(ItemStackJS.class, ItemStackJS::of);
		typeWrappers.register(IngredientJS.class, IngredientJS::of);
		typeWrappers.register(IngredientStackJS.class, o -> IngredientJS.of(o).asIngredientStack());
		typeWrappers.register(Text.class, Text::of);
		typeWrappers.register(BlockStatePredicate.class, BlockStatePredicate::of);
		typeWrappers.register(FluidStackJS.class, FluidStackJS::of);
		typeWrappers.register(RecipeFilter.class, RecipeFilter::of);
		typeWrappers.register(BiomeFilter.class, BiomeFilter::of);
		typeWrappers.register(MobFilter.class, MobFilter::of);
		typeWrappers.register(MaterialJS.class, MaterialListJS.INSTANCE::of);
	}

	private static <T> void wrapRegistry(TypeWrappers typeWrappers, Class<T> c, Registry<T> registry) {
		typeWrappers.register(c, o -> {
			if (o == null) {
				return null;
			} else if (c.isAssignableFrom(o.getClass())) {
				return (T) o;
			}

			return registry.get(new ResourceLocation(o.toString()));
		});
	}
}
