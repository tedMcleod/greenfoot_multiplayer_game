package com.tinocs.mp.javafxengine;

import com.tinocs.javafxengine.Actor;
import com.tinocs.javafxengine.World;
import com.tinocs.mp.client.Client;

import javafx.scene.image.Image;

/**
 * An MPActor is an actor that is represented across multiple clients. MPActors have an actorId that allows
 * messages to identify the same actor across multiple clients and a clientId to indicate which client owns
 * the original LocalActor that the MPActor is representing. If an actor is a subclass of MPActor, but NOT a
 * subclass of LocalActor, then it is a representing a LocalActor that was created on another client.
 * The client that created the LocalActor this MPActor is representing generates the actorId and tells the
 * other clients to create an MPActor with the same actorId and a clientId matching the id of the client that
 * created the LocalActor.
 * 
 * In general all broadcasts will either be to the room the client is in or to all clients on the server if
 * the client is not in a room.
 * 
 * @author Ted_McLeod
 * @version 6/27/2023
 */
public abstract class MPActor extends Actor {
    
    private String actorId;
    private String clientId;
    
    /**
     * Create an MPActor with the given actorId and clientId.
     * @param actorId the actorId
     * @param clientId the clientId
     */
    public MPActor(String actorId, String clientId) {
        this.actorId = actorId;
        this.clientId = clientId;
    }
    
    /**
     * return the actorId
     * @return the actorId
     */
    public String getActorId() {
        return actorId;
    }
    
    /**
     * return the clientId
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }
    
    /**
     * do what needs to be done when this MPActor is destroyed
	 * (default implementation is to remove this actor from the world).
     */
    public void onDestroy() {
        getWorld().remove(this);
    }
    
    /**
     * If this actor is in an MPWorld, call onDestroy() on this actor
     * and if the client is connected, broadcast to the other clients
     * that this actor is destroyed.
     */
    public void destroy() {
        MPWorld gw = getWorldOfType(MPWorld.class);
        if (gw != null) {
            onDestroy();
            if (gw.getClient() != null && gw.getClient().isConnected()) {
                String msg = JavafxEngineEventHandler.CMD_DESTROY + " " + getActorId();
                String roomId = gw.getClient().getCurrentRoomId();
                if (roomId == null) {
                    gw.getClient().broadcastMessage(msg);
                } else {
                    gw.getClient().broadcastMessageToRoom(msg, roomId);
                }
            }
        }
    }
    
    /**
     * Broadcast a message to the other clients. If the client is in a room,
     * the message will be broadcast only to that room, otherwise it will be
     * broadcast to all clients on the server. This method will do nothing
     * if this actor is not in an MPWorld or the world does not have a client.
     * @param msg the message to broadcast
     */
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
