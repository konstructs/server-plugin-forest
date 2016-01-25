package konstructs.forest;

import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;

import konstructs.plugin.KonstructsActor;
import konstructs.api.*;

class CanATreeGrowHere extends KonstructsActor {
    private final Position position;
    private final ForestConfig config;
    private final BlockTypeId leaves;
    private final BlockTypeId vacuum = BlockTypeId.vacuum();
    private boolean trunkIsOk = false;

    public CanATreeGrowHere(ActorRef universe, Position position, ForestConfig config) {
        super(universe);
        this.position = position;
        this.config = config;
        this.leaves = config.getLeaves();
        queryForTrunk();
    }

    private void query(int radi, int height, int yOffset) {
        Position start =
            new Position(position.x() - radi,
                         position.y() + yOffset,
                         position.z() - radi);
        Position end =
            new Position(position.x() + radi,
                         position.y() + height + yOffset,
                         position.z() + radi);
        boxQuery(start, end);
    }

    private void queryForTrunk() {
        query(config.getTrunkRadi(), config.getTrunkHeight(), 0);
    }

    private void queryForCrown() {
        query(config.getCrownRadi(), config.getCrownHeight(), config.getTrunkHeight());
    }

    private void canGrow() {
        getContext().parent().tell(new PlantTree(position), getSelf());
        getContext().stop(getSelf()); /* We are done, let's die*/
    }

    private void canNotGrow() {
        replaceBlock(position, BlockTypeId.vacuum()); /* Remove the sapling */
        getContext().stop(getSelf()); /* We are done, let's die*/
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        for(Map.Entry<Position, BlockTypeId> p: result.result().toPlaced().entrySet()) {
            if(!(p.getValue().equals(vacuum) || // Ignore vacuum
                 p.getValue().equals(leaves) || // Ignore leaves from the same sort of tree
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
