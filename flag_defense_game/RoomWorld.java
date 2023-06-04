import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Arrays;

/**
 * Write a description of class RoomWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoomWorld extends GameWorld {
    
    private StartGameButton startBtn;
    private String roomId;
    private boolean shouldLeave = false;
    private boolean shouldStartGame = false;
    
    public RoomWorld(String roomId, String roomName) {
        super(600, 400, 1, false);
        this.roomId = roomId;
        Text roomNameLabel = new Text(roomName);
        
        roomNameLabel.setSize(60);
        int rnlw = roomNameLabel.getImage().getWidth();
        int rnlh = roomNameLabel.getImage().getHeight();
        addObject(roomNameLabel, getWidth() / 2, rnlh / 2);
        
        LeaveRoomButton leaveBtn = new LeaveRoomButton();
        int lbw = leaveBtn.getImage().getWidth();
        int lbh = leaveBtn.getImage().getHeight();
        addObject(leaveBtn, getWidth() - lbw / 2, getHeight() - lbh / 2);
    }
    
    public void showStartButton() {
        if (startBtn == null) {
            startBtn = new StartGameButton();
            int sbw = startBtn.getImage().getWidth();
            int sbh = startBtn.getImage().getHeight();
            addObject(startBtn, getWidth() / 2, getHeight() - sbh / 2);
        }
    }
    
    public void updateUserLabels() {
        removeObjects(getObjects(UserLabel.class));
        int lastBottomEdgeY = 0;
        int margin = 5;
        Set<String> memberSet = getClient().getClientsInRoom(roomId);
        String[] memberIds = memberSet.toArray(new String[memberSet.size()]);
        Arrays.sort(memberIds);
        DemoRoomsClient drc = (DemoRoomsClient)getClient();
        for (String memberId : memberIds) {
            String name = drc.getUserName(memberId);
            if (name == null) name = "unknown";
            UserLabel label = new UserLabel(memberId, name);
            label.setSize(30);
            int y = lastBottomEdgeY + margin + label.getImage().getHeight() / 2;
            addObject(label, margin + label.getImage().getWidth() / 2, y);
            lastBottomEdgeY = y + label.getImage().getHeight() / 2;
        }
    }
    
    private void startGame() {
        if (Debug.DEBUG) System.out.println("Starting game in Room World");;
        BattleMapWorld bw = new BattleMapWorld(1);
        getClient().setEventHandler(new BattleMapEventHandler(bw));
        bw.setClient(getClient());
        Greenfoot.setWorld(bw);
    }
    
    public void shouldStartGame() {
        shouldStartGame = true;
    }
    
    private void leaveRoom() {
        LobbyWorld lw = new LobbyWorld();
        getClient().setEventHandler(new LobbyEventHandler(lw));
        lw.setClient(getClient());
        Greenfoot.setWorld(lw);
    }
    
    public void shouldLeave() {
        shouldLeave = true;
    }
    
    
    @Override
    public void act() {
        updateUserLabels();
        String ownerId = getClient().getRoomOwner(roomId);
        if (ownerId != null && ownerId.equals(getClient().getId())) {
            showStartButton();
        }
        if (shouldStartGame) startGame();
        else if (shouldLeave) leaveRoom();
    }
}
