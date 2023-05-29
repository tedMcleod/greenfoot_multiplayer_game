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
        updateUserLabels();
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
            label.setSize(60);
            int y = lastBottomEdgeY + margin + label.getImage().getHeight() / 2;
            addObject(label, margin + label.getImage().getWidth() / 2, y);
            lastBottomEdgeY = y + label.getImage().getHeight() / 2;
        }
    }
}
