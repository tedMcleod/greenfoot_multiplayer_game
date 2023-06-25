package com.tinocs.mp.javafxengine;

import engine.*;

import java.util.List;

import com.tinocs.mp.client.Client;

import java.util.ArrayList;

public abstract class MPWorld extends World {
    Client client;
    
    public void setClient(Client client) {
        this.client = client;
        JavafxEngineEventHandler eh = (JavafxEngineEventHandler)client.getEventHandler();
        if (eh != null) {
            eh.setWorld(this);
        }
    }

    public Client getClient() {
        return client;
    }

    public MPActor getGameActor(String actorId) {
        for (MPActor ga : getObjects(MPActor.class)) {
            if (ga.getActorId().equals(actorId)) {
                return ga;
            }
        }
        return null;
    }

    public List<MPActor> getClientActors(String clientId) {
        List<MPActor> clientActors = new ArrayList<>();
        for (MPActor ga : getObjects(MPActor.class)) {
            if (ga.getClientId().equals(clientId)) {//
                clientActors.add(ga);
            }
        }
        return clientActors;
    }
    
    @Override
    public void stop() {
    	super.stop();
    	stopped();
    }

    public void stopped() {
        client.disconnect();
    }
}
