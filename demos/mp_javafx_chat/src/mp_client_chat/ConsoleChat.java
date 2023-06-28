package mp_client_chat;

import java.util.Scanner;
import com.tinocs.mp.client.Client;
import com.tinocs.mp.client.ClientEventHandler;

public class ConsoleChat {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to Console Chat!");
		System.out.print("Enter name: ");
		String name = in.nextLine();
		System.out.println("Type messages to chat with other clients.");
		
		// Create a new client to listen to localhost port 1234
		// if the server is on another computer, replace localhost
		// with the ip address of the server
		Client client = new Client("localhost", 1234);
		
		// Create a subclass of ClientEventHandler that listens for
		// chat messages and prints them to the console
		client.setEventHandler(new ClientEventHandler() {
			
		@Override
		public void handleCommand(String command, Client client) {
			Scanner reader = new Scanner(command);
			reader.next(); // sender's client id (not needed in this case)
			String cmd = reader.next();
			if (cmd.equals("CHAT")) {
				// The rest of the command after the next space is the message
				System.out.println(reader.nextLine().substring(1));
			}
			reader.close();
		}
		});
		
		// Tell the client to connect to the server and start listening for commands
		client.start();
		
		// broadcast whatever the user types to the other clients
		while(true) {
			client.broadcastMessage("CHAT " + name + ": " + in.nextLine());
		}
	}
}
