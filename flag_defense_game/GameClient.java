import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * THIS IS NOT ACCURATE ANYMORE SINCE THERE ARE EVENT HANDLERS
 * The GameClient class controls the client's connection to the server. It handles writing and reading data
 * to and from the server and other clients. Subclasses should override the handleCommand(String command)
 * and the onIdAssigned(String clientId) methods to execute any actions that should happen in response
 * to being assigned a clientId by the server and in response to any commands sent by the server or other clients.
 * 
 * The overall model of communication is:
 * <ol>
 * <li>client connects</li>
 * <li>server sends a message to just that client telling the client what id they have been assigned.
 * <pre>
 * Example:
 * 5e42484f-7f52-4be2-9ab0-c41796387504 ID
 * </pre>
 * </li>
 * <li>The </li>
 * </ol>
 * 
 * @author Ted McLeod 
 * @version 5/7/2023 
 */
public class GameClient implements Runnable {

    //public static final String CMD_ROOMS_INFO = "ROOMS_INFO";
    public static final String CMD_ROOM_ADDED = "ROOM_ADDED";
    public static final String CMD_ADD_ROOM_FAIL = "ADD_ROOM_FAILED";
    public static final String CMD_ROOM_REMOVED = "ROOM_REMOVED";
    public static final String CMD_ROOM_OWNER = "ROOM_OWNER";

    public static final String CMD_ID = "ID";
    public static final String CMD_DISCONNECT = "DC";
    public static final String CMD_JOINED = "JOINED";

    public static final String CMD_JOINED_ROOM = "JOINED_ROOM";
    public static final String CMD_JOIN_ROOM_FAIL = "JOIN_ROOM_FAIL";
    public static final String CMD_LEFT_ROOM = "LEFT_ROOM";
    public static final String CMD_CLOSED_ROOM = "ROOM_CLOSED";
    public static final String CMD_OPENED_ROOM = "ROOM_OPENED";

    private final String hostName;
    private final int portNumber;
    private Socket sock;
    private String id;
    private volatile boolean isConnected;
    private ClientEventHandler eventHandler;

    /* replicated data from server */
    // Each key is the client id and each value is the socket of that client
    private ConcurrentHashMap<String, Boolean> activeClients = new ConcurrentHashMap<>();

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

    private int maxRooms;
    
    /**
     * Initialize a GameClient to connect to the given hostName and portNumber.
     * @param hostName the host name
     * @param portNumber the port number
     */
    public GameClient (String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        id = null;
        isConnected = false;
        this.eventHandler = null;
    }

