import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class StartButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class JoinRoomButton extends Button {
    
    private String roomId;
    
    private static final Color FULL_COLOR = new Color(100, 100, 0);
    private static final Color OPEN_COLOR = new Color(0, 100, 0);
    private static final Color CLOSED_COLOR = new Color(100, 0, 0);
    
    
    public JoinRoomButton(String roomId, String roomName) {
        super(roomName);
        setSize(40);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        this.roomId = roomId;
    }
    
    @Override
    public void onClick() {
        LobbyWorld lw = (LobbyWorld)getWorld();
        lw.getClient().broadcastMessage("JOIN_ROOM " + roomId);
    }
    
    @Override
    public void act() {
        super.act();
        LobbyWorld lw = (LobbyWorld)getWorld();
        String name = lw.getClient().getRoomName(roomId);
        int numClientsInRoom = lw.getClient().getClientsInRoom(roomId).size();
        int capacity = lw.getClient().getRoomCapacity(roomId);
        boolean isClosed = lw.getClient().roomIsClosed(roomId);
        if (isClosed) {
            setText(name + "(" + numClientsInRoom + "/" + capacity + ") - Game In Progress");
            setBackground(CLOSED_COLOR);
        } else if (numClientsInRoom == capacity) {
            setText(name + "(Full)");
            setBackground(FULL_COLOR);
        } else {
            setText(name + "(" + numClientsInRoom + "/" + capacity + ")");
            setBackground(OPEN_COLOR);
        }
        
    }
    
    public String getRoomId() {
        return roomId;
    }
}
