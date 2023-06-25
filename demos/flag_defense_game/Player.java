import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.tinocs.mp.greenfoot.*;

/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player extends LocalActor {

    public static final int SIZE = 24;
    private int shotCooldown = 0;
    private int shotDelay = 50;
    private int boosterCooldown = 0;
    private int boosterDelay = 200;
    private int speedCooldown = 0;
    private int speedDuration = 50;
    
    private int normalSpeed = 1;
    private int boostedSpeed = 3;
    private int speed = normalSpeed;
    
    private int flagTime = 0;
    private final int TIME_TO_WIN = 2000;

    public Player(String clientId) {
        super(clientId);
        setOtherClass(OtherPlayer.class);
        scaleToFitSize(SIZE);
    }

    public void act() {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        if (bw.isStarted() && bw.getWinningPlayer() == null) {
            if (shotCooldown > 0) shotCooldown--;
            if (boosterCooldown > 0) boosterCooldown--;
            if (speedCooldown > 0) speedCooldown--;
            if (speedCooldown == 0) speed = normalSpeed;
            handleUserInput();
            bw.updateGameHud();
        }
    }

    public void handleUserInput() {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        int ww = bw.getWidth();
        int wh = bw.getHeight();
        int pw = getImage().getWidth();
        int ph = getImage().getHeight();
        int x = getX();
        int y = getY();
        int rot = getRotation();
        if (Greenfoot.isKeyDown("x") && boosterCooldown == 0) {
            boosterCooldown = boosterDelay;
            speedCooldown = speedDuration;
            speed = boostedSpeed;
        }
        if (Greenfoot.isKeyDown("down")) {
            move(-speed);
        } else if (Greenfoot.isKeyDown("up")) {
            move(speed);
        }
        
        if (isTouching(Wall.class) || getX() - pw / 2 < 0 || getY() - ph / 2 < 0 || getX() + pw / 2 > ww || getY() + ph / 2 > wh - BattleMapWorld.HUD_HEIGHT) {
            setLocation(x, y);
        }

        if (Greenfoot.isKeyDown("left")) {
            turn(-1);
        } else if (Greenfoot.isKeyDown("right")) {
            turn(1);
        }
        
        if (isTouching(Wall.class)) {
            setRotation(rot);
        }
            
        if (Greenfoot.isKeyDown("space") && shotCooldown == 0) {
            shotCooldown = shotDelay;
            fireShot();
        }
        
    }

    public void fireShot() {
        Shot shot = new Shot(getClientId());
        getWorld().addObject(shot, getX(), getY());
        shot.setRotation(getRotation());
    }
    
    public int getBoosterCooldown() {
        return boosterCooldown;
    }
    
    public int getSpeedCooldown() {
        return speedCooldown;
    }
    
    public int getShotCooldown() {
        return shotCooldown;
    }
    
    public int getBoosterDelay() {
        return boosterDelay;
    }
    
    public int getSpeedDuration() {
        return speedDuration;
    }
    
    public int getShotDelay() {
        return shotDelay;
    }

    public int getFlagTime() {
        return flagTime;
    }
    
    public void win() {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        bw.setPlayerWon(bw.getClient().getId());
        bw.updateGameHud();
        broadcastMessage("WIN");
    }
    
    public void incrementFlagTime() {
        flagTime++;
        if (flagTime == TIME_TO_WIN) {
            win();
        }
    }
    
    public void onHit() {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        Actor startLoc = bw.getStartLoc(getClientId());
        setLocation(startLoc.getX(), startLoc.getY());
        turnTowards(bw.getWidth() / 2, bw.getHeight() / 2);
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
