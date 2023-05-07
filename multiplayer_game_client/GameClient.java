import greenfoot.World;
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

public abstract class GameClient implements Runnable {
    private String hostName;
    private int portNumber;
    private Socket sock;
    private StringProperty id;
    private boolean stayConnected;
    private BooleanProperty connected;
    private World world;
    private long time;
    
    public GameClient (String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        id = new SimpleStringProperty();
        stayConnected = true;
        connected = new SimpleBooleanProperty();
        connected.set(false);
        world = null;
    }
    
    public synchronized void sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        Thread clientThread = new Thread(this);
        clientThread.start();
    }

    @Override
    public void run() {
        time = System.currentTimeMillis();
        try (
            Socket sock = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        ) {
            this.sock = sock;
            String nextCommand = in.readLine();
            connected.set(true);
            while (nextCommand != null && stayConnected && !Thread.interrupted()){
                final String cmd = nextCommand;
                Platform.runLater(()->{
                    try (Scanner reader = new Scanner(cmd)) {
                        String id = reader.next();
                        String com = reader.next();
                        if (com.equals("ID")) {
                            setId(id);
                        }
                        doCommand(cmd);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                });
                nextCommand = in.readLine();
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostName);
        } catch (IOException e) {
            System.err.println("I/O exception for connection: " +
                hostName);
        } finally {
            connected.set(false);
        }
    }

    public StringProperty getIdProperty() {
        return id;
    }
    
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public boolean isStayConnected() {
        return stayConnected;
    }

    public void setStayConnected(boolean stayConnected) {
        this.stayConnected = stayConnected;
    }

    public BooleanProperty getConnected() {
        return connected;
    }
    
    public World getWorld() {
        return world;
    }
    
    public void setWorld(World world) {
        this.world = world;
    }
    
    public abstract void doCommand(String command);
}
