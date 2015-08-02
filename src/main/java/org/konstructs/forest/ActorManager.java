package org.konstructs.forest;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import konstructs.api.BlockUpdate;
import konstructs.api.BlockPosition;
import konstructs.api.PutBlock;
import konstructs.api.GetBlock;
import konstructs.api.DestroyBlock;
import static konstructs.PlayerActor.ReceiveBlock;
import konstructs.Position;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

public class ActorManager extends UntypedActor {

    ActorRef universe;

    public ActorManager(ActorRef universe) {
        this.universe = universe;
    }

    /**
     * Called by Akka when we receive a message
     */
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

        if (message instanceof ReceiveBlock) {
            ReceiveBlock receiveBlock = (ReceiveBlock)message;
            onReceiveBlock(receiveBlock);
            return;
        }

    }

    /**
     * Return universe ActorRef.
     * @return ActorRef
     */
    public ActorRef getUniverse() {
        return universe;
    }

    /**
     * This function is called when we receive a BlockPosition message.
     */
    public void onBlockPosition(BlockPosition blockPosition) {
        System.out.println("called onBlockPosition: not implemented");
    }

    /**
     * This function is called when we receive a BlockUpdate message.
     */
    public void onBlockUpdate(BlockUpdate blockUpdate) {
        System.out.println("called onBlockUpdate: not implemented");
    }

    /**
     * This function is called when we receive a ReceiveBlock message.
     */
    public void onReceiveBlock(ReceiveBlock receiveBlock) {
        System.out.println("called ReceiveBlock: not implemented");
    }

    /**
     * Write a collection of blocks to the world.
     * @param   blocks      A collection of blocks.
     */
    public void putBlocks(Collection<PutBlock> blocks) {
        for (PutBlock b : blocks) {
            putBlock(b);
        }
    }

    /**
     * Write a single block to the world.
     * @param   b   A block
     */
    public void putBlock(PutBlock b) {
        universe.tell(b, getSender());
    }

    /**
     * Write a single block to the world.
     * @param   p   The position of the block
     * @param   w   The block type
     */
    public void putBlock(Position p, int w) {
        universe.tell(new PutBlock(p, w), getSender());
    }

    /**
     * Ask the server for a block
     * @param   p   The position
     */
    public void getBlock(Position p) {
        universe.tell(new GetBlock(p), getSelf());
    }

    /**
     * Destroy a block.
     * @param   p   The position
     */
    public void destroyBlock(Position p) {
        universe.tell(new DestroyBlock(p), getSender());
    }

    /**
     * Destroy a collection of blocks.
     * @param   blocks      A collection of blocks.
     */
    public void destroyBlocks(Collection<PutBlock> blocks) {
        for (PutBlock b : blocks) {
            destroyBlock(b.pos());
        }
    }

    /**
     * Schedule a message to my self
     * @param   obj  The object to send
     * @param   msec Time to wait, in milliseconds
     */
    public void scheduleSelfOnce(Object obj, int msec) {
        getContext().system().scheduler().scheduleOnce(
                Duration.create(msec, TimeUnit.MILLISECONDS),
                getSelf(), obj, getContext().system().dispatcher(), null);
    }

}
