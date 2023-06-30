package com.tinocs.mp.greenfoot;

import greenfoot.GreenfootImage;

/**
 * A LocalImage is used as the image of a {@link LocalActor}. A LocalImage saves a reference to the actor they
 * belong to and whenever the image changes, it broadcasts the change to the other clients so they can
 * update the images of the corresponding actors in their worlds to reflect changes.
 * 
 * @author Ted McLeod 
 * @version 5/8/2023
 */
public class LocalImage extends GreenfootImage {
    
	// the actor this image is the image of
    private LocalActor actor;
    
    /**
     * Create a LocalImage that is a copy of the given image and belongs to the given actor.
     * 
     * @param image the image to copy
     * @param actor the {@link LocalActor} this image belongs to
     */
    public LocalImage(GreenfootImage image, LocalActor actor) {
        super(image);
        this.actor = actor;
    }
    
    /**
     * Create a LocalImage loaded from the file with the given fileName that belongs to the given actor.
     * The file with the given fileName should be located in the images folder of the scenario.
     * 
     * @param fileName the name of the image file
     * @param actor the {@link LocalActor} this image belongs to
     */
    public LocalImage(String fileName, LocalActor actor) {
        super(fileName);
        this.actor = actor;
    }
    
    /**
     * Returns the {@link LocalActor} this image belongs to
     * 
     * @return the {@link LocalActor} this image belongs to
     */
    public LocalActor getActor() {
        return actor;
    }
    
    /**
     * Set the {@link LocalActor} this image belongs to
     * 
     * @param actor the {@link LocalActor} this image belongs to
     */
    protected void setActor(LocalActor actor) {
    	this.actor = actor;
    }
    
    /**
     * Broadcast the given message to the other clients.
     * If the actor is null or is not in an MPWorld with a connected client,
     * then nothing happens.
     * 
     * @param message the message to broadcast.
     */
    public void broadcast(String message) {
    	if (actor != null) actor.broadcastMessage(message);
    }
    
    /**
     * Set the transparency of the image and broadcast the change to the other clients.
     * 
     * @param t A value in the range 0 to 255. 0 is completely transparent (invisible)
	 * 		  and 255 is completely opaque (the default).
     */
    @Override
    public void setTransparency(int t) {
        super.setTransparency(t);
        if (actor != null) broadcast(GreenfootEventHandler.getTransparencyCmd(actor.getActorId(), getTransparency()));
    }
    
    /**
     * Mirror this image horizontally and broadcast the change to the other clients.
     */
    @Override
    public void mirrorHorizontally() {
        super.mirrorHorizontally();
        if (actor != null) broadcast(GreenfootEventHandler.getMirrorHorizontallyCmd(actor.getActorId()));
    }
    
    /**
     * Mirror this image vertically and broadcast the change to the other clients.
     */
    @Override
    public void mirrorVertically() {
        super.mirrorVertically();
        if (actor != null) broadcast(GreenfootEventHandler.getMirrorVerticallyCmd(actor.getActorId()));
    }
    
    /**
     * Scale this image to the given width and height and broadcast the change to the other clients.
     * 
     * @param width the width after scaling
     * @param height the height after scaling
     */
    @Override
    public void scale(int width, int height) {
        super.scale(width, height);
        if (actor != null) broadcast(GreenfootEventHandler.getScaleCmd(actor.getActorId(), getWidth(), getHeight()));
    }
}
