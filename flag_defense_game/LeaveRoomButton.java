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
    public synchronized void onClick() {
        RoomWorld rw = getWorldOfType(RoomWorld.class);
        if (rw != null) {
            LobbyWorld lw = new LobbyWorld();
            rw.getClient().setEventHandler(new LobbyEventHandler(lw));
            lw.setClient(rw.getClient());
            lw.setNeedsUpdate();
            Greenfoot.setWorld(lw);
            lw.getClient().broadcastMessage("LEAVE_ROOM " + room.getId());
            
        }
    }
}
