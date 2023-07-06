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
     * Called right before this MPActor is removed from an {@link MPWorld}.
     * By default this does nothing. Override this method to do something
     * right before removal.
     */
    public void willBeRemoved() {
    	
    }
    
    /**
     * Called right after this MPActor has been removed from an {@link MPWorld}.
     * By default this does nothing. Override this method to do something
     * right after removal. Note that calling getWorld() inside this method
     * will return null because this actor has already been removed. To 
     * access the world the actor used to be in, use the world parameter.
     * @param world the world this actor was removed from.
     */
    public void hasBeenRemoved(MPWorld world) {
    	
    }
    
    /**
     * Broadcast a message to the other clients.
     * If this actor is not in a world or if the world does not have a connected client, then
     * nothing will happen.
     * If this client is in a room, the message will be broadcast to the room, otherwise it will
     * be broadcast to all other clients.
     * @param msg the message to broadcast
     */
    public void broadcastMessage(String msg) {
        MPWorld mpw = getWorldOfType(MPWorld.class);
        if (mpw != null && mpw.getClient() != null) {
            String roomId = mpw.getClient().getCurrentRoomId();
            if (roomId == null) {
                mpw.getClient().broadcastMessage(msg);
            } else {
                mpw.getClient().broadcastMessageToRoom(msg, roomId);
            }
        }
    }
}
