package com.tinocs.mp.javafxengine;
import java.util.UUID;

/**
 * A LocalActor is an actor controlled by this client. Every LocalActor has a
 * corresponding GameActor that will be created on other clients and will mirror
 * all the actions of this local actor. The client that has the LocalActor is
 * responsible for telling other clients when to move, rotate, remove...etc the
 * corresponding GameActor representing this LocalActor in their world.
 * 
 * Note that currently using the setImage(GreenfootImage img) method will break the relationship
 * because there is no system for telling the corresponding GameActor to change its image to
 * an image that isn't in a file. This could be circumvented by making a custom function in the LocalActor
 * that draws the custom image and then sends a message to the other clients to call a custom method on the
 * corresponding actors that draws the same image.
 * 
 * @author Ted McLeod 
 * @version 5/7/2023
 */
public abstract class LocalActor extends MPActor {
    
    // the class used to show this actor on other client
    private Class<? extends MPActor> otherClass;
    
    public LocalActor(String clientId) {
        super(UUID.randomUUID().toString(), clientId);
        xProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_MOVE + " " + getActorId() + " " + nv + " " + getY());
        });
        
        yProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_MOVE + " " + getActorId() + " " + getX() + " " + nv);
        });
        
        rotateProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_ROTATE + " " + getActorId() + " " + nv);
        });
    }
    
    public void setOtherClass(Class<? extends MPActor> otherClass) {
    	// TODO: broadcast a message to other clients to replace existing instances of
    	// the previous other class with instances of the new otherClass
        this.otherClass = otherClass;
    }
    
    public Class<? extends MPActor> getOtherClass() {
        return otherClass;
    }
    
    @Override
    public void addedToWorld() {
        broadcastMessage(JavafxEngineEventHandler.CMD_ADD + " " + otherClass.getName() + " " + getActorId() + " " + getX() + " " + getY());
    }
    
    public void setImage(String resourcePath) {
    	super.setImage(resourcePath);
        broadcastMessage(JavafxEngineEventHandler.CMD_IMAGE + " " + getActorId() + " " + resourcePath);
    }
    
}