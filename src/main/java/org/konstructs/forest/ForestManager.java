package org.konstructs.forest;

import akka.actor.UntypedActor;
import akka.actor.Props;
import konstructs.plugin.Config;
import konstructs.plugin.PluginConstructor;

/* Plugin that managed trees on the server.
 */
public class ForestManager extends UntypedActor {

    public ForestManager() {
    }

    public void onReceive(Object message) {
        unhandled(message);
    }

    @PluginConstructor
    public static Props props(String pluginName) {
        return Props.create(ForestManager.class);
    }
}
