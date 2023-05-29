import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class CreateRoomButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CreateRoomButton extends Button {
    
    private TextField nameField;
    
    public CreateRoomButton(TextField nameField) {
        super("Create");
        this.nameField = nameField;
    }
    
    @Override
    public void onClick() {
        LobbyWorld lw = (LobbyWorld)getWorld();
        String roomName = nameField.getText();
        lw.getClient().addRoom(roomName, 4);
    }
}
