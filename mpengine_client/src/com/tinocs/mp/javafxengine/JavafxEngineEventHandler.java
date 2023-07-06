package com.tinocs.mp.javafxengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

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
    public static final String CMD_REMOVE = "REMOVE";
    
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
     * 	<li>{@link #CMD_ROTATE}: calls {@link #handleRotateCmd(String, double)}</li>
     * 	<li>{@link #CMD_IMAGE}: calls {@link #handleSetImageCmd(String, String)}</li>
     * 	<li>{@link #CMD_OPACITY}: calls {@link #handleSetOpacityCmd(String, double)}</li>
     * 	<li>{@link #CMD_SCALE}: calls {@link #handleScaleCmd(String, double, double)}</li>
     * 	<li>{@link #CMD_METHOD}: calls {@link #handleMethodCmd(String, String, List)}</li>
     * 	<li>{@link #CMD_REMOVE}: calls {@link #handleRemoveCmd(String)}</li>
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
            handleMoveCmd(actorId, x, y);
        } else if (cmd.equals(CMD_ROTATE)) {
            // The command will be in the form: senderId ROT actorId angle
            String actorId = scan.next();
            double angle = scan.nextDouble();
            handleRotateCmd(actorId, angle);
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
        } else if (cmd.equals(CMD_REMOVE)) {
            // The command will be in the form: senderId DESTROY actorId
            String actorId = scan.next();
            handleRemoveCmd(actorId);
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
    private void handleAddCmd(String className, double x, double y, List<String> parameters) {
    	try {
            Class<?> cls = Class.forName(className, true, getClass().getClassLoader());
            Class<?>[] paramTypes = new Class<?>[parameters.size()];
            for (int i = 0; i < paramTypes.length; i++) paramTypes[i] = String.class;
            Constructor<?> constr = cls.getDeclaredConstructor(paramTypes);
            Actor actor = (Actor)constr.newInstance(parameters.toArray());
            actor.setX(x);
            actor.setY(y);
        	getWorld().add(actor);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException err) {
            err.printStackTrace();
        }
    }
    
    /**
     * Returns a string defining a {@link #CMD_ADD} command.
     * Note that the senderId will not be included in the command because that token
     * is automatically added to the beginning of every command when it is broadcast.
     * If the class is not a subclass of {@link Actor}, the other clients will throw a {@link ClassCastException}
     * when trying to handle the command.
     * @param cls The class to make an instance of
     * @param x the x-coordinate of the position to add the actor
     * @param y the y-coordinate of the position to add the actor
     * @param parameters the parameters to pass to the constructor
     * @return a String defining a CMD_ADD command
     */
    public static String getAddCmd(Class<?> cls, double x, double y, Object... parameters) {
    	String cmd = Client.getCmdStr(CMD_ADD, cls.getName(), x, y);
    	if (parameters.length > 0) cmd += " " + Client.getCmdStr(parameters);
    	return cmd;
    }
    
    /**
     * Sets the position of the actor with the given actorId to (x, y)
     * @param actorId the id of the actor
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private void handleMoveCmd(String actorId, double x, double y) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) {
        	mpa.setX(x);
        	mpa.setY(y);
        }
    }
    
    /**
     * Returns a string defining a {@link #CMD_MOVE} command.
     * @param actorId the id of the actor to move
     * @param x the x-coordinate of the actor's new location
     * @param y the y-coordinate of the actor's new location
     * @return a string defining a {@link #CMD_MOVE} command
     */
    public static String getMoveCmd(String actorId, int x, int y) {
    	return Client.getCmdStr(CMD_MOVE, actorId, x, y);
    }
    
    /**
     * Sets the rotation of the actor with the given actorId to the given angle
     * @param actorId the id of the actor
     * @param angle the angle to set the rotation to
     */
    private void handleRotateCmd(String actorId, double angle) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.setRotate(angle);
    }
    
    /**
     * Returns a string defining a {@link #CMD_ROTATE} command. 
     * @param actorId the id of the actor to rotate
     * @param angle the angle to set the rotation of the actor to
     * @return a string defining a {@link #CMD_ROTATE} command
     */
    public static String getRotateCmd(String actorId, int angle) {
    	return Client.getCmdStr(CMD_ROTATE, actorId, angle);
    }
    
    /**
     * Sets the image of the actor with the given actorId to the image at the given url.
     * See {@link Actor#setImage(String)}
     * 
     * @param actorId the id of the actor
     * @param url the path to the image resource.
     */
    private void handleSetImageCmd(String actorId, String url) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.setImage(url);
    }
    
    /**
     * Returns a string defining a {@link #CMD_IMAGE} command.
     * If the image is in the images package and is named "pic.png", the url would be "images/pic.png".
     * For more information, see {@link Actor#setImage(String)}.
     * @param actorId the id of the actor to set the image of
     * @param url the path to the image resource.
     * @return a string defining a {@link #CMD_IMAGE} command
     * @throws IllegalArgumentException if the url contains any whitespace characters since that would cause other clients
     * to fail to parse the image name correctly.
     */
    public static String getImageCmd(String actorId, String url) {
    	if (Pattern.compile("\\s").matcher(url).find()) throw new IllegalArgumentException("url contains whitespace: " + url);
    	return Client.getCmdStr(CMD_IMAGE, actorId, url);
    }
    
    /**
     * Sets the opacity of the actor with the given actorId to the given opacity on a scale of 0 to 1.
     * See {@link Actor#setOpacity(double)}
     * @param actorId the id of the actor
     * @param opacity the opacity
     */
    private void handleSetOpacityCmd(String actorId, double opacity) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null)  mpa.setOpacity(opacity);
    }
    
    /**
     * Returns a string defining a {@link #CMD_OPACITY} command.
     * The opacity should be a number between 0 and 1 inclusive.
     * Any number less than 0 will be treated as 0 and any number
     * greater than 1 will be treated as 1.
     * @param actorId the id of the actor to set the opacity of
     * @param opacity the opacity to set the image to
     * @return a string defining a {@link #CMD_OPACITY} command
     */
    public static String getOpacityCmd(String actorId, int opacity) {
    	return Client.getCmdStr(CMD_OPACITY, actorId, opacity);
    }
    
    /**
     * Sets the scaleX of the actor with the given actorId
     * See {@link Actor#setScaleX(double)}
     * @param actorId the id of the actor
     * @param scaleX the horizontal scale factor 
     */
    private void handleScaleXCmd(String actorId, double scaleX) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) {
        	mpa.setScaleX(scaleX);
        }
    }
    
    /**
     * Returns a string defining a {@link #CMD_SCALE_X} command.
     * @param actorId the id of the actor to scale
     * @param scaleX the horizontal scale factor
     * @return a string defining a {@link #CMD_SCALE_X} command
     */
    public static String getScaleXCmd(String actorId, double scaleX) {
    	return Client.getCmdStr(CMD_SCALE_X, actorId, scaleX);
    }
    
    /**
     * Sets the scaleY of the actor with the given actorId
     * See {@link Actor#setScaleY(double)}
     * @param actorId the id of the actor
     * @param scaleY the vertical scale factor
     */
    private void handleScaleYCmd(String actorId, double scaleY) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) {
        	mpa.setScaleY(scaleY);
        }
    }
    
    /**
     * Returns a string defining a {@link #CMD_SCALE_Y} command.
     * @param actorId the id of the actor to scale
     * @param scaleX the vertical scale factor
     * @return a string defining a {@link #CMD_SCALE_Y} command
     */
    public static String getScaleYCmd(String actorId, double scaleY) {
    	return Client.getCmdStr(CMD_SCALE_Y, actorId, scaleY);
    }
    
    /**
     * Call the method with the given method name and parameters on the actor with the given actorId.
     * The parameters must be all strings, so any parsing of parameters into other data types must be
     * done in the method being called.
     * @param actorId the id of the actor
     * @param methodName the name of the method to be called
     * @param parameters the parameters to pass to the method
     */
    private void handleMethodCmd(String actorId, String methodName, List<String> parameters) {
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
     * Returns a string defining a {@link #CMD_METHOD} command.
     * @param actorId the id of the actor to call the method on
     * @param methodName the name of the method to call
     * @param parameters the parameters to pass to the method
     * @return a string defining a {@link #CMD_METHOD} command
     */
    public static String getMethodCmd(String actorId, String methodName, Object... parameters) {
    	String cmd = Client.getCmdStr(CMD_METHOD, methodName);
    	if (parameters.length > 0) cmd += " " + Client.getCmdStr(parameters);
    	return cmd;
    }
    
    /**
     * Handle the {@link #CMD_REMOVE} command which removes the actor with
	 * the given actorId from the world.
     * @param actorId the id of the actor to remove from the world
     */
    private void handleRemoveCmd(String actorId) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) getWorld().remove(mpa);
    }
    
    /**
     * Returns a string defining a {@link #CMD_REMOVE} command.
     * @param actorId the id of the actor to remove from the world
     * @return a string defining a {@link #CMD_REMOVE} command
     */
    public static String getRemoveCmd(String actorId) {
    	return Client.getCmdStr(CMD_REMOVE, actorId);
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
