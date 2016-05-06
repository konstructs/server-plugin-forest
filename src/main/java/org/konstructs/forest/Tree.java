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
import konstructs.utils.ProbabilisticProduction;
import konstructs.utils.ProductionRule;
import konstructs.plugin.KonstructsActor;
import konstructs.api.*;
import konstructs.api.messages.ViewBlockResult;

class Tree extends KonstructsActor {
    private static final LSystem SYSTEM = getLSystem();

    private final BlockMachine machine;
    private final BlockFilter forestBlocks;
    private final Random r = new Random();
    private final Position position;
    private final ForestConfig config;
    private final int maxGenerations;
    private final float speed;
    private String state;

    public Tree(ActorRef universe, Position sapling, ForestConfig config, float speed) {
        super(universe);
        this.position = sapling;
        this.config = config;
        this.state = config.getInitialState();
        this.maxGenerations = config.getMinGenerations() +
            r.nextInt(config.getMaxGenerations() -
                      config.getMinGenerations());
        viewBlock(sapling);
        this.forestBlocks = BlockFilterFactory
            .VACUUM
            .or(BlockFilterFactory
                .withBlockTypeId(config.getLeaves()))
            .or(BlockFilterFactory
                .withBlockTypeId(config.getSapling()));
        this.machine = getBlockMachine(config);
        this.speed = speed;
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
                             (int)((float)(config.getMinGrowthDelay() * 1000 +
                              r.nextInt(config.getRandomGrowthDelay()) * 1000) / speed));
        }
    }

    private void seed() {
        /* Plant seeds */
        int seeds = r.nextInt(config.getMaxSeedsPerGeneration() + 1);
        for(int i = 0; i < seeds; i++) {
            Position p = position.add(new Position(nextRandomSeedDistance(),0, nextRandomSeedDistance()));
            getContext().parent().tell(new TryToSeedTree(p), getSelf());
        }
    }

    private void grow(int generation) {
        Map<Position, BlockTypeId> removeOldBlocks =
            BlockMachine.VACUUM_MACHINE.interpret(state, position);
        state = SYSTEM.iterate(state);
        removeOldBlocks.putAll(machine.interpret(state, position));
        replaceBlocks(forestBlocks, removeOldBlocks);
        seed();
        scheduleGrowth(generation + 1);
    }

    public void onViewBlockResult(ViewBlockResult result) {

        if(result.getBlock().getType().equals(config.getSapling())) {
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


    public static Props props(ActorRef universe, Position start, ForestConfig config, float speed) {
        return Props.create(Tree.class, universe, start, config, speed);
    }

    private static LSystem getLSystem() {
        ProbabilisticProduction leafGrowthDirections[] = {
            new ProbabilisticProduction(20, "c[&[d]]"),
            new ProbabilisticProduction(20, "c[&[+d]]"),
            new ProbabilisticProduction(20, "c[&[-d]]"),
            new ProbabilisticProduction(20, "c[&[--d]]"),
            new ProbabilisticProduction(20, "cc")
        };

        ProbabilisticProduction trunkGrowth[] = {
            new ProbabilisticProduction(40, "a[&[c][-c][--c][+c]]"),
            new ProbabilisticProduction(60, "bbba")
        };

        ProductionRule[] rules = {
            new DeterministicProductionRule("cc",
                                            "c[&[c][-c][--c][+c]]c[&[c][-c][--c][+c]]"),
            new DeterministicProductionRule("a", "aa"),
            new ProbabilisticProductionRule("c", leafGrowthDirections),
            new ProbabilisticProductionRule("aa", trunkGrowth)
        };

        return new LSystem(rules);
    }

    private static BlockMachine getBlockMachine(ForestConfig config) {
        Map<Character, BlockTypeId> blockMapping = new HashMap<Character, BlockTypeId>();
        blockMapping.put('a', config.getWood());
        blockMapping.put('b', config.getWood());
        blockMapping.put('c', config.getLeaves());
        blockMapping.put('d', config.getLeaves());
        return new BlockMachine(blockMapping);
    }
}
