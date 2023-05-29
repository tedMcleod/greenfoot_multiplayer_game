import java.util.Scanner;
import greenfoot.*;
import javafx.application.Platform;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
public abstract class GreenfootClient extends GameClient  {
    
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
    public GreenfootClient(String hostName, int portNumber) {
        super(hostName, portNumber);
        world = null;
    }

    /**
     * handle messages from other clients.
     */
    @Override
    public void doCommand(String command) {
        GameWorld gw = getWorld();
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        String senderId = scan.next();
        
        // The second token is the command (ADD, MOVE, ROT, DESTROY...etc)
        String cmd = scan.next();
        
        // if the command is ADD a GameActor representing a LocalActor controlled by the sender
        if (cmd.equals(CMD_ADD)) {
            // The command will be in the form: senderId ADD className actorId x y
            String className = scan.next();
            String actorId = scan.next();
            
            // Create an instance of the class given by the className and add it to the world at the given x, y
            try {
                Class<?> cls = Class.forName(className);
                Constructor<?> constr = cls.getDeclaredConstructor(String.class, String.class);
                Actor actor = (Actor)constr.newInstance(actorId, senderId);
                int x = scan.nextInt();
                int y = scan.nextInt();
                gw.addObject(actor, x, y);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException err) {
                err.printStackTrace();
            }
            
        // if the command is reporting that the sender disconnected
        } else if (cmd.equals(GameClient.CMD_DISCONNECT)) {
            // The command will be in the form: senderId DC
            // remove all the GameActors representing LocalActors controlled by the sender
            for (GameActor ga : gw.getClientActors(senderId)) {
                gw.removeObject(ga);
            }
            
        // if the command is to move a GameActor
        } else if (cmd.equals(CMD_MOVE)) {
            // The command will be in the form: senderId MOVE actorId x y
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                int x = scan.nextInt();
                int y = scan.nextInt();
                ga.setLocation(x, y);
            }
            
        // if the command is to set the rotation of a GameActor
        } else if (cmd.equals(CMD_ROTATE)) {
            // The command will be in the form: senderId ROT actorId angle
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                int angle = scan.nextInt();
                ga.setRotation(angle);
            }
            
        // if the command is the set the image of the actor to an image in a file
        } else if (cmd.equals(CMD_IMAGE)) {
            // The command will be in the form: senderId IMG actorId fileName
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                String fileName = scan.next();
                ga.setImage(fileName);
            }
        // if the command is to set the transparency of the image
        } else if (cmd.equals(CMD_TRANSPARENCY)) {
            // The command will be in the form: senderId ALPHA actorId value
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                int value = scan.nextInt();
                ga.getImage().setTransparency(value);
            }
        
        // if the command is to mirror the image horizontally
        } else if (cmd.equals(CMD_MIRROR_H)) {
            // The command will be in the form: senderId MIRH actorId
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                ga.getImage().mirrorHorizontally();
            }
            
        // if the command is to mirror the image vertically
        } else if (cmd.equals(CMD_MIRROR_V)) {
            // The command will be in the form: senderId MIRV actorId
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                ga.getImage().mirrorVertically();
            }
        
        // if the command is to scale the image
        } else if (cmd.equals(CMD_SCALE)) {
            // The command will be in the form: senderId SCALE actorId width height
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                int w = scan.nextInt();
                int h = scan.nextInt();
                ga.getImage().scale(w, h);
            }
        
        // if the command is to call a method on the actor
        } else if (cmd.equals(CMD_METHOD)) {
            // The command will be in the form: senderId METHOD actorId methodName param1 param2 param3...
            // custom methods should take String parameters and the method should parse them as needed 
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                String methodName = scan.next();
                ArrayList<String> params = new ArrayList<>();
                while (scan.hasNext()) params.add(scan.next());
                Class<?>[] paramTypes = new Class<?>[params.size()];
                for (int i = 0; i < paramTypes.length; i++) paramTypes[i] = String.class;
                try {
                    Method method = ga.getClass().getMethod(methodName, paramTypes);
                    method.invoke(ga, params.toArray());
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException err) {
                    err.printStackTrace();
                }
            }
        
        // if the command is to destroy the actor
        } else if (cmd.equals(CMD_DESTROY)) {
            // The command will be in the form: senderId DESTROY actorId
            String actorId = scan.next();
            GameActor ga = gw.getGameActor(actorId);
            if (ga != null) {
                ga.destroy();
            }
            
        // if the command is to respond to the sender just joining the server
        } else if (cmd.equals(GameClient.CMD_JOINED)) {
            // The command will be in the form: senderId JOINED
            // This will respond to the sender client with messages saying to add the
            // corresponding classes that represent each LocalActor this client controls
            for (LocalActor la : gw.getObjects(LocalActor.class)) {
                String msg = GreenfootClient.CMD_ADD + " " + la.getOtherClass().getName() + " " + la.getActorId() + " " + la.getX() + " " + la.getY();
                sendMessage(msg, senderId);
            }
        }
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
