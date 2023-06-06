package mp_client_base;
import java.util.Set;

/**
 * The ClientEventHandler interface provides methods for responding to messages sent to the GameClient.
 * 
 * @author Ted McLeod 
 * @version 6/6/2023
 */
public interface ClientEventHandler {
    
    String ROOM_FULL = "FULL";
    String ROOM_CLOSED = "CLOSED";
    String NO_SUCH_ROOM = "NO_SUCH_ROOM";
    
    /**
     * Respond to the given command from another client.
     * Recommendation: first make this method simply print the command so you can see how to parse it.
     * @param command the message to respond to
     * @param client the client the command was received by
     */
    void handleCommand(String command, GameClient client);
    
    /**
     * Do what needs to be done when this client is assigned an ID by the server.
     * @param clientId the id assigned to this client by the server
     * @param client the client the command was received by
     */
    default void onIdAssigned(String clientId, GameClient client) {}

    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     * @param client the client the command was received by
     */
    default void onDisconnected(GameClient client) {}
    
    
    /**
     * Called when another client joins the server.
     * @param clientId the id of the client that joined
     * @param client the GameClient that received this command
     */
    default void handleOtherClientJoined(String clientId, GameClient client) {}
    
    /**
     * Called when another client disconnects from the server.
     * @param clientId the id of the client that disconnected
     * @param client the GameClient that received this command
     */
    default void handleOtherClientDisconnected(String clientId, GameClient client) {}

    /**
     * Called when a room is added to the server
     * @param room a RoomInfo object defining the room that was added
     * @param client the GameClient that received this command
     */
    default void handleRoomAdded(RoomInfo room, GameClient client) {}
    
    /**
     * Called when an attempt to add a room to the server fails. Typically this happens
     * when the server has already reached maximum rooms and a command to make another room
     * is sent.
     * @param roomName the proposed name of the room that failed to be created
     * @param capacity the proposed capacity of the room that failed to be created
     * @param client the GameClient that received this command
     */
    default void handleAddRoomFailed(String roomName, int capacity, GameClient client) {}

    /**
     * Called when a room is removed from the server
     * @param roomId the id of the room that was removed
     * @param client the GameClient that received this command
     */
    default void handleRoomRemoved(String roomId, GameClient client) {}
    
    /**
     * Called when a client joins a room.
     * @param clientId the id of the client that joined the room
     * @param roomId the id of the room the client joined
     * @param client the GameClient that received this command
     */
    default void handleClientJoinedRoom(String clientId, String roomId, GameClient client) {}
    
    /**
     * Called when an attempt by the client with this event handler to join a room fails. The reason given could be:
     * <ul>
     * 	<li>ROOM_FULL</li>
     * <li>ROOM_CLOSED</li>
     * <li>NO_SUCH_ROOM</li>
     * </ul>
     * @param reason the reason given for failure
     * @param roomId the id of the room
     * @param client the GameClient that received this command (also the one who tried to join the room)
     */
    default void handleJoinRoomFailed(String reason, String roomId, GameClient client) {}
    
    /**
     * Called when a client leaves a room.
     * @param clientId the id of the client that left the room
     * @param roomId the id of the room the client left
     * @param client the GameClient that received this command
     */
    default void handleClientLeftRoom(String clientId, String roomId, GameClient client) {}
    
    /**
     * Called when the ownership of a room  changes.
     * @param roomId the id of the room the for which ownership changed
     * @param ownerId the id of the new owner
     * @param client the GameClient that received this command
     */
    default void handleRoomOwnership(String roomId, String ownerId, GameClient client) {}
    
    
    /**
     * Called when a room is closed (blocking clients from joining).
     * You should close a room before starting a game so players cannot join after the game started.
     * @param roomId the id of the room that was closed
     * @param client the GameClient that received this command
     */
    default void handleRoomClosed(String roomId, GameClient client) {}
    
    /**
     * Called when a room is opened (allowing clients to join)
     * You should close a room before starting a game so players cannot join after the game started.
     * @param roomId the id of the room that was closed
     * @param client the GameClient that received this command
     */
    default void handleRoomOpened(String roomId, GameClient client) {}
    
}