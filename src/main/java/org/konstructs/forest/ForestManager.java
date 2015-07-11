package org.konstructs.forest;

import akka.actor.ActorRef;
import akka.actor.Props;
import konstructs.plugin.PluginConstructor;
import konstructs.api.BlockUpdate;

public class ForestManager extends ActorManager {

    MakeTree tree;

    public ForestManager(ActorRef uni) {
        super(uni);
        tree = new MakeTree();
    }

    public void onBlockUpdate(BlockUpdate blockUpdate) {
        System.out.println(blockUpdate);
        if (blockUpdate.newW() == 37) {
            putBlock(tree.buildTree(blockUpdate.pos()));
        }
    }

    @PluginConstructor
    public static Props props(String pluginName, ActorRef universe) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        return Props.create(currentClass, universe);
    }
}
