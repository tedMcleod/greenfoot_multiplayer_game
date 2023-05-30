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
    
    public void handleClientLeftRoom(String clientId, String roomId, GameClient client) {
        if (roomId.equals(client.getId())) {
            GameWorld gw = getWorld();
            gw.removeObjects(gw.getClientActors(clientId));
        }
    }
    
}
