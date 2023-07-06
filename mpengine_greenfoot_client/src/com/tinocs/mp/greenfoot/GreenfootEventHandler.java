package com.tinocs.mp.greenfoot;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.tinocs.mp.client.Client;
import com.tinocs.mp.client.ClientEventHandler;

import greenfoot.Actor;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

/**
 * <p>This is an implementation of the {@link ClientEventHandler} for Greenfoot games.</p>
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
 *                         
 *  </li>
 * </ol>
 * 
 * <p>In general whenever a LocalActor changes state (added to the world, image, position or rotation changes),
 * the change is automatically broadcast to the other clients and the corresponding MPActor is updated.</p>
 * 
 * <p>While calling {@link Actor#setImage(String)} on a LocalActor will result in a broadcast that changes
 * the image of the corresponding MPActors on other clients, {@link Actor#setImage(GreenfootImage)} will not
 * broadcast anything because there is no filename to pass. If you want to change the image to a generated image
 * not simply read from a file, then you should create a custom method in the LocalActor that changes the
 * image and also broadcasts to all the clients to call that same method on the corresponding MPActor. Then copy
 * that method to the corresponding MPActor class.
 * 
 * @author Ted McLeod 
 * @version 5/7/2023
 */
public abstract class GreenfootEventHandler implements ClientEventHandler {
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
    
    /**<p>Command to set the transparency of an actor to the given value.</p>
	 * The command will be in the form: senderId ALPHA actorId value
	 */
    public static final String CMD_TRANSPARENCY = "ALPHA";
    
    /**<p>Command to mirror the image of an actor horizontally.</p>
	 * The command will be in the form: senderId MIRH actorId
	 */
    public static final String CMD_MIRROR_HORIZONTALLY = "MIRH";
    
    /**<p>Command to mirror the image of an actor vertically.</p>
	 * The command will be in the form: senderId MIRV actorId
	 */
    public static final String CMD_MIRROR_VERTICALLY = "MIRV";
    
    /**<p>Command to scale the image of an actor to the given width and height.</p>
	 * The command will be in the form: senderId SCALE actorId width height
	 */
    public static final String CMD_SCALE = "SCALE";
    
    // call a custom method on an actor
    /**<p>use this command to tell the other clients to call a custom method on an actor with a given id</p>
     * <p>The command will be in the form: senderId METHOD actorId methodName param1 param2 param3...</p>
     * <p>Custom methods should take String parameters and the method should parse them as needed</p>
     */
    public static final String CMD_METHOD = "METHOD";
    
    /**<p>Command to remove an actor.</p>
	 * The command will be in the form: senderId REMOVE actorId
	 */
    public static final String CMD_REMOVE = "REMOVE";
    
    private MPWorld world;
    
    /**
     * Create a GreenfootEventHandler with a reference to the given MPWorld.
     * @param world the world
     */
    public GreenfootEventHandler(MPWorld world) {
        this.world = world;
    }
    
    /**
     * remove all the MPActors controlled by the client that disconnected.
     * @param clientId the id of the other client that disconnected
     * @param client a reference to this client
     */
    @Override
    public void handleOtherClientDisconnected(String clientId, Client client) {
        for (MPActor mpa : world.getClientActors(clientId)) {
            world.removeObject(mpa);
        }
    }
    
    /**
     * When another client joins, if they are either in the same room as this client or
     * this client and the other client are both not in a room, then for each LocalActor
     * in this world, send a message to the other client to tell it to add an MPActor of the
     * type returned by calling {@link LocalActor#getOtherClass()} on that LocalActor.
     * @param clientId the id of the other client that joined
     * @param client a reference to this client
     */
    @Override
    public void handleOtherClientJoined(String clientId, Client client) {
        // This will respond to the sender client with messages saying to add the
        // corresponding classes that represent each LocalActor this client controls
        String myRoomId = client.getCurrentRoomId();
        String otherRoomId = client.getIdOfRoomContainingClient(clientId);
        if (otherRoomId == null && myRoomId == null || (myRoomId != null && myRoomId.equals(otherRoomId))) {
            for (LocalActor la : getWorld().getObjects(LocalActor.class)) {
            	String msg = getAddCmd(la.getOtherClass(), la.getX(), la.getY(), la.getConstructorParameters());
                client.sendMessage(msg, clientId);
            }
        }
    }

