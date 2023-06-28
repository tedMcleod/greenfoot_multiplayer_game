package com.tinocs.mp.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadServer implements Runnable {

    // Each key is the client id and each value is the socket of that client
    private ConcurrentHashMap<String, Socket> activeClients = new ConcurrentHashMap<>();

    // each key is a room id and the value is a map where each key is a client and value is true
    // (effectively a set of clients in the room)
    //                       roomId ->              clientId -> true
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> rooms = new ConcurrentHashMap<>();

    // Each key is a room id and each value is the name of the room
    //                      roomId -> roomName
    private ConcurrentHashMap<String, String> roomNames = new ConcurrentHashMap<>();

    // each key is the room id and the value is the capacity of the room
    private ConcurrentHashMap<String, Integer> roomCapacities = new ConcurrentHashMap<>();

    // Each key is a client id, and each value is the id of the room the client is in
    // if not in a room, the client will not be in this map
    private ConcurrentHashMap<String, String> roomsByClient = new ConcurrentHashMap<>();
    
    private ConcurrentHashMap<String, String> roomOwners = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> closedRooms = new ConcurrentHashMap<>();
    
    private final int MAX_ROOMS;
    
    // A reference to the ServerSocket
    private ServerSocket ssock;

    /**
     * Create a MultiThreadServer with the given socket and effectively no maximum number of rooms.
     * 
     * @param sock the socket
     */
    public MultiThreadServer(ServerSocket sock) {
        this(sock, Integer.MAX_VALUE);
    }
    
    /**
     * Create a MultiThreadServer with the given socket and the given maximum number of rooms.
     * 
     * @param sock the socket
     * @param maxRooms the maximum number of rooms
     */
    public MultiThreadServer(ServerSocket sock, int maxRooms) {
        ssock = sock;
        MAX_ROOMS = maxRooms;
    }

    /**
     * <p>
     * Adds a client socket to the map of active clients and sends a message
     * to the client informing them of their id and the state of the server.
     * Here is an example message assigning
     * a client the id <b>20ce60c2-0822-4154-95c4-b22ba4f28254</b>:
     * </p>
     * <pre>
     * 20ce60c2-0822-4154-95c4-b22ba4f28254 ID ae3af79d-67fc-45bf-b3d4-1542323e240f 20ce60c2-0822-4154-95c4-b22ba4f28254 ||2147483647
     * </pre>
     * 
     * This says the client has been assigned id 20ce60c2-0822-4154-95c4-b22ba4f28254 and the clients connected at the moment are:
     * <b>ae3af79d-67fc-45bf-b3d4-1542323e240f</b> and <b>20ce60c2-0822-4154-95c4-b22ba4f28254</b>
     * 
     * || means there are currently no rooms because all the room info would be listed between | and |
     * 2147483647 is the maximum number of rooms (in this case it is Integer.MAX_VALUE)
     * 
     * @param id the id of the client
     * @param sock the client socket
     */
    private void addClient(String id, Socket sock) {
        activeClients.put(id, sock);
        sendMessage(id + " ID " + getClientInitState(), id);
    }
    
    /**
     * Remove the client with the given id. This is done once the client
     * has closed the socket.
     * @param clientId
     */
    public void removeClient(String clientId) {
    	if (activeClients.containsKey(clientId)) {
    		activeClients.remove(clientId);
	        leaveRoom(clientId);
	        broadcast(clientId + " DC");
	        System.out.println("Client disconnected: " + clientId);
    	}
    }

    /** 
     * <p>
     * Creates a room with the given room name and broadcasts the id of the created room
     * If the name contains white space characters such as space, tab or new line, or 
     * illegal room name characters '|' or ',' they will be removed.
     * The broadcast will be in the form ROOM_ADDED roomId roomName capacity null false
     * where null represents the ownerId (null because there can't be an owner yet)
     * and false indicates the room is not closed (it always starts out open)
     * </p>
     * 
     * <p>
     * Here is an example broadcast in response to adding a room with capacity 6
     * named <b>HappyHome</b> with id <b>c9a5de7c-7828-44dc-8fcc-da7a6a2f55dd</b>
     * </p>
     * 
     * <pre>
     * ROOM_ADDED c9a5de7c-7828-44dc-8fcc-da7a6a2f55dd HappyHome 6 null false
     * </pre>
     * <p>
     * Note that if the MAX_ROOMS cap would be exceeded by this room, this method will return false
     * and the ClientThread will send an error message to the client that requested the room be created
     * in the form: ADD_ROOM_FAILED roomName roomCapacity
     * </p>
     * <p>
     * For example, if the call above failed, the message would be:
     * </p>
     * <pre>
     * ADD_ROOM_FAILED HappyHome 6
     * </pre>
     * 
     * 
     * @param roomName the name of the room (commas will be removed from the name)
     * @param capacity the maximum number of clients allowed to join the room (if < 1, it will be set to 1)
     * @return true if the room was successfully created and false otherwise (i.e. max rooms was reached)
     */ 
    public boolean addRoom(String roomName, int capacity) {
        if (rooms.size() < MAX_ROOMS) {
            // remove all pipes '|', commas ',' and spaces ' ' from room name
            if (capacity < 1) capacity = 1;
            roomName = roomName.replaceAll("[\\s|,]", "");
            String roomId = generateUUID();
            rooms.put(roomId, new ConcurrentHashMap<>());
            roomNames.put(roomId, roomName);
            roomCapacities.put(roomId, capacity);
            broadcast("ROOM_ADDED " + roomId + " " + roomName + " " + capacity + " " + null + " " + false);
            return true;
        } else {
            return false;
        }
    }
    
    /** returns a string describing the state of the server in the form used to pass to the clients */
    public String getClientInitState() {
        return getKeysStr(activeClients) + "|" + getRoomsStr() + "|" + MAX_ROOMS;
    }
    
    private <T> String getKeysStr(Map<T, ?> map) {
        String str = "";
        for (T key : map.keySet()) {
            str += key + " ";
        }
        return str;
    }
    
    private String getRoomsStr() {
        String msg = "";
        for (Map.Entry<String, ConcurrentHashMap<String, Boolean>> roomEntry : rooms.entrySet()) {
            String roomId = roomEntry.getKey();
            ConcurrentHashMap<String, Boolean> members = roomEntry.getValue();
            String roomName = roomNames.get(roomId);
            int capacity = roomCapacities.get(roomId);
            String ownerId = null;
            if (roomOwners.containsKey(roomId)) {
                ownerId = roomOwners.get(roomId);
            }
            boolean closed = closedRooms.containsKey(roomId);
            msg += " " + roomId + " " + roomName + " " + capacity + " " + ownerId + " " + closed;
            for (String clientId : members.keySet()) {
                msg += " " + clientId;
            }
            msg += " ,";
        }
        if (rooms.entrySet().size() > 0) msg = msg.substring(0, msg.length() - 2);
        return msg;
    }

    /**
     * <p>
     * Removes the room and notifies all clients that the room was removed.
     * If the room does not exist, nothing will happen.
     * Before removing the room, each client in the room leaves the room,
     * which will generate messages reporting the clients leaving
     * (see the {@link #leaveRoom(String, String) leaveRoom} method).
     * <p>
     * The notification message will be in the form: ROOM_REMOVED roomId
     * <p>
     * <p>
     * Example of notification message removing room with id <b>bde5b7b2-7fda-4445-9e19-97f7bc5113a0</b>
     * </p>
     * <pre>
     * ROOM_REMOVED bde5b7b2-7fda-4445-9e19-97f7bc5113a0
     * </pre>
     * 
     * @param roomId the id of the room to remove
     */
    public void removeRoom(String roomId) {
        if (rooms.containsKey(roomId)) {
            for (String clientId : rooms.get(roomId).keySet()) {
                leaveRoom(clientId);
            }
            rooms.remove(roomId);
            roomNames.remove(roomId);
            roomCapacities.remove(roomId);
            roomOwners.remove(roomId);
            closedRooms.remove(roomId);
            broadcast("ROOM_REMOVED " + roomId);
        }
    }
 
    /**
     * <p>
     * The client with the given clientId joins the room with the given roomId.
     * Each clientId will only appear in the set once, so adding the same clientId
     * multiple times will do nothing. If the room was successfully joined, a message
     * is broacast to all clients in the form: <b>JOINED_ROOM clientId roomId</b>
     * </p>
     * <p>
     * Example of a client with id <b>361e1424-1d06-4610-b1de-6392c789562f</b>
     * joining a room with id <b>d8b7f670-fc7c-4939-8679-7ea09cfd693c</b>
     * </p>
     * <pre>
     * JOINED_ROOM 361e1424-1d06-4610-b1de-6392c789562f d8b7f670-fc7c-4939-8679-7ea09cfd693c
     * </pre>
     * <p>
     * If the client failed to join the room because the room was full, a message is sent
     * to that client saying: JOIN_ROOM_FAIL FULL roomId
     * </p>
     * <p>
     * For example, in the attempt above, if the room was full, the following message
     * would be sent to the client with id <b>361e1424-1d06-4610-b1de-6392c789562f</b>
     * </p>
     * <pre>
     * JOIN_ROOM_FAIL FULL d8b7f670-fc7c-4939-8679-7ea09cfd693c
     * </pre>
     * <p>
     * If the client failed to join the room because the room does not exist, a message is sent
     * to that client saying: JOIN_ROOM_FAIL NO_SUCH_ROOM roomId
     * </p>
     * <p>
     * For example, in the attempt above, if the room was full, the following message
     * would be sent to the client with id <b>361e1424-1d06-4610-b1de-6392c789562f</b>
     * </p>
     * <pre>
     * JOIN_ROOM_FAIL NO_SUCH_ROOM d8b7f670-fc7c-4939-8679-7ea09cfd693c
     * </pre>
     * 
     * @param clientId the id of the client joining the room
     * @param roomId the id of the room the client is joining
     */
    public void joinRoom(String clientId, String roomId) {
        if (rooms.containsKey(roomId)) {
            ConcurrentHashMap<String, Boolean> members = rooms.get(roomId);
            boolean isOwner = false;
            if (members.size() == 0 && roomCapacities.get(roomId) > 0 && !closedRooms.containsKey(roomId)) {
                roomOwners.put(roomId, clientId);
                isOwner = true;
            }
            if (closedRooms.containsKey(roomId)) {
                sendMessage("JOIN_ROOM_FAIL CLOSED " + roomId, clientId);
            } else if (members.size() < roomCapacities.get(roomId)) {
                members.put(clientId, true);
                roomsByClient.put(clientId, roomId);
                broadcast("JOINED_ROOM " + clientId + " " + roomId);
                if (isOwner) broadcast("ROOM_OWNER " + roomId + " " + clientId);
            } else {
                sendMessage("JOIN_ROOM_FAIL FULL " + roomId, clientId);
            }
        } else {
            sendMessage("JOIN_ROOM_FAIL NO_SUCH_ROOM " + roomId, clientId);
        }
    }

    /**
     * <p>
     * Remove the given client from their current room. 
     * When the client leaves, a message will be broadcasts to all clients in the form: LEFT_ROOM clientId roomId
     * </p>
     * <p>
     * For example, if a client with id <b>361e1424-1d06-4610-b1de-6392c789562f</b> leaves a room with id <b>d8b7f670-fc7c-4939-8679-7ea09cfd693c</b>, the message will be:
     * </p>
     * <pre>
     * LEFT_ROOM 361e1424-1d06-4610-b1de-6392c789562f d8b7f670-fc7c-4939-8679-7ea09cfd693c
     * </pre>
     * 
     * @param clientId the id of the client leaving the room
     */
    public void leaveRoom(String clientId) {
        if (roomsByClient.containsKey(clientId)) {
            String roomId = roomsByClient.get(clientId);
            ConcurrentHashMap<String, Boolean> roomRoster = rooms.get(roomId);
            roomRoster.remove(clientId);
            roomsByClient.remove(clientId);
            String newOwnerId = null;
            if (roomOwners.containsKey(roomId) && roomOwners.get(roomId).equals(clientId)) {
                if (roomRoster.size() > 0) {
                	newOwnerId = roomRoster.keys().nextElement();
                    roomOwners.put(roomId, newOwnerId);
                } else {
                    roomOwners.remove(roomId);
                }
            }
            broadcast("LEFT_ROOM " + clientId + " " + roomId);
            if (newOwnerId != null) {
                broadcast("ROOM_OWNER " + roomId + " " + newOwnerId);
            }
            if (roomRoster.size() == 0 && closedRooms.containsKey(roomId)) {
                openRoom(roomId);
            }
        }
    }
    
    /**
     * Closes the room with the given roomId which will prevent clients from joining the room.
     * This can be useful if you don't want clients to be able to join a room while a game is in
     * progress or for any other reason. A message will be sent to all clients on the server
     * announcing the room in closed in the form ROOM_CLOSED roomId.
 	 * <p>For example, if a room with id
     * 33efb9ac-b905-4676-ac2f-ea345313d137 was closed, the message would say:</p>
     * <pre>
     * ROOM_CLOSED 33efb9ac-b905-4676-ac2f-ea345313d137
     * </pre>
     * @param roomId the id of the room to close
     */
    public void closeRoom(String roomId) {
        if (rooms.containsKey(roomId)) {
            closedRooms.put(roomId, true);
            broadcast("ROOM_CLOSED " + roomId);
        }
    }
    
    /**
     * Opens a room with the given roomId that was previously closed.
     * Passing a roomId of a room that is not closed will have no effect.
     * If the room was previously closed and is now open, a message will
     * be broadcast to all clients announcing the room is open in the form:
     * ROOM_OPENED roomId
     * <p>For example, if a room with id 
     * 33efb9ac-b905-4676-ac2f-ea345313d137 was opened, the message would say:</p>
     * <pre>
     * ROOM_OPENED 33efb9ac-b905-4676-ac2f-ea345313d137
     * </pre>
     * 
     * @param roomId the id of the room to open
     */
    public void openRoom(String roomId) {
        if (closedRooms.containsKey(roomId)) {
            closedRooms.remove(roomId);
            broadcast("ROOM_OPENED " + roomId);
        }
    }

    /**
     * Sends a message to the client with the given id.
     * 
     * @param message the message
     * @param toId the id of the client to send the message to
     */
    public synchronized void sendMessage(String message, String toId) {
        Socket sock = activeClients.get(toId);
        if (sock != null) {
            try {
                PrintStream pstream = new PrintStream(sock.getOutputStream(), true);
                pstream.println(message);
            } catch (IOException e) {
                activeClients.remove(toId);
                e.printStackTrace();
            }
        }
    }

    /**
     * Broadcast a message to every client on the server
     * 
     * @param message the message
     */
    public void broadcast(String message) {
        for (String clientId : activeClients.keySet()) {
            sendMessage(message, clientId);
        }
    }

    /**
     * Broadcast a message from the given client to every OTHER client on the server (excluding the sender)
     * 
     * @param message the message
     * @param fromId the client id of the sender
     */
    public void broadcast(String message, String fromId) {
        for (Map.Entry<String, Socket> entry : activeClients.entrySet()) {
            if (!fromId.equals(entry.getKey())) sendMessage(message, entry.getKey());
        }
    }

    /**
     * Send a message to every client in the room with the given roomId.
     * If the room does not exist nothing will happen.
     * 
     * @param message the message
     * @param roomId the id of the room to send the message to
     */
    public void roomBroadcast(String message, String roomId) {
        if (rooms.containsKey(roomId)) {
            for (String toId : rooms.get(roomId).keySet()) {
                sendMessage(message, toId);
            }
        }
    }

    /**
     * Send a message to every client in the room with the given roomId excluding the client with the given fromId.
     * If the room does not exist nothing will happen.
     * 
     * @param message the message
     * @param roomId the id of the room to send the message to
     * @param fromId the client id of the sender
     * @return true if a room with the given id exists and false otherwise 
     */
    public void roomBroadcast(String message, String roomId, String fromId) {
        if (rooms.containsKey(roomId)) {
            for (String toId : rooms.get(roomId).keySet()) {
                if (!fromId.equals(toId)) {
                    sendMessage(message, toId);
                }
            }
        }
    }
    
    /**
     * Send a message indicating an invalid command was received. For example, if a command
     * started out with <b>JOIN_ROOM</b> but was not followed by a string representing the roomId,
     * this message would be sent back.
     * @param cmd the command
     * @param toId the id of the client to send this error to (the one who sent the invalid command)
     */
    public void sendInvalidCommandErrorMessage(String cmd, String toId) {
    	sendMessage("INVALID_CMD " + cmd, toId);
    }

    /**
     * generate and return a random unique id.
     * @return a randomly generated unique id.
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * The main loop that listens for and accepts connections.
     * Each connected client is listened to on a new thread.
     */
    @Override
    public void run(){
        try {
            System.out.println("Listening at " + InetAddress.getLocalHost().getHostAddress() + ":" + ssock.getLocalPort());
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        while (true) {
            Socket sock;
            try {
                sock = ssock.accept();
                String id = generateUUID();
                addClient(id, sock);
                System.out.println("Client connected to " + sock.getInetAddress() + " and assigned UUID: " + id);
                new Thread(new ClientThread(sock, this, id)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
