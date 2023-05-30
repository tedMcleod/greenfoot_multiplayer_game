import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Write a description of class RoomWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoomWorld extends GameWorld {
    
    private RoomInfo room;
    private ConcurrentHashMap<String, String> userNamesById = new ConcurrentHashMap<>();
    
    public RoomWorld(RoomInfo room) {
        super(600, 400, 1, false);
        this.room = room;
        Text roomNameLabel = new Text(room.getName());
        roomNameLabel.setSize(60);
        int rnlw = roomNameLabel.getImage().getWidth();
        int rnlh = roomNameLabel.getImage().getHeight();
        addObject(roomNameLabel, getWidth() / 2, rnlh / 2);
        
        LeaveRoomButton leaveBtn = new LeaveRoomButton(room);
        int lbw = leaveBtn.getImage().getWidth();
        int lbh = leaveBtn.getImage().getHeight();
        addObject(leaveBtn, getWidth() - lbw / 2, getHeight() - lbh / 2);
        
        StartGameButton startBtn = new StartGameButton(room);
        int sbw = startBtn.getImage().getWidth();
        int sbh = startBtn.getImage().getHeight();
        addObject(startBtn, getWidth() / 2, getHeight() - sbh / 2);

        updateUserLabels();
    }
    
    public ConcurrentHashMap<String, String> getUserNamesById() {
        return new ConcurrentHashMap<>(userNamesById);
    }
    
    public RoomInfo getRoom() {
        return room;
    }
    
    public void setUserName(String clientId, String name) {
        userNamesById.put(clientId, name);
        updateUserLabels();
    }
    
    public void updateUserLabels() {
        System.out.println("Updating labels for room " + room);
        System.out.println("usernames: " + userNamesById);
        removeObjects(getObjects(UserLabel.class));
        int lastBottomEdgeY = 0;
        int margin = 5;
        for (String clientId : room.members()) {
            String name = userNamesById.get(clientId);
            if (name == null) name = "unknown";
            UserLabel label = new UserLabel(clientId, name);
            label.setSize(30);
            int y = lastBottomEdgeY + margin + label.getImage().getHeight() / 2;
            addObject(label, margin + label.getImage().getWidth() / 2, y);
            lastBottomEdgeY = y + label.getImage().getHeight() / 2;
        }
    }
}
