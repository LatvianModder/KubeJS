package dev.latvian.kubejs.world.gen.filter.mob;

import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.regex.Pattern;

@FunctionalInterface
public interface MobFilter extends BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> {
	MobFilter ALWAYS_TRUE = (cat, data) -> true;
	MobFilter ALWAYS_FALSE = (cat, data) -> false;

	boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data);

	static MobFilter of(@Nullable Object o) {
		if (o == null || o == ALWAYS_TRUE) {
			return ALWAYS_TRUE;
		} else if (o == ALWAYS_FALSE) {
			return ALWAYS_FALSE;
		}

		if (o instanceof String) {
			return idFilter((String) o);
		}

		ListJS list = ListJS.orSelf(o);

		if (list.isEmpty()) {
			return ALWAYS_TRUE;
		} else if (list.size() > 1) {
			OrFilter predicate = new OrFilter();

			for (Object o1 : list) {
				MobFilter p = of(o1);

				if (p == ALWAYS_TRUE) {
					return ALWAYS_TRUE;
				} else if (p != ALWAYS_FALSE) {
					predicate.list.add(p);
				}
			}

			return predicate.list.isEmpty() ? ALWAYS_FALSE : predicate.list.size() == 1 ? predicate.list.get(0) : predicate;
		}

		MapJS map = MapJS.of(list.get(0));

		if (map == null || map.isEmpty()) {
			return ALWAYS_TRUE;
		}

		AndFilter predicate = new AndFilter();

		if (map.get("or") != null) {
			predicate.list.add(of(map.get("or")));
		}

		if (map.get("not") != null) {
			predicate.list.add(new NotFilter(of(map.get("not"))));
		}

		try {
			if (map.get("id") != null) {
				predicate.list.add(idFilter(map.get("id").toString()));
			}

			if (map.get("type") != null) {
				predicate.list.add(idFilter(map.get("type").toString()));
			}

			if (map.get("category") != null) {
				predicate.list.add(new CategoryFilter(MobCategory.byName(map.get("category").toString())));
			}

			// TODO: Add other mob filters
		} catch (Exception ex) {
			ScriptType.STARTUP.console.error("Error trying to create MobFilter: " + ex.getMessage());
			return ALWAYS_FALSE;
		}

		return predicate.list.isEmpty() ? ALWAYS_TRUE : predicate.list.size() == 1 ? predicate.list.get(0) : predicate;
	}

	static MobFilter idFilter(String s) {
		Pattern pattern = UtilsJS.parseRegex(s);
		if (pattern != null) {
			return new RegexIDFilter(pattern);
		}

		return s.charAt(0) == '#' ? new CategoryFilter(MobCategory.byName(s.substring(1))) : new IDFilter(UtilsJS.getMCID(s));
	}

}
