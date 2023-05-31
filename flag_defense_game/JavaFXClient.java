import javafx.application.Platform;

/**
 * Write a description of class JavaFXClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class JavaFXClient extends GameClient {
    
    public JavaFXClient (String hostName, int portNumber) {
        super(hostName, portNumber);
    }
    
    // process the command in Platform.runLater to satisfy
    // multithread requirements for JavaFX programs
    protected void processCommand(String cmd) {
        Platform.runLater(()->{
            super.processCommand(cmd);
        });
    }
        
}
