import com.tinocs.mp.client.*;
import com.tinocs.mp.greenfoot.*;
import java.util.Set;
import java.util.Scanner;
import greenfoot.Greenfoot;

/**
 * Write a description of class RoomEventHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoomEventHandler extends FlagDefenseEventHandler {

    private static final String CMD_START_GAME = "START_GAME";
    private String roomId;

    public RoomEventHandler(MPWorld world, String roomId){
        super(world);
        this.roomId = roomId;
    }

    @Override
    public void handleCommand(String command, Client client) {
        super.handleCommand(command, client);
        if (client.debug()) System.out.println("Attempting to handle command in RoomEventHandler " + command);
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        String senderId = scan.next();
        if (client.debug()) System.out.println("senderId = " + senderId);
        // The second token is the command (ADD, MOVE, ROT, DESTROY...etc)
        String cmd = scan.next();
        if (client.debug()) System.out.println("cmd = " + cmd);

        if (cmd.equals(CMD_START_GAME)) {
            handleStartGameCmd(senderId);
        } else {
            if (client.debug()) System.out.println("Command not handled by RoomEventHandler " + command);
        }
    }
    
    protected void handleStartGameCmd(String clientId) {
        RoomWorld rw = (RoomWorld)getWorld();
        rw.shouldStartGame();
    }

    @Override
    public void handleClientLeftRoom(String clientId, String roomId, Client client) {
        if (clientId.equals(client.getId()) && roomId.equals(client.getIdOfRoomContainingClient(client.getId()))) {
            RoomWorld rw = (RoomWorld) getWorld();
            rw.shouldLeave();
        }
    }
}
