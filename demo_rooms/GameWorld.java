import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.ArrayList;

public abstract class GameWorld extends World {
    JavaFXClient client;

    public GameWorld(int width, int height, int cellSize, boolean bounded) {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(width, height, cellSize, bounded);
    }
    
    public void setClient(JavaFXClient client) {
        this.client = client;
        GreenfootEventHandler eh = (GreenfootEventHandler)client.getEventHandler();
        if (eh != null) {
            eh.setWorld(this);
        }
    }

    public JavaFXClient getClient() {
        return client;
    }

    public GameActor getGameActor(String actorId) {
        for (GameActor ga : getObjects(GameActor.class)) {
            if (ga.getActorId().equals(actorId)) {
                return ga;
            }
        }
        return null;
    }

    public List<GameActor> getClientActors(String clientId) {
        List<GameActor> clientActors = new ArrayList<>();
        for (GameActor ga : getObjects(GameActor.class)) {
            if (ga.getClientId().equals(clientId)) {//
                clientActors.add(ga);
            }
        }
        return clientActors;
    }

    @Override
    public void stopped() {
        client.disconnect();
    }
}
