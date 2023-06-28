package com.tinocs.javafxengine;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * <p>Actor is an abstract base class for sprites in an arcade style game.  Because Actor
 * extends ImageView, you have access to all the ImageView commands such as:</p>
 * <ul>
 *     <li> getX(), getY(), setX(), setY()</li>
 *     <li> setImage()</li>
 *     <li> getFitHeight(), getFitWidth(), setFitWidth(), setFitHeight()</li>
 * </ul>
 * @author Ted_McLeod
 *
 */
public abstract class Actor extends ImageView {
	
	private ChangeListener<Parent> addedToWorldListener = new ChangeListener<Parent>() {
		@Override
		public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
			if (newValue instanceof World) addedToWorld();
		}
	};
	
	/**
	 * Create an instance of an Actor.
	 */
	public Actor() {
		super();
		parentProperty().addListener(addedToWorldListener);
	}

	
	public Actor(Image image) {
		super(image);
		parentProperty().addListener(addedToWorldListener);
	}

	public Actor(String url) {
		super(url);
		parentProperty().addListener(addedToWorldListener);
	}

	/**
	 * Moves this actor by the given dx and dy.
	 * @param dx the amount to move horizontally (change in x)
	 * @param dy the amount to move vertically (change in y)
	 */
	public void move(double dx, double dy) {
		setX(getX() + dx);
		setY(getY() + dy);
	}
	
	/**
	 * returns the world this actor is in, or null if it is not in a world.
	 * @return the world this actor is in, or null if it is not in a world
	 */
	public World getWorld() {
		Node parent = getParent();
		return parent instanceof World ? (World)parent : null;
	}
	
	/**
     * Return the world this actor is in typecasted to the given type,
     * or null if this actor is not in a world of the given type.
     * @param <W> the type of World returned
     * @param cls The class object representing the type of world to return (Example: MyWorld.class)
     * @return the world this actor is in typecasted to the given type,
     *         or null if this actor is not in a world of the given type.
     */
    public <W extends World> W getWorldOfType(Class<W> cls) {
    	World world = getWorld();
    	if (world != null && cls.isInstance(getWorld())) return cls.cast(world);
    	return null;
    }
	
	/**
	 * Returns The width of the current image of this actor.
	 * @return the width of the current image of this actor, taking into account any transformations.
	 */
	public double getWidth() {
		return getBoundsInParent().getWidth();
	}
	
	/**
	 * Returns The height of the current image of this actor.
	 * @return the height of the current image of this actor, taking into account any transformations.
	 */
	public double getHeight() {
		return getBoundsInParent().getHeight();
	}
	
	/**
	 * Returns a list of the actors of a given type intersecting this actor.
	 * Note that an actor never includes itself in the list of intersecting actors.
	 * @param <A> the class of intersecting actors that will be in the returned list
	 * @param cls The type of intersecting actors that should be in the list
	 * @return a list of all actors of the given type intersecting this actor
	 */
	public <A extends Actor> List<A> getIntersectingObjects(Class<A> cls) {
		ArrayList<A> list = new ArrayList<>();
		for (A a : getWorld().getObjects(cls)) {
			if (a != this && a.getBoundsInParent().intersects(getBoundsInParent())) list.add(a);
		}
		return list;
	}
	
	/**
	 * Returns one actor of the given class that is intersecting this actor (other than itself).
	 * @param <A> the class of intersecting actor that will be in the returned
	 * @param cls the type of actor to return
	 * @return an intersecting actor of the given class, or null if no such actor
	 */
	public <A extends Actor> A getOneIntersectingObject(Class<A> cls) {
		for (A a : getWorld().getObjects(cls)) {
			if (a != this && a.getBoundsInParent().intersects(getBoundsInParent())) return a;
		}
		return null;
	}
	
	/**
	 * This method is called every frame once start has been called on the world.
	 * @param now the current time in nanoseconds.
	 */
	public abstract void act(long now);
	
	/**
	 * This method is called when an actor is added to the world and should
	 * be overridden in subclasses as desired.
	 */
	public void addedToWorld() {
		// meant to be overridden.
	}
	
	/**
	 * Set the image of this actor to the image at the given url.
	 * The image will be loaded by {@link com.tinocs.javafxengine.ImageCache#getImage(String)}
	 * which cache the image. If you don't want the image cached, use the method
	 * {@link com.tinocs.javafxengine.Actor#setImageNoCache(String)}
	 * @param url the url of the image file. For example, if the image is in the
     *        images package and is named "pic.png", the url would be "images/pic.png"
	 */
	public void setImage(String url) {
		setImage(ImageCache.getImage(url));
	}
	
	/**
	 * Set the image of this actor to the image at the given url and do not cache the image.
	 * @param url
	 */
	public void setImageNoCache(String url) {
		setImage(new Image(url));
	}

}
