import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.UUID;

/**
 * A LocalActor is an actor controlled by this client. Every LocalActor has a
 * corresponding GameActor that will be created on other clients and will mirror
 * all the actions of this local actor. The client that has the LocalActor is
 * responsible for telling other clients when to move, rotate, remove...etc the
 * corresponding GameActor representing this LocalActor in their world.
 * 
 * Note that currently using the setImage(GreenfootImage img) method will break the relationship
 * because there is no system for telling the corresponding GameActor to change its image to
 * an image that isn't in a file. This could be circumvented by making a custom function in the LocalActor
 * that draws the custom image and then sends a message to the other clients to call a custom method on the
 * corresponding actors that draws the same image.
 * 
 * @author Ted McLeod 
 * @version 5/7/2023
 */
public abstract class LocalActor extends GameActor {
    
    // the class used to show this actor on other client
    private Class<? extends GameActor> otherClass;
    
    public LocalActor(String clientId) {
        super(UUID.randomUUID().toString(), clientId);
        // replace default GreenfootImage with a LocalImage
        setImage(new LocalImage(getImage(), this));
    }
    
    public void setOtherClass(Class<? extends GameActor> otherClass) {
        this.otherClass = otherClass;
    }
    
    public Class<? extends GameActor> getOtherClass() {
        return otherClass;
    }
    
    @Override
    public void addedToWorld(World world) {
        broadcastMessage(GreenfootEventHandler.CMD_ADD + " " + otherClass.getName() + " " + getActorId() + " " + getX() + " " + getY());
    }
    
    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        broadcastMessage(GreenfootEventHandler.CMD_MOVE + " " + getActorId() + " " + getX() + " " + getY());
    }
    
    @Override
    public void setRotation(int rotation) {
        super.setRotation(rotation);
        broadcastMessage(GreenfootEventHandler.CMD_ROTATE + " " + getActorId() + " " + getRotation());
    }
    
    @Override
    public void setImage(String fileName) {
        super.setImage(new LocalImage(fileName, this));
        broadcastMessage(GreenfootEventHandler.CMD_IMAGE + " " + getActorId() + " " + fileName);
    }
    
}
