import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.ArrayList;

public abstract class GameWorld extends World {
    GameClient client;

    public GameWorld(int width, int height, int cellSize, boolean bounded) {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(width, height, cellSize, bounded);
        setPaintOrder(Player.class, OtherPlayer.class, Shot.class, OtherShot.class);
    }
    
    public void setClient(GameClient client) {
        this.client = client;
        client.setWorld(this);
    }

    public GameClient getClient() {
        return client;
    }

    public GameActor getGameActor(String id) {
        List<GameActor> gmActors = getObjects(GameActor.class);
        for (GameActor ga : gmActors) {
            if (ga.getId().equals(id)) {
                return ga;
            }
        }
        return null;
    }

    public List<GameActor> getClientActors(String clientId) {
        List<GameActor> clientActors = new ArrayList<>();
        List<GameActor> gmActors = getObjects(GameActor.class);
        for (GameActor ga : gmActors) {
            if (ga.getClientId().equals(clientId)) {
                clientActors.add(ga);
            }
        }
        return clientActors;
    }

    @Override
    public void stopped() {
        client.sendMessage("DC");
    }

    @Override
    public void started() {
        GameClient client = getClient();
        if (client == null || !client.getConnected().get()) {
            Greenfoot.setWorld(new TitleWorld());
        }
    }
}
