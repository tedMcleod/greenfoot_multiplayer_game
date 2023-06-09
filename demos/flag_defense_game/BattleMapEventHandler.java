import com.tinocs.mp.client.*;
import com.tinocs.mp.greenfoot.*;
import java.util.Scanner;

/**
 * Write a description of class BattleMapEventHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BattleMapEventHandler extends FlagDefenseEventHandler {
    private static final String CMD_READY = "READY";
    private static final String CMD_WIN = "WIN";
    private static final String CMD_HIT = "HIT";

    public BattleMapEventHandler(MPWorld world){
        super(world);
    }

    @Override
    public void handleCommand(String command, Client client) {
        super.handleCommand(command, client);
        Scanner scan = new Scanner(command);
        // The first token is always the id of the client who sent the message
        String senderId = scan.next();
        if (client.debug()) System.out.println("senderId = " + senderId);
        // The second token is the command (ADD, MOVE, ROT, DESTROY...etc)
        String cmd = scan.next();
        if (client.debug()) System.out.println("cmd = " + cmd);

        if (cmd.equals(CMD_READY)) {
            handleReadyCmd(senderId);
        } else if (cmd.equals(CMD_WIN)) {
            handleWinCmd(senderId);
        } else if (cmd.equals(CMD_HIT)) {
            String actorId = scan.next();
            handleHitCmd(senderId, actorId);
        } else {
            if (client.debug()) System.out.println("Command not handled by BattleMapEventHandler " + command);
        }
    }
    
    protected void handleHitCmd(String clientId, String actorId) {
        if (getWorld().getMPActor(actorId) instanceof Player) {
            Player player = (Player)getWorld().getMPActor(actorId);
            if (player != null) player.onHit();
        }
    }

    protected void handleReadyCmd(String clientId) {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        bw.setPlayerReady(clientId);
    }
    
    protected void handleWinCmd(String clientId) {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        bw.setPlayerWon(clientId);
    }
    
    public void handleClientLeftRoom(String clientId, String roomId, Client client) {
        if (roomId.equals(client.getId())) {
            MPWorld gw = getWorld();
            gw.removeObjects(gw.getClientActors(clientId));
        }
    }
    
}
