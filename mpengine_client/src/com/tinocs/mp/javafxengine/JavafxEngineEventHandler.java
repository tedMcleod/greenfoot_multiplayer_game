package com.tinocs.mp.javafxengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.tinocs.javafxengine.Actor;
import com.tinocs.javafxengine.World;
import com.tinocs.mp.client.Client;
import com.tinocs.mp.client.ClientEventHandler;

/**
 * This is an implementation of the {@link ClientEventHandler} for games using the
 * com.tinocs.javafxengine library (see {@link Actor} and {@link World}.
 * 
 * This system is built on the core principal that actors will fall into one of three
 * categories:
 * <ol>
 * 		<li>{@link LocalActor}: An Actor whose state is controlled by this client. 
 * 			These actors will typically have an act method that performs actions every frame.
 * 			Each {@link LocalActor} will be associated with a corresponding subclass
 * 			of {@link MPActor} and an instance of that corresponding MPActor
 *			will mirror everything the LocalActor does on each other client. Each MPActor has an actorId and a
 *			clientId indicating which actor it represents across all clients and which client controls it, allowing
 *			messages to indicate which MPActor to call methods on. These actors can still be told to do something
 * 			by another client via messages, but the controlling client will be the one that actually
 * 			executes code to respond to the message.</li>
 * 		<li>{@link MPActor}: Actors controlled by another client are repesented
 * 			by subclasses of MPActor that are not subclasses of LocalActor. These will be the actors that
 *			mirror the state of a LocalActor controlled by another client. To change the state of one of these
 * 			actors in a way that should be visible to other clients, a message should be sent to the controlling
 *			client to change the LocalActor associated with it, which will result in a broadcast telling
 *			all clients to do the same action to the corresponding MPActor in their program.</li>
 * 		<li>{@link Actor}: Normal actors whose state is only relevant to the local client, such as HUD elements
 *          or whose state doesn't change, such as permanent scenery.
 *  </li>
 * </ol>
 * 
 * Not every property is handled, so subclasses may need to add more functionality for complex effects.
 * 
 * @author Ted McLeod 
 * @version 5/7/2023
 */
public abstract class JavafxEngineEventHandler implements ClientEventHandler {
    // Commands to change actor properties
	/**<p>Command to add an actor to the world.</p>
	 * The command will be in the form: senderId ADD className x y param1 param2 param3...
	 */
    public static final String CMD_ADD = "ADD";
    
    /**<p>Command to move an actor to the given position.</p>
	 * The command will be in the form: senderId MOVE actorId x y
	 */
    public static final String CMD_MOVE = "MOVE";
    
    /**<p>Command to rotate an actor to the given angle.</p>
	 * The command will be in the form: senderId ROT actorId angle
	 */
    public static final String CMD_ROTATE = "ROT";
    
    // Commands to manipulate the image of an actor
    /**<p>Command to set the image of an actor to the image at the given url.</p>
	 * <p>The command will be in the form: senderId IMG actorId url</p>
	 * <p>Note that if you want to set the image to a modified image rather
	 *    than an image located in a file, you should just make a custom
	 *    method that does that and send a
	 *    {@link #CMD_METHOD} command.</p>
	 */
    public static final String CMD_IMAGE = "IMG";
    
    /**<p>Command to set the opacity of an actor to the given value.</p>
	 * The command will be in the form: senderId OPACITY actorId value
	 */
    public static final String CMD_OPACITY = "OPACITY";
    
    /**<p>Command to set the horizontal scale factor of an actor to the given scaleX </p>
	 * The command will be in the form: senderId SCALE actorId scaleX
	 */
    public static final String CMD_SCALE_X = "SCALEX";
    
    /**<p>Command to set the vertical scale factor of an actor to the given scaleY </p>
	 * The command will be in the form: senderId SCALE actorId scaleY
	 */
    public static final String CMD_SCALE_Y = "SCALEY";
    
    /**<p>use this command to tell the other clients to call a custom method on an actor with a given id</p>
     * <p>The command will be in the form: senderId METHOD actorId methodName param1 param2 param3...</p>
     * <p>Custom methods should take String parameters and the method should parse them as needed</p>
     */
    public static final String CMD_METHOD = "METHOD";
    
    /**<p>Command to remove an actor.</p>
	 * The command will be in the form: senderId DESTROY actorId
	 */
    public static final String CMD_DESTROY = "DESTROY";
    
    private MPWorld world;
    
    /**
     * Create a GreenfootClient that will connect to the given hostName and portNumber.
     */
    public JavafxEngineEventHandler(MPWorld world) {
        this.world = world;
    }
    
