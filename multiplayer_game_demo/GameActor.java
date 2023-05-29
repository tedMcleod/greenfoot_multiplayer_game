import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public abstract class GameActor extends Actor {
    
    private String actorId;
    private String clientId;
    
    public GameActor(String actorId, String clientId) {
        this.actorId = actorId;
        this.clientId = clientId;
    }
    
    public String getActorId() {
        return actorId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void onDestroy(GameWorld world) {
        world.removeObject(this);
    }
    
    public void destroy() {
        GameWorld gw = getWorldOfType(GameWorld.class);
        if (gw != null) {
            onDestroy(gw);
            if (gw.getClient() != null && gw.getClient().isConnected()) {
                gw.getClient().broadcastMessage("DESTROY " + getActorId());
            }
        }
    }
}
