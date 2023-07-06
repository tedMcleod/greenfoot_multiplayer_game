import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.tinocs.mp.greenfoot.*;
import com.tinocs.mp.client.Client;

public class Shot extends LocalActor {
    
    public static final int SIZE = 12;
    
    public Shot(Client client) {
        super(client, OtherShot.class);
        setImage("beeper.png");
        scaleToFitSize(SIZE);
    }
    
    public void act() {
        move(5);
        OtherPlayer op = (OtherPlayer)getOneIntersectingObject(OtherPlayer.class);
        int w = getImage().getWidth();
        int h = getImage().getHeight();
        int ww = getWorld().getWidth();
        int wh = getWorld().getHeight();
        if (op != null) {
            getWorld().removeObject(this);
            op.onHit();
        } else {
            int x = getX();
            int y = getY();
            if (x - w / 2 > ww || x + w / 2 < 0 || y - h / 2 > wh || y + h / 2 < 0) {
                getWorld().removeObject(this);
            } else {
                Wall wall = (Wall) getOneIntersectingObject(Wall.class);
                if (wall != null) {
                   getWorld().removeObject(this);
                }
            }
        }
    }
    
    public void scaleToFitSize(int size) {
        double origW = getImage().getWidth();
        double origH = getImage().getHeight();
        double scaleFactor;
        if (origW >= origH) {
            scaleFactor = size / origW;
        } else {
            scaleFactor = size / origH;
        }
        int w = (int)(scaleFactor * origW);
        int h = (int)(scaleFactor * origH);
        getImage().scale(w, h);
    }
}
