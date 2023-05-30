import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

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
        GameWorld gw = new LobbyWorld();
        DemoRoomsClient client = new DemoRoomsClient(tw.getAddress(), tw.getPort(), tw.getName());
        client.start();
        client.setEventHandler(new LobbyEventHandler(gw));
        gw.setClient(client);
        Greenfoot.setWorld(gw);
    }
}
