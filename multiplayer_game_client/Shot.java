import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.UUID;

public class Shot extends LocalActor {
    
    public Shot() {
        super(UUID.randomUUID().toString());
    }
    
    @Override
    public void addedToWorld(World world) {
        if (getWorldOfType(GameWorld.class).getClient() != null) {
            getWorldOfType(GameWorld.class).getClient().sendMessage("ADDOS " + getId() + " " + getX() + " " + getY());
        }
    }
    
    public void act() {
        move(5);
        OtherPlayer op = (OtherPlayer)getOneIntersectingObject(OtherPlayer.class);
        int w = getImage().getWidth();
        int h = getImage().getHeight();
        int ww = getWorld().getWidth();
        int wh = getWorld().getHeight();
        if (op != null) {
            destroy();
            op.destroy();
        } else {
            int x = getX();
            int y = getY();
            if (x - w / 2 > ww || x + w / 2 < 0 || y - h / 2 > wh || y + h / 2 < 0) {
                destroy();
            }
        }
    }
}
