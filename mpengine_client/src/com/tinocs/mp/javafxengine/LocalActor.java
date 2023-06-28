package com.tinocs.mp.javafxengine;
import java.util.UUID;

import com.tinocs.mp.client.Client;

/**
 * <p>A LocalActor is an actor controlled by this client. Every LocalActor has a
 * corresponding MPActor that will be created on other clients and will mirror
 * all the actions of this local actor. The client that has the LocalActor is
 * responsible for telling other clients when to move, rotate, remove...etc the
 * corresponding MPActor representing this LocalActor in their world.</p>
 * 
 * <p>Every LocalActor listens to the following properties and broadcasts a message
 * informing the other clients of any changes so the corresponding MPActor can be updated:</p>
 * 
 * <ul>
 * 	<li>{@link javafx.scene.image.ImageView#xProperty()}</li>
 * 	<li>{@link javafx.scene.image.ImageView#yProperty()}</li>
 * 	<li>{@link javafx.scene.image.ImageView#rotateProperty()}</li>
 * 	<li>{@link javafx.scene.image.ImageView#opacityProperty()}</li>
 * 	<li>{@link javafx.scene.image.ImageView#scaleXProperty()}</li>
 * 	<li>{@link javafx.scene.image.ImageView#scaleYProperty()}</li>
 * 
 * </ul>
 * Note that currently using the {@link javafx.scene.image.ImageView#setImage(javafx.scene.image.Image)} method
 * will break the relationship between this LocalActor's image and the image of the MPActor
 * because there is no system for telling the corresponding MPActor to change its image to
 * an image that isn't in a file. This can easily be circumvented by defining a custom method in both the LocalActor
 * subclass and the corresponding MPActor subclass that creates the image. You can then make the method defined
 * in the LocalActor subclass broadcast to the other clients to call the corresponding method on the MPActor
 * with the same actorId. You can use a similar strategy for manipulating any other properties.
 * 
 * @author Ted McLeod 
 * @version 5/7/2023
 */
public abstract class LocalActor extends MPActor {
    
    // the class used to show this actor on other client
    private Class<? extends MPActor> otherClass;
    
    /**
     * Create a LocalActor with the given Client and corresponding MPActor class
	 * that will represent it on other clients.
     * @param client the client this LocalActor belongs to
     * @param otherClass the corresponding MPActor class that will represent this LocalActor on other clients
     */
    public LocalActor(Client client, Class<? extends MPActor> otherClass) {
        super(UUID.randomUUID().toString(), client.getId());
        this.otherClass = otherClass;
        xProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_MOVE + " " + getActorId() + " " + nv + " " + getY());
        });
        
        yProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_MOVE + " " + getActorId() + " " + getX() + " " + nv);
        });
        
        rotateProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_ROTATE + " " + getActorId() + " " + nv);
        });
        
        opacityProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_OPACITY + " " + getActorId() + " " + nv);
        });
        
        scaleXProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_SCALE_X + " " + getActorId() + " " + nv);
        });
        
        scaleYProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.CMD_SCALE_Y + " " + getActorId() + " " + nv);
        });
    }
    
    /**
     * Returns the class used to represent this actor on other clients
     * @return the class used to represent this actor on other clients
     */
    public Class<? extends MPActor> getOtherClass() {
        return otherClass;
    }
    
    /**
     * broadcasts a message to the other clients to add an instance of the other class representing this class to the world in the same position.
     * If there are any other properties that need to be set, you should override this method to broadcast message to update those properties too.
     */
    @Override
    public void addedToWorld() {
        broadcastMessage(JavafxEngineEventHandler.CMD_ADD + " " + otherClass.getName() + " " + getActorId() + " " + getX() + " " + getY());
    }
    
    /**
     * <p>Sets the image to the image at the given url and broadcasts the change to the other clients.
     * This method should be preferred over {@link javafx.scene.image.ImageView#setImage(javafx.scene.image.Image)}
     * when possible for two reasons:</p>
     * <ol>
     * 	<li>{@link javafx.scene.image.ImageView#setImage(javafx.scene.image.Image)} cannot automatically broadcast the change
     *     to the other clients because there is no text parameter to represent the image like there is for a url.</li>
     *  <li>This method will cache the image so all images from the same url can point to the same Image object in memory.</li>
     * </ol>
     * 
     * <p>If you must use {@link javafx.scene.image.ImageView#setImage(javafx.scene.image.Image)} then
     * you will need to define a custom method in both the LocalActor subclass and the corresponding MPActor subclass
	 * that creates the image. You can then make the method defined in the LocalActor subclass broadcast to the other
	 * clients to call the corresponding method on the MPActor with the same actorId.</p>
     * @param url the url of the image
     */
    @Override
    public void setImage(String url) {
    	super.setImage(url);
        broadcastMessage(JavafxEngineEventHandler.CMD_IMAGE + " " + getActorId() + " " + url);
    }
    
}
