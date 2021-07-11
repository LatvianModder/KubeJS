package dev.latvian.kubejs.world.gen.ruletest.type;

import dev.latvian.kubejs.world.gen.ruletest.AnyRuleTest;
import dev.latvian.kubejs.world.gen.ruletest.InvertRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public interface KubeJSRuleTests {
	RuleTestType<AnyRuleTest> ANY = RuleTestType.register("kubejs:any", AnyRuleTest.CODEC);
	RuleTestType<InvertRuleTest> INVERT = RuleTestType.register("kubejs:invert", InvertRuleTest.CODEC);
}
