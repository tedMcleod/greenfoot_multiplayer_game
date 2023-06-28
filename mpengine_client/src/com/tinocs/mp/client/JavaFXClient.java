package com.tinocs.mp.client;

import javafx.application.Platform;

/**
 * <p>To simplify the process of making multiplayer javafx games, this class overrides the
 * {@link com.tinocs.mp.client.Client#processCommand(String)} method to ensure each command
 * is executed on the JavaFX Application Thread.</p>
 * <p>Reasoning: Doing certain operations on other threads will cause javafx to throw an exception,
 * so extending this class instead of Client will resolve that issue. This class makes it
 * easy by simply running all commands on the JavaFX Application Thread, but that does remove some
 * advantages of multiple threads. If performance is a concern, you could choose to extend Client and
 * decide on a case by case basis whether you need process the command on the javafx thread or not.
 * Creation of JavaFX Scene and Stage objects as well as modification of scene graph operations to live
 * objects (those objects already attached to a scene) must be done on the JavaFX application thread.
 * See {@link javafx.application.Platform#runLater(Runnable)} and {@link javafx.application.Application}
 * for more information.</p>
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

	/**
	 * process the command on the JavaFX Application Thread.
	 */
	@Override
	void processCommand(String cmd) {
		Platform.runLater(() -> {
			super.processCommand(cmd);
		});
	}
}
