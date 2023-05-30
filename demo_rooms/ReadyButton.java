import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ReadyButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ReadyButton extends Button {

    public ReadyButton() {
        super("Not Ready");
        setSize(40);
        setForeground(Color.GREEN);
        setBackground(Color.RED);
    }

    @Override
    public synchronized void onClick() {
        BattleMapWorld bw = getWorldOfType(BattleMapWorld.class);
        if (bw != null && !bw.isPlayerReady(bw.getClient().getId())) {
            bw.setPlayerReady(bw.getClient().getId());
            setText("Ready");
            setBackground(new Color(50, 25, 120));
            setForeground(new Color(100, 20, 0));
        }
    }
}
