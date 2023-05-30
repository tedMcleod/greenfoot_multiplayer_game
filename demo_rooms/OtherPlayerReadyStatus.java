import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class OtherPlayerReadyStatus here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OtherPlayerReadyStatus extends Text {
    
    private String clientId;
    
    public OtherPlayerReadyStatus(String clientId) {
        super("Not Ready");
        setSize(40);
        setForeground(Color.GREEN);
        setBackground(Color.RED);
        this.clientId = clientId;
    }
    
    @Override
    public void addedToWorld(World w) {
        if (w instanceof BattleMapWorld) {
            BattleMapWorld bw = (BattleMapWorld)w;
            setText(bw.getClientName(clientId));
        }
    }
    
    @Override
    public void act() {
        BattleMapWorld bw = getWorldOfType(BattleMapWorld.class);
        if (bw != null) {
            if (bw.isPlayerReady(clientId)) {
                setBackground(new Color(50, 25, 120));
                setForeground(new Color(100, 20, 0));
            } else {
                setForeground(Color.GREEN);
                setBackground(Color.RED);
            }
        }
    }
    
    
}
