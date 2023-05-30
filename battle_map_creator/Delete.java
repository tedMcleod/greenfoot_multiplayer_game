import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * A tool for deleting level elements in the editor.
 * 
 * @author Ted McLeod 
 * @version 4/14/2023
 */
public class Delete extends Actor {
    public <A extends Actor> List<A> getTouchedActors(Class<A> cls) {
        return getIntersectingObjects(cls);
    }
}
