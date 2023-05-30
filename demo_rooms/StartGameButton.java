import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class StartGameButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StartGameButton extends Button {
    
    private RoomInfo room;

    public StartGameButton(RoomInfo room) {
        super("Start");
        setSize(40);
        setForeground(Color.GREEN);
        setBackground(Color.RED);
        this.room = room;
    }

    @Override
    public void onClick() {
        GameWorld gw = getWorldOfType(GameWorld.class);
        if (gw != null) {
            gw.getClient().broadcastMessageToRoom("START_GAME", room.getId());
        }
    }
    
}
