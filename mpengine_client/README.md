# mpengine Client

The mpengine client, paired with the mpengine server form a system for creating multiplayer games
in Java. The mpengine javafx client makes it easier to make multiplayer games in javafx. There are
two libraries, but you will generally only use one of them:

1. mpengine_client: Use this to make networked applications if you don't plan on using the javafx engine.
   Even if you are using javafx, you can use this to make like a chat program rather than a multiplayer game.
2. mpengine_javafx_client: Use this if you are making a javafx game. The JavaFXClient ensures that all commands
   are executed on the javafx thread because javafx will throw an exception if the scene is changed in another
   thread. This also comes with a game engine similar to Greenfoot but with World being a subclass of a javafx Pane
   and Actor being a subclass of a javafx ImageView. It also has classes to represent actors whose actions that will
   automatically be mirrored on all other clients which can make it simpler to implement the most common features in a
   multiplayer game.

## Console Chat Example

Here is an example of a simple chat program that broadcasts anything a client types in the console
and prints chat messages that were broadcast from other clients. Before running this program,
you should first run the mpengine_server so there will be a server to connect to. This program
as written assumes you are running the server on the same computer. If you have the server running
on a different computer, simply change "localhost" to the ip address of that computer. The server
prints its own ip address and the port it is listening to, so just look at the console on the server.
You can run this program on the same computer multiple times or on different computers to see the messages
showing up on all the clients.

```java
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
```

## Building Jars

To build a jar, simply double click (open) the appropriate jar description file in eclipse:

build_mpengine_client.jardesc OR build_mpengine_javafx_client.jardesc

You can change the filename to update the version number as desired.