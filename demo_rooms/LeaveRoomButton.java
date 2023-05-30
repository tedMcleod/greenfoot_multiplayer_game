import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class LeaveRoomButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LeaveRoomButton extends Button {
    
    private RoomInfo room;

    public LeaveRoomButton(RoomInfo room) {
        super("Leave");
        setSize(40);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        this.room = room;
    }

    @Override
    public void onClick() {
        GameWorld gw = getWorldOfType(GameWorld.class);
        if (gw != null) {
            gw.getClient().broadcastMessage("LEAVE_ROOM " + room.getId());
        }
    }
}
