package mp_engine;

import engine.*;
import mp_client_base.GameClient;

import java.util.List;
import java.util.ArrayList;

public abstract class GameWorld extends World {
    GameClient client;
    
    public void setClient(GameClient client) {
        this.client = client;
        EngineEventHandler eh = (EngineEventHandler)client.getEventHandler();
        if (eh != null) {
            eh.setWorld(this);
        }
    }

    public GameClient getClient() {
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
    public void stop() {
    	super.stop();
    	stopped();
    }

    public void stopped() {
        client.disconnect();
    }
}
