package org.konstructs.forest;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.Position;
import konstructs.plugin.PluginConstructor;
import konstructs.api.BlockUpdate;
import konstructs.api.BlockPosition;
import scala.concurrent.duration.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ForestManager extends ActorManager {

    MakeTree tree;

    public ForestManager(ActorRef uni) {
        super(uni);
        tree = new MakeTree();
    }

    /**
     * Listens to block updates from the entire server.
     */
    public void onBlockUpdate(BlockUpdate blockUpdate) {
        if (blockUpdate.newW() == 37) {
            destroyBlock(blockUpdate.pos());
            putBlocks(tree.buildTree(blockUpdate.pos()));
            waitTree(5, blockUpdate.pos());
        }
    }

    /**
     * Listen for block position questions that I asked for.
     * Ask for the block above if it's inside the ground, and
     * place a tree if it's a air block.
     */
    public void onBlockPosition(BlockPosition blockPosition) {
        Position pos = blockPosition.pos();
        if (blockPosition.w() > 0) {
            getBlock(new Position(pos.x(), pos.y() + 1 , pos.z()));
        } else {
            putBlock(pos, 37);
        }
    }

    /**
     * Wait for the specified number of seconds and find a new
     * random position for a tree.
     * @param   waitFor Number of seconds
     * @param   pos     The position of the old tree
     */
    private void waitTree(int waitFor, Position pos) {
        final Position lpos = pos;
        getContext().system().scheduler()
            .scheduleOnce(Duration.create(waitFor, TimeUnit.SECONDS),
                    new Runnable() {
                        @Override
                        public void run() {
                            Position rpos = randomCoordsXZ(100, lpos);
                            getBlock(new Position(rpos.x(), 1, rpos.z()));
                        }
                    }, getContext().system().dispatcher()
            );
    }

    /**
     * Get a random xz position inside a defined box.
     * @param   size  The size of the box
     * @param   in    A position in the center of the box
     * @return        A new random position
     */
    private Position randomCoordsXZ(int size, Position in) {
        Random random = new Random();
        int dx = random.nextInt(size) - size/2;
        int dz = random.nextInt(size) - size/2;

        return new Position(in.x() + dx, in.y(), in.z() + dz);
    }

    @PluginConstructor
    public static Props props(String pluginName, ActorRef universe) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        return Props.create(currentClass, universe);
    }
}
