import com.tinocs.mp.client.*;
import com.tinocs.mp.greenfoot.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

public class FlagDefenseEventHandler extends GreenfootEventHandler {
    
    private static final String CMD_USERNAME = "USER_NAME";
    private static final String CMD_OTHER_USERNAME = "OTHER_USERNAME";
    
    public FlagDefenseEventHandler(MPWorld world){
        super(world);
    }
    
    @Override
    public void handleCommand(String command, Client client) {
        super.handleCommand(command, client);
        if (client instanceof FlagDefenseClient) {
            FlagDefenseClient fdc = (FlagDefenseClient)client;
            Scanner scan = new Scanner(command);
            String senderId = scan.next();
            if (scan.hasNext()) {
                String cmd = scan.next();
                if (cmd.equals(CMD_USERNAME)) {
                    String name = scan.nextLine().trim();
                    fdc.setUserName(name, senderId);
                    fdc.sendMessage(CMD_OTHER_USERNAME + " " + fdc.getUserName(), senderId);
                } else if (cmd.equals(CMD_OTHER_USERNAME)) {
                    String name = scan.nextLine().trim();
                    fdc.setUserName(name, senderId);
                }
            }
        }
    }
}
