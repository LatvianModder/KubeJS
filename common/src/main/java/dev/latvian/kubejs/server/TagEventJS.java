package dev.latvian.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.core.TagBuilderKJS;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = {
				KubeJSEvents.ITEM_TAGS,
				KubeJSEvents.BLOCK_TAGS,
				KubeJSEvents.ENTITY_TYPE_TAGS,
				KubeJSEvents.FLUID_TAGS
		}
)
public class TagEventJS<T> extends EventJS {
	private static String getIdOfEntry(String s) {
		if (s.length() > 0 && s.charAt(s.length() - 1) == '?') {
			return s.substring(0, s.length() - 1);
		}

		return s;
	}

	@Nullable
	private static List<Predicate<String>> parsePriorityList(@Nullable Object o) {
		if (o == null) {
			return null;
		}

		List<Predicate<String>> list = new ArrayList<>();

		for (Object o1 : ListJS.orSelf(o)) {
			String s = String.valueOf(o1);

			if (s.startsWith("@")) {
				String m = s.substring(1);
				list.add(id -> id.startsWith(m));
			} else if (s.startsWith("!@")) {
				String m = s.substring(2);
				list.add(id -> !id.startsWith(m));
			} else {
				list.add(id -> id.equals(s));
			}
		}

		return list.isEmpty() ? null : list;
	}

	public static class TagWrapper<T> {
		private final TagEventJS<T> event;
		private final ResourceLocation id;
		private final SetTag.Builder builder;
		private final List<Tag.BuilderEntry> proxyList;
		private List<Predicate<String>> priorityList;

		private TagWrapper(TagEventJS<T> e, ResourceLocation i, SetTag.Builder t) {
			event = e;
			id = i;
			builder = t;
			proxyList = ((TagBuilderKJS) builder).getProxyListKJS();
			priorityList = null;
		}

		@Override
		public String toString() {
			return event.type + ":" + id;
		}

