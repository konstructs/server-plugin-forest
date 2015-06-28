package org.konstructs.example;

import akka.actor.UntypedActor;
import akka.actor.Props;
import konstructs.plugin.Config;
import konstructs.plugin.PluginConstructor;
import konstructs.api.SayFilter;
import konstructs.api.Say;

/* An example plugin for the konstructs server
 *
 * A plugin in konstructs is an Akka actor. Therefore it extends
 * UntypedActor. An Actor is a normal Java class, but with one
 * additional method: "onReceive". This method is the entry point of
 * the plugin. Whenever there is a server event that the plugin is
 * interested in, it will be sent to this method. In this example, the
 * plugin is only interested in filtering chat messages. Therefore, it
 * handles the "SayFilter" message in it's onReceive.
 *
 * For a plugin to be detected by the constructs server, it requires a
 * static method that creates an akka Props object for the plugin. The
 * props object can be seen as an alias for the constructor. When used
 * by the konstructs server, it will call the constructor with the
 * same arguments that it was created with. In this example, the Props
 * object is created by the "props" method. This method is annotated
 * with @PluginConstructor to be identified as a constructor for the
 * plugin. This means that this function will be called by the server
 * when the plugin is loaded to generate the Props object. When
 * calling a method annotated with @PluginConstructor the server
 * always passes the name given to the plugin in the configuration
 * file as the first argument. In this example, this is the
 * "pluginName" parameter. Other configuration values can also be
 * passed to a plugin. This is done by adding more parameters to the
 * method annotated with @PluginConstructor. Each parameter must be
 * annotated with a @Config annotation. This annotation takes one
 * argument, "key", which is the name of the configuration key that
 * will be used to get the value of the parameter. In this example the
 * server will require the configuration key "response-text" to be
 * present in the configuration to load the plugin. The value of this
 * key will be passed as the "responseText" parameter of the "props"
 * method. Using this methodology it is easy to make your plugin
 * configurable by the server administrator.
 */
public class MyPlugin extends UntypedActor {
    /* This is the text that will be added to the players message */
    private String responseText;

    public MyPlugin(String responseText) {
        this.responseText = responseText;
    }

    /*
     * This method is the plugins generic entry point for any server
     * event. This plugin currently only handles the SayFilter
     * message.
     */
    public void onReceive(Object message) {
        if(message instanceof SayFilter) {
            /* First we cast the message to the identified message type */
            SayFilter filter = (SayFilter)message;
            /*
             * Filter messages always contains information required by
             * the filtering system as well as the message itself. We
             * are only interested in the message itself which is
             * accessed by the "message" accessor.
             */
            Say say = filter.message();
            String text = say.text();
            /*
             * filter.continueWith passes a new message on to the next
             * plugin in the filtering chain. Other possibilities are
             * skip which sends the message back to the server without
             * passing through the rest of the filter chain or drop
             * which silently drops the message.
             */
            filter.continueWith(new Say(text + " <- " + responseText), getSender());
        } else {
            /*
             * By calling the unhandled method the plugin informs the
             * server that it couldn't handle the message. This is
             * very useful for server administrators since this may
             * indicate a configuration error.
             */
            unhandled(message);
        }
    }

    /*
     * This static method is the plugin constructor.  The first
     * argument is the name given to the plugin in the configuration
     * file. The second argument is a configuration value required by
     * the plugin. The server will try to read it from the
     * "response-text" key in the configuration. If it ca not find it,
     * it will refuse to load the plugin.
     *
     * The Props object returned is used by the server to create new
     * instances of this plugin.
     */
    @PluginConstructor
    public static Props props(String pluginName,
                              @Config(key = "response-text") String responseText) {
        /* We are not using the pluginName in this simple example */
        return Props.create(MyPlugin.class, responseText);
    }
}
