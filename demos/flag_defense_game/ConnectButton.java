import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.tinocs.mp.greenfoot.*;

/**
 * Write a description of class StartButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ConnectButton extends Button {
    
    public ConnectButton() {
        super("Connect");
        setSize(20);
        setForeground(Color.RED);
        setBackground(Color.GREEN);
    }
    
    @Override
    public synchronized void onClick() {
        TitleWorld tw = (TitleWorld)getWorld();
        TitleWorld.lastAddress = tw.getAddress();
        TitleWorld.lastPort = "" + tw.getPort();
        TitleWorld.lastName = tw.getName();
        MPWorld gw = new LobbyWorld();
        FlagDefenseClient client = new FlagDefenseClient(tw.getAddress(), tw.getPort(), tw.getName());
        client.setDebug(true);
        client.setEventHandler(new LobbyEventHandler(gw));
        gw.setClient(client);
        client.start();
        Greenfoot.setWorld(gw);
    }
}
