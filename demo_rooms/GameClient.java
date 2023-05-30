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

import javafx.application.Platform;

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

    public static final String CMD_ROOMS_INFO = "ROOMS_INFO";
    public static final String CMD_ROOM_ADDED = "ROOM_ADDED";
    public static final String CMD_ADD_ROOM_FAIL = "ADD_ROOM_FAILED";
    public static final String CMD_ROOM_REMOVED = "ROOM_REMOVED";

    public static final String CMD_ID = "ID";
    public static final String CMD_DISCONNECT = "DC";
    public static final String CMD_JOINED = "JOINED";

    public static final String CMD_JOINED_ROOM = "JOINED_ROOM";
    public static final String CMD_JOIN_ROOM_FAIL = "JOIN_ROOM_FAIL";
    public static final String CMD_LEFT_ROOM = "LEFT_ROOM";
    

    private String hostName;
    private int portNumber;
    private Socket sock;
    private String id;
    private boolean isConnected;
    private ClientEventHandler eventHandler;

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
    public synchronized void disconnect() {
        broadcastMessage(CMD_DISCONNECT);
    }

    public synchronized void addRoom(String roomName, int capacity) {
        roomName = roomName.replaceAll("[\\s,]+", "");
        broadcastMessage("ADD_ROOM " + roomName + " " + capacity);
    }

    public synchronized void removeRoom(String roomId) {
        broadcastMessage("REMOVE_ROOM " + roomId);
    }

    public synchronized void joinRoom(String roomId) {
        broadcastMessage("JOIN_ROOM " + roomId);
    }

    public synchronized void leaveRoom(String roomId) {
        broadcastMessage("LEAVE_ROOM " + roomId);
    }

    public synchronized void getRooms() {
        broadcastMessage("GET_ROOMS");
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
                //System.out.println(nextCommand);
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
        System.out.println("process command: " + cmd);
        try (Scanner reader = new Scanner(cmd)) {
            // every message starts with the clientId of the sender except server messages
            String firstToken = reader.next();
            if (firstToken.equals(CMD_ROOMS_INFO)) {
                if(eventHandler != null) eventHandler.handleRoomsInfo(getRoomsInfo(cmd.substring(CMD_ROOMS_INFO.length())), this);
            } else if (firstToken.equals(CMD_ROOM_ADDED)) {
                if(eventHandler != null) eventHandler.handleRoomAdded(getRoomInfo(cmd.substring(CMD_ROOM_ADDED.length())), this);
            } else if (firstToken.equals(CMD_ROOM_ADDED)) {
                if(eventHandler != null) eventHandler.handleRoomRemoved(reader.next(), this);
            } else if (firstToken.equals(CMD_ADD_ROOM_FAIL)) {
                String roomName = reader.next();
                int roomCapacity = reader.nextInt();
                if(eventHandler != null) eventHandler.handleAddRoomFailed(roomName, roomCapacity, this);
            }else if (firstToken.equals(CMD_JOINED_ROOM)) {
                String clientId = reader.next();
                String roomId = reader.next();
                if(eventHandler != null) eventHandler.handleClientJoinedRoom(clientId, roomId, this);
            } else if (firstToken.equals(CMD_JOIN_ROOM_FAIL)) {
                String reason = reader.next();
                String roomId = reader.next();
                if(eventHandler != null) eventHandler.handleJoinRoomFailed(reason, roomId, this);
            } else if (firstToken.equals(CMD_LEFT_ROOM)) {
                String clientId = reader.next();
                String roomId = reader.next();
                if(eventHandler != null) eventHandler.handleClientLeftRoom(clientId, roomId, this);
            } else if (reader.hasNext()){
                String secondToken = reader.next();
                if (secondToken.equals(CMD_ID)) {
                    // if the clientId is followed by "ID" then this client is being assigned an id
                    // when the id is set, this client will also broadcast a message to
                    // the other clients saying it just joined. This gives them a chance to
                    // tell this client anything it needs to know when it first joins,
                    // such as what other game objects already exist
                    setId(firstToken); 
                } else if (secondToken.equals(CMD_JOINED)) {
                    if(eventHandler != null) eventHandler.handleOtherClientJoined(firstToken, this);
                } else if (secondToken.equals(CMD_DISCONNECT)) {
                    if(eventHandler != null) eventHandler.handleOtherClientDisconnected(firstToken, this);
                } else {
                    System.out.println("Passing command to eventhandler " + cmd);
                    if(eventHandler != null) eventHandler.handleCommand(cmd, this);
                }
            } else {
                System.out.println("Passing command to eventhandler " + cmd);
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
    private void setId(String id) {
        this.id = id;
        if(eventHandler != null) eventHandler.onIdAssigned(id, this);
        broadcastMessage(CMD_JOINED);
    }

    private Set<RoomInfo> getRoomsInfo(String str) {
        //System.out.println("Getting room info: " + str);
        Set<RoomInfo> rooms = new HashSet<>();
        if (str.length() == 0) return rooms;
        String[] roomsSplit = str.split(",");
        for (String roomStr : roomsSplit) {
            rooms.add(getRoomInfo(roomStr.trim()));
        }
        return rooms;
    }

    private RoomInfo getRoomInfo(String str) {
        Scanner reader = new Scanner(str);
        String id = reader.next();
        String name = reader.next();
        int capacity = reader.nextInt();
        Set<String> members = new HashSet<>();
        while (reader.hasNext()) {
            members.add(reader.next());
        }
        return new RoomInfo(id, name, capacity, members);
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
        if(!isConnected && eventHandler != null) eventHandler.onDisconnected(this);
    }

    public void setEventHandler(ClientEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public ClientEventHandler getEventHandler() {
        return eventHandler;
    }

}
