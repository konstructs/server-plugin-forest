package org.konstructs.forest;

import java.util.Map;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;

import com.typesafe.config.Config;

import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;
import konstructs.api.*;
import konstructs.api.messages.*;

public class ForestPlugin extends KonstructsActor {
    private final ForestConfig config;
    private final BlockTypeId growsOn;
    private final BlockTypeId seedsOn;
    private final BlockTypeId sapling;
    private final BlockTypeId wood;
    private final int randomGrowth;
    private final Random random = new Random();
    private final ActorRef leafDecay;
    private final Position leafDecayRadi;
    private float speed = GlobalConfig.DEFAULT_SIMULATION_SPEED;

    public ForestPlugin(String name, ActorRef universe, ForestConfig config) {
        super(universe);
        this.config = config;
        this.growsOn = config.getGrowsOn();
        this.seedsOn = config.getSeedsOn();
        this.sapling = config.getSapling();
        this.wood = config.getWood();
        this.randomGrowth = config.getRandomGrowth();
        this.leafDecay = getContext().actorOf(LeafDecay.props(getUniverse(), config));
        int radi = config.getCrownRadi() * 2;
        this.leafDecayRadi = new Position(radi, radi, radi);
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
            BlockTypeId before = p.getValue().getBefore().getType();
            if(before.equals(wood) && !after.equals(wood)) {
                getUniverse().tell(new BoxQuery(Box.createAround(p.getKey(), leafDecayRadi)), leafDecay);
            } else if(after.equals(sapling)) {
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
        leafDecay.forward(config, getContext());
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
        props(String pluginName, ActorRef universe, Config config) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        ForestConfig forestConfig = new ForestConfig(config);
        return Props.create(currentClass, pluginName, universe, forestConfig);
    }
}