    /**
     * handle messages from other clients. The following commands are automatically handled:
     * <ul>
     * 	<li>{@link #CMD_ADD}: add an actor to the world. See {@link #getAddCmd(Class, int, int, Object...)}</li>
     * 	<li>{@link #CMD_MOVE}: Set the location of an actor. See {@link #getMoveCmd(String, int, int)}</li>
     * 	<li>{@link #CMD_ROTATE}: set the rotation of an actor. See {@link #getRotateCmd(String, int)}</li>
     * 	<li>{@link #CMD_TRANSPARENCY}: set the transparency of the image of an actor. See {@link #getTransparencyCmd(String, int)}</li>
     * 	<li>{@link #CMD_MIRROR_HORIZONTALLY}: mirror the image of an actor horizontally. See {@link #getMirrorHorizontallyCmd(String)}</li>
     * 	<li>{@link #CMD_MIRROR_VERTICALLY}: mirror the image of an actor vertically. See {@link #getMirrorVerticallyCmd(String)}</li>
     * 	<li>{@link #CMD_SCALE}: scale the image of an actor. See {@link #getScaleCmd(String, int, int)}</li>
     * 	<li>{@link #CMD_METHOD}: call a method on an actor. See {@link #getMethodCmd(String, String, Object...)}</li>
     * 	<li>{@link #CMD_REMOVE}: destroy an actor. See {@link #getRemoveCmd(String)}</li>
     * 	</ul>
     * @param command the command
     * @param client the client
     */
    @Override
    public void handleCommand(String command, Client client) {
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        scan.next(); // don't need senderId for any of these commands
        
        // The second token is the command (ADD, MOVE, ROT, REMOVE...etc)
        String cmd = scan.next();
        
        if (cmd.equals(CMD_ADD)) {
            // The command will be in the form: senderId ADD className x y param1 param2 param3...
            String className = scan.next();
            int x = scan.nextInt();
            int y = scan.nextInt();
            ArrayList<String> params = new ArrayList<>();
            while (scan.hasNext()) params.add(scan.next());
            handleAddCmd(className, x, y, params);
        } else if (cmd.equals(CMD_MOVE)) {
            // The command will be in the form: senderId MOVE actorId x y
            String actorId = scan.next();
            int x = scan.nextInt();
            int y = scan.nextInt();
            handleMoveCmd(actorId, x, y);
        } else if (cmd.equals(CMD_ROTATE)) {
            // The command will be in the form: senderId ROT actorId angle
            String actorId = scan.next();
            int angle = scan.nextInt();
            handleRotateCmd(actorId, angle);
        } else if (cmd.equals(CMD_IMAGE)) {
            // The command will be in the form: senderId IMG actorId fileName
            String actorId = scan.next();
            String fileName = scan.next();
            handleSetImageCmd(actorId, fileName);
        } else if (cmd.equals(CMD_TRANSPARENCY)) {
            // The command will be in the form: senderId ALPHA actorId value
            String actorId = scan.next();
            int value = scan.nextInt();
            handleTransparencyCmd(actorId, value);
        } else if (cmd.equals(CMD_MIRROR_HORIZONTALLY)) {
            // The command will be in the form: senderId MIRH actorId
            String actorId = scan.next();
            handleMirrorHorizontallyCmd(actorId);
        } else if (cmd.equals(CMD_MIRROR_VERTICALLY)) {
            // The command will be in the form: senderId MIRV actorId
            String actorId = scan.next();
            handleMirrorVerticallyCmd(actorId);
        } else if (cmd.equals(CMD_SCALE)) {
            // The command will be in the form: senderId SCALE actorId width height
            String actorId = scan.next();
            int w = scan.nextInt();
            int h = scan.nextInt();
            handleScaleCmd(actorId, w, h);
        } else if (cmd.equals(CMD_METHOD)) {
            // The command will be in the form: senderId METHOD actorId methodName param1 param2 param3...
            // custom methods should take String parameters and the method should parse them as needed 
            String actorId = scan.next();
            String methodName = scan.next();
            ArrayList<String> params = new ArrayList<>();
            while (scan.hasNext()) params.add(scan.next());
            handleMethodCmd(actorId, methodName, params);
        } else if (cmd.equals(CMD_REMOVE)) {
            // The command will be in the form: senderId REMOVE actorId
            String actorId = scan.next();
            handleRemoveCmd(actorId);
        }
        scan.close();
    }
    
