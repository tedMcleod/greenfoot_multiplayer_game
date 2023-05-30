import java.util.Scanner;

/**
 * Write a description of class BattleMapEventHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BattleMapEventHandler extends GreenfootEventHandler {
    private static final String CMD_READY = "READY";

    private RoomInfo room;

    public BattleMapEventHandler(GameWorld world, RoomInfo room){
        super(world);
        this.room = room;
    }

    @Override
    public void handleCommand(String command, GameClient client) {
        super.handleCommand(command, client);
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        String senderId = scan.next();
        System.out.println("senderId = " + senderId);
        // The second token is the command (ADD, MOVE, ROT, DESTROY...etc)
        String cmd = scan.next();
        System.out.println("cmd = " + cmd);

        if (cmd.equals(CMD_READY)) {
            handleReadyCmd(senderId);
        } else {
            System.out.println("Command not handled by BattleMapEventHandler " + command);
        }
    }

    protected void handleReadyCmd(String clientId) {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        bw.setPlayerReady(clientId);
    }

    // /**
     // * This method is called when this client is disconnected from the server.
     // * Subclasses should override this method to take actions after the client
     // * has been disconnected.
     // */
    // @Override
    // public void onDisconnected(GameClient client) {
        // System.out.println("Disconnected in room handler");
    // }

    // @Override
    // public void handleRoomRemoved(String roomId, GameClient client) {
        // System.out.println("Room removed being handled by Room Event Handler " + roomId);
    // }

    // @Override
    // public void handleClientJoinedRoom(String clientId, String roomId, GameClient client) {
        
    // }

    // @Override
    // public void handleClientLeftRoom(String clientId, String roomId, GameClient client) {
        
    // }
}
