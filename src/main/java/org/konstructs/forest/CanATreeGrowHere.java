package org.konstructs.forest;

import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;

import konstructs.plugin.KonstructsActor;
import konstructs.api.*;
import konstructs.api.messages.BoxQueryResult;

class CanATreeGrowHere extends KonstructsActor {
    private final Position position;
    private final ForestConfig config;
    private final BlockTypeId leaves;
    private final BlockTypeId thinLeaves;
    private final BlockTypeId vacuum = BlockTypeId.VACUUM;
    private final BlockFilter saplingFilter;
    private boolean trunkIsOk = false;

    public CanATreeGrowHere(ActorRef universe, Position position, ForestConfig config) {
        super(universe);
        this.position = position;
        this.config = config;
        this.leaves = config.getLeaves();
        this.thinLeaves = config.getThinLeaves();
        this.saplingFilter = BlockFilterFactory.withBlockTypeId(config.getSapling());
        queryForTrunk();
    }

    private void query(int radi, int height, int yOffset) {
        Position start =
            new Position(position.getX() - radi,
                         position.getY() + yOffset,
                         position.getZ() - radi);
        Position end =
            new Position(position.getX() + radi,
                         position.getY() + height + yOffset,
                         position.getZ() + radi);
        boxQuery(new Box(start, end));
    }

    private void queryForTrunk() {
        query(config.getTrunkRadi(), config.getTrunkHeight(), 1);
    }

    private void queryForCrown() {
        query(config.getCrownRadi(), config.getCrownHeight(), config.getTrunkHeight());
    }

    private void canGrow() {
        getContext().parent().tell(new PlantTree(position), getSelf());
        getContext().stop(getSelf()); /* We are done, let's die*/
    }

    private void canNotGrow() {
        replaceWithVacuum(saplingFilter, position); /* Remove the sapling */
        getContext().stop(getSelf()); /* We are done, let's die*/
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        for(Map.Entry<Position, BlockTypeId> p: result.getAsMap().entrySet()) {
            if(!(p.getValue().equals(vacuum) || // Ignore vacuum
                 p.getValue().equals(leaves) || // Ignore leaves from the same sort of tree
                 p.getValue().equals(thinLeaves) || // Ignore thin leaves from the same sort of tree
                 p.getKey().equals(position))) { // Ignore sapling at starting position
                canNotGrow();
                return;
            }
        }
        if(trunkIsOk) {
            canGrow();
        } else {
            trunkIsOk = true;
            queryForCrown();
        }
    }

    public static Props props(ActorRef universe, Position start, ForestConfig config) {
        return Props.create(CanATreeGrowHere.class, universe, start, config);
    }
}
