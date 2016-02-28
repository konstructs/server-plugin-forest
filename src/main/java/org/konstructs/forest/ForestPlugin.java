package org.konstructs.forest;

import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;
import konstructs.plugin.Config;
import konstructs.api.*;

public class ForestPlugin extends KonstructsActor {
    private final ForestConfig config;
    private final BlockTypeId growsOn;
    private final BlockTypeId sapling;
    public ForestPlugin(String name, ActorRef universe, ForestConfig config) {
        super(universe);
        this.config = config;
        this.growsOn = config.getGrowsOn();
        this.sapling = config.getSapling();
    }

    void tryToSeed(Position pos) {
        Position start =
            new Position(pos.x(),
                         Math.max(pos.y() - config.getSeedHeightDifference(),
                                  config.getMinSeedHeight()),
                         pos.z());
        Position end =
            new Position(pos.x() + 1,
                         Math.min(pos.y() + config.getSeedHeightDifference(),
                                  config.getMaxSeedHeight()),
                         pos.z() + 1);
        boxQuery(start, end);
    }

    void seeded(Position pos) {
        getContext().actorOf(CanATreeGrowHere.props(getUniverse(), pos, config));
    }

    void seed(Position pos) {
        putBlock(pos, Block.create(sapling));
        seeded(pos);
    }

    void plant(Position pos) {
        getContext().actorOf(Tree.props(getUniverse(), pos, config));
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        for(Map.Entry<Position, BlockTypeId> p: result.result().toPlaced().entrySet()) {
            if(p.getValue().equals(growsOn)) {
                seed(p.getKey().incY(1));
                return;
            }
        }
    }

    @Override
    public void onEventBlockUpdated(EventBlockUpdated update) {
        for(Map.Entry<Position, BlockTypeId> p: update.blocks().entrySet()) {
            if(p.getValue().equals(sapling)) {
                seeded(p.getKey());
            }
        }
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
              @Config(key = "sapling-block") String sapling,
              @Config(key = "grows-on") String growsOn,
              @Config(key = "max-seed-height-difference") int seedHeightDifference,
              @Config(key = "max-seed-height") int maxSeedHeight,
              @Config(key = "min-seed-height") int minSeedHeight,
              @Config(key = "max-generations") int maxGenerations,
              @Config(key = "min-generations") int minGenerations,
              @Config(key = "trunk-radi") int trunkRadi,
              @Config(key = "trunk-height") int trunkHeight,
              @Config(key = "crown-radi") int crownRadi,
              @Config(key = "crown-height") int crownHeight,
              @Config(key = "initial-state") String initialState,
              @Config(key = "min-growth-delay") int minGrowthDelay,
              @Config(key = "random-growth-delay") int randomGrowthDelay,
              @Config(key = "max-seeds-per-generation") int maxSeedsPerGeneration
              ) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        ForestConfig config =
            new ForestConfig(
                             wood,
                             leaves,
                             sapling,
                             growsOn,
                             seedHeightDifference,
                             maxSeedHeight,
                             minSeedHeight,
                             maxGenerations,
                             minGenerations,
                             trunkRadi,
                             trunkHeight,
                             crownRadi,
                             crownHeight,
                             initialState,
                             minGrowthDelay,
                             randomGrowthDelay,
                             maxSeedsPerGeneration);
        return Props.create(currentClass, pluginName, universe, config);
    }
}
