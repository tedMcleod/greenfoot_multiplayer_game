package mp_demo_game;

import com.tinocs.mp.client.Client;
import com.tinocs.mp.client.JavaFXClient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DemoGame extends Application {
	
	Client client = new JavaFXClient("localhost", 1234);

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane root = new BorderPane();
		DemoWorld dw = new DemoWorld();
		dw.setClient(client);
		client.setEventHandler(new DemoEngineEventHandler(dw));
		root.setCenter(dw);
		stage.setScene(new Scene(root));
		stage.show();
		client.start();
	}
	
	@Override
	public void stop() {
		// exits the program completely and stops the client thread
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}