    /**
     * Broadcast a message from this client to all the other clients.
     * @param message the message
     */
    public synchronized void broadcastMessage(String message) {
        try {
            if (sock != null) {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcast a message from this client to all the other clients in the given room.
     * @param message the message
     */
    public synchronized void broadcastMessageToRoom(String message, String roomId) {
        try {
            if (sock != null) {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("TO_ROOM " + roomId + " " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcast a messsage from this client to the client given by toId.
     * @param message the message
     * @param toId the id of the client to send the message to
     */
    public synchronized void sendMessage(String message, String toId) {
        try {
            if (sock != null) {
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("TO " + toId + " " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * disconnect from the server. Other clients will be sent a message informing them that this client was disconnected.
     */
    public void disconnect() {
        broadcastMessage(CMD_DISCONNECT);
    }

    public void addRoom(String roomName, int capacity) {
        roomName = roomName.replaceAll("[\\s,]+", "");
        broadcastMessage("ADD_ROOM " + roomName + " " + capacity);
    }

    public void removeRoom(String roomId) {
        broadcastMessage("REMOVE_ROOM " + roomId);
    }

    public void joinRoom(String roomId) {
        broadcastMessage("JOIN_ROOM " + roomId);
    }

    public void leaveRoom(String roomId) {
        broadcastMessage("LEAVE_ROOM " + roomId);
    }

    public void closeRoom(String roomId) {
        broadcastMessage("CLOSE_ROOM " + roomId);
    }

    public void openRoom(String roomId) {
        broadcastMessage("OPEN_ROOM " + roomId);
    }

    /**
     * start the thread that will connect to the server and begin reading and writing data.
     */
    public void start() {
        Thread clientThread = new Thread(this);
        clientThread.start();
    }

    /**
     * This method connects to the server and starts a loop that reads and writes data to and from the server.
     */
    @Override
    public void run() {
        // Create a socket, a PrintWriter to write to the socket and a BufferedReader to read from the socket
        // using try with resources so they will close automatically when the try-catch is finished.
        try (
        Socket sock = new Socket(hostName, portNumber);
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        ) {
            // save a reference to the socket in the sock instance variable
            this.sock = sock;
            // read the first command
            String nextCommand = in.readLine();
            setIsConnected(true);
            // keep getting the next command while still connected and thread is not interrupted
            while (nextCommand != null && !Thread.interrupted()){
                processCommand(nextCommand);
                // get the next command
                nextCommand = in.readLine();
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostName);
        } catch (IOException e) {
            System.err.println("I/O exception for connection: " + hostName);
        } finally {
            setIsConnected(false);
        }
    }

    protected void processCommand(String cmd) {
        if (Debug.DEBUG) System.out.println("process command: " + cmd);
        try (Scanner reader = new Scanner(cmd)) {
            // every message starts with the clientId of the sender except server messages
            String firstToken = reader.next();
            if (firstToken.equals(CMD_ROOM_ADDED)) {
                RoomInfo roomInfo = getRoomInfo(cmd.substring(CMD_ROOM_ADDED.length() + 1));
                addRoomInfo(roomInfo);
                if(eventHandler != null) eventHandler.handleRoomAdded(roomInfo, this);
            } else if (firstToken.equals(CMD_ROOM_REMOVED)) {
                String roomId = reader.next();
                removeRoomInfo(roomId);
                if(eventHandler != null) eventHandler.handleRoomRemoved(roomId, this);
            } else if (firstToken.equals(CMD_ADD_ROOM_FAIL)) {
                String roomName = reader.next();
                int roomCapacity = reader.nextInt();
                if(eventHandler != null) eventHandler.handleAddRoomFailed(roomName, roomCapacity, this);
            }else if (firstToken.equals(CMD_JOINED_ROOM)) {
                String clientId = reader.next();
                String roomId = reader.next();
                addMemberToRoom(clientId, roomId);
                if(eventHandler != null) eventHandler.handleClientJoinedRoom(clientId, roomId, this);
            } else if (firstToken.equals(CMD_JOIN_ROOM_FAIL)) {
                String reason = reader.next();
                String roomId = reader.next();
                if(eventHandler != null) eventHandler.handleJoinRoomFailed(reason, roomId, this);
            } else if (firstToken.equals(CMD_LEFT_ROOM)) {
                String clientId = reader.next();
                String roomId = reader.next();
                removeMemberFromRoom(clientId, roomId);
                if(eventHandler != null) eventHandler.handleClientLeftRoom(clientId, roomId, this);
            } else if (firstToken.equals(CMD_ROOM_OWNER)) {
                String roomId = reader.next();
                String clientId = reader.next();
                setRoomOwner(roomId, clientId);
                if(eventHandler != null) eventHandler.handleRoomOwnership(roomId, clientId, this);
            } else if (firstToken.equals(CMD_CLOSED_ROOM)) {
                String roomId = reader.next();
                setRoomClosed(roomId);
                if(eventHandler != null) eventHandler.handleRoomClosed(roomId, this);
            } else if (firstToken.equals(CMD_OPENED_ROOM)) {
                String roomId = reader.next();
                setRoomOpened(roomId);
                if(eventHandler != null) eventHandler.handleRoomOpened(roomId, this);
            } else if (reader.hasNext()){
                String secondToken = reader.next();
                if (secondToken.equals(CMD_ID)) {
                    // if the clientId is followed by "ID" then this client is being assigned an id
                    // when the id is set, this client will also broadcast a message to
                    // the other clients saying it just joined. This gives them a chance to
                    // tell this client anything it needs to know when it first joins,
                    // such as what other game objects already exist
                    
                    String stateStr = cmd.substring(cmd.indexOf(" ") + 1 + CMD_ID.length() + 1);
                    setId(firstToken, stateStr); 
                } else if (secondToken.equals(CMD_JOINED)) {
                    addActiveClient(firstToken);
                    if(eventHandler != null) eventHandler.handleOtherClientJoined(firstToken, this);
                } else if (secondToken.equals(CMD_DISCONNECT)) {
                    removeActiveClient(firstToken);
                    if(eventHandler != null) eventHandler.handleOtherClientDisconnected(firstToken, this);
                } else {
                    if (Debug.DEBUG) System.out.println("Passing command to eventhandler " + cmd);
                    if(eventHandler != null) eventHandler.handleCommand(cmd, this);
                }
            } else {
                if (Debug.DEBUG) System.out.println("Passing command to eventhandler " + cmd);
                if(eventHandler != null) eventHandler.handleCommand(cmd, this);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Get the id of this client.
     * @return the id of this client.
     */
    public String getId() {
        return id;
    }

    /**
     * set the id of this client. This should only be called internally to set
     * the id to the id assigned by the server.
     */
    protected void setId(String id, String state) {
        this.id = id;
        addActiveClient(id);
        initClientState(state);
        if(eventHandler != null) eventHandler.onIdAssigned(id, this);
        broadcastMessage(CMD_JOINED);
    }

    
    /*
    private ConcurrentHashMap<String, Boolean> activeClients = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> rooms = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> roomNames = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> roomCapacities = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> roomsByClient = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> roomOwners = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> closedRooms = new ConcurrentHashMap<>();
    private int maxRooms;
     */
    private void initClientState(String stateStr) {
        String[] split = stateStr.split("[|]");
        fillMapWithKeys(activeClients, split[0]);
        initRooms(split[1]);
        maxRooms = Integer.parseInt(split[2]);
    }

    private void fillMapWithKeys(Map<String, Boolean> map, String str) {
        Scanner reader = new Scanner(str);
        while (reader.hasNext()) map.put(reader.next(), true);
    }

    protected void initRooms(String str) {
        if (str.length() == 0) return;
        String[] split = str.split("[,]");
        for (String roomStr : split) {
            Scanner reader = new Scanner(roomStr);
            String roomId = reader.next();
            String name = reader.next();
            roomNames.put(roomId, name);
            int capacity = reader.nextInt();
            roomCapacities.put(roomId, capacity);
            String ownerId = reader.next();
            if (!ownerId.equals("null")) roomOwners.put(roomId, ownerId);
            boolean isClosed = reader.nextBoolean();
            if (isClosed) closedRooms.put(roomId, true);
            ConcurrentHashMap<String, Boolean> members = new ConcurrentHashMap<>();
            rooms.put(roomId, members);
            while (reader.hasNext()) {
                String clientId = reader.next();
                members.put(clientId, true);
                roomsByClient.put(clientId, roomId);
            }
        }
    }
    
    protected void addMemberToRoom(String clientId, String roomId) {
        ConcurrentHashMap<String, Boolean> members = rooms.get(roomId);
        if (members != null) members.put(clientId, true);
        roomsByClient.put(clientId, roomId);
    }
    
    protected void removeMemberFromRoom(String clientId, String roomId) {
        ConcurrentHashMap<String, Boolean> members = rooms.get(roomId);
        if (members != null) members.remove(clientId);
    }
    
    protected void setRoomOwner(String roomId, String clientId) {
        roomOwners.put(roomId, clientId);
    }
    
    protected void setRoomClosed(String roomId) {
        closedRooms.put(roomId, true);
    }
    
    protected void setRoomOpened(String roomId) {
        closedRooms.remove(roomId);
    }
    
    protected void addRoomInfo(RoomInfo room) {
        ConcurrentHashMap<String, Boolean> members = rooms.get(room.getId());
        if (members == null) {
            members = new ConcurrentHashMap<>();
            rooms.put(room.getId(), members);
        }
        roomNames.put(room.getId(), room.getName());
        roomCapacities.put(room.getId(), room.getCapacity());
        if (room.getOwnerId() != null) roomOwners.put(room.getId(), room.getOwnerId());
        if (room.isClosed()) closedRooms.put(room.getId(), true);
        for (String memberId : room.members()) {
            members.put(memberId, true);
            roomsByClient.put(memberId, room.getId());
        }
    }
    
    protected void removeRoomInfo(String roomId) {
        ConcurrentHashMap<String, Boolean> members = rooms.get(roomId);
        if (members != null) {
            for (String memberId : members.keySet()) {
                roomsByClient.remove(memberId);
            }
            roomNames.remove(roomId);
            roomCapacities.remove(roomId);
            roomOwners.remove(roomId);
            closedRooms.remove(roomId);
            rooms.remove(roomId);
        }
        
    }
    
    protected void addActiveClient(String clientId) {
        activeClients.put(clientId, true);
    }
    
    protected void removeActiveClient(String clientId) {
        activeClients.remove(clientId);
        if (roomsByClient.containsKey(clientId)) {
            String roomId = roomsByClient.get(clientId);
            roomsByClient.remove(clientId);
            ConcurrentHashMap<String, Boolean> members = rooms.get(roomId);
            if (members != null) members.remove(clientId);
            roomOwners.remove(clientId);
        }
    }
    
    private RoomInfo getRoomInfo(String str) {
        Scanner reader = new Scanner(str);
        String id = reader.next();
        String name = reader.next();
        int capacity = reader.nextInt();
        String ownerId = reader.next();
        if (ownerId.equals("null")) ownerId = null;
        boolean isClosed = reader.nextBoolean();
        Set<String> members = new HashSet<>();
        while (reader.hasNext()) {
            members.add(reader.next());
        }
        return new RoomInfo(id, name, capacity, members, ownerId, isClosed);
    }
    
    /**
     * Returns true if this client is currently connected to the server and false otherwise.
     * @return true if this client is currently connected to the server and false otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * sets the connection status of this client to the server.
     * @param connected whether this client is connected to the server
     */
    private void setIsConnected(boolean connected) {
        isConnected = connected;
        if(!isConnected && eventHandler != null)  eventHandler.onDisconnected(this);
    }

    public void setEventHandler(ClientEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public ClientEventHandler getEventHandler() {
        return eventHandler;
    }
    
    public String getRoomName(String roomId) {
        return roomNames.get(roomId);
    }
    
    public Set<String> getRoomIds() {
        return new HashSet<>(rooms.keySet());
    }
    
    public Set<String> getClientIds() {
        return new HashSet<>(activeClients.keySet());
    }
    
    public String getIdOfRoomContainingClient(String clientId) {
        return roomsByClient.get(clientId);
    }
    
    public String getCurrentRoomId() {
        return getIdOfRoomContainingClient(id);
    }
    
    public Set<String> getClientsInRoom(String roomId) {
        return new HashSet<>(rooms.get(roomId).keySet());
    }
    
    public int getRoomCapacity(String roomId) {
        return roomCapacities.get(roomId);
    }
    
    public String getRoomOwner(String roomId) {
        return roomOwners.get(roomId);
    }
    
    public boolean roomIsClosed(String roomId) {
        return closedRooms.containsKey(roomId);
    }
    
    public Set<String> getClosedRooms() {
        return new HashSet<>(closedRooms.keySet());
    }

}
