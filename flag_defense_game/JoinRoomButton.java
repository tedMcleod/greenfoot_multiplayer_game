import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class StartButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class JoinRoomButton extends Button {
    
    private RoomInfo room;
    
    public JoinRoomButton(RoomInfo room) {
        super(room.getName());
        setSize(40);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        this.room = room;
    }
    
    @Override
    public synchronized void onClick() {
        LobbyWorld lw = (LobbyWorld)getWorld();
        RoomWorld rw = new RoomWorld(room);
        lw.getClient().setEventHandler(new RoomEventHandler(rw, room));
        rw.setClient(lw.getClient());
        
        Greenfoot.setWorld(rw);
        rw.getClient().broadcastMessage("JOIN_ROOM " + room.getId());
    }
    
    @Override
    public void act() {
        super.act();
        String name = room.getName();
        if (room.members().size() == room.getCapacity()) {
            setText(name + "(Full)");
            setBackground(Color.GRAY);
        } else {
            setText(name + "(" + room.members().size() + ")");
            setBackground(Color.BLACK);
        }
    }
}