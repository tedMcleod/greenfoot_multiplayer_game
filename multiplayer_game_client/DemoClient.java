import greenfoot.*;

/**
 * Write a description of class DemoGameClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DemoClient extends GreenfootClient {
    
    public DemoClient(String hostName, int portNumber) {
        super(hostName, portNumber);
    }
    
    @Override
    public void onIdAssigned(String clientId) {
        GameWorld gw = (GameWorld)getWorld();
        Player myPlayer = new Player(clientId);
        int x = Greenfoot.getRandomNumber(gw.getWidth());
        int y = Greenfoot.getRandomNumber(gw.getHeight());
        gw.addObject(myPlayer, x, y);
    }
}
