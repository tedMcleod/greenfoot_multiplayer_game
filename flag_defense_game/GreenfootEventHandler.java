import java.util.Scanner;
import greenfoot.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * The GreenfootClient is an implementation of the GameClient for Greenfoot games.
 * Every Actor that needs to be shown on all clients is a GameActor.
 * GameActors that are controlled by this client are LocalActors.
 * Each client is responsible for controlling LocalActors and reporting
 * any visible actions to the other clients so they can update their corresponding
 * GameActor to reflect the changes.
 * 
 * Only the basic changes add, move, set rotation and destroy are handled currently.
 * Subclasses can add more commands as needed such as manipulating the image.
 * 
 * 
 * @author Ted McLeod 
 * @version 5/7/2023
 */
public abstract class GreenfootEventHandler implements ClientEventHandler {
    // Commands to change actor properties
    public static final String CMD_ADD = "ADD";
    public static final String CMD_MOVE = "MOVE";
    public static final String CMD_ROTATE = "ROT";
    
    // Commands to manipulate the image of an actor
    public static final String CMD_IMAGE = "IMG";
    public static final String CMD_TRANSPARENCY = "ALPHA";
    public static final String CMD_MIRROR_H = "MIRH";
    public static final String CMD_MIRROR_V = "MIRV";
    public static final String CMD_SCALE = "SCALE";
    
    // call a custom method on an actor
    public static final String CMD_METHOD = "METHOD";
    
    public static final String CMD_DESTROY = "DESTROY";
    
    private GameWorld world;
    
    /**
     * Create a GreenfootClient that will connect to the given hostName and portNumber.
     */
    public GreenfootEventHandler(GameWorld world) {
        this.world = world;
    }
    
    /**
     * If a client disconnects, remove all the game actors controlled by that client.
     */
    @Override
    public void handleOtherClientDisconnected(String clientId, GameClient client) {
        for (GameActor ga : world.getClientActors(clientId)) {
            getWorld().removeObject(ga);
        }
    }
    
    @Override
    public void handleOtherClientJoined(String clientId, GameClient client) {
        // This will respond to the sender client with messages saying to add the
        // corresponding classes that represent each LocalActor this client controls
        for (LocalActor la : getWorld().getObjects(LocalActor.class)) {
            String msg = GreenfootEventHandler.CMD_ADD + " " + la.getOtherClass().getName() + " " + la.getActorId() + " " + la.getX() + " " + la.getY();
            client.sendMessage(msg, clientId);
        }
    }

    /**
     * handle messages from other clients.
     */
    @Override
    public void handleCommand(String command, GameClient client) {
        System.out.println("Attempting to handle command in GreenfootEventHandler " + command);
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        String senderId = scan.next();
        
        // The second token is the command (ADD, MOVE, ROT, DESTROY...etc)
        String cmd = scan.next();
        
        if (cmd.equals(CMD_ADD)) {
            // The command will be in the form: senderId ADD className actorId x y
            String className = scan.next();
            String actorId = scan.next();
            int x = scan.nextInt();
            int y = scan.nextInt();
            handleAddCmd(className, actorId, senderId, x, y);
        } else if (cmd.equals(CMD_MOVE)) {
            // The command will be in the form: senderId MOVE actorId x y
            String actorId = scan.next();
            int x = scan.nextInt();
            int y = scan.nextInt();
            handleSetLocationCmd(actorId, x, y);
        } else if (cmd.equals(CMD_ROTATE)) {
            // The command will be in the form: senderId ROT actorId angle
            String actorId = scan.next();
            int angle = scan.nextInt();
            handleSetRotationCmd(actorId, angle);
        } else if (cmd.equals(CMD_IMAGE)) {
            // The command will be in the form: senderId IMG actorId fileName
            String actorId = scan.next();
            String fileName = scan.next();
            handleSetImageCmd(actorId, fileName);
        } else if (cmd.equals(CMD_TRANSPARENCY)) {
            // The command will be in the form: senderId ALPHA actorId value
            String actorId = scan.next();
            int value = scan.nextInt();
            handleSetTransparencyCmd(actorId, value);
        } else if (cmd.equals(CMD_MIRROR_H)) {
            // The command will be in the form: senderId MIRH actorId
            String actorId = scan.next();
            handleMirrorHorizontallyCmd(actorId);
        } else if (cmd.equals(CMD_MIRROR_V)) {
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
        } else if (cmd.equals(CMD_DESTROY)) {
            // The command will be in the form: senderId DESTROY actorId
            String actorId = scan.next();
            handleDestroyCmd(actorId);
        } else {
            System.out.println("Command not handled by GreenfootEventHandler " + command);
        }
    }
    
    protected void handleAddCmd(String className, String actorId, String clientId, int x, int y) {
        // Create an instance of the class given by the className and add it to the world at the given x, y
            try {
                Class<?> cls = Class.forName(className);
                Constructor<?> constr = cls.getDeclaredConstructor(String.class, String.class);
                Actor actor = (Actor)constr.newInstance(actorId, clientId);
                getWorld().addObject(actor, x, y);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException err) {
                err.printStackTrace();
            }
    }
    
    protected void handleSetLocationCmd(String actorId, int x, int y) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null) ga.setLocation(x, y);
    }
    
    protected void handleSetRotationCmd(String actorId, int angle) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null)  ga.setRotation(angle);
    }
    
    protected void handleSetImageCmd(String actorId, String fileName) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null) ga.setImage(fileName);
    }
    
    protected void handleSetTransparencyCmd(String actorId, int transparency) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null)  ga.getImage().setTransparency(transparency);
    }
    
    protected void handleMirrorHorizontallyCmd(String actorId) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null) ga.getImage().mirrorHorizontally();
    }
    
    protected void handleMirrorVerticallyCmd(String actorId) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null) ga.getImage().mirrorVertically();
    }
    
    protected void handleScaleCmd(String actorId, int w, int h) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null)  ga.getImage().scale(w, h);
    }
    
    protected void handleMethodCmd(String actorId, String methodName, List<String> parameters) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null) {
            Class<?>[] paramTypes = new Class<?>[parameters.size()];
            for (int i = 0; i < paramTypes.length; i++) paramTypes[i] = String.class;
            try {
                Method method = ga.getClass().getMethod(methodName, paramTypes);
                method.invoke(ga, parameters.toArray());
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException err) {
                err.printStackTrace();
            }
        }
    }
    
    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     */
    @Override
    public void onDisconnected(GameClient client) {
        Greenfoot.stop();
    }
    
    protected void handleDestroyCmd(String actorId) {
        GameActor ga = getWorld().getGameActor(actorId);
        if (ga != null) ga.destroy();
    }

    /**
     * return the GameWorld for this client.
     * @return the GameWorld for this client.
     */
    public GameWorld getWorld() {
        return world;
    }

    /**
     * Set the GameWorld for this client.
     * @param world the game world
     */
    public void setWorld(GameWorld world) {
        this.world = world;
    }
}
