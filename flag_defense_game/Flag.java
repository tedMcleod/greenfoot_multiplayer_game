import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class Flag here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Flag extends Actor {

    @Override
    public void act() {
        BattleMapWorld bw = (BattleMapWorld)getWorld();
        if (bw.isStarted() && bw.getWinningPlayer() == null) {
            List<Player> players = getObjectsInRange(100, Player.class);
            List<OtherPlayer> otherPlayers = getObjectsInRange(100, OtherPlayer.class);
            if (players.size() + otherPlayers.size() == 1) {
                setImage("red_flag.png");
                if (players.size() == 1) {
                    Player player = players.get(0);
                    player.incrementFlagTime();
                }
            } else if (players.size() + otherPlayers.size() > 1) {
                setImage("yellow_flag.png");
            } else {
                setImage("blue_flag.png");
            }
        }
    }
}
