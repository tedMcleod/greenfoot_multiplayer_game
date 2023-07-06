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
        if (client.getEventHandler() instanceof GreenfootEventHandler) {
        	GreenfootEventHandler eh = (GreenfootEventHandler)client.getEventHandler();
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
    
    /**
     * If an {@link MPActor} is removed, then broadcast a command to remove the actor from other clients.
     * If this client is in a room, the message will be broadcast to the room, otherwise it will
     * be broadcast to all other clients. Before an MPActor is removed, this method will call
     * {@link MPActor#willBeRemoved()} and after an MPActor has been removed, this method will call
     * {@link MPActor#hasBeenRemoved(MPWorld)}, passing this world as the parameter.
     */
    @Override
    public void removeObject(Actor object) {
    	if (object instanceof MPActor) {
    		MPActor mpa = (MPActor)object;
    		Client client = getClient();
            if (client != null && client.isConnected()) {
                String msg = GreenfootEventHandler.getRemoveCmd(mpa.getActorId());
                String roomId = client.getCurrentRoomId();
                if (roomId == null) {
                	client.broadcastMessage(msg);
                } else {
                	client.broadcastMessageToRoom(msg, roomId);
                }
            }
            mpa.willBeRemoved();
            super.removeObject(object);
            mpa.hasBeenRemoved(this);
    	} else {
    		super.removeObject(object);
    	}
    }

    @Override
    public void stopped() {
        client.disconnect();
    }
}
