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
    public void onIdAssigned(String clientId, GameClient client) {
        LobbyWorld lw = (LobbyWorld)getWorld();
        lw.setNeedsUpdate();
    }

    @Override
    public void handleRoomsInfo(Set<RoomInfo> rooms, GameClient client) {
        LobbyWorld lw = (LobbyWorld)getWorld();
        lw.updateRooms(rooms);
    }

    @Override
    public void handleRoomAdded(RoomInfo room, GameClient client) {
        client.getRooms();
    }

    @Override
    public void handleRoomRemoved(String roomId, GameClient client) {
        client.getRooms();
    }
    
    @Override
    public void handleClientJoinedRoom(String clientId, String roomId, GameClient client) {
        client.getRooms();
    }
    
    @Override
    public void handleJoinRoomFailed(String reason, String roomId, GameClient client) {
        System.out.println("Failed to join room " + roomId + " because " + reason);
    }
    
    @Override
    public void handleClientLeftRoom(String clientId, String roomId, GameClient client) {
        client.getRooms();
    }
    
}

