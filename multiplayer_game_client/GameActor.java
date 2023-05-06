import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public abstract class GameActor extends Actor {
    
    private String id;
    private String clientId;
    
    public GameActor(String id, String clientId) {
        this.id = id;
        this.clientId = clientId;
    }
    
    public String getId() {
        return id;
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
            if (gw.getClient() != null && gw.getClient().getConnected().get()) {
                gw.getClient().sendMessage("DESTROY " + getId());
            }
        }
    }
}
