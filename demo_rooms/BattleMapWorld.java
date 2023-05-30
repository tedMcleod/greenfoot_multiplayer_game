import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Write a description of class TankGameWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BattleMapWorld extends LevelWorld {

    public static final int HUD_HEIGHT = 50;
    private RoomInfo room;
    private ConcurrentHashMap<String, String> userNamesById;
    private ConcurrentHashMap<String, Boolean> readyStatus = new ConcurrentHashMap<>();
    
    // static initialization code - runs when the class is loaded, before the main method is called.
    // You can only call static methods and only access static fields.
    // In this case, we need to initialize the margins BEFORE the world is created
    static {
        // set the margins (open space) on the left, right, top and bottom sides of the world
        // The level will be drawn with the given spaces open on each side
        // Set the bottom margin to the height of your HUD
        LevelWorld.setMargins(0, 0, 0, HUD_HEIGHT);
    }
    
    // Default Constructor (loads the first txt in the levels folder)
    // public BattleMapWorld() {
        // this(1); // load level 1
    // }
    
    // loads the level given. For example, if level was 3, it would load the third txt file in the levels folder
    // You can add parameters to this constructor for lives and score. If you do, you need to pass default lives
    // and score values when you call this(1) in the default constructor.
    public BattleMapWorld(int level, RoomInfo room, ConcurrentHashMap<String, String> userNamesById) {
        super(level);
        this.room = room;
        this.userNamesById = userNamesById;
        for (String clientId : userNamesById.keySet()) {
            readyStatus.put(clientId, false);
        }
        drawHud();
    }
    
    public RoomInfo getRoom() {
        return room;
    }
    
    public ConcurrentHashMap<String, String> getUserNamesById() {
        return userNamesById;
    }
    
    @Override
    public void defineClassTypes() {
        // define which classes represent walls, ladders, bars, players, and enemies
        // TODO: REPLACE WITH YOUR CLASSES
        getLoader().setWallClass(Wall.class);
        getLoader().setFlagClass(Flag.class);
        getLoader().setPlayer1StartLocClass(Player1StartLoc.class);
        getLoader().setPlayer2StartLocClass(Player2StartLoc.class);
        getLoader().setPlayer3StartLocClass(Player3StartLoc.class);
        getLoader().setPlayer4StartLocClass(Player4StartLoc.class);
    }
    
    public void drawHud() {
        Rectangle hudLine = new Rectangle(getWidth(), 1);
        hudLine.setColor(Color.WHITE);
        addObject(hudLine, getWidth() / 2, getHeight() - HUD_HEIGHT);
    }
    
    public String getIdForPlayer(int playerNum) {
        ArrayList<String> players = new ArrayList<>(userNamesById.keySet());
        Collections.sort(players);
        return players.get(playerNum + 1);
    }
    
    public void setPlayerReady(String clientId) {
        readyStatus.put(clientId, true);
        if (clientId.equals(getClient().getId())) {
            getClient().broadcastMessageToRoom("READY", getRoom().getId());
        }
        if (allPlayersReady()) {
            startGame();
        }
    }
    
    public boolean allPlayersReady() {
        for (Map.Entry<String, Boolean> entry : readyStatus.entrySet()) {
            if (!entry.getValue()) return false;
        }
        return true;
    }
    
    public void startGame() {
        System.out.println("STARTING GAME");
    }
    
}
