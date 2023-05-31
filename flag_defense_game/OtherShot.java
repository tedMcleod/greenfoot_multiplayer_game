import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class OtherShot extends GameActor {
    
    public OtherShot(String id, String clientId) {
        super(id, clientId);
        scaleToFitSize(Shot.SIZE);
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
