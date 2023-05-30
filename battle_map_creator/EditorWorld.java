import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Point;
import java.util.List;
import java.lang.reflect.*;
import java.awt.Point;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * The EditorWorld class is a world that displays a level and instructions for editing the level.
 * You can add tiles of any type that has been defined using the addType method. You can save the level
 * when finished editing. You can edit a new level or load and existing level.
 * 
 * @author Ted McLeod
 * @version 4/14/2023
 */
public abstract class EditorWorld extends World {
    
    // The color of the background of the world
    private Color backgroundColor;
    
    // The color of the instructions text
    private Color instructionsColor;
    
    // The color of the line separating hud from the level
    private Color hudLineColor;
    
    // The color of the HUD background
    private Color hudBackgroundColor;
    
    /** how much should instances of each class be offset from the
     *  center of a grid position. You can define an offset by
     *  writing:
     *  
     *  <pre>
     *  EditorWorld.GRID_OFFSETS.put(ClassName.class, new Point(dx, dy));
     *  </pre>
     */
    public static Map<Class<? extends Actor>, Point> gridOffsets = new HashMap<>();
    
    /**
     * The grid width of each grid rectangle. All objects drawn in the editor will be snapped
     * to the center of a grid position where the grid is made up of imaginary
     * rectangles of the given dimensions. Except, they will be offset as
     * described if there is an offset defined in GRID_OFFSETS.
     */
    public static int gridWidth = 24;
    
    /**
     * The grid height of each grid rectangle. All objects drawn in the editor will be snapped
     * to the center of a grid position where the grid is made up of imaginary
     * rectangles of the given dimensions. Except, they will be offset as
     * described if there is an offset defined in GRID_OFFSETS.
     */
    public static int gridHeight = 24;
    
    /** The margin at the bottom of the HUD */
    public static int bottomMargin = 10;
    
    /** The first instructions that show up at the top of the hud */
    public static String[] firstInstructions = {
        "Click or drag to draw the current tile",
        "Press the following keys to change tiles, save this level or load a new level:",
    };
    
    /** The last lines of instructions that show up at the bottom of the hud */
    public static String[] lastInstructions = {
        "D = Delete    S = Save    O = Open"
    };
    
    // used in scaling the font size
    private static final int BASE_FONT_SIZE = 20;
    
    // A list of lines of directions that will be at the bottom of the Editor.
    private static List<String> instructions = getInitialInstructions();
    
    // the font size of the instructions text in the hud
    private static int instructionsFontSize = BASE_FONT_SIZE;
    
    // everything past this height is in the hud, so it should not be drawn in
    private int maxDrawHeight;
    
    // the type of actor that will be drawn when clicking or dragging in editor
    private Class<? extends Actor> drawClass;
    
    // The tile in the hud showing the type of tile that is currently active
    private Actor currentTile;
    
    // the level loading that loads levels from file and adds the appropriate tiles to the editor
    private LevelLoader loader;
    
    // A map that defines which keys will change the active tile type to which types
    private Map<String, Class<? extends Actor>> keyMap;
    
    /**
     * Initialize an EditorWorld with the given width and height
     * 
     * @param width the width of the level
     * @param the height of the level
     */
    public EditorWorld(int width, int height) { 
        super(width, height + getInstructionsFontSize(width) * instructions.size() + bottomMargin, 1);
        instructionsFontSize = getInstructionsFontSize(width);
        backgroundColor = Color.BLACK;
        drawBackground();
        instructionsColor = Color.WHITE;
        hudLineColor = Color.WHITE;
        hudBackgroundColor = new Color(25, 25, 25);
        keyMap = new HashMap<>();
        loader = new LevelLoader(this, new HashMap<>());
        defineTypes();
        maxDrawHeight = getHeight() - instructionsFontSize * instructions.size() - bottomMargin;
        drawHud();
    }
    
    /**
     * Called on the world every frame. Responds to user input.
     */
    public void act() {
        String key = Greenfoot.getKey();
        if (key != null) {
            key = key.toUpperCase();
            if (key.equals("S")) {
                int x = currentTile.getX();
                int y = currentTile.getY();
                removeObject(currentTile);
                String path = getLoader().promptForSavePath();
                if (path != null) getLoader().saveLevel(path, getWidth(), maxDrawHeight);
                addObject(currentTile, x, y);
            } else if (key.equals("O")) {
                loadLevel(this.getClass());
            } else if (key.equals("D")) {
                setDrawClass(null);
            } else {
                if (keyMap.containsKey(key)) {
                    setDrawClass(keyMap.get(key));
                }
            }
        }
        
        if (Greenfoot.mouseDragged(null) || Greenfoot.mousePressed(null)) {
            MouseInfo mi = Greenfoot.getMouseInfo();
            int x = mi.getX();
            int y = mi.getY();
            if (x > 0 && y > 0 && x < getWidth() && y < maxDrawHeight) {
                Point gp = getGridPos(x, y);
                Delete d = new Delete();
                addObject(d, gp.x, gp.y);
                List<Actor> actorsThere = d.getTouchedActors(Actor.class);
                removeObject(d);
                if (actorsThere.size() > 0) {
                    for (Actor actor : actorsThere) {
                        removeObject(actor);
                    }
                }
                if (drawClass != null) {
                    Actor a = getInstanceOfDrawClass();
                    Point offsets = gridOffsets.get(drawClass);
                    if (offsets == null) offsets = new Point(0, 0);
                    gp.x += offsets.x;
                    gp.y += offsets.y;
                    addObject(a, gp.x, gp.y);
                }
            }
        }
        
    }
    
