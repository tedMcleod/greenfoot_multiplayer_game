import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.tinocs.mp.client.*;
import com.tinocs.mp.greenfoot.*;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Write a description of class LobbyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LobbyWorld extends MPWorld {
    
    private String idOfRoomToJoin = null;

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

    public void updateRooms() {
        //removeObjects(getObjects(JoinRoomButton.class));
        Set<String> roomSet = getClient().getRoomIds();
        List<JoinRoomButton> buttons = getObjects(JoinRoomButton.class);
        ArrayList<JoinRoomButton> buttonsToKeep = new ArrayList<>();
        for (JoinRoomButton btn : buttons) {
            if (!roomSet.contains(btn.getRoomId())) removeObject(btn);
        }
        for (String roomId : roomSet) {
            if (getJoinRoomButton(roomId) == null) addRoom(roomId);
        }
        buttons = new ArrayList<>(getObjects(JoinRoomButton.class));
        Collections.sort(buttons, (a, b) -> a.getRoomId().compareTo(b.getRoomId()));
        if (buttons.size() > 0) {
            int margin = 5;
            int y = margin + buttons.get(0).getImage().getHeight() / 2;
            int x = getWidth() / 2;
            
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).setLocation(x, y);
                if (i + 1 < buttons.size()) {
                    Actor nextBtn = buttons.get(i + 1);
                    y += buttons.get(i).getImage().getHeight() / 2 + margin + nextBtn.getImage().getHeight() / 2;
                }
            }
        }
    }

    public JoinRoomButton getJoinRoomButton(String roomId) {
        for (JoinRoomButton btn : getObjects(JoinRoomButton.class)) {
            if (btn.getRoomId().equals(roomId)) return btn;
        }
        return null;
    }

    private void addRoom(String roomId) {
        JoinRoomButton btn = new JoinRoomButton(roomId, getClient().getRoomName(roomId));
        List<JoinRoomButton> buttons = getObjects(JoinRoomButton.class);
        int bh = btn.getImage().getHeight();
        int bw = btn.getImage().getWidth();
        int x = getWidth() / 2;
        int y = bh / 2 + buttons.size() * bh;
        addObject(btn, x, y);
    }
    
    public void setIdOfRoomToJoin(String roomId) {
        idOfRoomToJoin = roomId;
    }

    public void joinRoom() {
        if (idOfRoomToJoin != null) {
            String roomName = getClient().getRoomName(idOfRoomToJoin);
            if (roomName != null) {
                RoomWorld rw = new RoomWorld(idOfRoomToJoin, roomName);
                getClient().setEventHandler(new RoomEventHandler(rw, idOfRoomToJoin));
                rw.setClient(getClient());
                Greenfoot.setWorld(rw);
            }
            
        }
    }
    
    @Override
    public void act() {
        updateRooms();
        if (idOfRoomToJoin != null) joinRoom();
    }
}
