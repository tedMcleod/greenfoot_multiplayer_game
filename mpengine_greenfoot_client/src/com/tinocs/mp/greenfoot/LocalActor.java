package com.tinocs.mp.greenfoot;

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.UUID;

import com.tinocs.mp.client.Client;

/**
 * <p>A LocalActor is an actor controlled by this client. Every LocalActor has a
 * corresponding MPActor that will be created on other clients when the LocalActor
 * is added to a world and that {@link MPActor} will mirror all the actions of this local actor.
 * The client that created the LocalActor is responsible for telling other clients when to
 * move, rotate, remove...etc the corresponding MPActor representing this LocalActor in their world.
 * Most actions will automatically be broadcast, but some custom image manipulation may need to
 * be handled separately with {@link GreenfootEventHandler#CMD_METHOD} commands.</p>
 * 
 * <p>If this client is in a room, the broadcast messages will be to all other clients in the same room,
 * otherwise it will be to all clients.</p>
 * 
 * <p>Note that the {@link LocalActor#setImage(GreenfootImage)} method will not automatically broadcast
 * the image change to the other clients because there is no filename to send. If you want to change the
 * image of this actor to an image that is not in a file, you should make a method in the LocalActor
 * subclass that draws the image, sets the image of this actor to that image and then sends a
 * {@link GreenfootEventHandler#CMD_METHOD} command to the other clients that tells them to call a method
 * that performs the same actions on the actor with the same id in their world.</p>
 * 
 * @author Ted McLeod 
 * @version 5/7/2023
 */
public abstract class LocalActor extends MPActor {
    
    // the class used to show this actor on other client
    private Class<? extends MPActor> otherClass;
    
    /**
     * Create a LocalActor controlled by the given client and represented
	 * by the given otherClass on other clients.
	 * 
     * @param client the client that controls this LocalActor
     * @param otherClass the class that should be used to represent this actor on other clients.
     */
    public LocalActor(Client client, Class<? extends MPActor> otherClass) {
        super(UUID.randomUUID().toString(), client.getId());
        this.otherClass = otherClass;
        // replace default GreenfootImage with a LocalImage
        setImage(new LocalImage(getImage(), this));
    }
    
    /**
     * Returns the class used to represent this actor on other clients.
     * 
     * @return the class used to represent this actor on other clients
     */
    public Class<? extends MPActor> getOtherClass() {
        return otherClass;
    }
    
    /**
     * When a LocalActor is added to the world, a messages is broadcast to the other clients to
     * add an instance of the otherClass to the world.
     * 
     * @param world the world this actor was added to
     */
    @Override
    public void addedToWorld(World world) {
    	broadcastMessage(GreenfootEventHandler.getAddCmd(otherClass, getX(), getY(), getConstructorParameters()));
    }
    
    /**
     * Override this method to add to the String representing the parameters that should be passed to the constructor
     * of the corresponding MPActor on other clients (see {@link #getOtherClass()}). By default this method returns
	 * {actorId, clientId}, where actorId is the id of this actor and the clientId is the id of the client of the MPWorld
	 * this actor is in. If this actor is not in an MPWorld or if the client is null, then clientId will be null, but that
	 * should be harmless since in that case, the message this string is part of will not be broadcast.
	 * 
     * @return an array of parameters that should be passed to the constructor of the corresponding MPActor on other clients
     */
    public Object[] getConstructorParameters() {
    	String clientId = null;
    	MPWorld mpw = getWorldOfType(MPWorld.class);
        if (mpw != null && mpw.getClient() != null) {
        	clientId = mpw.getClient().getId();
        }
        return new Object[]{getActorId(), clientId};
    }
    
    /**
     * Sets the location of this actor and broadcasts a message to the other clients
     * telling them to move the actor with the same id in their world to the same location.
     * 
     * @param x the x-coordinate of the new location
     * @param y the y-coordinate of the new location
     */
    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        broadcastMessage(GreenfootEventHandler.getMoveCmd(getActorId(), getX(), getY()));
    }
    
    /**
     * Sets the rotation of this actor and broadcasts a message to the other clients
     * telling them to set the rotation of the actor with the same id in their world
     * to the same angle. Rotation is expressed as a degree value, range (0..359).
	 * Zero degrees is to the east (right-hand side of the world), and the angle increases clockwise.
	 * 
     * @param rotation The rotation in degrees
     */
    @Override
    public void setRotation(int rotation) {
        super.setRotation(rotation);
        broadcastMessage(GreenfootEventHandler.getRotateCmd(getActorId(), getRotation()));
    }
    
    /**
     * Set the image for this actor to a {@link LocalImage} loaded from the file with the given fileName and
	 * broadcasts a message to the other clients telling them to set the image of the actor with the same id
	 * in their world to the the image in the same file. The file may be in jpeg, gif or png format. The file
	 * should be located in the images folder of the project.
	 * 
     * @param fileName The name of the image file.
     * @throws IllegalArgumentException if the image can not be loaded.
     */
    @Override
    public void setImage(String fileName) throws IllegalArgumentException {
    	// If the old image is manipulated later, we don't want it to broadcast changes
    	// since it is no longer the image of this actor.
    	if (getImage() instanceof LocalImage) ((LocalImage)getImage()).setActor(null);
        super.setImage(new LocalImage(fileName, this));
        broadcastMessage(GreenfootEventHandler.getImageCmd(getActorId(), fileName));
    }
    
    /**
     * Sets the image of this actor to a {@link LocalImage} that is either a copy of the given image
     * (if the image passed in is not a LocalImage) or a direct reference to the image passed in
     * (if it is already a LocalImage). This method ensures the the image of a LocalActor is always
	 * a LocalImage, so changes to the image will be broadcast to the other clients. If you want the
	 * image of the actor to be a reference to the same image you pass in, you should create a LocalImage
     * and pass that image to this method.
     * 
     * @param image the image
     */
    @Override
    public void setImage(GreenfootImage image) {
    	// If the old image is manipulated later, we don't want it to broadcast changes unless getImage()
    	// and image are the exact same object (in which case, nothing is really changing).
    	if (getImage() instanceof LocalImage && getImage() != image) ((LocalImage)getImage()).setActor(null);
    	if (image instanceof LocalImage) {
    		((LocalImage)getImage()).setActor(this); // link the local image to this actor
    		super.setImage(image);
    	}
    	else super.setImage(new LocalImage(image, this));
    }
    
}
