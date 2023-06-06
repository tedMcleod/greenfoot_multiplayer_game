import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

/**
 * Write a description of class DemoRoomsClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DemoRoomsClient extends GameClient {
    
    private static final String CMD_USERNAME = "USER_NAME";
    private static final String CMD_OTHER_USERNAME = "OTHER_USERNAME";
    private ConcurrentHashMap<String, String> userNames = new ConcurrentHashMap<>();
    private String userName;
    
    public DemoRoomsClient (String hostName, int portNumber, String userName) {
        super(hostName, portNumber);
        this.userName = userName;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getUserName(String clientId) {
        return userNames.get(clientId);
    }
    
    @Override
    protected void setId(String id, String state) {
        userNames.put(id, userName);
        super.setId(id, state);
        broadcastMessage(CMD_USERNAME + " " + userName);
    }
    
    @Override
    protected void processCommand(String command) {
        if (Debug.DEBUG) System.out.println("DemoClient processing: " + command);
        Scanner scan = new Scanner(command);
        String senderId = scan.next();
        if (scan.hasNext()) {
            String cmd = scan.next();
            if (cmd.equals(CMD_USERNAME)) {
                String name = scan.nextLine().trim();
                userNames.put(senderId, name);
                sendMessage(CMD_OTHER_USERNAME + " " + userName, senderId);
            } else if (cmd.equals(CMD_OTHER_USERNAME)) {
                String name = scan.nextLine().trim();
                userNames.put(senderId, name);
            }
        }
        super.processCommand(command);
    }
}
