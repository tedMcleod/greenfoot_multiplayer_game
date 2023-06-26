package scenario;

import com.tinocs.greenfoot.text.IntegerValidator;
import com.tinocs.greenfoot.text.TextField;

import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.World;

public class MyWorld extends World {

	public MyWorld() {
		super(600, 400, 1);
		setBackground("space1.jpg");
		Greenfoot.playSound("252840__csaszi__macpro-startup.mp3");
		
		// TextField Example
		TextField tf = new TextField(200);
		tf.setText("Enter Text");
		tf.setBackground(Color.WHITE);
		tf.setForeground(Color.RED);
		tf.setSize(20);
		addObject(tf, getWidth() / 2, getHeight() / 2);
		
		// TextValidator example
		TextField nf = new TextField(150);
		nf.setText("Enter An Integer");
		nf.setBackground(Color.WHITE);
		nf.setForeground(Color.GREEN);
		nf.setSize(20);
		nf.setValidator(new IntegerValidator());
		addObject(nf, getWidth() / 2, 3 * getHeight() / 4);
	}

}