    /**
     * Handle the {@link #CMD_ADD} command which creates and instance of the given class and adds it to the world.
     * The class must be a subclass of Actor or this method will throw a {@link ClassCastException}.
     * @param className The name of the class to make an instance of
     * @param x the x-coordinate of the position to add the actor
     * @param y the y-coordinate of the position to add the actor
     * @param parameters parameters to pass to the constructor when creating the actor
     */
    private void handleAddCmd(String className, int x, int y, List<String> parameters) {
        // Create an instance of the class given by the className and add it to the world at the given x, y
            try {
            	// executing Class.forName(className) without specifying the loader doesn't find the class
            	// because it tries to find it in a different class loader.
            	Class<?> cls = Class.forName(className, true, getClass().getClassLoader());
                Class<?>[] paramTypes = new Class<?>[parameters.size()];
                for (int i = 0; i < paramTypes.length; i++) paramTypes[i] = String.class;
                Constructor<?> constr = cls.getDeclaredConstructor(paramTypes);
                Actor actor = (Actor)constr.newInstance(parameters.toArray());
            	getWorld().addObject(actor, x, y);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException err) {
            	// For some reason, System.err messages do not get printed in the greenfoot console, so
            	// this prints the error to System.out to make sure it is visible.
            	// Also, this makes it easier to see exactly when the error occurred.
            	PrintStream errStrm = System.err;
            	System.setErr(System.out);
                err.printStackTrace();
                System.setErr(errStrm);
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
    public static String getAddCmd(Class<?> cls, int x, int y, Object... parameters) {
    	String cmd = Client.getCmdStr(CMD_ADD, cls.getName(), x, y);
    	if (parameters.length > 0) cmd += " " + Client.getCmdStr(parameters);
    	return cmd;
    }
    
    /**
     * Handle the {@link #CMD_MOVE} command which moves the actor with the given actorId to (x, y)
     * @param actorId the id of the actor to move
     * @param x the x-coordinate of the actor's new location
     * @param y the y-coordinate of the actor's new location
     */
    private void handleMoveCmd(String actorId, int x, int y) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.setLocation(x, y);
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
     * Handle the {@link #CMD_ROTATE} command which sets the rotation of the actor with the given actorId to the given angle.
     * @param actorId the id of the actor to rotate
     * @param angle the angle to set the rotation of the actor to.
     */
    private void handleRotateCmd(String actorId, int angle) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null)  mpa.setRotation(angle);
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
     * Handle the {@link #CMD_IMAGE} command which sets the image of the actor with the given actorId
     * @param actorId the id of the actor to set the image of
     * @param fileName the name of the file containing the image data (should be located in the images folder)
     */
    private void handleSetImageCmd(String actorId, String fileName) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.setImage(fileName);
    }
    
    /**
     * Returns a string defining a {@link #CMD_IMAGE} command.
     * The filename should be the name of an image file in the images folder of the scenario.
     * @param actorId the id of the actor to set the image of
     * @param fileName the name of the file containing the image data
     * @return a string defining a {@link #CMD_IMAGE} command
     * @throws IllegalArgumentException if fileName contains any whitespace since that would cause other clients
     * to fail to parse the image name correctly.
     */
    public static String getImageCmd(String actorId, String fileName) {
    	if (Pattern.compile("\\s").matcher(fileName).find()) throw new IllegalArgumentException("fileName contains whitespace: " + fileName);
    	return Client.getCmdStr(CMD_IMAGE, actorId, fileName);
    }
    
    /**
     * Handle the {@link #CMD_TRANSPARENCY} command which sets the transparency of the
     * image of the actor with the given actorId. The transparency must be 0-255 or
     * an exception will be thrown.
     * @param actorId the id of the actor whose image will have the its transparency set
     * @param transparency the transparency to set the image to
     */
    private void handleTransparencyCmd(String actorId, int transparency) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null)  mpa.getImage().setTransparency(transparency);
    }
    
