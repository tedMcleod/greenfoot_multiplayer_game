import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player extends LocalActor {
    
    private int shotCooldown = 0;
    
    public Player(String clientId) {
        super(clientId, clientId);
    }
    
    public void act() {
        if (shotCooldown > 0) shotCooldown--;
        if (Greenfoot.isKeyDown("down")) {
            move(-1);
        } else if (Greenfoot.isKeyDown("up")) {
            move(1);
        }
        
        if (Greenfoot.isKeyDown("left")) {
            turn(-1);
        } else if (Greenfoot.isKeyDown("right")) {
            turn(1);
        }
        
        if (Greenfoot.isKeyDown("space") && shotCooldown == 0) {
            shotCooldown = 20;
            fireShot();
        }
    }
    
    public void fireShot() {
        Shot shot = new Shot(getClientId());
        getWorld().addObject(shot, getX(), getY());
        shot.setRotation(getRotation());
    }
    
    @Override
    public void onDestroy(GameWorld world) {
        Greenfoot.stop();
        Greenfoot.setWorld(new TitleWorld());
        Greenfoot.start();
    }
}
