package org.konstructs.forest;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import konstructs.api.BlockUpdate;
import konstructs.api.BlockPosition;
import konstructs.api.PutBlock;
import java.util.Collection;

public class ActorManager extends UntypedActor {

    ActorRef universe;

    public ActorManager(ActorRef universe) {
        this.universe = universe;
    }

    public void onReceive(Object message) {

        if (message instanceof BlockPosition) {
            BlockPosition blockPosition = (BlockPosition)message;
            onBlockPosition(blockPosition);
            return;
        }

        if (message instanceof BlockUpdate) {
            BlockUpdate blockUpdate = (BlockUpdate)message;
            onBlockUpdate(blockUpdate);
            return;
        }

    }

    public void onBlockPosition(BlockPosition blockPosition) {
        System.out.println("called onBlockPosition: not implemented");
    }

    public void onBlockUpdate(BlockUpdate blockUpdate) {
        System.out.println("called onBlockUpdate: not implemented");
    }

    public void putBlock(Collection<PutBlock> blocks) {
        for (PutBlock b : blocks) {
            universe.tell(b, getSender());
        }
    }

}