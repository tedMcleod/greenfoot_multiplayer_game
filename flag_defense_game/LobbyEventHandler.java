import java.util.Set;
import greenfoot.Greenfoot;

/**
 * Write a description of class TankGameClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LobbyEventHandler extends GreenfootEventHandler {
    
    public LobbyEventHandler(GameWorld world){
        super(world);
    }
    
    @Override
    public void handleClientJoinedRoom(String clientId, String roomId, GameClient client) {
        if (clientId.equals(client.getId())) {
            LobbyWorld lw = (LobbyWorld)getWorld();
            lw.setIdOfRoomToJoin(roomId);
        }
    }
}

