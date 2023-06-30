package com.tinocs.mp.javafxengine;

import java.util.List;

import com.tinocs.javafxengine.World;
import com.tinocs.mp.client.Client;

import java.util.ArrayList;

/**
 * An MPWorld represents a world in a multiplayer game. MPWorlds keep a referent to
 * a {@link Client} that can communicate with the other clients.
 * @author Ted_McLeod
 *
 */
public abstract class MPWorld extends World {
    private Client client;
    
    /**
     * Set the client to the given client. If the client has a {@link JavafxEngineEventHandler}
     * then the world of that event handler will be set to this world.
     * @param client the client
     */
    public void setClient(Client client) {
        this.client = client;
        if (client.getEventHandler() instanceof JavafxEngineEventHandler) {
        	JavafxEngineEventHandler eh = (JavafxEngineEventHandler)client.getEventHandler();
        	eh.setWorld(this);
        }
    }

    /**
     * Returns the client.
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Returns the the MPActor in this world with the given actorId
     * or null if no such actor is in this world.
     * @param actorId the actorId of the actor to return
     * @return the the MPActor in this world with the given actorId
     * 		   or null if no such actor is in this world.
     */
    public MPActor getMPActor(String actorId) {
        for (MPActor ga : getObjects(MPActor.class)) {
            if (ga.getActorId().equals(actorId)) {
                return ga;
            }
        }
        return null;
    }

    /**
     * Returns a list of all the actors with the given clientId.
     * @param clientId the clientId of the actors
     * @return a list of all the actors with the given clientId
     */
    public List<MPActor> getClientActors(String clientId) {
        List<MPActor> clientActors = new ArrayList<>();
        for (MPActor ga : getObjects(MPActor.class)) {
            if (ga.getClientId().equals(clientId)) {//
                clientActors.add(ga);
            }
        }
        return clientActors;
    }
    
    /**
     * In addition to stopping the timer, also call the stopped method.
     * Subclasses can override the stopped() method to perform actions
     * that should occur if stop() is called. By default, the client
     * is disconnected.
     */
    @Override
    public void stop() {
    	super.stop();
    	stopped();
    }

    /**
     * Subclasses can override this method to perform actions
     * that should occur if stop() is called. By default,
     * the client is disconnected.
     */
    public void stopped() {
        client.disconnect();
    }
}
