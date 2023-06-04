import java.util.Set;
import java.util.HashSet;

/**
 * Write a description of class RoomData here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoomInfo {
    
    private String id;
    private String name;
    private int capacity;
    private Set<String> members;
    private String ownerId;
    private boolean isClosed;
    
    public RoomInfo(String id, String name, int capacity, Set<String> members, String ownerId, boolean isClosed) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.ownerId = ownerId;
        this.members = new HashSet<>(members);
        this.isClosed = isClosed;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId() {
        this.ownerId = ownerId;
    }
    
    public boolean isClosed() {
        return isClosed;
    }
    
    public void setClosed(boolean closed) {
        isClosed = closed;
    }
    
    public Set<String> members() {
        return new HashSet<>(members);
    }
    
    public void addMember(String clientId) {
        members.add(clientId);
    }
    
    public void removeMember(String clientId) {
        members.remove(clientId);
    }
    
    @Override
    public String toString() {
        return "Room (name:" + name + " id:" + id + " cap:" + capacity + " owner:" + ownerId + " members:" + members + ")";
    }
}
