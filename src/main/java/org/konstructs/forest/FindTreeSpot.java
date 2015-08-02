package org.konstructs.forest;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.Position;
import konstructs.api.BlockPosition;

public class FindTreeSpot extends ActorManager {

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
    public void onBlockPosition(BlockPosition blockPosition) {
        Position pos = blockPosition.pos();
        if (blockPosition.w() > 0) {
            lastW = blockPosition.w();
            getBlock(new Position(pos.x(), pos.y() + 1 , pos.z()));
        } else {
            if (lastW == 1) {
                putBlock(pos, 19);
            }
            getContext().stop(getSelf());
        }
    }


    public static Props props(ActorRef universe, Position position) {
        return Props.create(FindTreeSpot.class, universe, position);
    }
}
