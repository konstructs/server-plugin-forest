package org.konstructs.forest;

import akka.actor.UntypedActor;
import akka.actor.Props;
import akka.actor.ActorRef;
import konstructs.plugin.Config;
import konstructs.plugin.PluginConstructor;

/* Plugin that managed trees on the server.
 */
public class ForestManager extends UntypedActor {

    public ForestManager() {
        System.out.println("Okay, I'm loaded now");
    }

    public void onReceive(Object message) {
        System.out.println("Hello there! I got a message.");
        unhandled(message);
    }

    @PluginConstructor
    public static Props props(String pluginName, ActorRef universe) {
        System.out.println("Prop me!");
        return Props.create(ForestManager.class);
    }
}
