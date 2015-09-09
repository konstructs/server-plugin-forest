package org.konstructs.forest;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.api.Position;
import konstructs.api.Block;
import konstructs.api.GetBlockResponse;
import konstructs.plugin.KonstructsActor;

public class FindTreeSpot extends KonstructsActor {

    int lastW;

    public FindTreeSpot(ActorRef universe, Position position) {
        super(universe);
        getBlock(position);
    }

    /**
     * Listen for block position questions that I asked for.
     * Ask for the block above if it's inside the ground, and
     * place a tree if it's a air block.
     */
    public void onGetBlockResponse(GetBlockResponse blockResponse) {
        Position pos = blockResponse.pos();
        if (blockResponse.block().w() > 0) {
            lastW = blockResponse.block().w();
            getBlock(new Position(pos.x(), pos.y() + 1 , pos.z()));
        } else {
            if (lastW == 1) {
                putBlock(pos, Block.create(19));
            }
            getContext().stop(getSelf());
        }
    }


    public static Props props(ActorRef universe, Position position) {
        return Props.create(FindTreeSpot.class, universe, position);
    }
}
