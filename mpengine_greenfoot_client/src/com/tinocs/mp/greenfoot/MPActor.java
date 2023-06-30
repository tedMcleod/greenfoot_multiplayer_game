package com.tinocs.mp.greenfoot;

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
/**
 * An MPActor is an actor that is replicated across multiple clients. The actorId is the identifier that
 * identifies which actors represent the same object across clients. The clientId is the id of the client
 * that owns the MPActor. If the actor is a {@link LocalActor}, then the clientId will be this client's id,
 * otherwise the clientId will be the id of whichever client told this client to create this actor (usually
 * the client that created a corresponding LocalActor).
 * 
 * @author Ted_McLeod
 * @version 6/30/2023
 */
public abstract class MPActor extends Actor {
    
    private String actorId;
    private String clientId;
    
    /**
     * Create an MPActor with the given actorId and clientId.
     * @param actorId the id of this actor
     * @param clientId the id of the client that owns this actor
     */
    public MPActor(String actorId, String clientId) {
        this.actorId = actorId;
        this.clientId = clientId;
    }
    
    /**
     * Returns the id of this actor
     * @return the id of this actor
     */
    public String getActorId() {
        return actorId;
    }
    
    /**
     * Returns the id of the client that owns this actor.
     * @return the id of the client that owns this actor.
     */
    public String getClientId() {
        return clientId;
    }
    
    /**
     * Remove this actor from the world and broadcast to the other clients that it was removed.
     */
    public void remove() {
        MPWorld mpw = getWorldOfType(MPWorld.class);
        if (mpw != null) {
            mpw.removeObject(this);
            if (mpw.getClient() != null && mpw.getClient().isConnected()) {
                String msg = GreenfootEventHandler.getRemoveCmd(getActorId());
                String roomId = mpw.getClient().getCurrentRoomId();
                if (roomId == null) {
                    mpw.getClient().broadcastMessage(msg);
                } else {
                    mpw.getClient().broadcastMessageToRoom(msg, roomId);
                }
            }
        }
    }
    
    public void broadcastMessage(String msg) {
        MPWorld gw = getWorldOfType(MPWorld.class);
        if (gw != null && gw.getClient() != null) {
            String roomId = gw.getClient().getCurrentRoomId();
            if (roomId == null) {
                gw.getClient().broadcastMessage(msg);
            } else {
                gw.getClient().broadcastMessageToRoom(msg, roomId);
            }
        }
    }
}
