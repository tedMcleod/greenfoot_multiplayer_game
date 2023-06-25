import com.tinocs.mp.client.*;
import com.tinocs.mp.greenfoot.*;
import java.util.Set;
import greenfoot.Greenfoot;

/**
 * Write a description of class TankGameClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LobbyEventHandler extends FlagDefenseEventHandler {
    
    public LobbyEventHandler(MPWorld world){
        super(world);
    }
    
    @Override
    public void handleClientJoinedRoom(String clientId, String roomId, Client client) {
        if (clientId.equals(client.getId())) {
            LobbyWorld lw = (LobbyWorld)getWorld();
            lw.setIdOfRoomToJoin(roomId);
        }
    }
}

