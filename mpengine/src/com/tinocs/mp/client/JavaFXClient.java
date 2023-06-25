package com.tinocs.mp.client;

import javafx.application.Platform;

/**
 * To simplify the process of making multiplayer javafx games, this class overrides the
 * processCommand method to ensure each command is executed on the javafx thread.
 * Doing certain operations on other threads will cause javafx to throw an exception,
 * so extending this class instead of GameClient will resolve that issue.
 * If performance is a concern, you could choose to extend GameClient and handle any
 * multithread issues yourself on a case by case basis (see Platform.runLater())
 * @author Ted_McLeod
 *
 */
public class JavaFXClient extends Client {

	/**
     * Initialize a JavaFXClient to connect to the given hostName and portNumber.
     * @param hostName the host name
     * @param portNumber the port number
     */
	public JavaFXClient(String hostName, int portNumber) {
		super(hostName, portNumber);
	}

	@Override
	void processCommand(String cmd) {
		Platform.runLater(() -> {
			super.processCommand(cmd);
		});
	}
}
