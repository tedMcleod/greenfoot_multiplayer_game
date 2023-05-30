package mpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Each client that connects to the server is given its own thread that runs the code in run().
 * @author Ted_McLeod
 *
 */
public class ClientThread implements Runnable {
    private Socket csocket;
    private MultiThreadServer server;
    private String id;

    public ClientThread(Socket csocket, MultiThreadServer server, String id) {
        this.csocket = csocket;
        this.server = server;
        this.id = id;
    }

    /**
     * The code run by each client thread. It listens for messages and sends the messages on to other clients as described
     * in the ServerDriver API.
     */
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            String inputLine = in.readLine();
            while (inputLine != null) {
                final String cmd = inputLine;
                try (Scanner reader = new Scanner(cmd)) {
                    String first = reader.next();
                    if (first.equals("TO")) {
                        String toId = reader.next();
                        String restOfCmd = reader.nextLine();
                        server.sendMessage(id + restOfCmd, toId);
                    } else if (first.equals("DC")) {
                        break;
                    } else if (first.equals("TO_ROOM")) {
                        String roomId = reader.next();
                        String restOfCmd = reader.nextLine();
                        server.roomBroadcast(id + restOfCmd, roomId, id);
                    } else if (first.equals("ADD_ROOM")) {
                        String roomName = reader.next();
                        int roomCapacity = reader.nextInt();
                        if (!server.addRoom(roomName, roomCapacity)) {
                            server.sendMessage("ADD_ROOM_FAILED " + roomName + " " + roomCapacity, id); 
                        }
                    } else if (first.equals("REMOVE_ROOM")) {
                        String roomId = reader.next();
                        server.removeRoom(roomId);
                    } else if (first.equals("JOIN_ROOM")) {
                        String roomId = reader.next();
                        server.joinRoom(id, roomId);
                    } else if (first.equals("LEAVE_ROOM")) {
                        server.leaveRoom(id);
                    } else if (first.equals("GET_ROOMS")) {
                        server.sendRoomsInfo(id);
                    } else {
                        server.broadcast(id + " " + cmd, id);
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
                inputLine = in.readLine();
            }
            csocket.close();
        } catch (IOException e) {
            System.out.println("Client " + id + " socket close IOException: " + e);
        } finally {
            server.removeClient(id);
        }
    }
}