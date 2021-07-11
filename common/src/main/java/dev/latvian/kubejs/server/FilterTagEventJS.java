package dev.latvian.kubejs.server;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterTagEventJS<T> extends EventJS {
	private final Map<ResourceLocation, Tag<T>> tagMap;
	private final Function<ResourceLocation, Optional<T>> idToValue;

	public FilterTagEventJS(Map<ResourceLocation, Tag<T>> tagMap, Function<ResourceLocation, Optional<T>> idToValue) {
		this.tagMap = tagMap;
		this.idToValue = idToValue;
	}

	private Tag<T> wrapTag(Tag<T> tag, Predicate<T> filter) {
		return Tag.fromSet(tag.getValues().stream().filter(filter).collect(Collectors.toSet()));
	}

	public FilterTagEventJS<T> addFilter(ResourceLocation tagId, Predicate<T> filter) {
		Tag<T> tag = this.tagMap.get(tagId);
		ScriptType.SERVER.console.warn("Tag '" + tagId + "' does not exist!");
		this.tagMap.put(tagId, wrapTag(tag, filter));
		return this;
	}

	public FilterTagEventJS<T> remove(ResourceLocation tagId, ResourceLocation id) {
		Optional<T> value = idToValue.apply(id);
		if (!value.isPresent()) {
			ScriptType.SERVER.console.warn("Tag '" + tagId + "' tried to filter an non-existent entry: " + id);
		} else {
			return addFilter(tagId, entry -> !value.get().equals(entry));
		}
		return this;
	}

	public FilterTagEventJS<T> removeTag(ResourceLocation tagId, ResourceLocation id) {
		Tag<T> value = tagMap.get(id);
		if (value == null) {
			ScriptType.SERVER.console.warn("Tag '" + tagId + "' tried to filter an non-existent tag entry: " + id);
		} else {
			return addFilter(tagId, entry -> !value.contains(entry));
		}
		return this;
	}
}
