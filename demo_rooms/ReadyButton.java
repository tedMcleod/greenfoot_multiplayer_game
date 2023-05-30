import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ReadyButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ReadyButton extends Button {

    public ReadyButton() {
        super("Ready");
        setSize(40);
        setForeground(Color.GREEN);
        setBackground(Color.RED);
    }

    @Override
    public void onClick() {
        BattleMapWorld bw = getWorldOfType(BattleMapWorld.class);
        if (bw != null) {
            bw.setPlayerReady(bw.getClient().getId());
        }
    }
}
