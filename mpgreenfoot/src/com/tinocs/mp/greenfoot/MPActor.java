package com.tinocs.mp.greenfoot;

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public abstract class MPActor extends Actor {
    
    private String actorId;
    private String clientId;
    
    public MPActor(String actorId, String clientId) {
        this.actorId = actorId;
        this.clientId = clientId;
    }
    
    public String getActorId() {
        return actorId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void onDestroy(MPWorld world) {
        world.removeObject(this);
    }
    
    public void destroy() {
        MPWorld gw = getWorldOfType(MPWorld.class);
        if (gw != null) {
            onDestroy(gw);
            if (gw.getClient() != null && gw.getClient().isConnected()) {
                String msg = "DESTROY " + getActorId();
                String roomId = gw.getClient().getCurrentRoomId();
                if (roomId == null) {
                    gw.getClient().broadcastMessage(msg);
                } else {
                    gw.getClient().broadcastMessageToRoom(msg, roomId);
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
