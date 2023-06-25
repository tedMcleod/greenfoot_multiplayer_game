package mp_demo_game;

import java.util.Scanner;

import com.tinocs.mp.javafxengine.MPActor;
import com.tinocs.mp.javafxengine.MPWorld;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class OtherPlayer extends MPActor {
	
	private boolean imageIsFiltered = false;

	public OtherPlayer(String actorId, String clientId) {
		super(actorId, clientId);
		setImage("resources/CharacterLeft_Standing.png");
		setFitWidth(40);
		setFitHeight(40);
	}
	
	public void filterImage(int[] rgb, double blueMult) {
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

	@Override
	public void act(long arg0) {
		if (!imageIsFiltered) {
			MPWorld gw = getWorldOfType(MPWorld.class);
			DemoEngineEventHandler eh = (DemoEngineEventHandler)gw.getClient().getEventHandler();
			String colorFilter = eh.getColorFilterForClient(getClientId());
			if (colorFilter != null) {
				Scanner reader = new Scanner(colorFilter);
				int[] rgb = new int[3];
				rgb[0] = reader.nextInt();
				rgb[1] = reader.nextInt();
				rgb[2] = reader.nextInt();
				double blueMult = reader.nextDouble();
				reader.close();
				filterImage(rgb, blueMult);
				imageIsFiltered = true;
			}
		}
		
	}

}
