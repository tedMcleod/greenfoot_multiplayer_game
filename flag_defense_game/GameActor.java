import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public abstract class GameActor extends Actor {
    
    private String actorId;
    private String clientId;
    private RoomInfo room;
    
    public GameActor(String actorId, String clientId) {
        this(actorId, clientId, null);
    }
    
    public GameActor(String actorId, String clientId, RoomInfo room) {
        this.actorId = actorId;
        this.clientId = clientId;
        this.room = room;
    }
    
    public String getActorId() {
        return actorId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public RoomInfo getRoom() {
        return room;
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
                if (room == null) {
                    gw.getClient().broadcastMessage(msg);
                } else {
                    gw.getClient().broadcastMessageToRoom(msg, room.getId());
                }
                
            }
        }
    }
    
    public void broadcastMessage(String msg) {
        GameWorld gw = getWorldOfType(GameWorld.class);
        if (gw != null && gw.getClient() != null) {
            if (getRoom() == null) {
                gw.getClient().broadcastMessage(msg);
            } else {
                gw.getClient().broadcastMessageToRoom(msg, getRoom().getId());
            }
        }
    }
}
