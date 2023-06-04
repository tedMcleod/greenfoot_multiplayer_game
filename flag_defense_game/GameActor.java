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
        if (Debug.DEBUG) System.out.println(getClass().getName() + " calling destroy");
        GameWorld gw = getWorldOfType(GameWorld.class);
        if (gw != null) {
            onDestroy(gw);
            if (gw.getClient() != null && gw.getClient().isConnected()) {
                String msg = "DESTROY " + getActorId();
                String roomId = gw.getClient().getCurrentRoomId();
                if (roomId == null) {
                    gw.getClient().broadcastMessage(msg);
                } else {
                    gw.getClient().broadcastMessageToRoom(msg, roomId);
                }
            }
        }
    }
    
    public void broadcastMessage(String msg) {
        GameWorld gw = getWorldOfType(GameWorld.class);
        if (gw != null && gw.getClient() != null) {
            String roomId = gw.getClient().getCurrentRoomId();
            if (roomId == null) {
                gw.getClient().broadcastMessage(msg);
            } else {
                gw.getClient().broadcastMessageToRoom(msg, roomId);
            }
        }
    }
}