    /**
     * If a client disconnects, remove all the MPActors controlled by that client.
     */
    @Override
    public void handleOtherClientDisconnected(String clientId, Client client) {
        for (MPActor mpa : world.getClientActors(clientId)) {
            getWorld().remove(mpa);
        }
    }
    
    @Override
    public void handleOtherClientJoined(String clientId, Client client) {
        // This will respond to the sender client with messages saying to add the
        // corresponding classes that represent each LocalActor this client controls
        String myRoomId = client.getCurrentRoomId();
        String otherRoomId = client.getIdOfRoomContainingClient(clientId);
        if (otherRoomId == null && myRoomId == null || (myRoomId != null && myRoomId.equals(otherRoomId))) {
            for (LocalActor la : getWorld().getObjects(LocalActor.class)) {
                String msg = JavafxEngineEventHandler.CMD_ADD + " " + la.getOtherClass().getName() + " " + la.getActorId() + " " + la.getX() + " " + la.getY();
                client.sendMessage(msg, clientId);
            }
        }
    }

    /**
     * handle messages from other clients. The following commands are already handled as described below.
     * If you override this method, be sure to call super.handleCommand(command, client) so these commands
     * will still be handled. If you want to modify what these commands do, you can simply override the corresponding methods.
     * <ul>
     * 	<li>{@link #CMD_ADD}: calls {@link #handleAddCmd(String, String, String, double, double)}</li>
     * 	<li>{@link #CMD_MOVE}: calls {@link #handleMethodCmd(String, String, List)}</li>
     * 	<li>{@link #CMD_ROTATE}: calls {@link #handleSetRotationCmd(String, double)}</li>
     * 	<li>{@link #CMD_IMAGE}: calls {@link #handleSetImageCmd(String, String)}</li>
     * 	<li>{@link #CMD_OPACITY}: calls {@link #handleSetOpacityCmd(String, double)}</li>
     * 	<li>{@link #CMD_SCALE}: calls {@link #handleScaleCmd(String, double, double)}</li>
     * 	<li>{@link #CMD_METHOD}: calls {@link #handleMethodCmd(String, String, List)}</li>
     * 	<li>{@link #CMD_DESTROY}: calls {@link #handleDestroyCmd(String)}</li>
     * </ul>
     */
    @Override
    public void handleCommand(String command, Client client) {
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        String senderId = scan.next();
        
        // The second token is the command (ADD, MOVE, ROT, DESTROY...etc)
        String cmd = scan.next();
        
        if (cmd.equals(CMD_ADD)) {
            // The command will be in the form: senderId ADD className x y param1 param2 param3...
            String className = scan.next();
            double x = scan.nextDouble();
            double y = scan.nextDouble();
            ArrayList<String> params = new ArrayList<>();
            while (scan.hasNext()) params.add(scan.next());
            handleAddCmd(className, x, y, params);
        } else if (cmd.equals(CMD_MOVE)) {
            // The command will be in the form: senderId MOVE actorId x y
            String actorId = scan.next();
            double x = scan.nextDouble();
            double y = scan.nextDouble();
            handleSetLocationCmd(actorId, x, y);
        } else if (cmd.equals(CMD_ROTATE)) {
            // The command will be in the form: senderId ROT actorId angle
            String actorId = scan.next();
            double angle = scan.nextDouble();
            handleSetRotationCmd(actorId, angle);
        } else if (cmd.equals(CMD_IMAGE)) {
            // The command will be in the form: senderId IMG actorId url
            String actorId = scan.next();
            String url = scan.next();
            handleSetImageCmd(actorId, url);
        } else if (cmd.equals(CMD_OPACITY)) {
            // The command will be in the form: senderId OPACITY actorId value
            String actorId = scan.next();
            double value = scan.nextDouble();
            handleSetOpacityCmd(actorId, value);
        } else if (cmd.equals(CMD_SCALE_X)) {
            // The command will be in the form: senderId SCALEX actorId scaleX
            String actorId = scan.next();
            double scaleX = scan.nextDouble();
            handleScaleXCmd(actorId, scaleX);
        } else if (cmd.equals(CMD_SCALE_Y)) {
            // The command will be in the form: senderId SCALEY actorId scaleY
            String actorId = scan.next();
            double scaleY = scan.nextDouble();
            handleScaleYCmd(actorId, scaleY);
        } else if (cmd.equals(CMD_METHOD)) {
            // The command will be in the form: senderId METHOD actorId methodName param1 param2 param3...
            // custom methods should take String parameters and the method should parse them as needed 
            String actorId = scan.next();
            String methodName = scan.next();
            ArrayList<String> params = new ArrayList<>();
            while (scan.hasNext()) params.add(scan.next());
            handleMethodCmd(actorId, methodName, params);
        } else if (cmd.equals(CMD_DESTROY)) {
            // The command will be in the form: senderId DESTROY actorId
            String actorId = scan.next();
            handleDestroyCmd(actorId);
        }
        scan.close();
    }
    
