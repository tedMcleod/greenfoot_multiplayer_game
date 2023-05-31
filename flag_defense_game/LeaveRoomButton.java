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
            System.out.println("leaving room and going to lobby");
            LobbyWorld lw = new LobbyWorld();
            System.out.println("Created LobbyWorld");
            
            rw.getClient().setEventHandler(new LobbyEventHandler(lw));
            System.out.println("setEventHandler done");
            lw.setClient(rw.getClient());
            System.out.println("setClient done");
            lw.setNeedsUpdate();
            Greenfoot.setWorld(lw);
            System.out.println("set world done");
            lw.getClient().broadcastMessage("LEAVE_ROOM " + room.getId());
            
        }
    }
}
