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
 * 	<li>{@link #xProperty()}</li>
 * 	<li>{@link #yProperty()}</li>
 * 	<li>{@link #rotateProperty()}</li>
 * 	<li>{@link #opacityProperty()}</li>
 * 	<li>{@link #scaleXProperty()}</li>
 * 	<li>{@link #scaleYProperty()}</li>
 * 
 * </ul>
 * Note that currently using the {@link #setImage(javafx.scene.image.Image)} method
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
        	broadcastMessage(JavafxEngineEventHandler.getMoveCmd(getActorId(), nv.doubleValue(), getY()));
        });
        
        yProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.getMoveCmd(getActorId(), getX(), nv.doubleValue()));
        });
        
        rotateProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.getRotateCmd(getActorId(), nv.doubleValue()));
        });
        
        opacityProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.getOpacityCmd(getActorId(), nv.doubleValue()));
        });
        
        scaleXProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.getScaleXCmd(getActorId(), nv.doubleValue()));
        });
        
        scaleYProperty().addListener((obj, ov, nv) -> {
        	broadcastMessage(JavafxEngineEventHandler.getScaleYCmd(getActorId(), nv.doubleValue()));
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
     * If the constructor takes more parameters, you should override the {@link #getConstructorParameters()} method to include those parameters.
     */
    @Override
    public void addedToWorld() {
    	broadcastMessage(JavafxEngineEventHandler.getAddCmd(otherClass, getX(), getY(), getConstructorParameters()));
    }
    
    /**
     * <p>Override this method to add parameters that should be passed to the constructor of the corresponding MPActor
     * on other clients (see {@link #getOtherClass()}). By default this method returns {actorId, clientId}, where actorId
	 * is the id of this actor and the clientId is the id of the client of the MPWorld this actor is in.
	 * If this actor is not in an MPWorld or if the client is null, then clientId will be null, but that
	 * should be harmless since in that case, the message this string is part of will not be broadcast.</p>
	 * 
	 * <p>Make sure the parameter objects are either strings or are objects for which the toString() method returns the desired
     * String as they will ultimately be converted to strings for the broadcast message. Primitives are fine because they will
     * be autoboxed to an instance of the corresponding wrapper class and later converted to a string representation.</p>
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
     * <p>Sets the image to the image at the given url and broadcasts the change to the other clients.
     * This method should be preferred over {@link #setImage(javafx.scene.image.Image)}
     * when possible for two reasons:</p>
     * <ol>
     * 	<li>{@link #setImage(javafx.scene.image.Image)} cannot automatically broadcast the change
     *     to the other clients because there is no text parameter to represent the image like there is for a url.</li>
     *  <li>This method will cache the image so all images from the same url can point to the same Image object in memory.</li>
     * </ol>
     * 
     * <p>If you must use {@link #setImage(javafx.scene.image.Image)} then you will need to define
     * a custom method in both the LocalActor subclass and the corresponding MPActor subclass
	 * that creates the image. You can then make the method defined in the LocalActor subclass broadcast
	 * to the other clients to call the corresponding method on the MPActor with the same actorId.</p>
     * @param url the url of the image
     */
    @Override
    public void setImage(String url) {
    	super.setImage(url);
        broadcastMessage(JavafxEngineEventHandler.getImageCmd(getActorId(), url));
    }
    
}
