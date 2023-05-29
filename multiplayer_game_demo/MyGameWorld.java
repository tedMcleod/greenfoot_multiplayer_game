import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MyGameWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyGameWorld extends GameWorld {

    public MyGameWorld() {
        super(600, 400, 1, false);
        setPaintOrder(Player.class, OtherPlayer.class, Shot.class, OtherShot.class);
    }
    
    @Override
    public void started() {
        GameClient client = getClient();
        if (client == null || !client.isConnected()) {
            Greenfoot.setWorld(new TitleWorld());
        }
    }
}
