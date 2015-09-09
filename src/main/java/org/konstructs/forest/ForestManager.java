package org.konstructs.forest;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.plugin.PluginConstructor;
import konstructs.plugin.KonstructsActor;
import konstructs.api.Position;
import konstructs.api.BlockDataUpdate;
import java.util.Random;

public class ForestManager extends KonstructsActor {

    MakeTree tree;
    int numberOfTrees;
    int treeDistance;
    int treeSpawnRate;
    int stopAt;

    public ForestManager(ActorRef uni) {
        super(uni);
        tree = new MakeTree();
        numberOfTrees = 0;
        treeDistance = 20 + new Random().nextInt(50);
        treeSpawnRate = 1 + new Random().nextInt(3);
        stopAt = 100 + new Random().nextInt(300);
    }

    /**
     * Receive all messages to our actor
     */
    public void onReceive(Object message) {

        // Get out custom message
        if (message instanceof TreePackage) {
            TreePackage tp = (TreePackage)message;

            // Remove the sapling, spawn a tree, select a random position
            // and ask the server for the chosen block.
            if (tp.is("build-tree")) {
                numberOfTrees++;

                // Stop new tree
                if (new Random().nextInt(stopAt) < numberOfTrees) return;

                destroyBlock(tp.pos());
                String production = tree.runGeneration("a[&[c][-c][--c][+c]]c");
                putBlocks(tree.runBlockMachine(production, tp.pos()));

                scheduleSelfOnce(new TreePackage("grow-tree", tp.pos(), production, 1),
                        new Random().nextInt(5) * 1000);

            // Generate a new generation and update the tree.
            } else if (tp.is("grow-tree")) {
                String production = tree.runGeneration(tp.getProduction());

                // Remove old tree
                destroyBlocks(tree.runBlockMachine(tp.getProduction(), tp.pos()));

                // Place new tree
                putBlocks(tree.runBlockMachine(production, tp.pos()));

                if (tp.getGeneration() < 7 && new Random().nextInt(100) > tp.getGeneration() * 4) {
                    scheduleSelfOnce(
                            new TreePackage("grow-tree", tp.pos(), production, tp.getGeneration() + 1),
                            new Random().nextInt(5) * 1000);
                } else {
                    for (int i=treeSpawnRate; i > 0; i--) {
                        getContext().actorOf(
                            FindTreeSpot.props(getUniverse(), randomCoordsXZ(treeDistance, tp.pos())));
                    }
                }
            }

        }

        // Call logic in ActorManager
        super.onReceive(message);
    }

    /**
     * Listens to block updates from the entire server.
     * If the trigger block is placed, schedule a tree.
     */
    public void onBlockDataUpdate(BlockDataUpdate blockUpdate) {
        if (blockUpdate.newW() == 19) {
            scheduleSelfOnce(new TreePackage("build-tree", blockUpdate.pos()),
                    new Random().nextInt(10) * 1000);
        }
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

        return new Position(in.x() + dx, 0, in.z() + dz);
    }

    @PluginConstructor
    public static Props props(String pluginName, ActorRef universe) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        return Props.create(currentClass, universe);
    }
}
