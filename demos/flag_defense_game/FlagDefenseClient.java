import com.tinocs.mp.client.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

/**
 * Write a description of class DemoRoomsClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FlagDefenseClient extends Client {

    private static final String CMD_USERNAME = "USER_NAME";
    private static final String CMD_OTHER_USERNAME = "OTHER_USERNAME";
    private ConcurrentHashMap<String, String> userNames = new ConcurrentHashMap<>();
    private String userName;

    public FlagDefenseClient (String hostName, int portNumber, String userName) {
        super(hostName, portNumber);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        if (getId() != null) userNames.put(getId(), userName);
    }

    public void setUserName(String userName, String id) {
        userNames.put(id, userName);
        if (getId() != null && id.equals(getId())) this.userName = userName;
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
}
