package dev.latvian.kubejs.item;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.hooks.FoodPropertiesHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class FoodBuilder {
	private int hunger;
	private float saturation;
	private boolean meat;
	private boolean alwaysEdible;
	private boolean fastToEat;
	private final List<Pair<Supplier<MobEffectInstance>, Float>> effects = Lists.newArrayList();
	public Consumer<ItemFoodEatenEventJS> eaten;

	public FoodBuilder() {}

	public FoodBuilder(FoodProperties properties) {
		hunger = properties.getNutrition();
		saturation = properties.getSaturationModifier();
		meat = properties.isMeat();
		alwaysEdible = properties.canAlwaysEat();
		fastToEat = properties.isFastFood();

		properties.getEffects().forEach(pair -> {
			effects.add(Pair.of(pair::getFirst, pair.getSecond()));
		});
	}

	public FoodBuilder hunger(int h) {
		hunger = h;
		return this;
	}

	public FoodBuilder saturation(float s) {
		saturation = s;
		return this;
	}

	public FoodBuilder meat(boolean flag) {
		meat = flag;
		return this;
	}

	public FoodBuilder meat() {
		return meat(true);
	}

	public FoodBuilder alwaysEdible(boolean flag) {
		alwaysEdible = flag;
		return this;
	}

	public FoodBuilder alwaysEdible() {
		return alwaysEdible(true);
	}

	public FoodBuilder fastToEat(boolean flag) {
		fastToEat = flag;
		return this;
	}

	public FoodBuilder fastToEat() {
		return fastToEat(true);
	}

	public FoodBuilder effect(ResourceLocation potion, int duration, int amplifier, float probability) {
		effects.add(Pair.of(() -> new MobEffectInstance(UtilsJS.getPotion(potion), duration, amplifier), probability));
		return this;
	}

	public FoodBuilder removeEffect(ResourceLocation potion) {
		MobEffect toFindEffect = UtilsJS.getPotion(potion);
		if(toFindEffect == null) {
			return this;
		}

		effects.removeIf(pair -> {
			MobEffectInstance effectInstance = pair.getKey().get();
			return effectInstance.getDescriptionId().equals(toFindEffect.getDescriptionId());
		});

		return this;
	}

	public FoodBuilder eaten(Consumer<ItemFoodEatenEventJS> e) {
		eaten = e;
		return this;
	}

	public FoodProperties build() {
		FoodProperties.Builder b = new FoodProperties.Builder();
		b.nutrition(hunger);
		b.saturationMod(saturation);

		if (meat) {
			b.meat();
		}

		if (alwaysEdible) {
			b.alwaysEat();
		}

		if (fastToEat) {
			b.fast();
		}

		for (Pair<Supplier<MobEffectInstance>, Float> effect : effects) {
			FoodPropertiesHooks.effect(b, effect.getLeft(), effect.getRight());
		}

		return b.build();
	}
}