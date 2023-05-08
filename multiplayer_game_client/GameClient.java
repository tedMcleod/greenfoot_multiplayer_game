import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * The GameClient class controls the client's connection to the server. It handles writing and reading data
 * to and from the server and other clients. Subclasses should override the doCommand(String command)
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
public abstract class GameClient implements Runnable {
    
    public static final String CMD_DISCONNECT = "DC";
    public static final String CMD_JOINED = "JOINED";
    
    private String hostName;
    private int portNumber;
    private Socket sock;
    private String id;
    private boolean isConnected;
    
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
                final String cmd = nextCommand;
                // process the command in Platform.runLater to satisfy
                // multithread requirements for JavaFX programs
                Platform.runLater(()->{
                    try (Scanner reader = new Scanner(cmd)) {
                        // every message starts with the clientId of the sender
                        String id = reader.next();
                        String com = reader.next();
                        // if the clientId is followed by "ID" then this client is being assigned an id
                        if (com.equals("ID")) {
                            // when the id is set, this client will also broadcast a message to
                            // the other clients saying it just joined. This gives them a chance to
                            // tell this client anything it needs to know when it first joins,
                            // such as what other game objects already exist
                            setId(id);
                        } else {
                            // in all other cases, call the doCommand method so subclasses
                            // can decide how to process the command
                            doCommand(cmd);
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                });
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
        onIdAssigned(id);
        broadcastMessage(CMD_JOINED);
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
        onDisconnected();
    }
    
    /**
     * This method is called when this client is disconnected from the server.
     * Subclasses should override this method to take actions after the client
     * has been disconnected.
     */
    public void onDisconnected() {
        // do what needs to be done after this client is disconnected.
    }
    
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
    public abstract void doCommand(String command);
    
    /**
     * Do what needs to be done when this client is assigned an ID by the server.
     * @param clientId the id assigned to this client by the server
     */
    public abstract void onIdAssigned(String clientId);
    
}
