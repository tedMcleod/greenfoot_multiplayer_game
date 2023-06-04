import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class OtherPlayer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OtherPlayer extends GameActor {

    public OtherPlayer(String id, String clientId) {
        super(id, clientId);
        scaleToFitSize(Player.SIZE);
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

    public void onHit() {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        if (bw != null && bw.getClient() != null) {
            broadcastMessage("HIT " + getActorId());
        }
    }
}