		public TagWrapper<T> add(Object ids) {
			for (Object o : ListJS.orSelf(ids)) {
				String s = String.valueOf(o);

				if (s.startsWith("#")) {
					TagWrapper<T> w = event.get(new ResourceLocation(s.substring(1)));
					builder.addTag(w.id, KubeJS.MOD_ID);
					event.addedCount += w.proxyList.size();

					if (ScriptType.SERVER.console.shouldPrintDebug()) {
						ScriptType.SERVER.console.debug("+ " + this + " // " + w.id);
					}
				} else {
					Pattern pattern = UtilsJS.parseRegex(s);

					if (pattern != null && event.actualRegistry != null) {
						for (ResourceLocation sid : event.actualRegistry.getIds()) {
							if (pattern.matcher(sid.toString()).find()) {
								Optional<T> v = event.registry.apply(sid);

								if (v.isPresent()) {
									builder.addElement(sid, KubeJS.MOD_ID);
									event.addedCount++;

									if (ScriptType.SERVER.console.shouldPrintDebug()) {
										ScriptType.SERVER.console.debug("+ " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
									}
								} else {
									ScriptType.SERVER.console.error("+ " + this + " // " + s + " [Not found!]");
								}
							}
						}
					} else {
						ResourceLocation sid = new ResourceLocation(s);
						Optional<T> v = event.registry.apply(sid);

						if (v.isPresent()) {
							builder.addElement(sid, KubeJS.MOD_ID);
							event.addedCount++;

							if (ScriptType.SERVER.console.shouldPrintDebug()) {
								ScriptType.SERVER.console.debug("+ " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
							}
						} else {
							ScriptType.SERVER.console.error("+ " + this + " // " + s + " [Not found!]");
						}
					}
				}
			}

			return this;
		}

		public TagWrapper<T> remove(Object ids) {
			for (Object o : ListJS.orSelf(ids)) {
				String s = String.valueOf(o);

				if (s.startsWith("#")) {
					TagWrapper<T> w = event.get(new ResourceLocation(s.substring(1)));
					String entryId = w.id.toString();
					int originalSize = proxyList.size();
					proxyList.removeIf(proxy -> getIdOfEntry(proxy.getEntry().toString()).equals(s));
					int removedCount = proxyList.size() - originalSize;

					if (removedCount == 0) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ScriptType.SERVER.console.warn(s + " didn't contain tag " + this + ", skipped");
						}
					} else {
						event.removedCount -= removedCount;

						if (ScriptType.SERVER.console.shouldPrintDebug()) {
							ScriptType.SERVER.console.debug("- " + this + " // " + w.id);
						}
					}
				} else {
					Pattern pattern = UtilsJS.parseRegex(s);

					if (pattern != null && event.actualRegistry != null) {
						for (ResourceLocation sid : event.actualRegistry.getIds()) {
							if (pattern.matcher(sid.toString()).find()) {
								Optional<T> v = event.registry.apply(sid);

								if (v.isPresent()) {
									int originalSize = proxyList.size();
									proxyList.removeIf(proxy -> getIdOfEntry(proxy.getEntry().toString()).equals(s));
									int removedCount = proxyList.size() - originalSize;

									if (removedCount == 0) {
										if (ServerSettings.instance.logSkippedRecipes) {
											ScriptType.SERVER.console.warn(s + " didn't contain tag " + id + ", skipped");
										}
									} else {
										event.removedCount -= removedCount;

										if (ScriptType.SERVER.console.shouldPrintDebug()) {
											ScriptType.SERVER.console.debug("- " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
										}
									}
								} else {
									ScriptType.SERVER.console.error("- " + this + " // " + s + " [Not found!]");
								}
							}
						}
					} else {
						ResourceLocation sid = new ResourceLocation(s);
						Optional<T> v = event.registry.apply(sid);

						if (v.isPresent()) {
							int originalSize = proxyList.size();
							proxyList.removeIf(proxy -> getIdOfEntry(proxy.getEntry().toString()).equals(s));
							int removedCount = proxyList.size() - originalSize;

							if (removedCount == 0) {
								if (ServerSettings.instance.logSkippedRecipes) {
									ScriptType.SERVER.console.warn(s + " didn't contain tag " + id + ", skipped");
								}
							} else {
								event.removedCount -= removedCount;

								if (ScriptType.SERVER.console.shouldPrintDebug()) {
									ScriptType.SERVER.console.debug("- " + this + " // " + s + " [" + v.get().getClass().getName() + "]");
								}
							}
						} else {
							ScriptType.SERVER.console.error("- " + this + " // " + s + " [Not found!]");
						}
					}
				}
			}

			return this;
		}

		public TagWrapper<T> removeAll() {
			if (ScriptType.SERVER.console.shouldPrintDebug()) {
				ScriptType.SERVER.console.debug("- " + this + " // (all)");
			}

			if (!proxyList.isEmpty()) {
				event.removedCount += proxyList.size();
				proxyList.clear();
			} else if (ServerSettings.instance.logSkippedRecipes) {
				ScriptType.SERVER.console.warn("Tag " + this + " didn't contain any elements, skipped");
			}

			return this;
		}

		public void setPriorityList(@Nullable Object o) {
			priorityList = parsePriorityList(o);
		}

		private void gatherAllItemIDs(HashSet<String> set, Tag.Entry entry) {
			if (entry instanceof Tag.ElementEntry) {
				set.add(entry.toString());
			} else if (entry instanceof Tag.TagEntry) {
				TagWrapper<T> w = event.tags.get(new ResourceLocation(entry.toString().substring(1)));

				if (w != null && w != this) {
					for (Tag.BuilderEntry proxy : w.proxyList) {
						gatherAllItemIDs(set, proxy.getEntry());
					}
				}
			}
		}

		/**
		 * Yes its not as efficient as it could be, but this doesn't get run too often. This way it keeps order of original tags.
		 */
		public boolean sort() {
			List<List<Tag.BuilderEntry>> listOfLists = new ArrayList<>();

			for (int i = 0; i < priorityList.size() + 1; i++) {
				listOfLists.add(new ArrayList<>());
			}

			for (Tag.BuilderEntry proxy : proxyList) {
				boolean added = false;

				HashSet<String> set = new HashSet<>();
				gatherAllItemIDs(set, proxy.getEntry());

				for (String id : set) {
					for (int i = 0; i < priorityList.size(); i++) {
						if (priorityList.get(i).test(id)) {
							listOfLists.get(i).add(proxy);
							added = true;
							break;
						}
					}
				}

				if (!added) {
					listOfLists.get(priorityList.size()).add(proxy);
				}
			}

			List<Tag.BuilderEntry> proxyList0 = new ArrayList<>(proxyList);

			proxyList.clear();

			for (List<Tag.BuilderEntry> list : listOfLists) {
				proxyList.addAll(list);
			}

			if (!proxyList0.equals(proxyList)) {
				if (ScriptType.SERVER.console.shouldPrintDebug()) {
					ScriptType.SERVER.console.debug("* Re-arranged " + this);
				}

				return true;
			}

			return false;
		}
	}

	private final String type;
	private final Map<ResourceLocation, SetTag.Builder> map;
	private final Function<ResourceLocation, Optional<T>> registry;
	private Map<ResourceLocation, TagWrapper<T>> tags;
	private int addedCount;
	private int removedCount;
	private List<Predicate<String>> globalPriorityList;
	private me.shedaniel.architectury.registry.Registry<T> actualRegistry;

