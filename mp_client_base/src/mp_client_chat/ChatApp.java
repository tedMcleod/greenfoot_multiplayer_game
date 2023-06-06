package mp_client_chat;

import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import mp_client_base.ClientEventHandler;
import mp_client_base.GameClient;

public class ChatApp extends Application {
	
	GameClient client = new GameClient("localhost", 1234);

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("McChat");
		BorderPane root = new BorderPane();
		
		HBox userBox = new HBox();
		userBox.setSpacing(5);
		Label userLabel = new Label("Username:");
		userBox.getChildren().add(userLabel);
		TextField userName = new TextField("Anonymous");
		userBox.getChildren().add(userName);
		root.setTop(userBox);
		
		userName.textProperty().addListener((obj, ov, nv) -> {
			userName.setText(nv.replaceAll("[\\s]", ""));
		});
		
		TextArea chatArea = new TextArea();
		root.setCenter(chatArea);
		
		HBox chatBox = new HBox();
		chatBox.setSpacing(5);
		Label msgLabel = new Label("Message:");
		chatBox.getChildren().add(msgLabel);
		TextField messageField = new TextField();
		HBox.setHgrow(messageField, Priority.ALWAYS);
		chatBox.getChildren().add(messageField);
		root.setBottom(chatBox);
		
		messageField.setOnKeyReleased(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				client.broadcastMessage("CHAT " + userName.getText() + " " + messageField.getText());
				chatArea.appendText("\n" + userName.getText() + ": " + messageField.getText());
				messageField.setText("");
			}
		});
		
		Scene scene = new Scene(root, 600, 400);
		
		stage.setScene(scene);
		stage.show();
		
		// set up client
		client.setEventHandler(new ClientEventHandler() {
			
			@Override
			public void handleCommand(String command, GameClient client) {
				//System.out.println(command);
				Scanner reader = new Scanner(command);
				String senderId = reader.next();
				String cmd = reader.next();
				if (cmd.equals("CHAT")) {
					String name = reader.next();
					String msg = reader.nextLine();
					chatArea.appendText("\n" + name + ": " + msg);
					messageField.setText("");
				}
				
			}
		});
		client.start();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
