import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class LeaveRoomButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LeaveRoomButton extends Button {

    public LeaveRoomButton() {
        super("Leave");
        setSize(40);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
    }

    @Override
    public void onClick() {
        RoomWorld rw = getWorldOfType(RoomWorld.class);
        if (rw != null) {
            rw.getClient().broadcastMessage("LEAVE_ROOM " + rw.getClient().getIdOfRoomContainingClient(rw.getClient().getId()));
        }
    }
}
