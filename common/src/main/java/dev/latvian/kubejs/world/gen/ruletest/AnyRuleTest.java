package dev.latvian.kubejs.world.gen.ruletest;

import com.mojang.serialization.Codec;
import dev.latvian.kubejs.world.gen.ruletest.type.KubeJSRuleTests;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class AnyRuleTest extends RuleTest {

	public static final Codec<AnyRuleTest> CODEC = RuleTest.CODEC
			.listOf()
			.fieldOf("rules")
			.xmap(AnyRuleTest::new, (t) -> t.rules)
			.codec();

	public final List<RuleTest> rules;

	public AnyRuleTest() {
		this(new ArrayList<>());
	}

	public AnyRuleTest(List<RuleTest> rules) {
		this.rules = rules;
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		for (RuleTest test : rules) {
			if (test.test(blockState, random)) {
				return true;
			}
		}

		return rules.isEmpty();
	}

	@Override
	protected RuleTestType<AnyRuleTest> getType() {
		return KubeJSRuleTests.ANY;
	}
}