    /**
     * Defines the types of tiles that can be added to a level.
     * Add the types using the addType method as in this example:
     * 
     * <pre>
     * // add a Wall class that is written as a W in the save file and
     * // can be set as the active tile by pressing W
     * addType("W", "W", Wall.class);
     * </pre>
     */
    public abstract void defineTypes();
    
    /**
     * Draw the background of the editor world
     */
    public void drawBackground() {
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(backgroundColor);
        bg.fillRect(0, 0, getWidth(), getHeight());
        setBackground(bg);
    }
    
    /**
     * Returns the initial instructions consisting of the firstInstructions followed by the lastInstructions.
     * @return the initial instructions
     */
    public static List<String> getInitialInstructions() {
        ArrayList<String> ins = new ArrayList<>(Arrays.asList(firstInstructions));
        ins.addAll(Arrays.asList(lastInstructions));
        return ins;
    }
    
    /**
     * Clear all the custom instructions. Effectively initializes the instructions to be the initial instructions
     */
    public static void clearCustomInstructions() {
        instructions = getInitialInstructions();
    }
    
    /**
     * Add a line of instructions just above the last instructions
     * 
     * @param instruction the line of instructions to add
     */
    public static void addCustomInstruction(String instruction) {
        int i = instructions.size() - lastInstructions.length;
        instructions.add(i, instruction);
    }
    
    /**
     * Remove a line of instructions
     * 
     * @param instruction the line of instructions to remove
     */
    public static void removeInstruction(String instruction) {
        instructions.remove(instruction);
    }
    
    /**
     * Returns the font size scaled according to the width of the level
     * 
     * @return the font size scaled according to the width of the level
     */
    public static int getInstructionsFontSize(int width) {
        return BASE_FONT_SIZE * width / 600;
    }
    
    /**
     * Returns the color of the instructions text.
     * 
     * @return the color of the instructions text.
     */
    public Color getTextColor() {
        return instructionsColor;
    }
    
    /**
     * Set the color of instructions text.
     * 
     * @param color the color to set the text to
     */
    public void setTextColor(Color color) {
        instructionsColor = color;
        for (Text txt : getObjects(Text.class)) {
            txt.setForeground(color);
        }
    }
    
    /**
     * Returns the color of the line between the hud and the level.
     * 
     * @return the color of the line between the hud and the level.
     */
    public Color getHudLineColor() {
        return hudLineColor;
    }
    
    /**
     * Set the color of line between the hud and the level.
     * 
     * @param color the color to set the line to
     */
    public void setHudLineColor(Color color) {
        hudLineColor = color;
        drawHudLine();
    }
    
    /**
     * Returns the color of the hud background.
     * 
     * @return the color of the hud background.
     */
    public Color getHudBackgroundColor() {
        return hudBackgroundColor;
    }
    
    /**
     * Set the color of hud background.
     * 
     * @param color the color to set the hud background to
     */
    public void setHudBackgroundColor(Color color) {
        hudBackgroundColor = color;
        drawHudBackground();
        drawHudLine();
    }
    
    /**
     * Returns the color of the background.
     * 
     * @return the color of the background.
     */
    public Color getBackgroundColor() {
        return hudBackgroundColor;
    }
    
    /**
     * Set the color of background.
     * 
     * @param color the color to set the background to
     */
    public void setBackgroundColor(Color color) {
        backgroundColor = color;
        drawBackground();
        drawHudBackground();
        drawHudLine();
    }
    
    /**
     * Adds the given class as a type of tile that can be added to the level.
     * The symbol is the string that will be written to the file when saving tiles
     * of this type and the key is the key that should be pressed to make that
     * the active tile.
     * 
     * @param symbol the symbol to write to the file that represents that type of tile
     * @param key the key that should be pressed to make this tile the active tile
     * @param cls the class to add (example: Ball.class)
     * 
     * @throws IllegalArgumentException if the symbol contains white space
     */
    public void addType(String symbol, String key, Class<? extends Actor> cls) {
        for (int i = 0; i < symbol.length(); i++) {
            if (Character.isWhitespace(symbol.charAt(i))) throw new IllegalArgumentException("Illegal whitespace found in symbol");
        }
        getLoader().defineType(symbol, cls);
        keyMap.put(key.toUpperCase(), cls);
    }
    
