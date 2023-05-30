import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;

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
    private boolean allPlayersSpawned = false;
    private String winningPlayer = null;
    private int winCount = 0;

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
    public BattleMapWorld(int level, RoomInfo room, ConcurrentHashMap<String, String> userNamesById, String clientId) {
        super(level);
        this.room = room;
        this.userNamesById = userNamesById;
        drawHud(clientId);
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

    public void drawHud(String myClientId) {
        Rectangle hudLine = new Rectangle(getWidth(), 1);
        hudLine.setColor(Color.WHITE);
        addObject(hudLine, getWidth() / 2, getHeight() - HUD_HEIGHT);

        ArrayList<String> players = new ArrayList<>(userNamesById.keySet());
        Collections.sort(players);
        int horizontalSpacing = getWidth() / room.getCapacity();
        int x = horizontalSpacing / 2;
        for (String clientId : players) {
            readyStatus.put(clientId, false);
            Actor btn;
            if (clientId.equals(myClientId)) {
                btn = new ReadyButton();
            } else {
                btn = new OtherPlayerReadyStatus(clientId);
            }
            addObject(btn, x, getHeight() - HUD_HEIGHT / 2);
            x += horizontalSpacing;
        } 

    }

    public String getIdForPlayer(int playerNum) {
        ArrayList<String> players = new ArrayList<>(userNamesById.keySet());
        Collections.sort(players);
        return players.get(playerNum + 1);
    }

    public int getPlayerNum(String clientId) {
        ArrayList<String> players = new ArrayList<>(userNamesById.keySet());
        Collections.sort(players);
        return players.indexOf(clientId);
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

    public String getClientName(String clientId) {
        return userNamesById.get(clientId);
    }

    public boolean isPlayerReady(String clientId) {
        return readyStatus.get(clientId);
    }

    public boolean allPlayersReady() {
        for (Map.Entry<String, Boolean> entry : readyStatus.entrySet()) {
            if (!entry.getValue()) return false;
        }
        return true;
    }

    public void startGame() {
        System.out.println("STARTING GAME");
        int playerNum = getPlayerNum(getClient().getId());
        Actor startLoc;
        if (playerNum == 1) {
            startLoc = getObjects(Player1StartLoc.class).get(0);
        } else if (playerNum == 2) {
            startLoc = getObjects(Player2StartLoc.class).get(0);
        } else if (playerNum == 3) {
            startLoc = getObjects(Player3StartLoc.class).get(0);
        } else {
            startLoc = getObjects(Player4StartLoc.class).get(0);
        }
        int startX = startLoc.getX();
        int startY = startLoc.getY();

        System.out.println("Start Loc: (" + startX + ", " + startY + ")");
        Player player = new Player(getClient().getId());
        addObject(player, startX, startY);
    }

    public boolean allPlayersSpawned() {
        return allPlayersSpawned;
    }

    public void setPlayerWon(String clientId) {
        winningPlayer = clientId;
    }

    @Override
    public void act() {
        if (!allPlayersSpawned && allPlayersReady()) {
            if (getObjects(OtherPlayer.class).size() == room.members().size() - 1) {
                allPlayersSpawned = true;
            }
        } else if (allPlayersSpawned && getObjects(OtherPlayer.class).size() == 0 && winningPlayer == null) {
            setPlayerWon(getClient().getId());
            getClient().broadcastMessageToRoom("WIN", room.getId());
        } else if (winningPlayer != null) {
            if (winCount == 0) {
                Text winText = new Text(getClientName(winningPlayer) + " Won!");
                winText.setSize(60);
                winText.setForeground(Color.RED);
                addObject(winText, getWidth() / 2, getHeight() / 2);
            }
            winCount++;
            if (winCount == 600) {
                LobbyWorld lw = new LobbyWorld();
                getClient().setEventHandler(new LobbyEventHandler(lw));
                lw.setClient(getClient());
                lw.setNeedsUpdate();
                Greenfoot.setWorld(lw);
                lw.getClient().broadcastMessage("LEAVE_ROOM " + room.getId());
            }
        }
    }

}
