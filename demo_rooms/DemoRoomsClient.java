/**
 * Write a description of class DemoRoomsClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DemoRoomsClient extends JavaFXClient {
    
    private String userName;
    
    public DemoRoomsClient (String hostName, int portNumber, String userName) {
        super(hostName, portNumber);
        this.userName = userName;
    }
    
    public String getUserName() {
        return userName;
    }
}