    /**
     * Returns a string defining a {@link #CMD_TRANSPARENCY} command.
     * The transparency must be 0-255 or an exception will be thrown on the other clients.
     * @param actorId the id of the actor to set the transparency of
     * @param transparency the transparency to set the image to
     * @return a string defining a {@link #CMD_TRANSPARENCY} command
     */
    public static String getTransparencyCmd(String actorId, int transparency) {
    	return Client.getCmdStr(CMD_TRANSPARENCY, actorId, transparency);
    }
    
    /**
     * Handle the {@link #CMD_MIRROR_HORIZONTALLY} command which mirrors the image
	 * of the actor with the given actorId horizontally.
     * @param actorId the id of the actor whose image will be mirrored
     */
    private void handleMirrorHorizontallyCmd(String actorId) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.getImage().mirrorHorizontally();
    }
    
    /**
     * Returns a string defining a {@link #CMD_MIRROR_HORIZONTALLY} command.
     * @param actorId the id of the actor whose image will be mirrored
     * @return a string defining a {@link #CMD_MIRROR_HORIZONTALLY} command
     */
    public static String getMirrorHorizontallyCmd(String actorId) {
    	return Client.getCmdStr(CMD_MIRROR_HORIZONTALLY, actorId);
    }
    
    /**
     * Handle the {@link #CMD_MIRROR_VERTICALLY} command which mirrors the image
	 * of the actor with the given actorId vertically.
     * @param actorId the id of the actor whose image will be mirrored
     */
    private void handleMirrorVerticallyCmd(String actorId) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null) mpa.getImage().mirrorVertically();
    }
    
    /**
     * Returns a string defining a {@link #CMD_MIRROR_VERTICALLY} command.
     * @param actorId the id of the actor whose image will be mirrored
     * @return a string defining a {@link #CMD_MIRROR_VERTICALLY} command
     */
    public static String getMirrorVerticallyCmd(String actorId) {
    	return Client.getCmdStr(CMD_MIRROR_VERTICALLY, actorId);
    }
    
    /**
     * Handle the {@link #CMD_SCALE} command which scales the image
	 * of the actor with the given actorId to the given width and height.
     * @param actorId the id of the actor whose image will be scaled
     * @param width the width after scaling
     * @param height the height after scaling
     */
    private void handleScaleCmd(String actorId, int width, int height) {
        MPActor mpa = getWorld().getMPActor(actorId);
        if (mpa != null)  mpa.getImage().scale(width, height);
    }
    
    /**
     * Returns a string defining a {@link #CMD_SCALE} command.
     * @param actorId the id of the actor whose image will be scaled
     * @param width the width after scaling
     * @param height the height after scaling
     * @return a string defining a {@link #CMD_SCALE} command
     */
    public static String getScaleCmd(String actorId, int width, int height) {
    	return Client.getCmdStr(CMD_SCALE, actorId, width, height);
    }
    
    /**
     * Handle the {@link #CMD_METHOD} command which calls the given method on the actor
     * with the given actorId.
     * @param actorId the id of the actor to call the method on
     * @param methodName the name of the method to call
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
            	// For some reason, System.err messages do not get printed in the greenfoot console, so
            	// this prints the error to System.out to make sure it is visible.
            	// Also, this makes it easier to see exactly when the error occurred.
            	PrintStream errStrm = System.err;
            	System.setErr(System.out);
                err.printStackTrace();
                System.setErr(errStrm);
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
        if (mpa != null) getWorld().removeObject(mpa);
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
     * By default this method calls {@link Greenfoot#stop()}.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     * @param client this client
     */
    @Override
    public void onDisconnected(Client client) {
        Greenfoot.stop();
    }

    /**
     * Returns the MPWorld for this client.
     * @return the MPWorld for this client.
     */
    public MPWorld getWorld() {
        return world;
    }

    /**
     * Set the MPWorld for this client.
     * @param world the MPWorld
     */
    public void setWorld(MPWorld world) {
        this.world = world;
    }
}
