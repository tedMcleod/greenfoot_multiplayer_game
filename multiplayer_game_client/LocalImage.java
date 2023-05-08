import greenfoot.GreenfootImage;

/**
 * A LocalImage is a GreenfootImage that is used as the image of a LocalActor and informs other clients
 * to update the images of the corresponding actors in their worlds to reflect changes made to this image.
 * 
 * @author Ted McLeod 
 * @version 5/8/2023
 */
public class LocalImage extends GreenfootImage {
    
    private LocalActor actor;
    
    public LocalImage(GreenfootImage img, LocalActor actor) {
        super(img);
        this.actor = actor;
    }
    
    public LocalImage(String fileName, LocalActor actor) {
        super(fileName);
        this.actor = actor;
    }
    
    public LocalActor getActor() {
        return actor;
    }
    
    @Override
    public void setTransparency(int t) {
        super.setTransparency(t);
        if (actor.getWorldOfType(GameWorld.class).getClient() != null) {
            actor.getWorldOfType(GameWorld.class).getClient().broadcastMessage(GreenfootClient.CMD_TRANSPARENCY + " " + actor.getActorId() + " " + t);
        }
    }
    
    @Override
    public void mirrorHorizontally() {
        super.mirrorHorizontally();
        if (actor.getWorldOfType(GameWorld.class).getClient() != null) {
            actor.getWorldOfType(GameWorld.class).getClient().broadcastMessage(GreenfootClient.CMD_MIRROR_H + " " + actor.getActorId());
        }
    }
    
    @Override
    public void mirrorVertically() {
        super.mirrorVertically();
        if (actor.getWorldOfType(GameWorld.class).getClient() != null) {
            actor.getWorldOfType(GameWorld.class).getClient().broadcastMessage(GreenfootClient.CMD_MIRROR_V + " " + actor.getActorId());
        }
    }
    
    @Override
    public void scale(int width, int height) {
        super.scale(width, height);
        if (actor.getWorldOfType(GameWorld.class).getClient() != null) {
            actor.getWorldOfType(GameWorld.class).getClient().broadcastMessage(GreenfootClient.CMD_SCALE + " " + actor.getActorId() + " " + width + " " + height);
        }
    }
}
