package dev.latvian.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import dev.latvian.kubejs.docs.KubeJSEvent;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.util.DynamicMap;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
@KubeJSEvent(
		server = { KubeJSEvents.RECIPES }
)
public class RecipeEventJS extends EventJS {
	private static final Map<Class, MethodHandle> TR_CONSTRUCTORS = new ConcurrentHashMap<>();
	private static final Map<Class, MethodHandle> TR_DESERIALIZERS = new ConcurrentHashMap<>();
	public static final ResourceLocation FORGE_CONDITIONAL = new ResourceLocation("forge:conditional");

	public static RecipeEventJS instance;

	private final Map<ResourceLocation, RecipeTypeJS> typeMap;
	private final List<Recipe<?>> fallbackedRecipes = new ArrayList<>();
	private final List<RecipeJS> originalRecipes;
	private final List<RecipeJS> addedRecipes;
	private final Set<RecipeJS> removedRecipes;
	private final Map<ResourceLocation, RecipeFunction> functionMap;

	private final DynamicMap<DynamicMap<RecipeFunction>> recipeFunctions;
	private AtomicInteger modifiedRecipes;

	public RecipeEventJS(Map<ResourceLocation, RecipeTypeJS> t) {
		typeMap = t;
		originalRecipes = new ArrayList<>();

		ScriptType.SERVER.console.info("Scanning recipes...");

		addedRecipes = new ArrayList<>();
		removedRecipes = new HashSet<>();
		functionMap = new HashMap<>();
		recipeFunctions = new DynamicMap<>(n -> new DynamicMap<>(p -> getRecipeFunction(new ResourceLocation(n, p))));

		SpecialRecipeSerializerManager.INSTANCE.reset();
		SpecialRecipeSerializerManager.INSTANCE.post(ScriptType.SERVER, KubeJSEvents.RECIPES_SERIALIZER_SPECIAL_FLAG);
	}

	private static MethodHandle getTRRecipeConstructor(Recipe<?> resultRecipe, RecipeJS recipe) throws NoSuchMethodException, IllegalAccessException {
		Class<? extends Recipe> recipeClass = resultRecipe.getClass();
		MethodHandle handle = TR_CONSTRUCTORS.get(recipeClass);
		if (handle != null) {
			return handle;
		}

		handle = MethodHandles.lookup().findConstructor(recipeClass, MethodType.methodType(void.class, recipe.type.serializer.getClass(), ResourceLocation.class));
		TR_CONSTRUCTORS.put(recipeClass, handle);
		return handle;
	}

	private static MethodHandle getTRRecipeSerializer(Recipe<?> resultRecipe) throws NoSuchMethodException, IllegalAccessException {
		Class<? extends Recipe> recipeClass = resultRecipe.getClass();
		MethodHandle handle = TR_DESERIALIZERS.get(recipeClass);
		if (handle != null) {
			return handle;
		}

		handle = MethodHandles.lookup().findVirtual(resultRecipe.getClass(), "deserialize", MethodType.methodType(void.class, JsonObject.class));
		TR_DESERIALIZERS.put(recipeClass, handle);
		return handle;
	}