    /**
     * Set the class that should be drawn when clicking or dragging.
     * 
     * @param cls The class that should be drawn when clicking or dragging.
     */
    public void setDrawClass(Class<? extends Actor> cls) {
        drawClass = cls;
        updateCurrentTile();
    }
    
    /**
     * The coordinates of the center of the grid square that contains (x, y)
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the coordinates of the center of the grid square that contains (x, y)
     */
    public Point getGridPos(int x, int y) {
        int gw = gridWidth;
        int gh = gridHeight;
        int gridX = x / gw;
        int gridY = y / gh;
        int gx = gridX * gw + gw / 2;
        int gy = gridY * gh + gh / 2;
        return new Point(gx,gy);
    }

    /**
     * Snap all the actors in the world to the center of grid coordinates.
     * This should really only be used for debugging purposes since it will 
     * snap all actors to grid including Text objects.
     */
    public void snapToGrid() {
        for (Actor actor : getObjects(Actor.class)) {
            Point gp = getGridPos(actor.getX(), actor.getY());
            Point offsets = gridOffsets.get(drawClass);
            if (offsets != null) {
                gp.x += offsets.x;
                gp.y += offsets.y;
            }
            actor.setLocation(gp.x,gp.y);
        }
    }
    
    /**
     * Get an instance of the current active class.
     * Precondition: The class must have a default constructor
     * 
     * @return an instance of the current active class
     */
    public Actor getInstanceOfDrawClass() {
        try {
            Actor a = getLoader().getActorOfType(drawClass);
            return a;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // constructor failed
        }
        return null;
    }
    
    /**
     * Draw the Hud (instructions, current tile label...etc)
     */
    public void drawHud() {
        drawHudBackground();
        drawInstructions();
        drawCurrentTileLabel();
        drawHudLine();
        setDrawClass(null);
    }
    
    /**
     * Draw the background of the hud
     */
    public void drawHudBackground() {
        GreenfootImage bg = getBackground();
        bg.setColor(hudBackgroundColor);
        bg.fillRect(0, maxDrawHeight, getWidth(), getHeight() - maxDrawHeight);
    }
    
    protected void drawHudLine() {
        getBackground().setColor(hudLineColor);
        getBackground().drawLine(0, maxDrawHeight, getWidth(), maxDrawHeight);
    }
    
    /**
     * Draws the instructions.
     */
    protected void drawInstructions() {
        int y = maxDrawHeight + instructionsFontSize / 2;
        for (int i = 0; i < instructions.size(); i++) {
            Text t = new Text(instructions.get(i), instructionsFontSize, instructionsColor);
            addObject(t, t.getImage().getWidth() / 2, y);
            y += t.getImage().getHeight();
        }
    }
    
    /**
     * Draws the label for the current active tile.
     */
    protected void drawCurrentTileLabel() {
        Text t = new Text(getCurrentTileLabel(), instructionsFontSize, instructionsColor);
        int x = getWidth() - gridWidth - t.getImage().getWidth() / 2;
        int y = getHeight() - t.getImage().getHeight() / 2;
        addObject(t, x, y);
    }
    
    /**
     * returns the current tile label
     * 
     * @return the current tile label
     */
    public String getCurrentTileLabel() {
        return "Current Tile: ";
    }
    
    /**
     * update the tile showing what the current active tile is
     */
    public void updateCurrentTile() {
        if (currentTile != null) {
            removeObject(currentTile);
        }
        if (drawClass != null) {
            currentTile = getInstanceOfDrawClass();
        } else {
            currentTile = new Delete();
        }
        
        int x = getWidth() - currentTile.getImage().getWidth() / 2;
        int y = getHeight() - currentTile.getImage().getHeight() / 2;
        addObject(currentTile, x, y);
    }
    
    /**
     * return the level loader
     * 
     * @return the level loader
     */
    public LevelLoader getLoader() {
        return loader;
    }
    
    /**
     * Ask the user to pick a path and then load that level in a subclass of EditorWorld of the given type.
     */
    public static void loadLevel(Class<? extends EditorWorld> editorClass) {
        String path = LevelLoader.promptForLoadPath();
        if (path != null) {
            int w = LevelLoader.getWorldDimensions(path).width;
            int h = LevelLoader.getWorldDimensions(path).height;
            try {
                Constructor<? extends EditorWorld> constr = editorClass.getConstructor(int.class, int.class);
                EditorWorld ew = constr.newInstance(w, h);
                ew.getLoader().loadLevel(path);
                ew.drawHud();
                Greenfoot.setWorld(ew);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
