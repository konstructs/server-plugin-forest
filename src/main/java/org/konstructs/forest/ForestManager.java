package org.konstructs.forest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import akka.actor.UntypedActor;
import akka.actor.Props;
import akka.actor.ActorRef;
import konstructs.plugin.Config;
import konstructs.plugin.PluginConstructor;
import konstructs.api.BlockUpdate;
import konstructs.api.PutBlock;
import konstructs.*;

/* Plugin that managed trees on the server.
 */
public class ForestManager extends UntypedActor {
    private ActorRef universe;


    public ForestManager(ActorRef universe) {
        this.universe = universe;
        System.out.println("Okay, I'm loaded now");
    }

    public void onReceive(Object message) {
        System.out.println("Hello there! I got a message.");
        if(message instanceof BlockUpdate) {
            BlockUpdate update = (BlockUpdate)message;
            if(update.newW() == 37) {
                Position pos = update.pos();
                List<ProductionRule> rules = new ArrayList<ProductionRule>();

                rules.add(new DeterministicProductionRule("cc", "c[&[c][-c][--c][+c]]c[&[c][-c][--c][+c]]"));
                rules.add(new DeterministicProductionRule("a","aa"));

                List<ProbalisticProduction> leafGrowthDirections =
                    new ArrayList<ProbalisticProduction>();
                leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[d]]"));
                leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[+d]]"));
                leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[-d]]"));
                leafGrowthDirections.add(new ProbalisticProduction(20, "c[&[--d]]"));
                leafGrowthDirections.add(new ProbalisticProduction(20, "cc"));
                rules.add(ProbabilisticProductionRule.fromList("c", leafGrowthDirections));

                List<ProbalisticProduction> trunkGrowth =
                    new ArrayList<ProbalisticProduction>();
                trunkGrowth.add(new ProbalisticProduction(40, "a[&[c][-c][--c][+c]]"));
                trunkGrowth.add(new ProbalisticProduction(60, "bbba"));
                rules.add(ProbabilisticProductionRule.fromList("aa", trunkGrowth));
                LSystem l = LSystem.fromList(rules);

                Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
                blockMapping.put('a', 5);
                blockMapping.put('b', 5);
                blockMapping.put('c', 15);
                blockMapping.put('d', 15);
                BlockMachine m = BlockMachine.fromJavaMap(blockMapping);

                String tree = l.iterate("a[&[c][-c][--c][+c]]c", 7);
                Collection<PutBlock> blocks = m.interpretJava(tree, pos.copy(pos.x(), pos.y() - 1, pos.z()));
                for(PutBlock b: blocks)
                    universe.tell(b, getSender());
            }
        } else {
            unhandled(message);
        }
    }

    @PluginConstructor
    public static Props props(String pluginName, ActorRef universe) {
        System.out.println("Prop me!");
        return Props.create(ForestManager.class, universe);
    }
}
