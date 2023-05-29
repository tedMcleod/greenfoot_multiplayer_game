import java.util.Set;

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
    
    public void onIdAssigned(String clientId, GameClient client) {
        client.getRooms();
    }
    
    @Override
    public void handleCommand(String command, GameClient client) {
        super.handleCommand(command, client);
    }
    
    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     */
    public void onDisconnected(GameClient client) {
        System.out.println("Disconnected in Lobby Event Handler");
    }

    public void handleRoomsInfo(Set<RoomInfo> rooms, GameClient client) {
        for (RoomInfo room : rooms) {
            LobbyWorld lw = (LobbyWorld)getWorld();
            lw.addRoom(room);
        }
    }

    public void handleRoomAdded(RoomInfo room, GameClient client) {
        LobbyWorld lw = (LobbyWorld)getWorld();
            lw.addRoom(room);
    }

    public void handleRoomRemoved(String roomId, GameClient client) {
        
    }
}

