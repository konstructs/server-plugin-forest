package org.konstructs.forest;

import java.util.Map;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;
import konstructs.plugin.Config;
import konstructs.api.*;
import konstructs.api.messages.*;

public class ForestPlugin extends KonstructsActor {
    private final ForestConfig config;
    private final BlockTypeId growsOn;
    private final BlockTypeId seedsOn;
    private final BlockTypeId sapling;
    private final int randomGrowth;
    private final Random random = new Random();
    private float speed = GlobalConfig.DEFAULT_SIMULATION_SPEED;

    public ForestPlugin(String name, ActorRef universe, ForestConfig config) {
        super(universe);
        this.config = config;
        this.growsOn = config.getGrowsOn();
        this.seedsOn = config.getSeedsOn();
        this.sapling = config.getSapling();
        this.randomGrowth = config.getRandomGrowth();
    }

    void tryToSeed(Position pos) {
        Position start =
            pos.withY(pos.getY() - config.getSeedHeightDifference());
        Position end =
            new Position(pos.getX() + 1,
                         pos.getY() + config.getSeedHeightDifference(),
                         pos.getZ() + 1);
        boxQuery(new Box(start, end));
    }

    void seeded(Position pos) {
        getContext().actorOf(CanATreeGrowHere.props(getUniverse(), pos, config));
    }

    void seed(Position pos) {
        replaceVacuumBlock(pos, Block.create(sapling));
        seeded(pos);
    }

    void plant(Position pos) {
        getContext().actorOf(Tree.props(getUniverse(), pos, config, speed));
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        Map<Position, BlockTypeId> placed = result.getAsMap();
        for(Map.Entry<Position, BlockTypeId> p: placed.entrySet()) {
            if(p.getValue().equals(growsOn) || p.getValue().equals(seedsOn)) {
                Position pos = p.getKey().addY(1);
                BlockTypeId above = placed.get(pos);
                if(above != null && above.equals(BlockTypeId.VACUUM)) {
                    seed(pos);
                    return;
                }
            }
        }
    }

    @Override
    public void onBlockUpdateEvent(BlockUpdateEvent update) {
        for(Map.Entry<Position, BlockUpdate> p: update.getUpdatedBlocks().entrySet()) {
            BlockTypeId after = p.getValue().getAfter().getType();
            if(after.equals(sapling)) {
                seeded(p.getKey());
            } else if(after.equals(growsOn) &&
                      random.nextInt(10000) <= randomGrowth) {
                /* Try to seed a new tree */
                scheduleSelfOnce(new TryToSeedTree(p.getKey()),
                                 (int)((float)(config.getMinGrowthDelay() * 1000 +
                                               random.nextInt(config.getRandomGrowthDelay()) * 1000) / speed));
            }
        }
    }

    @Override
    public void onGlobalConfig(GlobalConfig config) {
        speed = config.getSimulationSpeed();
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof SeedTree) {
            SeedTree seedTree = (SeedTree)message;
            seed(seedTree.getPosition());
        } else if(message instanceof PlantTree) {
            PlantTree plantTree = (PlantTree)message;
            plant(plantTree.getPosition());
        } else if(message instanceof TryToSeedTree) {
            TryToSeedTree tryToSeedTree = (TryToSeedTree)message;
            tryToSeed(tryToSeedTree.getPosition());
        } else {
            super.onReceive(message); // Handle konstructs messages
        }
    }

    @PluginConstructor
    public static Props
        props(
              String pluginName,
              ActorRef universe,
              @Config(key = "wood-block") String wood,
              @Config(key = "leaves-block") String leaves,
              @Config(key = "thin-leaves-block") String thinLeaves,
              @Config(key = "sapling-block") String sapling,
              @Config(key = "grows-on") String growsOn,
              @Config(key = "seeds-on") String seedsOn,
              @Config(key = "max-seed-height-difference") int seedHeightDifference,
              @Config(key = "max-generations") int maxGenerations,
              @Config(key = "min-generations") int minGenerations,
              @Config(key = "trunk-radi") int trunkRadi,
              @Config(key = "trunk-height") int trunkHeight,
              @Config(key = "crown-radi") int crownRadi,
              @Config(key = "crown-height") int crownHeight,
              @Config(key = "initial-state") String initialState,
              @Config(key = "min-growth-delay") int minGrowthDelay,
              @Config(key = "random-growth-delay") int randomGrowthDelay,
              @Config(key = "max-seeds-per-generation") int maxSeedsPerGeneration,
              @Config(key = "seed-every-generation") int seedEveryGeneration,
              @Config(key = "random-growth") int randomGrowth
              ) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        ForestConfig config =
            new ForestConfig(
                             wood,
                             leaves,
                             thinLeaves,
                             sapling,
                             growsOn,
                             seedsOn,
                             seedHeightDifference,
                             maxGenerations,
                             minGenerations,
                             trunkRadi,
                             trunkHeight,
                             crownRadi,
                             crownHeight,
                             initialState,
                             minGrowthDelay,
                             randomGrowthDelay,
                             maxSeedsPerGeneration,
                             seedEveryGeneration,
                             randomGrowth);
        return Props.create(currentClass, pluginName, universe, config);
    }
}
