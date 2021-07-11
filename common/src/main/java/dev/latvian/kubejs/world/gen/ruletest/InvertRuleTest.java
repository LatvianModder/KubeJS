package dev.latvian.kubejs.world.gen.ruletest;

import com.mojang.serialization.Codec;
import dev.latvian.kubejs.world.gen.ruletest.type.KubeJSRuleTests;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.Random;

/**
 * @author LatvianModder
 */
public class InvertRuleTest extends RuleTest {

	public static final Codec<InvertRuleTest> CODEC = RuleTest.CODEC
			.fieldOf("original")
			.xmap(InvertRuleTest::new, (t) -> t.original)
			.codec();

	public final RuleTest original;

	public InvertRuleTest(RuleTest t) {
		original = t;
	}

	@Override
	public boolean test(BlockState blockState, Random random) {
		return !original.test(blockState, random);
	}

	@Override
	protected RuleTestType<InvertRuleTest> getType() {
		return KubeJSRuleTests.INVERT;
	}
}
