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
    public void onClick() {
        TitleWorld tw = (TitleWorld)getWorld();
        TitleWorld.lastAddress = tw.getAddress();
        TitleWorld.lastPort = "" + tw.getPort();
        TitleWorld.lastName = tw.getName();
        GameWorld gw = new LobbyWorld();
        gw.setClient(new DemoRoomsClient(tw.getAddress(), tw.getPort(), tw.getName()));
        gw.getClient().setEventHandler(new LobbyEventHandler(gw));
        gw.getClient().start();
        Greenfoot.setWorld(gw);
    }
}
