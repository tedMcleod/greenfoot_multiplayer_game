import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Point;

/**
 * Write a description of class LodeRunnerEditorWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BattleMapEditorWorld extends EditorWorld {
    
    /** The size of each square in the imaginary grid in a lode runner level.
     * If you change this, then each tile will be scaled to fit the new grid size.
     * Be aware that any hardcoded values in your other code could be broken if this
     * is changed.
     */
    public static int gridSize = 24;
    
    // this code runs when the class is loaded (at the beginning of the program before any instance is created)
    static {
        // Initialize the Grid Offsets for Gold and Bar
        EditorWorld.gridWidth = gridSize;
        EditorWorld.gridHeight = gridSize;
        
        // If you add a tile that needs to be placed anywhere other than the center of a grid square
        // then define the offset here
        
        
        // If you added extra tile types, then you should add directions indicating what key will
        // make that be the type of tile that is drawn
        EditorWorld.addCustomInstruction("W = Wall    1 = Player 1    2 = Player 2    F = Flag");
    }
    
    /** Initialize a BattleMapEditorWorld with the given height and width.
     * @param width the width of the world
     * @param height the height of the world
     */
    public BattleMapEditorWorld(int width, int height) {
        super(width, height);
    }
    
    // If you add a new type of tile, simply add the type here
    @Override
    public void defineTypes() {
        addType("W", "W", Wall.class);
        addType("1", "1", Player1StartLoc.class);
        addType("2", "2", Player2StartLoc.class);
        addType("3", "3", Player3StartLoc.class);
        addType("4", "4", Player4StartLoc.class);
        addType("F", "F", Flag.class);
    }
    
    @Override
    public void drawHud() {
        super.drawHud();
        setDrawClass(Wall.class);
    }
}
