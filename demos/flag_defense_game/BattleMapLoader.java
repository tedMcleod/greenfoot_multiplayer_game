import greenfoot.*;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * This is a specialized level loader that has convience methods for defining the symbols associated with common classes in Lode Runner.
 * It also can load a level given just the level number.
 * To load level N, it loads the Nth file in the levels folder when sorted in alphabetical order.
 * 
 * @author Ted McLeod
 * @version 3/24/2022
 */
public class BattleMapLoader extends LevelLoader {
    
    /** The symbol representing a wall **/
    public static final String WALL = "W";
    
    /** The symbol for a flag */
    public static final String FLAG = "F";
    
    /** The symbols representing player start locations **/
    public static final String PLAYER_1_START_LOC = "1";
    public static final String PLAYER_2_START_LOC = "2";
    public static final String PLAYER_3_START_LOC = "3";
    public static final String PLAYER_4_START_LOC = "4";
    
    /** A constructor that creates a TankLevelLoader to load levels in the given world.
     * 
     * @param world the world to load levels in
     */
    public BattleMapLoader(World world) {
        super(world);
    }
    
    /**
     * sets the class that should be used to represent walls
     * 
     * @param cls the class that represents walls
     */
    public void setWallClass(Class<? extends Actor> cls) {
        defineType(WALL, cls);
    }
    
    /**
     * sets the class that should be used to represent player 1 start location
     * 
     * @param cls the class that represents player 1 start location
     */
    public void setPlayer1StartLocClass(Class<? extends Actor> cls) {
        defineType(PLAYER_1_START_LOC, cls);
    }
    
    /**
     * sets the class that should be used to represent player 2 start location
     * 
     * @param cls the class that represents player 2 start location
     */
    public void setPlayer2StartLocClass(Class<? extends Actor> cls) {
        defineType(PLAYER_2_START_LOC, cls);
    }
    
    /**
     * sets the class that should be used to represent player 3 start location
     * 
     * @param cls the class that represents player 3 start location
     */
    public void setPlayer3StartLocClass(Class<? extends Actor> cls) {
        defineType(PLAYER_3_START_LOC, cls);
    }
    
    /**
     * sets the class that should be used to represent player 4 start location
     * 
     * @param cls the class that represents player 4 start location
     */
    public void setPlayer4StartLocClass(Class<? extends Actor> cls) {
        defineType(PLAYER_4_START_LOC, cls);
    }
    
    /**
     * sets the class that should be used to represent player 4 start location
     * 
     * @param cls the class that represents player 4 start location
     */
    public void setFlagClass(Class<? extends Actor> cls) {
        defineType(FLAG, cls);
    }
    
    /**
     * Gets an array of all the levels in the levels folder or returns null if there is no levels folder.
     * 
     * @return an array of all the levels in the levels folder or null if no levels folder exists
     */
    private static String[] getLevels() {
        File levelsDir = new File(ROOT + "/levels");
        String[] levels = null;
        if (!levelsDir.exists()) {
            System.out.println("Warning: This project has no levels folder!");
        } else {
            File[] files = levelsDir.listFiles();
            ArrayList<File> levelsList = new ArrayList<>();
            for (File f : files) {
                if (f.getName().endsWith(".txt")) levelsList.add(f);
            }
            
            Collections.sort(levelsList, new Comparator<File>() {
                public int compare(File a, File b) {
                    return a.getName().compareTo(b.getName());
                }
            });
            
            levels = new String[levelsList.size()];
            for (int i = 0; i < levelsList.size(); i++) {
                levels[i] = levelsList.get(i).getAbsolutePath();
            }
        }
        return levels;
    }
    
    /**
     * returns the number of levels in the levels folder
     * 
     * @return the number of levels in the levels folder.
     */
    public static int numLevels() {
        return getLevels().length;
    }
    
    /**
     * Loads the given level with the boundaries of the level offset by the given (offX, offY)
     */
    public void loadLevel(int level, int offX, int offY) {
        loadLevel(getLevelPath(level), offX, offY);
    }

    /**
     * returns the file path to the given level, where levels are the files in the levels folder of the project.
     * For example, LodeRunnerLevelLoader.getLevelPath(3) would return the path to the third file in the levels folder
     * when they are listed alphabetically. If there is no such file, an exception will be thrown.
     * 
     * @return the file path to the given level
     */
    public static String getLevelPath(int level) {
        return getLevels()[level - 1];
    }
    
    public static String getLevelName(String levelPath) {
        int lastSlash = levelPath.lastIndexOf("/");
        int lastPeriod = levelPath.lastIndexOf(".");
        return levelPath.substring(lastSlash + 1, lastPeriod);
    }
   
}
