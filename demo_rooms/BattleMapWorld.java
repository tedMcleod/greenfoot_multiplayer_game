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

    public static final int HUD_HEIGHT = 54;
    private RoomInfo room;
    private ConcurrentHashMap<String, String> userNamesById;
    private ConcurrentHashMap<String, Boolean> readyStatus = new ConcurrentHashMap<>();
    private boolean allPlayersSpawned = false;
    private String winningPlayer = null;
    private int winCount = 0;
    private boolean started = false;
    
    private Text speedCooldownLabel;
    private Text boosterCooldownLabel;
    private Text shotCooldownLabel;
    private Rectangle speedBar = new Rectangle(100, HUD_HEIGHT / 3);
    private Rectangle boosterBar = new Rectangle(100, HUD_HEIGHT / 3);
    private Rectangle shotBar = new Rectangle(100, HUD_HEIGHT / 3);
    
    private Text flagTimeTxt;

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
    
    public void updateGameHud() {
        List<Player> players = getObjects(Player.class);
        if (players.size() > 0) {
            Player player = players.get(0);
            int padding = 3;
            int barLength = 100;
            if (speedCooldownLabel == null) {
                speedCooldownLabel = new Text("Speed");
                speedCooldownLabel.setSize((HUD_HEIGHT - padding * 4) / 3);
                speedCooldownLabel.setForeground(Color.WHITE);
                int w = speedCooldownLabel.getImage().getWidth();
                int h = speedCooldownLabel.getImage().getHeight();
                int labelY = getHeight() - HUD_HEIGHT + padding + h / 2;
                int labelX = padding + w / 2;
                addObject(speedCooldownLabel, labelX, labelY);
                
                speedBar = new Rectangle(barLength, HUD_HEIGHT / 3);
                speedBar.getImage().setColor(Color.BLACK);
                speedBar.getImage().fill();
                addObject(speedBar, labelX + w / 2 + speedBar.getImage().getWidth() / 2 + padding, labelY);
            }
            if (boosterCooldownLabel == null) {
                boosterCooldownLabel = new Text("Boost");
                boosterCooldownLabel.setSize((HUD_HEIGHT - padding * 4) / 3);
                boosterCooldownLabel.setForeground(Color.WHITE);
                int w = boosterCooldownLabel.getImage().getWidth();
                int h = boosterCooldownLabel.getImage().getHeight();
                int labelY = getHeight() - HUD_HEIGHT / 2;
                int labelX = padding + w / 2;
                addObject(boosterCooldownLabel, labelX, labelY);
                
                boosterBar = new Rectangle(barLength, HUD_HEIGHT / 3);
                boosterBar.getImage().setColor(Color.BLACK);
                boosterBar.getImage().fill();
                addObject(boosterBar, labelX + w / 2 + boosterBar.getImage().getWidth() / 2 + padding, labelY);
            }
            if (shotCooldownLabel == null) {
                shotCooldownLabel = new Text("Power");
                shotCooldownLabel.setSize((HUD_HEIGHT - padding * 4) / 3);
                shotCooldownLabel.setForeground(Color.WHITE);
                int w = shotCooldownLabel.getImage().getWidth();
                int h = shotCooldownLabel.getImage().getHeight();
                int labelY = getHeight() - padding - h / 2;
                int labelX = padding + w / 2;
                addObject(shotCooldownLabel, labelX, labelY);
                
                shotBar = new Rectangle(barLength, HUD_HEIGHT / 3);
                shotBar.getImage().setColor(Color.BLACK);
                shotBar.getImage().fill();
                addObject(shotBar, labelX + w / 2 + shotBar.getImage().getWidth() / 2 + padding, labelY);
            }
            if (flagTimeTxt == null) {
                flagTimeTxt = new Text("0");
                flagTimeTxt.setSize((HUD_HEIGHT - padding * 2));
                flagTimeTxt.setForeground(Color.WHITE);
                flagTimeTxt.setAlign(Text.ALIGN_RIGHT);
                int w = flagTimeTxt.getImage().getWidth();
                int h = flagTimeTxt.getImage().getHeight();
                int labelY = getHeight() - HUD_HEIGHT / 2;
                int labelX = getWidth() - padding - w / 2;
                addObject(flagTimeTxt, labelX, labelY);
            }
            
            fillBar(speedBar, player.getSpeedCooldown(), player.getSpeedDuration(), Color.GREEN);
            fillBar(boosterBar, player.getBoosterCooldown(), player.getBoosterDelay(), Color.BLUE);
            fillBar(shotBar, player.getShotCooldown(), player.getShotDelay(), Color.RED);
            
            flagTimeTxt.setText("" + player.getFlagTime());
            
        }
    }
    
    public void fillBar(Actor bar, int value, int maxValue, Color color) {
        int barLength = bar.getImage().getWidth();
        bar.getImage().setColor(Color.BLACK);
        bar.getImage().fill();
        bar.getImage().setColor(color);
        int boosterFillW = value * barLength / maxValue;
        bar.getImage().fillRect(0, 0, boosterFillW, HUD_HEIGHT / 3);
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
    
    public Actor getStartLoc(String clientId) {
        int playerNum = getPlayerNum(clientId);
        if (playerNum == 1) {
            return getObjects(Player1StartLoc.class).get(0);
        } else if (playerNum == 2) {
            return getObjects(Player2StartLoc.class).get(0);
        } else if (playerNum == 3) {
            return getObjects(Player3StartLoc.class).get(0);
        } else {
            return getObjects(Player4StartLoc.class).get(0);
        }
    }

    public void startGame() {
        System.out.println("STARTING GAME");
        Actor startLoc = getStartLoc(getClient().getId());
        int startX = startLoc.getX();
        int startY = startLoc.getY();
        System.out.println("Start Loc: (" + startX + ", " + startY + ")");
        Player player = new Player(getClient().getId());
        addObject(player, startX, startY);
        player.turnTowards(getWidth() / 2, getHeight() / 2);
    }

    public boolean allPlayersSpawned() {
        return allPlayersSpawned;
    }

    public void setPlayerWon(String clientId) {
        winningPlayer = clientId;
    }
    
    public String getWinningPlayer() {
        return winningPlayer;
    }
    
    public boolean isStarted() {
        return started;
    }

    @Override
    public void act() {
        if (!allPlayersSpawned && allPlayersReady()) {
            if (getObjects(OtherPlayer.class).size() == room.members().size() - 1) {
                allPlayersSpawned = true;
            }
        } else if (allPlayersSpawned && !started) {
            started = true;
            removeObjects(getObjects(ReadyButton.class));
            removeObjects(getObjects(OtherPlayerReadyStatus.class));
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
