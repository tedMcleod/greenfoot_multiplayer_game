import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class LobbyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LobbyWorld extends GameWorld {

    public LobbyWorld() {
        super(600, 400, 1, false);
        
        int margin = 5;
        
        Text roomNameLabel = new Text("Room Name");
        roomNameLabel.setSize(14);
        int rnlw = roomNameLabel.getImage().getWidth();
        int rnlh = roomNameLabel.getImage().getHeight();
        int rnlx = margin + rnlw / 2;
        addObject(roomNameLabel, rnlx, getHeight() - rnlh / 2 - margin);
        
        TextField roomNameField = new TextField(200);
        roomNameField.setSize(14);
        int rnfw = roomNameField.getImage().getWidth();
        int rnfh = roomNameField.getImage().getHeight();
        int rnfx = rnlx + rnlw / 2 + rnfw / 2;
        addObject(roomNameField, rnfx, getHeight() - rnfh / 2 - margin);
        
        CreateRoomButton createRoomBtn = new CreateRoomButton(roomNameField);
        createRoomBtn.setSize(14);
        int crbw = createRoomBtn.getImage().getWidth();
        int crbh = createRoomBtn.getImage().getHeight();
        int crbx = rnfx + rnfw / 2 + crbw / 2;
        addObject(createRoomBtn, crbx, getHeight() - crbh / 2 - margin);
    }
    
    public void addRoom(RoomInfo room) {
        JoinRoomButton btn = new JoinRoomButton(room);
        List<JoinRoomButton> buttons = getObjects(JoinRoomButton.class);
        int bh = btn.getImage().getHeight();
        int bw = btn.getImage().getWidth();
        int x = getWidth() / 2;
        int y = bh / 2 + buttons.size() * bh;
        addObject(btn, x, y);
    }
}
