package com.tinocs.mp.client;
import java.util.Set;
import java.util.HashSet;

/**
 * A class for storing information about a room.
 * 
 * @author Ted McLeod
 * @version 6/6/2023
 */
public class RoomInfo {
    
    private String id;
    private String name;
    private int capacity;
    private Set<String> members;
    private String ownerId;
    private boolean isClosed;
    
    /**
     * Create a RoomInfo storing data about a room with the given data.
     * @param id the id of the room
     * @param name the name of the room
     * @param capacity the capacity of the room
     * @param members a set of the ids of clients in this room
     * @param ownerId the id of the owner of this room
     * @param isClosed whether or not this room is closed
     */
    public RoomInfo(String id, String name, int capacity, Set<String> members, String ownerId, boolean isClosed) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.ownerId = ownerId;
        this.members = new HashSet<>(members);
        this.isClosed = isClosed;
    }
    
    /**
     * Returns the id of this room
     * @return the id of this room
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the name of this room
     * @return the name of this room
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the capacity of this room
     * @return the capacity of this room
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Returns the id of the owner of this room
     * @return the id of the owner of this room
     */
    public String getOwnerId() {
        return ownerId;
    }
    
    /**
     * Returns whether or not this room is closed
     * @return whether or not this room is closed
     */
    public boolean isClosed() {
        return isClosed;
    }
    
    /**
     * Returns a set of the ids of the clients in this room
     * @return a set of the ids of the clients in this room
     */
    public Set<String> members() {
        return new HashSet<>(members);
    }
    
    @Override
    public String toString() {
        return "Room (name:" + name + " id:" + id + " cap:" + capacity + " owner:" + ownerId + " members:" + members + ")";
    }
}
