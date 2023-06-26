package com.tinocs.mp.javafxengine;
import engine.*;
import javafx.scene.image.Image;

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
        world.remove(this);
    }
    
    public <W extends World> W getWorldOfType(Class<W> cls) {
    	World world = getWorld();
    	if (world != null && cls.isInstance(getWorld())) return cls.cast(world);
    	return null;
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
    
    public void setImage(String resourcePath) {
    	setImage(new Image(resourcePath));
    }
}