	public TagEventJS(String t, Map<ResourceLocation, SetTag.Builder> m, Function<ResourceLocation, Optional<T>> r) {
		type = t;
		map = m;
		registry = r;
		addedCount = 0;
		removedCount = 0;
		globalPriorityList = null;
		actualRegistry = null;

		switch (type) {
			case "items":
				actualRegistry = UtilsJS.cast(KubeJSRegistries.items());
				break;
			case "blocks":
				actualRegistry = UtilsJS.cast(KubeJSRegistries.blocks());
				break;
			case "fluids":
				actualRegistry = UtilsJS.cast(KubeJSRegistries.fluids());
				break;
			case "entity_types":
				actualRegistry = UtilsJS.cast(KubeJSRegistries.entityTypes());
				break;
		}
	}

	public String getType() {
		return type;
	}

	public void post(String event) {
		Path dumpFile = KubeJSPaths.EXPORTED.resolve("tags/" + type + ".txt");

		if (!Files.exists(dumpFile)) {
			try {
				if (!Files.exists(dumpFile.getParent())) {
					Files.createDirectories(dumpFile.getParent());
				}

				List<String> lines = new ArrayList<>();

				map.forEach((tagId, tagBuilder) -> {
					lines.add("");
					lines.add("#" + tagId);
					tagBuilder.getEntries().forEach(builderEntry -> lines.add("- " + builderEntry.getEntry()));
				});

				lines.add(0, "To refresh this file, delete it and run /reload command again! Last updated: " + DateFormat.getDateTimeInstance().format(new Date()));

				Files.write(dumpFile, lines);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		tags = new HashMap<>();

		for (Map.Entry<ResourceLocation, SetTag.Builder> entry : map.entrySet()) {
			TagWrapper<T> w = new TagWrapper<>(this, entry.getKey(), entry.getValue());
			tags.put(entry.getKey(), w);
			ScriptType.SERVER.console.debug(type + "/#" + entry.getKey() + "; " + w.proxyList.size());
		}

		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, event);
		ScriptType.SERVER.console.setLineNumber(false);

		int reordered = 0;

		for (TagWrapper<T> wrapper : tags.values()) {
			if (wrapper.priorityList == null) {
				wrapper.priorityList = globalPriorityList;
			}

			if (wrapper.priorityList != null && wrapper.sort()) {
				reordered++;
			}
		}

		if (ServerSettings.dataExport != null && actualRegistry != null) {
			JsonObject tj = ServerSettings.dataExport.getAsJsonObject("tags");

			if (tj == null) {
				tj = new JsonObject();
				ServerSettings.dataExport.add("tags", tj);
			}

			JsonObject tj1 = tj.getAsJsonObject(type);

			if (tj1 == null) {
				tj1 = new JsonObject();
				tj.add(type, tj1);
			}

			for (Map.Entry<ResourceLocation, SetTag.Builder> entry : map.entrySet()) {
				JsonArray a = new JsonArray();
				entry.getValue().getEntries().forEach(e -> a.add(e.getEntry().toString()));
				tj1.add(entry.getKey().toString(), a);
			}
		}

		ScriptType.SERVER.console.info("[" + type + "] Found " + tags.size() + " tags, added " + addedCount + " objects, removed " + removedCount + " objects"/*, reordered " + reordered + " tags"*/);
	}

	public TagWrapper<T> get(ResourceLocation id) {
		TagWrapper<T> t = tags.get(id);

		if (t == null) {
			t = new TagWrapper<>(this, id, SetTag.Builder.tag());
			tags.put(id, t);
			map.put(id, t.builder);
		}

		return t;
	}

	public TagWrapper<T> add(ResourceLocation tag, Object ids) {
		return get(tag).add(ids);
	}

	public TagWrapper<T> remove(ResourceLocation tag, Object ids) {
		return get(tag).remove(ids);
	}

	public TagWrapper<T> removeAll(ResourceLocation tag) {
		return get(tag).removeAll();
	}

	public void removeAllTagsFrom(Object ids) {
		for (Object o : ListJS.orSelf(ids)) {
			String id = String.valueOf(o);

			for (TagWrapper<T> tagWrapper : tags.values()) {
				tagWrapper.proxyList.removeIf(proxy -> getIdOfEntry(proxy.getEntry().toString()).equals(id));
			}
		}
	}

	public void setGlobalPriorityList(@Nullable Object o) {
		globalPriorityList = parsePriorityList(o);
	}
}