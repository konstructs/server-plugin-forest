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
import konstructs.api.Position;
import konstructs.api.PutBlock;
import java.util.Collection;

public class MakeTree {

    private LSystem getLSystem() {
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

        return LSystem.fromList(rules);
    }

    public String runGenerations(String str, int n) {
        return getLSystem().iterate(str, n);
    }


    public String runGeneration(String str) {
        return runGenerations(str, 1);
    }

    public Collection<PutBlock> runBlockMachine(String str, Position pos) {
        Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
        blockMapping.put('a', 5);
        blockMapping.put('b', 5);
        blockMapping.put('c', 15);
        blockMapping.put('d', 15);
        BlockMachine m = BlockMachine.fromJavaMap(blockMapping);

        return m.interpretJava(str, pos.copy(pos.x(), pos.y() - 1, pos.z()));
    }

}