	public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> jsonMap) {
		RecipeJS.itemErrors = false;
		ScriptType.SERVER.console.setLineNumber(true);
		Stopwatch timer = Stopwatch.createStarted();

		JsonObject allRecipeMap = new JsonObject();

		for (Map.Entry<ResourceLocation, JsonObject> entry : jsonMap.entrySet()) {
			ResourceLocation recipeId = entry.getKey();

			if (Platform.isForge() && recipeId.getPath().startsWith("_")) {
				continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			}

			String recipeIdAndType = recipeId + "[unknown:type]";

			try {
				JsonObject json = entry.getValue();

				ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(json, "type"));

				recipeIdAndType = recipeId + "[" + type + "]";

				if (!processConditions(json, "conditions")) {
					if (ServerSettings.instance.logSkippedRecipes) {
						ScriptType.SERVER.console.info("Skipping loading recipe " + recipeIdAndType + " as it's conditions were not met");
					}

					continue;
				}

				if (type.equals(FORGE_CONDITIONAL)) {
					JsonArray items = GsonHelper.getAsJsonArray(json, "recipes");
					boolean skip = true;

					for (int idx = 0; idx < items.size(); idx++) {
						JsonElement e = items.get(idx);

						if (!e.isJsonObject()) {
							throw new RecipeExceptionJS("Invalid recipes entry at index " + idx + " Must be JsonObject");
						}

						JsonObject o = e.getAsJsonObject();

						if (processConditions(o, "conditions")) {
							json = o.get("recipe").getAsJsonObject();
							type = new ResourceLocation(GsonHelper.getAsString(json, "type"));
							recipeIdAndType = recipeId + "[" + type + "]";
							skip = false;
							break;
						}
					}

					if (skip) {
						if (ServerSettings.instance.logSkippedRecipes) {
							ScriptType.SERVER.console.info("Skipping loading recipe " + recipeIdAndType + " as it's conditions were not met");
						}

						continue;
					}
				}

				RecipeFunction function = getRecipeFunction(type);

				if (function.type == null) {
					throw new MissingRecipeFunctionException("Unknown recipe type!").fallback();
				}

				RecipeJS recipe = function.type.factory.get();
				recipe.id = recipeId;
				recipe.type = function.type;
				recipe.json = json;
				recipe.originalRecipe = function.type.serializer.fromJson(recipeId, json);

				if (recipe.originalRecipe == null) {
					if (ServerSettings.instance.logSkippedRecipes) {
						ScriptType.SERVER.console.info("Skipping loading recipe " + recipeIdAndType + " as it's conditions were not met");
					}

					continue;
				}

				recipe.deserializeJson();
				originalRecipes.add(recipe);

				if (ScriptType.SERVER.console.shouldPrintDebug()) {
					if (SpecialRecipeSerializerManager.INSTANCE.isSpecial(recipe.originalRecipe)) {
						ScriptType.SERVER.console.debug("Loaded recipe " + recipeIdAndType + ": <dynamic>");
					} else {
						ScriptType.SERVER.console.debug("Loaded recipe " + recipeIdAndType + ": " + recipe.getFromToString());
					}
				}

				if (ServerSettings.dataExport != null) {
					JsonObject exp = new JsonObject();
					exp.add("recipe", json);

					if (!recipe.inputItems.isEmpty()) {
						JsonArray array = new JsonArray();

						for (IngredientJS in : recipe.inputItems) {
							array.add(in.toJson());
						}

						exp.add("inputs", array);
					}

					if (!recipe.outputItems.isEmpty()) {
						JsonArray array = new JsonArray();

						for (ItemStackJS out : recipe.outputItems) {
							array.add(out.toResultJson());
						}

						exp.add("outputs", array);
					}

					allRecipeMap.add(recipe.getId(), exp);
				}
			} catch (Throwable ex) {
				if (!(ex instanceof RecipeExceptionJS) || ((RecipeExceptionJS) ex).fallback) {
					if (ServerSettings.instance.logErroringRecipes) {
						ScriptType.SERVER.console.warn("Failed to parse recipe '" + recipeIdAndType + "'! Falling back to vanilla", ex);
					}

					try {
						fallbackedRecipes.add(Objects.requireNonNull(RecipeManager.fromJson(recipeId, entry.getValue())));
					} catch (NullPointerException | IllegalArgumentException | JsonParseException ex2) {
						if (ServerSettings.instance.logErroringRecipes) {
							ScriptType.SERVER.console.warn("Failed to parse recipe " + recipeIdAndType, ex2);
						}
					} catch (Exception ex3) {
						ScriptType.SERVER.console.warn("Failed to parse recipe " + recipeIdAndType + ":");
						ex3.printStackTrace();
					}
				} else if (ServerSettings.instance.logErroringRecipes) {
					ScriptType.SERVER.console.warn("Failed to parse recipe '" + recipeIdAndType + "'", ex);
				}
			}
		}

		MutableInt removed = new MutableInt(0), added = new MutableInt(0), failed = new MutableInt(0), fallbacked = new MutableInt(0);
		modifiedRecipes = new AtomicInteger(0);

		ScriptType.SERVER.console.getLogger().info("Found {} recipes and {} failed recipes in {}", originalRecipes.size(), fallbackedRecipes.size(), timer.stop());
		timer.reset().start();
		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		ScriptType.SERVER.console.setLineNumber(false);
		ScriptType.SERVER.console.getLogger().info("Posted recipe events in {}", timer.stop());

		Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipeMap = new HashMap<>();

		timer.reset().start();
		originalRecipes.stream()
				.filter(recipe -> {
					if (removedRecipes.contains(recipe)) {
						removed.increment();
						return false;
					}
					return true;
				})
				.map(recipe -> {
					try {
						recipe.serializeJson();
						Recipe<?> resultRecipe = Objects.requireNonNull(recipe.type.serializer.fromJson(recipe.getOrCreateId(), recipe.json));
						if (Platform.isFabric()) {
							// Fabric: we love tech reborn
							if (recipe.type.serializer.getClass().getName().contains("RebornRecipeType")) {
								MethodHandle constructor = getTRRecipeConstructor(resultRecipe, recipe);
								resultRecipe = (Recipe<?>) constructor.invoke(recipe.type.serializer, recipe.getOrCreateId());
								getTRRecipeSerializer(resultRecipe).invoke(resultRecipe, recipe.json);
							}
						}
						recipe.originalRecipe = resultRecipe;
					} catch (Throwable ex) {
						ScriptType.SERVER.console.warn("Error parsing recipe " + recipe + ": " + recipe.json, ex);
						failed.increment();
					}
					return recipe.originalRecipe;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, Function.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					//modified.add(map.size());
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});
		fallbackedRecipes.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, Function.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					fallbacked.add(map.size());
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});
		ScriptType.SERVER.console.getLogger().info("Modified & removed recipes in {}", timer.stop());

		timer.reset().start();
		addedRecipes.stream()
				.map(recipe -> {
					try {
						recipe.serializeJson();
						Recipe<?> resultRecipe = Objects.requireNonNull(recipe.type.serializer.fromJson(recipe.getOrCreateId(), recipe.json));
						if (Platform.isFabric()) {
							// Fabric: we love tech reborn
							if (recipe.type.serializer.getClass().getName().contains("RebornRecipeType")) {
								MethodHandle constructor = getTRRecipeConstructor(resultRecipe, recipe);
								resultRecipe = (Recipe<?>) constructor.invoke(recipe.type.serializer, recipe.getOrCreateId());
								getTRRecipeSerializer(resultRecipe).invoke(resultRecipe, recipe.json);
							}
						}
						recipe.originalRecipe = resultRecipe;
					} catch (Throwable ex) {
						ScriptType.SERVER.console.warn("Error creating recipe " + recipe + ": " + recipe.json, ex);
						failed.increment();
					}
					return recipe.originalRecipe;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, Function.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					added.add(map.size());
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});

		if (ServerSettings.dataExport != null) {
			for (RecipeJS r : removedRecipes) {
				JsonElement e = allRecipeMap.get(r.getId());

				if (e instanceof JsonObject) {
					((JsonObject) e).addProperty("removed", true);
				}
			}

			ServerSettings.dataExport.add("recipes", allRecipeMap);
		}

		ScriptType.SERVER.console.getLogger().info("Added recipes in {}", timer.stop());
		pingNewRecipes(newRecipeMap);
		((RecipeManagerKJS) recipeManager).setRecipesKJS(newRecipeMap);
		ScriptType.SERVER.console.getLogger().info("Added {} recipes, removed {} recipes, modified {} recipes, with {} failed recipes and {} fall-backed recipes", added.getValue(), removed.getValue(), modifiedRecipes.get(), failed.getValue(), fallbacked.getValue());
		RecipeJS.itemErrors = false;
	}

	@ExpectPlatform
	private static void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
	}

	public DynamicMap<DynamicMap<RecipeFunction>> getRecipes() {
		return recipeFunctions;
	}

	public RecipeJS addRecipe(RecipeJS r, RecipeTypeJS type, ListJS args1) {
		addedRecipes.add(r);

		if (ServerSettings.instance.logAddedRecipes) {
			ScriptType.SERVER.console.info("+ " + r.getType() + ": " + r.getFromToString());
		} else if (ScriptType.SERVER.console.shouldPrintDebug()) {
			ScriptType.SERVER.console.debug("+ " + r.getType() + ": " + r.getFromToString());
		}

		return r;
	}

	public RecipeFilter customFilter(RecipeFilter filter) {
		return filter;
	}

	public void forEachRecipe(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			originalRecipes.forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			originalRecipes.stream().filter(filter).forEach(consumer);
		}
	}

	public void forEachRecipeAsync(RecipeFilter filter, Consumer<RecipeJS> consumer) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			originalRecipes.parallelStream().forEach(consumer);
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			originalRecipes.parallelStream().filter(filter).forEach(consumer);
		}
	}

	public int countRecipes(RecipeFilter filter) {
		if (filter == RecipeFilter.ALWAYS_TRUE) {
			return originalRecipes.size();
		} else if (filter != RecipeFilter.ALWAYS_FALSE) {
			return (int) originalRecipes.stream().filter(filter).count();
		}

		return 0;
	}

	public int remove(RecipeFilter filter) {
		MutableInt count = new MutableInt();
		forEachRecipe(filter, r ->
		{
			if (removedRecipes.add(r)) {
				if (ServerSettings.instance.logRemovedRecipes) {
					ScriptType.SERVER.console.info("- " + r + ": " + r.getFromToString());
				} else if (ScriptType.SERVER.console.shouldPrintDebug()) {
					ScriptType.SERVER.console.debug("- " + r + ": " + r.getFromToString());
				}

				count.increment();
			}
		});
		return count.getValue();
	}

	public int replaceInput(RecipeFilter filter, IngredientJS ingredient, IngredientJS with, boolean exact) {
		AtomicInteger count = new AtomicInteger();
		String is = ingredient.toString();
		String ws = with.toString();

		forEachRecipeAsync(filter, r ->
		{
			if (r.replaceInput(ingredient, with, exact)) {
				count.incrementAndGet();

				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes) {
					ScriptType.SERVER.console.info("~ " + r + ": IN " + is + " -> " + ws);
				} else if (ScriptType.SERVER.console.shouldPrintDebug()) {
					ScriptType.SERVER.console.debug("~ " + r + ": IN " + is + " -> " + ws);
				}
			}
		});

		modifiedRecipes.addAndGet(count.get());
		return count.get();
	}

	public int replaceInput(RecipeFilter filter, IngredientJS ingredient, IngredientJS with) {
		return replaceInput(filter, ingredient, with, false);
	}

	public int replaceInput(IngredientJS ingredient, IngredientJS with) {
		return replaceInput(RecipeFilter.ALWAYS_TRUE, ingredient, with);
	}

	public int replaceOutput(RecipeFilter filter, IngredientJS ingredient, ItemStackJS with, boolean exact) {
		AtomicInteger count = new AtomicInteger();
		String is = ingredient.toString();
		String ws = with.toString();

		forEachRecipeAsync(filter, r ->
		{
			if (r.replaceOutput(ingredient, with, exact)) {
				count.incrementAndGet();

				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes) {
					ScriptType.SERVER.console.info("~ " + r + ": OUT " + is + " -> " + ws);
				} else if (ScriptType.SERVER.console.shouldPrintDebug()) {
					ScriptType.SERVER.console.debug("~ " + r + ": OUT " + is + " -> " + ws);
				}
			}
		});

		modifiedRecipes.addAndGet(count.get());
		return count.get();
	}

	public int replaceOutput(RecipeFilter filter, IngredientJS ingredient, ItemStackJS with) {
		return replaceOutput(filter, ingredient, with, false);
	}

	public int replaceOutput(IngredientJS ingredient, ItemStackJS with) {
		return replaceOutput(RecipeFilter.ALWAYS_TRUE, ingredient, with);
	}

	public RecipeFunction getRecipeFunction(@Nullable ResourceLocation id) {
		if (id == null) {
			throw new NullPointerException("Recipe type is null!");
		}

		return functionMap.computeIfAbsent(id, location ->
		{
			RecipeSerializer<?> serializer = KubeJSRegistries.recipeSerializers().get(location);

			if (serializer != null) {
				RecipeTypeJS typeJS = typeMap.get(location);
				return new RecipeFunction(this, location, typeJS != null ? typeJS : new CustomRecipeTypeJS(serializer));
			} else {
				return new RecipeFunction(this, location, null);
			}
		});
	}

	public RecipeFunction getShaped() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.SHAPED_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getShapeless() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.SHAPELESS_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getSmelting() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.SMELTING_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getBlasting() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.BLASTING_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getSmoking() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.SMOKING_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getCampfireCooking() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getStonecutting() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.STONECUTTER, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getSmithing() {
		return getRecipeFunction(Registries.getId(RecipeSerializer.SMITHING, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeJS custom(Object o) {
		MapJS json = MapJS.of(o);
		ResourceLocation id = new ResourceLocation(json.getOrDefault("type", "").toString());
		return getRecipeFunction(id).createRecipe(new Object[]{json});
	}

	public void printTypes() {
		ScriptType.SERVER.console.info("== All recipe types ==");
		HashSet<String> list = new HashSet<>();
		originalRecipes.forEach(r -> list.add(r.type.toString()));
		list.stream().sorted().forEach(ScriptType.SERVER.console::info);
		ScriptType.SERVER.console.info(list.size() + " types");
	}

	public void printExamples(String type) {
		List<RecipeJS> list = originalRecipes.stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
		Collections.shuffle(list);

		ScriptType.SERVER.console.info("== Random examples of '" + type + "' ==");

		for (int i = 0; i < Math.min(list.size(), 5); i++) {
			RecipeJS r = list.get(i);
			ScriptType.SERVER.console.info("- " + r.getOrCreateId() + ":\n" + JsonUtilsJS.toPrettyString(r.json));
		}
	}

	public void setItemErrors(boolean b) {
		RecipeJS.itemErrors = b;
	}

	@ExpectPlatform
	private static boolean processConditions(JsonObject json, String key) {
		throw new AssertionError();
	}
}