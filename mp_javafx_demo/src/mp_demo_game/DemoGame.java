package mp_demo_game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mp_client_base.GameClient;
import mp_client_base.JavaFXClient;
import mp_engine.EngineEventHandler;

public class DemoGame extends Application {
	
	GameClient client = new JavaFXClient("localhost", 1234);

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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
}
