package mp_demo_game;


import java.util.ArrayList;
import java.util.Arrays;

import com.tinocs.mp.javafxengine.LocalActor;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Player extends LocalActor {
	
	private double dx;
	private double dy;
	private double ddy = 1;
	private int[] rgb;
	private double blueMult;
	
	public Player(String clientId) {
		super(clientId);
		setImage("resources/CharacterLeft_Standing.png");
		setFitWidth(40);
		setFitHeight(40);
		dx = 0;
		dy = 0;
		setOtherClass(OtherPlayer.class);
		rgb = getRandomRGBFilter();
		blueMult = getRandomBlueMultiplier();
		filterImage();
	}

	@Override
	public void act(long now) {
		handleUserInput();
		move();
	}
	
	public void move() {
		
		if (getY() < getWorld().getHeight() - getHeight()) dy += ddy;
		if (dx != 0 || dy != 0) {
			move(dx, dy);
			handleEdges();
		}
	}
	
	public void handleEdges() {
		if (getX() < -getWidth() / 2) {
			setX(getWorld().getWidth() - getWidth() / 2);
		} else if (getX() > getWorld().getWidth() - getWidth() / 2) {
			setX(-getWidth() / 2);
		}
		if (getY() + getHeight() > getWorld().getHeight()) {
			dy = 0;
			setY(getWorld().getHeight() - getHeight());
		}
	}
	
	public boolean isOnGround() {
		return getY() + getHeight() == getWorld().getHeight();
	}
	
	public void handleUserInput() {
		if (getWorld().isKeyPressed(KeyCode.LEFT)) {
			dx = -3;
		} else if (getWorld().isKeyPressed(KeyCode.RIGHT)) {
			dx = 3;
		} else if (getWorld().isKeyPressed(KeyCode.DOWN)) {
			dx = 0;
		} else if (getWorld().isKeyPressed(KeyCode.UP) && isOnGround()) {
			dy = -20;
		}
	}
	
	private double getRandomBlueMultiplier() {
		return Math.random();
	}
	
	private int[] getRandomRGBFilter() {
		int[] rgb = new int[3];
		ArrayList<Integer> indexes = new ArrayList<>(Arrays.asList(0, 1, 2));
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = indexes.remove((int)(Math.random() * indexes.size()));
		}
		return rgb;
	}
	
	public double getBlueMultiplier() {
		return blueMult;
	}
	
	public int[] getRGBFilter() {
		return rgb;
	}
	
	public void filterImage() {
		WritableImage img = new WritableImage(getImage().getPixelReader(), (int)getImage().getWidth(), (int)getImage().getHeight());
		PixelReader reader = img.getPixelReader();
		PixelWriter writer = img.getPixelWriter();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				Color c = reader.getColor(x, y);
				int redIndex = rgb[0];
				double redVal;
				if (redIndex == 0) redVal = c.getRed();
				else if (redIndex == 1) redVal = c.getGreen();
				else redVal = c.getBlue() * blueMult;
				
				int greenIndex = rgb[1];
				double greenVal;
				if (greenIndex == 0) greenVal = c.getRed();
				else if (greenIndex == 1) greenVal = c.getGreen();
				else greenVal = c.getBlue() * blueMult;
				
				int blueIndex = rgb[2];
				double blueVal;
				if (blueIndex == 0) blueVal = c.getRed();
				else if (blueIndex == 1) blueVal = c.getGreen();
				else blueVal = c.getBlue() * blueMult;
				
				writer.setColor(x, y, new Color(redVal, greenVal, blueVal, c.getOpacity()));
			}
		}
		setImage(img);
	}
}
