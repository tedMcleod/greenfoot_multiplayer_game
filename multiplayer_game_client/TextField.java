import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Write a description of class TextField here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TextField extends Text {

    private boolean isSelected;
    private int width;
    private TextValidator validator;
    private boolean hasTyped = false;

    public TextField() {
        this(100);
    }

    public TextField(int w) {
        width = w;
        isSelected = false;
        validator = s -> true;
        updateImage();
    }

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
                    String str = clearText ? key : getText() + key;
                    if (validator.isValid(str)) {
                        hasTyped = true;
                        setText(str);
                    }
                }
            }
        }
    }

    public void paste() {
        try {
            StringReader reader = (StringReader)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.plainTextFlavor);
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

    public void setWidth(int w) {
        width = w;
        updateImage();
    }

    public int getWidth() {
        return width;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        updateImage();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setValidator(TextValidator v) {
        validator = v;
    }

    public TextValidator getValidator() {
        return validator;
    }

    @Override
    protected void updateImage() {
        super.updateImage();
        if (width > 0) {
            GreenfootImage txtImg = getImage();
            GreenfootImage bg = new GreenfootImage(width, getSize() + 2);
            bg.setColor(getBackground());
            bg.fill();
            bg.setColor(isSelected ? Color.RED : Color.BLACK);
            bg.drawRect(0, 0, bg.getWidth() - 1, bg.getHeight() - 1);
            bg.drawImage(txtImg, 1, 1);
            setImage(bg);
        }
    }
}
