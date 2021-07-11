package dev.latvian.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockModificationEventJS;
import dev.latvian.kubejs.item.ItemModificationEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextTranslate;
import dev.latvian.kubejs.world.WorldJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class UtilsJS {
	public static final Random RANDOM = new Random();
	public static final Pattern REGEX_PATTERN = Pattern.compile("\\/(.*)\\/([a-z]*)");

	public interface TryIO {
		void run() throws IOException;
	}

	public static void init() {
	}

	public static void tryIO(TryIO tryIO) {
		try {
			tryIO.run();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o) {
		return (T) o;
	}

	@Nullable
	public static Pattern parseRegex(Object o) {
		if (o instanceof CharSequence || o instanceof NativeRegExp) {
			return regex(o.toString());
		} else if (o instanceof Pattern) {
			return (Pattern) o;
		}

		return null;
	}

	@Nullable
	public static Pattern regex(String string) {
		if (string.length() < 3) {
			return null;
		}

		Matcher matcher = REGEX_PATTERN.matcher(string);

		if (matcher.matches()) {
			int flags = 0;
			String f = matcher.group(2);

			for (int i = 0; i < f.length(); i++) {
				switch (f.charAt(i)) {
					case 'd':
						flags |= Pattern.UNIX_LINES;
						break;
					case 'i':
						flags |= Pattern.CASE_INSENSITIVE;
						break;
					case 'x':
						flags |= Pattern.COMMENTS;
						break;
					case 'm':
						flags |= Pattern.MULTILINE;
						break;
					case 's':
						flags |= Pattern.DOTALL;
						break;
					case 'u':
						flags |= Pattern.UNICODE_CASE;
						break;
					case 'U':
						flags |= Pattern.UNICODE_CHARACTER_CLASS;
						break;
				}
			}

			return Pattern.compile(matcher.group(1), flags);
		}

		return null;
	}

	public static String toRegexString(Pattern pattern) {
		StringBuilder sb = new StringBuilder("/");
		sb.append(pattern.pattern());
		sb.append('/');

		int flags = pattern.flags();

		if ((flags & Pattern.UNIX_LINES) != 0) {
			sb.append('d');
		}

		if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
			sb.append('i');
		}

		if ((flags & Pattern.COMMENTS) != 0) {
			sb.append('x');
		}

		if ((flags & Pattern.MULTILINE) != 0) {
			sb.append('m');
		}

		if ((flags & Pattern.DOTALL) != 0) {
			sb.append('s');
		}

		if ((flags & Pattern.UNICODE_CASE) != 0) {
			sb.append('u');
		}

		if ((flags & Pattern.UNICODE_CHARACTER_CLASS) != 0) {
			sb.append('U');
		}

		return sb.toString();
	}

	public static void queueIO(Runnable runnable) {
		/*FIXME: ThreadedFileIOBase.getThreadedIOInstance().queueIO(() -> {

			try
			{
				runnable.run();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			return false;
		});
		 */

		try {
			runnable.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Path getFile(String path) throws IOException {
		return KubeJS.verifyFilePath(KubeJS.getGameDirectory().resolve(path));
	}

	@Nullable
	public static Object copy(@Nullable Object o) {
		if (o instanceof Copyable) {
			return ((Copyable) o).copy();
		} else if (o instanceof JsonElement) {
			return JsonUtilsJS.copy((JsonElement) o);
		} else if (o instanceof Tag) {
			return ((Tag) o).copy();
		}

		return o;
	}

	@Nullable
	public static Object wrap(@Nullable Object o, JSObjectType type) {
		//Primitives and already normalized objects
		if (o == null || o instanceof WrappedJS || o instanceof Number || o instanceof Character || o instanceof String || o instanceof Enum || o.getClass().isPrimitive() && !o.getClass().isArray()) {
			return o;
		} else if (o instanceof CharSequence || o instanceof ResourceLocation) {
			return o.toString();
		} else if (o instanceof Wrapper) {
			return wrap(((Wrapper) o).unwrap(), type);
		}
		// Vanilla text component
		else if (o instanceof Component) {
			Text t = new TextString("");

			List<Component> list = new ArrayList<>();
			list.add((Component) o);
			list.addAll(((Component) o).getSiblings());

			for (Component c : list) {
				Text t1;

				if (c instanceof TranslatableComponent) {
					t1 = new TextTranslate(((TranslatableComponent) c).getKey(), ((TranslatableComponent) c).getArgs());
				} else {
					t1 = new TextString(c.getContents());
				}

				//TODO: Replace with AT
				t1.bold(c.getStyle().isBold());
				t1.italic(c.getStyle().isItalic());
				t1.underlined(c.getStyle().isUnderlined());
				t1.strikethrough(c.getStyle().isStrikethrough());
				t1.obfuscated(c.getStyle().isObfuscated());
				t1.insertion(c.getStyle().getInsertion());

				ClickEvent ce = c.getStyle().getClickEvent();

				if (ce != null) {
					if (ce.getAction() == ClickEvent.Action.RUN_COMMAND) {
						t1.click("command:" + ce.getValue());
					} else if (ce.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
						t1.click("suggest_command:" + ce.getValue());
					} else if (ce.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
						t1.click("copy:" + ce.getValue());
					} else if (ce.getAction() == ClickEvent.Action.OPEN_URL) {
						t1.click(ce.getValue());
					}
				}

				HoverEvent he = c.getStyle().getHoverEvent();

				if (he != null && he.getAction() == HoverEvent.Action.SHOW_TEXT) {
					t1.hover(Text.of(he.getValue(HoverEvent.Action.SHOW_TEXT)));
				}

				t.append(t1);
			}

			return t;
		}
		// Maps
		else if (o instanceof Map) {
			if (!type.checkMap()) {
				return null;
			}

			MapJS map = new MapJS(((Map) o).size());
			map.putAll((Map) o);
			return map;
		}
		// Lists, Collections, Iterables, GSON Arrays
		else if (o instanceof Iterable) {
			if (!type.checkList()) {
				return null;
			}

			ListJS list = new ListJS();

			for (Object o1 : (Iterable) o) {
				list.add(o1);
			}

			return list;
		}
		// Arrays (and primitive arrays are a pain)
		else if (o.getClass().isArray()) {
			if (type.checkList()) {
				return ListJS.ofArray(o);
			} else {
				return null;
			}
		}
		// GSON Primitives
		else if (o instanceof JsonPrimitive) {
			return JsonUtilsJS.toPrimitive((JsonPrimitive) o);
		}
		// GSON Objects
		else if (o instanceof JsonObject) {
			if (!type.checkMap()) {
				return null;
			}

			MapJS map = new MapJS(((JsonObject) o).size());

			for (Map.Entry<String, JsonElement> entry : ((JsonObject) o).entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}

			return map;
		}
		// GSON and NBT Null
		else if (o instanceof JsonNull || o instanceof EndTag) {
			return null;
		}
		// NBT
		else if (o instanceof CompoundTag) {
			if (!type.checkMap()) {
				return null;
			}

			CompoundTag nbt = (CompoundTag) o;

			MapJS map = new MapJS(nbt.size());

			for (String s : nbt.getAllKeys()) {
				map.put(s, nbt.get(s));
			}

			return map;
		} else if (o instanceof NumericTag) {
			return ((NumericTag) o).getAsNumber();
		} else if (o instanceof StringTag) {
			return ((StringTag) o).getAsString();
		}

		return o;
	}

	public static int parseInt(@Nullable Object object, int def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number) {
			return ((Number) object).intValue();
		}

		try {
			String s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Integer.parseInt(s);
		} catch (Exception ex) {
			return def;
		}
	}

	public static double parseDouble(@Nullable Object object, double def) {
		if (object == null) {
			return def;
		} else if (object instanceof Number) {
			return ((Number) object).doubleValue();
		}

		try {
			String s = object.toString();

			if (s.isEmpty()) {
				return def;
			}

			return Double.parseDouble(String.valueOf(object));
		} catch (Exception ex) {
			return def;
		}
	}

	public static Stat<ResourceLocation> getStat(ResourceLocation id) {
		return Stats.CUSTOM.get(id);
	}

	public static ToolType getToolType(String id) {
		return ToolType.byName(id);
	}

	public static WorldJS getWorld(Level world) {
		if (world.isClientSide()) {
			return getClientWorld();
		} else {
			return ServerJS.instance.getWorld(world);
		}
	}

	public static WorldJS getClientWorld() {
		return KubeJS.PROXY.getClientWorld();
	}

	@Nullable
	public static MobEffect getPotion(ResourceLocation id) {
		return Registry.MOB_EFFECT.get(id);
	}

	public static String getID(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "minecraft:air";
		}

		if (s.indexOf(':') == -1) {
			return "minecraft:" + s;
		}

		return s;
	}

	public static ResourceLocation getMCID(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return new ResourceLocation("minecraft:air");
		}

		return new ResourceLocation(s);
	}

	public static String getNamespace(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "minecraft";
		}

		int i = s.indexOf(':');
		return i == -1 ? "minecraft" : s.substring(0, i);
	}

	public static String getPath(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "air";
		}

		int i = s.indexOf(':');
		return i == -1 ? s : s.substring(i + 1);
	}

	public static BlockState parseBlockState(String string) {
		if (string.isEmpty()) {
			return Blocks.AIR.defaultBlockState();
		}

		int i = string.indexOf('[');
		boolean hasProperties = i >= 0 && string.indexOf(']') == string.length() - 1;
		BlockState state = KubeJSRegistries.blocks().get(new ResourceLocation(hasProperties ? string.substring(0, i) : string)).defaultBlockState();

		if (hasProperties) {
			for (String s : string.substring(i + 1, string.length() - 1).split(",")) {
				String[] s1 = s.split("=", 2);

				if (s1.length == 2 && !s1[0].isEmpty() && !s1[1].isEmpty()) {
					Property<?> p = state.getBlock().getStateDefinition().getProperty(s1[0]);

					if (p != null) {
						Optional<?> o = p.getValue(s1[1]);

						if (o.isPresent()) {
							state = state.setValue(p, UtilsJS.cast(o.get()));
						}
					}
				}
			}
		}

		return state;
	}

	public static void postModificationEvents() {
		new BlockModificationEventJS().post(ScriptType.STARTUP, KubeJSEvents.BLOCK_MODIFICATION);
		new ItemModificationEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_MODIFICATION);
	}

	private static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
		if (element == null || element.isJsonNull()) {
			stream.writeByte('-');
		} else if (element instanceof JsonArray) {
			stream.writeByte('[');
			for (JsonElement e : (JsonArray) element) {
				writeJsonHash(stream, e);
			}
		} else if (element instanceof JsonObject) {
			stream.writeByte('{');
			for (Map.Entry<String, JsonElement> e : ((JsonObject) element).entrySet()) {
				stream.writeBytes(e.getKey());
				writeJsonHash(stream, e.getValue());
			}
		} else if (element instanceof JsonPrimitive) {
			stream.writeByte('=');
			if (((JsonPrimitive) element).isBoolean()) {
				stream.writeBoolean(element.getAsBoolean());
			} else if (((JsonPrimitive) element).isNumber()) {
				stream.writeDouble(element.getAsDouble());
			} else {
				stream.writeBytes(element.getAsString());
			}
		} else {
			stream.writeByte('?');
			stream.writeInt(element.hashCode());
		}
	}

	public static byte[] getJsonHashBytes(JsonElement json) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writeJsonHash(new DataOutputStream(baos), json);
		} catch (IOException ex) {
			ex.printStackTrace();
			int h = json.hashCode();
			return new byte[]{(byte) (h >> 24), (byte) (h >> 16), (byte) (h >> 8), (byte) (h >> 0)};
		}

		return baos.toByteArray();
	}
}