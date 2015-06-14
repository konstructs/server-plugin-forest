package org.konstructs.example;

import akka.actor.UntypedActor;
import akka.actor.Props;
import konstructs.plugin.Config;
import konstructs.plugin.PluginConstructor;


public class MyPlugin extends UntypedActor {
    private String userMessage;
    private String pluginName;

    public MyPlugin(String pluginName, String userMessage) {
        userMessage = userMessage;
        pluginName = pluginName;
    }

    public void onReceive(Object message) {
        unhandled(message);
    }

    @PluginConstructor
    public static Props props(String pluginName, @Config(key = "user-message") String userMessage) {
        return Props.create(MyPlugin.class, pluginName, userMessage);
    }
}
