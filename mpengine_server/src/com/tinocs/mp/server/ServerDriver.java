package com.tinocs.mp.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * <ul>
 * 		<li>Starts a server in port 1234 or on the port passed in the first argument when running the main method</li>
 *  	<li>Clients can send messages to talk to other clients. Generally, the messages are made up tokens separated by spaces.</li>
 *  	<li>For more information, see the GameClient class</li>
 * </ul>
 *
 * @author Ted_McLeod
*/
public class ServerDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port = 1234;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException err) {
                System.err.println("port must be an integer");
                err.printStackTrace();
            }
        }
        ServerSocket ssock = new ServerSocket(port);
        MultiThreadServer serve = new MultiThreadServer(ssock);
        new Thread(serve).start();
    }

}
