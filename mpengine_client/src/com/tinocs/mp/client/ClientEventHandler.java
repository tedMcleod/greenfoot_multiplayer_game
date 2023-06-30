package com.tinocs.mp.client;

/**
 * The ClientEventHandler interface provides methods for responding to messages from the server
 * or from other clients.
 * 
 * @author Ted McLeod 
 * @version 6/6/2023
 */
public interface ClientEventHandler {
    
	/** Joining a room failed because the room was full*/
    String ROOM_FULL = "FULL";
    
    /** Joining a room failed because the room was closed*/
    String ROOM_CLOSED = "CLOSED";
    
    /** Joining a room failed because the room does not exist*/
    String NO_SUCH_ROOM = "NO_SUCH_ROOM";
    
    /**
     * Respond to the given command from another client.
     * Recommendation: first make this method simply print the command so you can see how to parse it.
     * @param command the message to respond to
     * @param client the client the command was received by
     */
    default void handleCommand(String command, Client client) {}
    
    /**
     * Do what needs to be done when this client is assigned an ID by the server.
     * @param clientId the id assigned to this client by the server
     * @param client the client the command was received by
     */
    default void onIdAssigned(String clientId, Client client) {}

    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after this client
     * has been disconnected.
     * @param client the client the command was received by
     */
    default void onDisconnected(Client client) {}
    
    /**
     * Called when another client joins the server.
     * @param clientId the id of the client that joined
     * @param client the client that received this command
     */
    default void handleOtherClientJoined(String clientId, Client client) {}
    
    /**
     * Called when another client disconnects from the server.
     * @param clientId the id of the client that disconnected
     * @param client the client that received this command
     */
    default void handleOtherClientDisconnected(String clientId, Client client) {}

    /**
     * Called when a room is added to the server
     * @param room a RoomInfo object defining the room that was added
     * @param client the client that received this command
     */
    default void handleRoomAdded(RoomInfo room, Client client) {}
    
    /**
     * Called when an attempt to add a room to the server fails. Typically this happens
     * when the server has already reached maximum rooms and a command to make another room
     * is sent.
     * @param roomName the proposed name of the room that failed to be created
     * @param capacity the proposed capacity of the room that failed to be created
     * @param client the client that received this command
     */
    default void handleAddRoomFailed(String roomName, int capacity, Client client) {}

    /**
     * Called when a room is removed from the server
     * @param roomId the id of the room that was removed
     * @param client the client that received this command
     */
    default void handleRoomRemoved(String roomId, Client client) {}
    
    /**
     * Called when a client joins a room.
     * @param clientId the id of the client that joined the room
     * @param roomId the id of the room the client joined
     * @param client the client that received this command
     */
    default void handleClientJoinedRoom(String clientId, String roomId, Client client) {}
    
    /**
     * Called when an attempt by the client with this event handler to join a room fails. The reason given could be:
     * <ul>
     * 	<li>{@link #ROOM_FULL}</li>
     * 	<li>{@link #ROOM_CLOSED}</li>
     * 	<li>{@link #NO_SUCH_ROOM}</li>
     * </ul>
     * @param reason the reason given for failure
     * @param roomId the id of the room
     * @param client the client that received this command (also the one who tried to join the room)
     */
    default void handleJoinRoomFailed(String reason, String roomId, Client client) {}
    
    /**
     * Called when a client leaves a room.
     * @param clientId the id of the client that left the room
     * @param roomId the id of the room the client left
     * @param client the client that received this command
     */
    default void handleClientLeftRoom(String clientId, String roomId, Client client) {}
    
    /**
     * Called when the ownership of a room  changes.
     * @param roomId the id of the room the for which ownership changed
     * @param ownerId the id of the new owner
     * @param client the client that received this command
     */
    default void handleRoomOwnership(String roomId, String ownerId, Client client) {}
    
    /**
     * Called when a room is closed (blocking clients from joining).
     * @param roomId the id of the room that was closed
     * @param client the client that received this command
     */
    default void handleRoomClosed(String roomId, Client client) {}
    
    /**
     * Called when a room is opened (allowing clients to join)
     * @param roomId the id of the room that was closed
     * @param client the client that received this command
     */
    default void handleRoomOpened(String roomId, Client client) {}
    
}
