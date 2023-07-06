package com.tinocs.javafxengine;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * <p>A World is an extension of a Pane that holds Actor objects (Actor extends ImageView).
 * Since a World extends Pane, it already keeps track of its children Nodes.
 * You can use getObjects(cls) that returns a list of children of type cls.</p>
 * 
 * <p>To add actors to the world you can use the add(Actor actor) method.</p>
 *
 * <p>A World contains the following features:</p>
 * <ul>
 *   <li> Every frame, while the timer is running, act() is called on the world and on each actor in the world.</li>
 *	 <li> The timer can be started or stopped using the start() or stop() methods respectively.</li>
 *	 <li> The world keeps track of keys that are currently pressed.</li>
 *	 <li> Code that places actors in particular locations based on the size of the world should be written in the
 *        onDimensionsInitialized() method rather than the constructor because getWidth() and getHeight() will return
 *        0 in the constructor, but onDimensionsInitialized() is called exactly when getWidth() and getHeight() will
 *        actually return the correct values.Variable(s) that keep track of whether the width and height have been set.</li>
 * </ul>
 *
 * @author Ted_McLeod
 *
 */
public abstract class World extends Pane {
	private AnimationTimer timer;
	private boolean isStopped;
	private Set<KeyCode> keysPressed;
	private boolean widthIsSet;
	private boolean heightIsSet;


	/**
     * Creates a instance of a World.
	 */
	public World() {
		keysPressed = new HashSet<>();
		isStopped = true;
		
		widthIsSet = false;
		heightIsSet = false;

		widthProperty().addListener((ob, ov, nv) -> {
			if (!widthIsSet) {
				widthIsSet = true;
				if (widthIsSet && heightIsSet) {
					onDimensionsInitialized();
				}
			}
		});

		heightProperty().addListener((ob, ov, nv) -> {
			if (!heightIsSet) {
				heightIsSet = true;
				if (widthIsSet && heightIsSet) {
					onDimensionsInitialized();
				}
			}
		});
		
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				act(now);
				for (Actor a : getObjects(Actor.class)) {
					if (a.getWorld() != null) a.act(now);
				}
			}
		};

		sceneProperty().addListener((ob, ov, nv) -> {
			if (nv != null) {
				Platform.runLater(() -> {
					requestFocus();
				});
			}
		});
		
		setOnKeyPressed(e -> {
			keysPressed.add(e.getCode());
			onKeyPressed(e);
		});
		
		setOnKeyReleased(e -> {
			keysPressed.remove(e.getCode());
			onKeyReleased(e);
		});
	}

	/**
	 * Called when a key is released. Override this to do some action
	 * when a key is released.
	 * @param e the KeyEvent
	 */
	public void onKeyReleased(KeyEvent e) {
		
	}

	/**
	 * Called when a key is pressed. Override this to do some action
	 * when a key is pressed.
	 * @param e the KeyEvent
	 */
	public void onKeyPressed(KeyEvent e) {
		
	}

	/**
	 * Starts the timer that calls the act method on the world
	 * and on each Actor in the world each frame.
	 */
	public void start() {
		timer.start();
		isStopped = false;
	}

	/**
	 * Stops the timer that calls the act method on the world
	 * and on each Actor in the world each frame.
	 */
	public void stop() {
		timer.stop();
		isStopped = true;
	}

	/**
	 * Returns whether or not the world's timer is stopped
	 * @return true if the timer is stopped and false otherwise
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 * Adds the given actor to the world
	 * @param actor the actor to add.
	 */
	public void add(Actor actor) {
		getChildren().add(actor);
	}

	/**
	 * Removes the given actor from the world.
	 * @param actor The actor to remove.
	 */
	public void remove(Actor actor) {
		getChildren().remove(actor);
	}
	
	/**
	 * removes all the actors in the collection from this world.
	 * @param actors the collection of actors to remove
	 */
	public void removeAll(Collection<? extends Actor> actors) {
		getChildren().removeAll(actors);
	}

	/**
	 * Returns a list of all the actors in the world of the given class.
	 * 
	 * @param <A> the type of actors returned in the list (will also be the class represented by cls)
	 * @param cls The Class object representing the type of actor that will be in the list. To get all actors, pass Actor.class
	 * @return a list of all the actors in the world of the given class.
	 */
	public <A extends Actor> List<A> getObjects(Class<A> cls) {
		List<A> list = new LinkedList<A>();
		for (Node n : getChildren()) {
			if (cls.isInstance(n)) {
				list.add(cls.cast(n));
			}
		}
		return list;
	}

	/**
	 * Returns a list of all actors of the given class containing the given x, y
	 * @param <A> the type of actors returns in the list (will also be the class represented by cls)
	 * @param x the x value of the contained point
	 * @param y the y values of the contained point
	 * @param cls The Class object representing the type of actor that must contain the point.
	 *        To include all actors that contain the point, pass Actor.class
	 * @return a list of Actors of the given class that contain the given point
	 */
	public <A extends Actor> List<A> getObjectsAt(double x, double y, Class<A> cls) {
		List<A> list = new LinkedList<>();
		for (A a : getObjects(cls)) {
			if (a.getBoundsInParent().contains(x, y)) {
				list.add(a);
			}
		}
		return list;
	}

	/**
	 * Returns true if the given key is pressed and false otherwise.
	 * @param code The KeyCode for the key that is being checked
	 * @return true if the given key is currently pressed and false otherwise.
	 */
	public boolean isKeyPressed(KeyCode code) {
		return keysPressed.contains(code);
	}

	/**
	 * Subclasses should override this. This method is called after the world width and height have been initialized.
	 * This method would be a good place to add actors to your world because getWidth() and getHeight()
	 * will return the real dimensions of the world instead of 0.
	 */
	public abstract void onDimensionsInitialized();

	/**
	 * This method is called every frame once start has been called.
	 * @param now the current time in nanoseconds.
	 */
	public abstract void act(long now);

}
