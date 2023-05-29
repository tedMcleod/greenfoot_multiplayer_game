import java.util.Set;

/**
 * Write a description of class ClientEventHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface ClientEventHandler {
    
    String ROOM_FULL = "FULL";
    String NO_SUCH_ROOM = "NO_SUCH_ROOM";
    
    /**
     * Respond to the given command from another client.
     * Commands are typically in the format:
     * <pre>
     * senderId command actorId parameters
     * Example:
     * 7h91234b-7c33-4bd1-822c-a41796388099 MOVE 76j7398b-789g-23mj-992b-pkk392893847 231 337
     * </pre>
     * @param command the message to respond to
     */
    void handleCommand(String command, GameClient client);
    
    /**
     * Do what needs to be done when this client is assigned an ID by the server.
     * @param clientId the id assigned to this client by the server
     */
    default void onIdAssigned(String clientId, GameClient client) {}

    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     */
    default void onDisconnected(GameClient client) {}
    
    
    /**
     * handle the command saying
     */
    default void handleOtherClientJoined(String clientId, GameClient client) {}
    
    default void handleOtherClientDisconnected(String clientId, GameClient client) {}

    default void handleRoomsInfo(Set<RoomInfo> rooms, GameClient client) {}

    default void handleRoomAdded(RoomInfo room, GameClient client) {}

    default void handleRoomRemoved(String roomId, GameClient client) {}
    
    default void handleClientJoinedRoom(String clientId, String roomId, GameClient client) {}
    
    default void handleJoinRoomFailed(String reason, String roomId, GameClient client) {}
    
    default void handleClientLeftRoom(String clientId, String roomId, GameClient client) {}
    
    default void handleLeaveRoomFailed(String reason, String roomId, GameClient client) {}
    
}
