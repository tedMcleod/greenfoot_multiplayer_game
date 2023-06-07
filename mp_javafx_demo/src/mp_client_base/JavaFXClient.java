package mp_client_base;

import javafx.application.Platform;

public class JavaFXClient extends GameClient {

	public JavaFXClient(String hostName, int portNumber) {
		super(hostName, portNumber);
	}

	@Override
	protected void processCommand(String cmd) {
		Platform.runLater(() -> {
			super.processCommand(cmd);
		});
	}
}
