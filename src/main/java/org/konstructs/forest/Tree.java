package org.konstructs.forest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;

import konstructs.utils.BlockMachine;
import konstructs.utils.DeterministicProductionRule;
import konstructs.utils.LSystem;
import konstructs.utils.ProbabilisticProductionRule;
import konstructs.utils.ProbalisticProduction;
import konstructs.utils.ProductionRule;
import konstructs.plugin.KonstructsActor;
import konstructs.api.*;

class Tree extends KonstructsActor {
    private static final LSystem SYSTEM = getLSystem();

    private final BlockMachine machine;
    private final BlockFilter forestBlocks;
    private final Random r = new Random();
    private final Position position;
    private final ForestConfig config;
    private final int maxGenerations;
    private String state;

    public Tree(ActorRef universe, Position sapling, ForestConfig config) {
        super(universe);
        this.position = sapling;
        this.config = config;
        this.state = config.getInitialState();
        this.maxGenerations = config.getMinGenerations() +
            r.nextInt(config.getMaxGenerations() -
                      config.getMinGenerations());
        viewBlock(sapling);
        this.forestBlocks = BlockFilterFactory
            .vacuum()
            .or(BlockFilterFactory
                .withBlockTypeId(config.getLeaves()))
            .or(BlockFilterFactory
                .withBlockTypeId(config.getSapling()));
        this.machine = getBlockMachine(config);
    }

    private int nextRandomSeedDistance() {
        int direction  = r.nextInt(2) == 1 ? 1 : -1;
        return (config.getCrownRadi() + r.nextInt(config.getCrownRadi())) * direction;
    }

    private void scheduleGrowth(int generation) {
        if(generation > maxGenerations) {
            /* Tree is done */
            getContext().stop(getSelf());
        } else {
            /* Tree need to grow more */
            scheduleSelfOnce(new GrowTree(generation),
                             config.getMinGrowthDelay() * 1000 +
                             new Random().nextInt(config.getRandomGrowthDelay()) * 1000);
        }
    }

    private void seed() {
        /* Plant seeds */
        int seeds = r.nextInt(config.getMaxSeedsPerGeneration() + 1);
        for(int i = 0; i < seeds; i++) {
            Position p = new Position(position.x() + nextRandomSeedDistance(),
                                      position.y(),
                                      position.z() + nextRandomSeedDistance());
            getContext().parent().tell(new TryToSeedTree(p), getSelf());
        }
    }

    private void grow(int generation) {
        Map<Position, BlockTypeId> removeOldBlocks =
            BlockMachine.vacuumMachine().interpretJava(state, position);
        state = SYSTEM.iterate(state, 1);
        removeOldBlocks.putAll(machine.interpretJava(state, position));
        replaceBlocks(removeOldBlocks, forestBlocks);
        seed();
        scheduleGrowth(generation + 1);
    }

    public void onBlockViewed(BlockViewed block) {

        if(block.block().type().equals(config.getSapling())) {
            scheduleGrowth(0);
        } else {
            getContext().stop(getSelf()); /* Someone removed the sapling */
        }
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof GrowTree) {
            GrowTree growTree = (GrowTree)message;
            grow(growTree.getGeneration());
        } else {
            super.onReceive(message); // Handle konstructs messages
        }
    }


    public static Props props(ActorRef universe, Position start, ForestConfig config) {
        return Props.create(Tree.class, universe, start, config);
    }

    private static LSystem getLSystem() {
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

    private static BlockMachine getBlockMachine(ForestConfig config) {
        Map<Character, BlockTypeId> blockMapping = new HashMap<Character, BlockTypeId>();
        blockMapping.put('a', config.getWood());
        blockMapping.put('b', config.getWood());
        blockMapping.put('c', config.getLeaves());
        blockMapping.put('d', config.getLeaves());
        return BlockMachine.fromJavaMap(blockMapping);
    }
}
