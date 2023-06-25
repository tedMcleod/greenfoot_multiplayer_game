package com.tinocs.mp.greenfoot;

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.tinocs.mp.client.*;
import java.util.List;
import java.util.ArrayList;

public abstract class MPWorld extends World {
    Client client;

    public MPWorld(int width, int height, int cellSize, boolean bounded) {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(width, height, cellSize, bounded);
    }
    
    public void setClient(Client client) {
        this.client = client;
        GreenfootEventHandler eh = (GreenfootEventHandler)client.getEventHandler();
        if (eh != null) {
            eh.setWorld(this);
        }
    }

    public Client getClient() {
        return client;
    }

    public MPActor getMPActor(String actorId) {
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
    public void stopped() {
        client.disconnect();
    }
}
