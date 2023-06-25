import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.tinocs.mp.greenfoot.*;

public class Shot extends LocalActor {
    
    public static final int SIZE = 12;
    
    public Shot(String clientId) {
        super(clientId);
        setOtherClass(OtherShot.class);
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
            destroy();
            op.onHit();
        } else {
            int x = getX();
            int y = getY();
            if (x - w / 2 > ww || x + w / 2 < 0 || y - h / 2 > wh || y + h / 2 < 0) {
                destroy();
            } else {
                Wall wall = (Wall) getOneIntersectingObject(Wall.class);
                if (wall != null) {
                   destroy();
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
