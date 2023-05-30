import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Shot extends LocalActor {
    
    public Shot(String clientId) {
        super(clientId);
        setOtherClass(OtherShot.class);
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
