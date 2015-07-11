package org.konstructs.forest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import konstructs.BlockMachine;
import konstructs.DeterministicProductionRule;
import konstructs.LSystem;
import konstructs.ProbabilisticProductionRule;
import konstructs.ProbalisticProduction;
import konstructs.ProductionRule;
import konstructs.Position;
import konstructs.api.PutBlock;
import java.util.Collection;

public class MakeTree {

    public MakeTree() {}

    public Collection<PutBlock> buildTree(Position pos) {
        List<ProductionRule> rules = new ArrayList<ProductionRule>();

        rules.add(new DeterministicProductionRule("cc",
                    "c[&[c][-c][--c][+c]]c[&[c][-c][--c][+c]]"));
        rules.add(new DeterministicProductionRule("a", "aa"));

        List<ProbalisticProduction> leafGrowthDirections = new ArrayList<ProbalisticProduction>();
        leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[d]]"));
        leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[+d]]"));
        leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[-d]]"));
        leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[--d]]"));
        leafGrowthDirections.add(new ProbalisticProduction(20, "cc"));
        rules.add(ProbabilisticProductionRule.fromList("c", leafGrowthDirections));

        List<ProbalisticProduction> trunkGrowth = new ArrayList<ProbalisticProduction>();
        trunkGrowth.add(new ProbalisticProduction(40, "a[&[c][-c][--c][+c]]"));
        trunkGrowth.add(new ProbalisticProduction(60, "bbba"));
        rules.add(ProbabilisticProductionRule.fromList("aa", trunkGrowth));
        LSystem l = LSystem.fromList(rules);

        Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
        blockMapping.put('a', 5);
        blockMapping.put('b', 6);
        blockMapping.put('c', 15);
        blockMapping.put('d', 14);
        BlockMachine m = BlockMachine.fromJavaMap(blockMapping);

        String tree = l.iterate("a[&[c][-c][--c][+c]]c", 7);

        Collection<PutBlock> blocks = m.interpretJava(tree, pos.copy(pos.x(), pos.y() - 1, pos.z()));

        return blocks;
    }

}
