import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.FileDialog;
import java.awt.Dialog;
import java.io.File;

/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainMenuWorld extends World {
    // Create an instance variable to reference the LevelLoader for this world
    private LevelLoader loader;
    private int levelWidth = 28;
    private int levelHeight = 16;
    
    private static final String WIDTH_PREFIX = "WIDTH: ";
    private static final String HEIGHT_PREFIX = "HEIGHT: ";
    private static final int MIN_WIDTH = 15;
    private static final int MAX_WIDTH = 35;
    private static final int MIN_HEIGHT = 10;
    private static final int MAX_HEIGHT = 20;
    
    private Text heightLabel;
    private Text widthLabel;
    
    public MainMenuWorld() {    
        super(600, 400, 1);
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(Color.BLACK);
        bg.fillRect(0, 0, getWidth(), getHeight());
        setBackground(bg);
        Text title = new Text("Battle Map Editor");
        title.setForeground(Color.RED);
        title.setSize(50);
        int th = title.getImage().getHeight();
        int padding = 20;
        addObject(title, getWidth() / 2, padding + th / 2);
        
        Button loadLevelButton = new Button();
        loadLevelButton.setText("Load Level", 60, Color.RED, Color.GRAY);
        int llh = loadLevelButton.getImage().getHeight();
        loadLevelButton.setOnClick(e -> {
            EditorWorld.loadLevel(BattleMapEditorWorld.class);
        });
        addObject(loadLevelButton, title.getX(), title.getY() + th / 2 + llh / 2 + padding);        
        
        Button newLevelButton = new Button();
        newLevelButton.setText("New Level", 60, Color.RED, Color.GRAY);
        int nlh = newLevelButton.getImage().getHeight();
        newLevelButton.setOnClick(e -> {
            Greenfoot.setWorld(new BattleMapEditorWorld(levelWidth * EditorWorld.gridWidth, levelHeight * EditorWorld.gridHeight));
        });
        addObject(newLevelButton, loadLevelButton.getX(), loadLevelButton.getY() + llh / 2 + padding + nlh / 2);
        
        widthLabel = new Text(WIDTH_PREFIX + levelWidth, 40, Color.RED);
        int wlh = widthLabel.getImage().getHeight();
        int wlw = widthLabel.getImage().getWidth();
        addObject(widthLabel, title.getX(), newLevelButton.getY() + nlh / 2 + padding + wlh / 2);
        
        Button widthDecreaseBtn = new Button();
        widthDecreaseBtn.setText(" - ", 30, Color.RED, Color.GRAY);
        int wdw = widthDecreaseBtn.getImage().getWidth();
        addObject(widthDecreaseBtn, widthLabel.getX() - wlw / 2 - wdw / 2 - padding, widthLabel.getY());
        widthDecreaseBtn.setOnClick(e -> {
            if (levelWidth > MIN_WIDTH) setLevelWidth(levelWidth - 1);
        });
        
        Button widthIncreaseBtn = new Button();
        widthIncreaseBtn.setText(" + ", 30, Color.RED, Color.GRAY);
        int wiw = widthIncreaseBtn.getImage().getWidth();
        addObject(widthIncreaseBtn, widthLabel.getX() + wlw / 2 + wiw / 2 + padding, widthLabel.getY());
        widthIncreaseBtn.setOnClick(e -> {
            if (levelWidth < MAX_WIDTH) setLevelWidth(levelWidth + 1);
        });
        
        heightLabel = new Text(HEIGHT_PREFIX + levelHeight, 40, Color.RED);
        int hlh = heightLabel.getImage().getHeight();
        int hlw = heightLabel.getImage().getWidth();
        addObject(heightLabel, title.getX(), widthLabel.getY() + wlh / 2 + padding + hlh / 2);
    
        Button heightDecreaseBtn = new Button();
        heightDecreaseBtn.setText(" - ", 30, Color.RED, Color.GRAY);
        int hdw = heightDecreaseBtn.getImage().getWidth();
        addObject(heightDecreaseBtn, heightLabel.getX() - hlw / 2 - hdw / 2 - padding, heightLabel.getY());
        heightDecreaseBtn.setOnClick(e -> {
            if (levelHeight > MIN_HEIGHT) setLevelHeight(levelHeight - 1);
        });
        
        Button heightIncreaseBtn = new Button();
        heightIncreaseBtn.setText(" + ", 30, Color.RED, Color.GRAY);
        int hiw = heightIncreaseBtn.getImage().getWidth();
        addObject(heightIncreaseBtn, heightLabel.getX() + hlw / 2 + hiw / 2 + padding, heightLabel.getY());
        heightIncreaseBtn.setOnClick(e -> {
            if (levelHeight < MAX_HEIGHT) setLevelHeight(levelHeight + 1);
        });
    }
    
    public void setLevelHeight(int h) {
        levelHeight = h;
        heightLabel.setText(HEIGHT_PREFIX + levelHeight);
    }
    
    public void setLevelWidth(int h) {
        levelWidth = h;
        widthLabel.setText(WIDTH_PREFIX + levelWidth);
    }
        
    public LevelLoader getLoader() {
        return loader;
    }
}
