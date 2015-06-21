package org.konstructs.example;

import akka.actor.UntypedActor;
import akka.actor.Props;
import konstructs.plugin.Config;
import konstructs.plugin.PluginConstructor;
import konstructs.api.SayFilter;
import konstructs.api.Say;

public class MyPlugin extends UntypedActor {
    private String responseText;
    private String pluginName;

    public MyPlugin(String pluginName, String responseText) {
        this.responseText = responseText;
        this.pluginName = pluginName;
    }

    public void onReceive(Object message) {
        if(message instanceof SayFilter) {
            SayFilter say = (SayFilter)message;
            String text = say.message().text();
            say.continueWith(new Say("Did you just say: " + text + "?"), getSender());
        } else {
            unhandled(message);
        }
    }

    @PluginConstructor
    public static Props props(String pluginName, @Config(key = "response-text") String responseText) {
        return Props.create(MyPlugin.class, pluginName, responseText);
    }
}
