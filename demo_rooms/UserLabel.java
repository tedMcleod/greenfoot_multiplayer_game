import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class UserNameLabel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UserLabel extends Text {
    private String clientId;
    
    public UserLabel(String clientId, String name) {
        super(name);
        this.clientId = clientId;
    }
    
    public String getClientId() {
        return clientId;
    }
}
