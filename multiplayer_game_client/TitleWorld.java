import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TitleWorld extends World {
    
    public static String lastAddress = "localhost";
    public static String lastPort = "1234";
    private TextField addressField;
    private TextField portField;
    
    public TitleWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1);
        
        StartButton btn = new StartButton();
        addObject(btn, getWidth() / 2, getHeight() / 2);
        
        int margin = 5;
        
        Text addressLabel = new Text("Address ");
        addressLabel.setSize(14);
        int alw = addressLabel.getImage().getWidth();
        int alh = addressLabel.getImage().getHeight();
        addObject(addressLabel, margin + alw / 2, getHeight() - alh / 2 - margin);
        
        addressField = new TextField(100);
        addressField.setSize(14);
        int afw = addressField.getImage().getWidth();
        int afh = addressField.getImage().getHeight();
        addressField.setText(lastAddress);
        addObject(addressField, margin + alw + afw / 2, getHeight() - afh / 2 - margin);
        
        Text portLabel = new Text("Port ");
        portLabel.setSize(14);
        int plw = portLabel.getImage().getWidth();
        int plh = portLabel.getImage().getHeight();
        int afx = addressField.getX();
        addObject(portLabel, afx + afw / 2 + margin + plw / 2, getHeight() - plh / 2 - margin);
        
        portField = new TextField(50);
        portField.setSize(14);
        int pfw = portField.getImage().getWidth();
        int pfh = portField.getImage().getHeight();
        int plx = portLabel.getX();
        portField.setValidator(new IntegerValidator());
        portField.setText(lastPort);
        addObject(portField, plx + plw / 2 + margin + pfw / 2, getHeight() - pfh / 2 - margin);
    }
    
    public String getAddress() {
        return addressField.getText();
    }
    
    public int getPort() {
        try {
            return Integer.parseInt(portField.getText());
        } catch (NumberFormatException err) {
            return 0;
        }
    }
}
