package org.konstructs.forest;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Cancellable;

import konstructs.plugin.KonstructsActor;
import konstructs.api.*;
import konstructs.api.messages.*;

public class LeafDecay extends KonstructsActor {
    private static class Trigger {}
    private final Random random = new Random();
    private final Position radi;
    private final ActorRef leafRemover;
    private final BlockTypeId leavesId;
    private final BlockTypeId thinLeavesId;
    private final ForestConfig config;
    private Set<Position> leaves;
    private float speed = GlobalConfig.DEFAULT_SIMULATION_SPEED;
    private Cancellable scheduled;

    LeafDecay(ActorRef universe, ForestConfig config) {
        super(universe);
        this.radi = new Position(config.getCrownRadi(), config.getCrownRadi(), config.getCrownRadi());
        this.leaves = new HashSet<>();
        this.leafRemover = getContext().actorOf(LeafRemover.props(getUniverse(), config));
        this.leavesId = config.getLeaves();
        this.thinLeavesId = config.getThinLeaves();
        this.config = config;
        this.scheduled = schedule();
    }

    private Cancellable schedule() {
        FiniteDuration duration =
            Duration.create((long)((float)config.getLeafDecayDelay() / speed),
                            TimeUnit.MILLISECONDS);
        return getContext().system().scheduler().schedule(duration, duration, getSelf(), new Trigger(),
                                                          getContext().system().dispatcher(), getSelf());
    }

    @Override
    public void onBoxQueryResult(BoxQueryResult result) {
        Map<Position, BlockTypeId> placed = result.getAsMap();
        for(Map.Entry<Position, BlockTypeId> p: placed.entrySet()) {
            BlockTypeId type = p.getValue();
            if(type.equals(leavesId) || type.equals(thinLeavesId))
                leaves.add(p.getKey());
        }
    }

    @Override
    public void onGlobalConfig(GlobalConfig config) {
        speed = config.getSimulationSpeed();
        scheduled.cancel();
        scheduled = schedule();
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof Trigger && leaves.size() > 0) {
            int current = 0;
            int find = random.nextInt(leaves.size());
            for(Iterator<Position> i = leaves.iterator(); i.hasNext();) {
                Position p = i.next();
                if(current == find) { /* Random leaf found, try to remove it */
                    getUniverse().tell(new BoxQuery(Box.createAround(p, radi)), leafRemover);
                    i.remove();
                    return;
                }
                current++;
            }
        } else {
            super.onReceive(message); // Handle konstructs messages
        }
    }

    public static Props props(ActorRef universe, ForestConfig config) {
        return Props.create(LeafDecay.class, universe, config);
    }
}
