package com.tinocs.greenfoot.text;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Reader;

// Greenfoot classes
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;

/**
 * The TextField class repesents a text object that can be edited by clicking on it and typing text.
 * <ul>
 * 		<li>If a TextField starts with prefilled text, it will be cleared the first time a character is typed.</li>
 * 		<li>Backspace can be used to delete characters.</li>
 * 		<li>Holding down ctrl while pressing v will paste the contents of the clipboard (regardless of operating system).</li>
 * 		<li>If the text field is selected, it is outlined with the selected color (default red), otherwise the border is
 * 			the border color (default black).</li>
 * </ul>
 *<h3> Example Usage:</h3>
<pre>
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
</pre>
 * 
 * @author Ted McLeod 
 * @version 6/25/2023
 */
public class TextField extends Text {

    private boolean isSelected;
    private int width;
    private TextValidator validator;
    private boolean hasTyped = false;
    private Color borderColor = Color.BLACK;
    private Color selectedColor = Color.RED;

    /**
     * Create a blank TextField with width 100.
     */
    public TextField() {
        this(100);
    }

    /**
     * Create a blank TextField with the given width.
     * 
     * @param width the width of the text field
     */
    public TextField(int width) {
        this.width = width;
        isSelected = false;
        validator = s -> true;
        updateImage();
    }

    /** Controls the actions a TextField takes every frame.
     * 	<ul>
     * 		<li> if the mouse is clicked on this text field and it is not selected, it is selected and all other
     *           text fields are deselected.</li>
     *      <li> If the mouse is clicked anywhere not on this text field and it is currently selected,
     *           it is deselected. </li>
     *      <li> If this text field is selected, its text is updated to reflect what the user typed.</li>
     *      <li> If the TextValidator says the new text is invalid, it is not updated.</li>
     *      <li> Holding down ctrl while pressing v will paste the contents of the clipboard.</li>  
     *	</ul>
     */
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            if (!isSelected) Greenfoot.getKey(); // toss out keys pressed before selection
            setSelected(true);
            for(TextField field : getWorld().getObjects(TextField.class)) {
                if (field != this) field.setSelected(false);
            }
        } else {
            MouseInfo mi = Greenfoot.getMouseInfo();
            if (mi != null) {
                if (mi.getClickCount() > 0) {
                    setSelected(false);
                }
            }
        }
        if (isSelected) {
            String key = Greenfoot.getKey();
            if(key != null) {
            	if (key.equals("space")) key = " ";
                if (key.equals("backspace")) {
                    if (getText().length() > 0) {
                        setText(getText().substring(0, getText().length() - 1));
                    }
                } else if (Greenfoot.isKeyDown("control")) {
                    if (key.equals("v")) {
                        paste();
                    }
                } else if (key.length() == 1) {
                    boolean clearText = !hasTyped;
                    if (Greenfoot.isKeyDown("shift")) key = key.toUpperCase();
                    String str = clearText ? key : getText() + key;
                    if (validator.isValid(str)) {
                        hasTyped = true;
                        setText(str);
                    }
                }
            }
        }
    }

    /**
     * Paste the contents of the clipboard into this text field.
     */
    public void paste() {
        try {
        	Reader reader = DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this));
            String str = "";
            int next = reader.read();
            while (next != -1) {
                str += (char)next;
                next = reader.read();
            }
            str = hasTyped ? getText() + str : str;
            if (validator.isValid(str)) setText(str);
        }
        catch (UnsupportedFlavorException | IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * Set the width of this text field to the given width.
     * @param width the width of the text field
     */
    public void setWidth(int width) {
        this.width = width;
        updateImage();
    }

    /**
     * returns the width of this text field.
     * @return the width of this text field.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the selected status of this text field.
     * 
     * @param selected whether or not this field should be selected
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
        updateImage();
    }

    /**
     * returns whether or not this text field is selected.
     * @return true if this text field is selected and false otherwise.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * sets the text validator for this text field. The text validator determines
     * what text is allowed and the text is not updated if it would be invalid.
     * @param v the text validator
     */
    public void setValidator(TextValidator v) {
        validator = v;
    }

    /**
     * returns the current text validator.
     * @return the current text validator.
     */
    public TextValidator getValidator() {
        return validator;
    }
    
    /**
     * Sets the border color of this text field.
     * 
     * @param color the new border color
     */
    public void setBorderColor(Color color) {
        borderColor = color;
        updateImage();
    }

    /**
     * returns the border color of this text field.
     * @return the border color of this text field.
     */
    public Color getBorderColor() {
        return borderColor;
    }
    
    /**
     * Sets the color of the border around this text field when selected.
     * 
     * @param color the new selected color
     */
    public void setSelectedColor(Color color) {
        borderColor = color;
        updateImage();
    }

    /**
     * returns the selected border color of this text field.
     * @return the selected border color of this text field.
     */
    public Color getSelectedColor() {
        return selectedColor;
    }

    /**
     * In addition to drawing the current text, this draws the rectangular box
     * based on the current width, text size and background color. If the box is
     * selected, it is outlined with the selected color (default red), otherwise the border is
	 * the border color (default black).
     */
    @Override
    protected void updateImage() {
        super.updateImage();
        if (width > 0) {
            GreenfootImage txtImg = getImage();
            GreenfootImage bg = new GreenfootImage(width, getSize() + 2);
            bg.setColor(getBackground());
            bg.fill();
            bg.setColor(isSelected ? selectedColor : borderColor);
            bg.drawRect(0, 0, bg.getWidth() - 1, bg.getHeight() - 1);
            bg.drawImage(txtImg, 1, 1);
            setImage(bg);
        }
    }
}