    /**
     * Create an instance of the class with the given className and add it to the world at (x, y).
     * Assign it the given actorId and clientId and initializing it with the given parameters.
     * This method will throw a {@link ClassCastException} if the class is not a subclass of {@link Actor}.
     * @param className the name of the class to make an instance of
     * @param x the x position to add the actor
     * @param y the y position to add the actor
     * @param parameters the remaining parameters to pass to the constructor (must be Strings)
     */
    protected void handleAddCmd(String className, double x, double y, List<String> parameters) {
    	try {
            Class<?> cls = Class.forName(className);
            Class<?>[] paramTypes = new Class<?>[parameters.size()];
            for (int i = 0; i < paramTypes.length; i++) paramTypes[i] = String.class;
            Constructor<?> constr = cls.getDeclaredConstructor(paramTypes);
            Actor actor = (Actor)constr.newInstance(parameters.toArray());
        	getWorld().add(actor);
            actor.setX(x);
            actor.setY(y);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException err) {
            err.printStackTrace();
        }
    }
    
    /**
     * Sets the position of the actor with the given actorId to (x, y)
     * @param actorId the id of the actor
     * @param x the x coordinate
     * @param y the y coordinate
     */
    protected void handleSetLocationCmd(String actorId, double x, double y) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) {
        	mpa.setX(x);
        	mpa.setY(y);
        }
    }
    
    /**
     * Sets the rotation of the actor with the given actorId to the given angle
     * @param actorId the id of the actor
     * @param angle the angle to set the rotation to
     */
    protected void handleSetRotationCmd(String actorId, double angle) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.setRotate(angle);
    }
    
    /**
     * Sets the image of the actor with the given actorId to the image at the given url.
     * See {@link Actor#setImage(String)}
     * @param actorId the id of the actor
     * @param url the path to the image resource. For example, if the image is in the
     *        images package and is named "pic.png", the url would be "images/pic.png"
     */
    protected void handleSetImageCmd(String actorId, String url) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.setImage(url);
    }
    
    /**
     * Sets the opacity of the actor with the given actorId to the given opacity on a scale of 0 to 1.
     * See {@link Actor#setOpacity(double)}
     * @param actorId the id of the actor
     * @param opacity the opacity
     */
    protected void handleSetOpacityCmd(String actorId, double opacity) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null)  mpa.setOpacity(opacity);
    }
    
    /**
     * Sets the scaleX of the actor with the given actorId
     * See {@link Actor#setScaleX(double)}
     * @param actorId the id of the actor
     * @param scaleX the horizontal scale factor 
     */
    protected void handleScaleXCmd(String actorId, double scaleX) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) {
        	mpa.setScaleX(scaleX);
        }
    }
    
    /**
     * Sets the scaleY of the actor with the given actorId
     * See {@link Actor#setScaleY(double)}
     * @param actorId the id of the actor
     * @param scaleY the vertical scale factor
     */
    protected void handleScaleYCmd(String actorId, double scaleY) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) {
        	mpa.setScaleY(scaleY);
        }
    }
    
    /**
     * Call the method with the given method name and parameters on the actor with the given actorId.
     * The parameters must be all strings, so any parsing of parameters into other data types must be
     * done in the method being called.
     * @param actorId the id of the actor
     * @param methodName the name of the method to be called
     * @param parameters the parameters to pass to the method
     */
    protected void handleMethodCmd(String actorId, String methodName, List<String> parameters) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) {
            Class<?>[] paramTypes = new Class<?>[parameters.size()];
            for (int i = 0; i < paramTypes.length; i++) paramTypes[i] = String.class;
            try {
                Method method = mpa.getClass().getMethod(methodName, paramTypes);
                method.invoke(mpa, parameters.toArray());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException err) {
                err.printStackTrace();
            }
        }
    }
    
    /**
     * Calls destroy on the actor with the given actorId.
     * @param actorId the id of the actor
     */
 // TODO: fix this the way it is done in GreenfootEventHandler (just remove the actor). Refactor the command to remove.
    protected void handleDestroyCmd(String actorId) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.destroy();
    }
    
    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     */
    @Override
    public void onDisconnected(Client client) {
        getWorld().stop();
    }

    /**
     * return the GameWorld for this client.
     * @return the GameWorld for this client.
     */
    public MPWorld getWorld() {
        return world;
    }

    /**
     * Set the GameWorld for this client.
     * @param world the game world
     */
    public void setWorld(MPWorld world) {
        this.world = world;
    }
}
