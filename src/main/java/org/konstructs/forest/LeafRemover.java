package org.konstructs.forest;

import java.util.Arrays;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.plugin.KonstructsActor;
import konstructs.api.*;
import konstructs.api.messages.*;

public class LeafRemover extends KonstructsActor {
    private final BlockFilter leavesFilter;
    private final BlockTypeId wood;

    LeafRemover(ActorRef universe, ForestConfig config) {
        super(universe);
        this.wood = config.getWood();
        this.leavesFilter = BlockFilterFactory
            .withBlockTypeId(config.getLeaves())
            .or(BlockFilterFactory.withBlockTypeId(config.getThinLeaves()));
    }

    private Position getCenter(Box box) {
        Position size = box.getSize();
        return box
            .getFrom()
            .addX(size.getX() / 2)
            .addY(size.getY() / 2)
            .addZ(size.getZ() / 2);
    }

    private boolean findWood(BoxQueryResult result) {
        return Arrays.asList(result.getBlocks()).contains(wood);
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        if(!findWood(result)) {
            replaceWithVacuum(leavesFilter, getCenter(result.getBox()));
        }
    }

    public static Props props(ActorRef universe, ForestConfig config) {
        return Props.create(LeafRemover.class, universe, config);
    }

}
