import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class StartGameButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StartGameButton extends Button {

    public StartGameButton() {
        super("Start");
        setSize(40);
        setForeground(Color.GREEN);
        setBackground(Color.RED);
    }

    @Override
    public void onClick() {
        RoomWorld rw = getWorldOfType(RoomWorld.class);
        if (rw != null) {
            rw.getClient().closeRoom(rw.getClient().getCurrentRoomId());
            rw.getClient().broadcastMessageToRoom("START_GAME", rw.getClient().getCurrentRoomId());
            rw.shouldStartGame();
        }
    }
    
}
