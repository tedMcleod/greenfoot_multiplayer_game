import java.util.Set;
import java.util.Scanner;
import greenfoot.Greenfoot;

/**
 * Write a description of class RoomEventHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoomEventHandler extends GreenfootEventHandler {

    private static final String CMD_USERNAME = "USER_NAME";
    private static final String CMD_START_GAME = "START_GAME";

    private RoomInfo room;

    public RoomEventHandler(GameWorld world, RoomInfo room){
        super(world);
        this.room = room;
    }

    @Override
    public void handleCommand(String command, GameClient client) {
        super.handleCommand(command, client);
        System.out.println("Attempting to handle command in RoomEventHandler " + command);
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        String senderId = scan.next();
        System.out.println("senderId = " + senderId);
        // The second token is the command (ADD, MOVE, ROT, DESTROY...etc)
        String cmd = scan.next();
        System.out.println("cmd = " + cmd);

        if (cmd.equals(CMD_USERNAME)) {
            String name = scan.nextLine().trim();
            handleUserNameCmd(senderId, name);
        } else if (cmd.equals(CMD_START_GAME)) {
            handleStartGameCmd(senderId);
        } else {
            System.out.println("Command not handled by RoomEventHandler " + command);
        }
    }

    protected void handleUserNameCmd(String clientId, String name) {
        System.out.println("username being added for " + name);
        RoomWorld roomWorld = (RoomWorld)getWorld();
        room.addMember(clientId);
        roomWorld.setUserName(clientId, name);
    }
    
    protected void handleStartGameCmd(String clientId) {
        RoomWorld rw = (RoomWorld)getWorld();
        rw.startGame();
    }

    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     */
    @Override
    public void onDisconnected(GameClient client) {
        System.out.println("Disconnected in room handler");
    }

    @Override
    public void handleRoomRemoved(String roomId, GameClient client) {
        System.out.println("Room removed being handled by Room Event Handler " + roomId);
    }

    @Override
    public void handleClientJoinedRoom(String clientId, String roomId, GameClient client) {
        if (roomId.equals(room.getId())) {
            room.addMember(clientId);
            RoomWorld roomWorld = (RoomWorld)getWorld();
            DemoRoomsClient drc = (DemoRoomsClient)client;
            if (clientId.equals(client.getId())) {
                roomWorld.setUserName(clientId, drc.getUserName());
                drc.broadcastMessageToRoom("USER_NAME " + drc.getUserName(), roomId);
            } else {
                drc.sendMessage("USER_NAME " + drc.getUserName(), clientId);
            }
            roomWorld.updateUserLabels();
        }
    }

    @Override
    public void handleClientLeftRoom(String clientId, String roomId, GameClient client) {
        if (roomId.equals(room.getId())) {
            room.removeMember(clientId);
            RoomWorld roomWorld = (RoomWorld)getWorld();
            roomWorld.updateUserLabels();
            if (clientId.equals(client.getId())) {
                // System.out.println("leaving room and going to lobby");
                // LobbyWorld lw = new LobbyWorld();
                // System.out.println("Created LobbyWorld");
                
                // roomWorld.getClient().setEventHandler(new LobbyEventHandler(lw));
                // System.out.println("setEventHandler done");
                // lw.setClient(roomWorld.getClient());
                // System.out.println("setClient done");
                // lw.setNeedsUpdate();
                // Greenfoot.setWorld(lw);
                // System.out.println("set world done");
            }
        }
    }
}
