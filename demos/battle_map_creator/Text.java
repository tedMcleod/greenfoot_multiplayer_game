import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)



/**
 * This class can be used to display text on screen and 
 * provides many methods for customizing the appearance of the text.
 * 
 * @author Ted McLeod
 * @version 1.0
 */
public class Text extends Actor {
    /**
     * maintains constant center x value (default behavior).
     * This effectively means don't do anything special since setImage does not change x normally.
     */
    public static final int ALIGN_CENTER = 0;
    
    /**
     * maintains constant left edge x value.
     * Will change the x value to maintain the position of the left edge.
     */
    public static final int ALIGN_LEFT = 1;
    
    /**
     * maintains constant right edge x value.
     * Will change the x value to maintain the position of the right edge.
     */
    public static final int ALIGN_RIGHT = 2;

    /* This controls what happens when the image changes width
    (i.e. due to changing text). ALIGN_CENTER effectively means
    don't do anything special since setImage does not change x
    normally, but ALIGN_LEFT or ALIGN_RIGHT will change the x 
    value to maintain the position of the left or right edge */
    private int align;

    private String text;
    private int size;
    private Color foreground;
    private Color background;
    private Color outline;

    /**
     * Default constructor.
     * Creates a Text object that with defaults values.
     * text: ""
     * size: 60
     * foreground: null (black)
     * background: null (clear)
     * outline: null (none)
     * align: center
     */
    public Text() {
        this(""); // default text = ""
    }

    /**
     * Creates a Text object that with the given text but default values for all other fields.
     * size: 60
     * foreground: null (black)
     * background: null (clear)
     * outline: null (none)
     * align: center
     * 
     * @param text the text to display
     */
    public Text(String text) {
        this(text, 60); // default size = 60
    }
    
    /**
     * Creates a Text object that with the given number as text but default values for all other fields.
     * size: 60
     * foreground: null (black)
     * background: null (clear)
     * outline: null (none)
     * align: center
     * 
     * @param num the number to display
     */
    public Text(Number num) {
        this(num.toString(), 60); // default size = 60
    }

    /**
     * Creates a Text object that with the given text and size.
     * foreground: null (black)
     * background: null (clear)
     * outline: null (none)
     * align: center
     * 
     * @param text the text to display
     * @param size the font size of the text
     */
    public Text(String text, int size) {
        this(text, size, Color.BLACK); // default foreground = Black
    }
    
    /**
     * Creates a Text object that with the given number and size.
     * foreground: null (black)
     * background: null (clear)
     * outline: null (none)
     * align: center
     * 
     * @param num the number to display
     * @param size the font size of the text
     */
    public Text(Number num, int size) {
        this(num.toString(), size, Color.BLACK); // default foreground = Black
    }

    /**
     * Creates a Text object that with the given text, size, and foreground color (text color).
     * Other values are defaults:
     * background: null (clear)
     * outline: null (none)
     * align: center
     * 
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     */
    public Text(String text, int size, Color foreground) {
        this(text, size, foreground, new Color(0, 0, 0, 0)); // default background = transparent
    }
    
    /**
     * Creates a Text object that with the given number, size, and foreground color (text color).
     * Other values are defaults:
     * background: null (clear)
     * outline: null (none)
     * align: center
     * 
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     */
    public Text(Number num, int size, Color foreground) {
        this(num.toString(), size, foreground, new Color(0, 0, 0, 0)); // default background = transparent
    }

    /**
     * Creates a Text object that with the given text, size, foreground and background color.
     * Other values are defaults:
     * outline: null (none)
     * align: center
     * 
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     */
    public Text(String text, int size, Color foreground, Color background) {
        this(text, size, foreground, background, null); // default outline = null
    }
    
    /**
     * Creates a Text object that with the given number, size, foreground and background color.
     * Other values are defaults:
     * outline: null (none)
     * align: center
     * 
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     */
    public Text(Number num, int size, Color foreground, Color background) {
        this(num.toString(), size, foreground, background, null); // default outline = null
    }

    /**
     * Creates a Text object that with the given text, size, foreground color, background color, and outline color.
     * Alignment is defaulted to center.
     * 
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     */
    public Text(String text, int size, Color foreground, Color background, Color outline) {
        this(text, size, foreground, background, outline, ALIGN_CENTER); // default align = ALIGN_CENTER
    }
    
    /**
     * Creates a Text object that with the given number, size, foreground color, background color, and outline color.
     * Alignment is defaulted to center.
     * 
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     */
    public Text(Number num, int size, Color foreground, Color background, Color outline) {
        this(num.toString(), size, foreground, background, outline, ALIGN_CENTER); // default align = ALIGN_CENTER
    }

    /**
     * Creates a Text object that with the given text, size, foreground color, background color, outline color, and alignment.
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     * @param align the alignment of the text. valid values are: ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT
     */
    public Text(String text, int size, Color foreground, Color background, Color outline, int align) {
        setText(text, size, foreground, background, outline, align);
    }
    
    /**
     * Creates a Text object that with the given number, size, foreground color, background color, outline color, and alignment.
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     * @param align the alignment of the text. valid values are: ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT
     */
    public Text(Number num, int size, Color foreground, Color background, Color outline, int align) {
        setText(num.toString(), size, foreground, background, outline, align);
    }

    /**
     * Sets the text to the given text.
     * @param text the text to display 
     */
    public void setText(String text) {
        setText(text, getSize());
    }
    
    /**
     * Sets the text to the given number.
     * @param num the number to display 
     */
    public void setText(Number num) {
        setText(num.toString(), getSize());
    }

    /**
     * Sets the text to the given text with the given font size.
     * @param text the text to display
     * @param size the font size of the text
     */
    public void setText(String text, int size) {
        setText(text, size, getForeground());
    }
    
    /**
     * Sets the text to the given number with the given font size.
     * @param num the number to display
     * @param size the font size of the text
     */
    public void setText(Number num, int size) {
        setText(num.toString(), size, getForeground());
    }

    /**
     * Sets the text to the given text with the given font size and foregound color.
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     */
    public void setText(String text, int size, Color foreground) {
        setText(text, size, foreground, getBackground());
    }
    
    /**
     * Sets the text to the given number with the given font size and foregound color.
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     */
    public void setText(Number num, int size, Color foreground) {
        setText(num.toString(), size, foreground, getBackground());
    }

    /**
     * Sets the text to the given text with the given font size, foregound color, and background color.
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     */
    public void setText(String text, int size, Color foreground, Color background) {
        setText(text, size, foreground, background, getOutline());
    }
    
    /**
     * Sets the text to the given number with the given font size, foregound color, and background color.
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     */
    public void setText(Number num, int size, Color foreground, Color background) {
        setText(num.toString(), size, foreground, background, getOutline());
    }

    /**
     * Sets the text to the given text with the given font size, foregound color, background color, and outline color.
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     */
    public void setText(String text, int size, Color foreground, Color background, Color outline) {
        setText(text, size, foreground, background, outline, getAlign());
    }
    
    /**
     * Sets the text to the given number with the given font size, foregound color, background color, and outline color.
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     */
    public void setText(Number num, int size, Color foreground, Color background, Color outline) {
        setText(num.toString(), size, foreground, background, outline, getAlign());
    }

    /**
     * Sets the text, size, foreground color, background color, outline color, and alignment.
     * @param text the text to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     * @param align the alignment of the text. valid values are: ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT
     */
    public void setText(String text, int size, Color foreground, Color background, Color outline, int align) {
        this.text = text;
        this.size = size;
        if (foreground == null) foreground = Color.BLACK;
        this.foreground = foreground;
        if (background == null) background = new Color(0, 0, 0, 0);
        this.background = background;
        this.outline = outline;
        setAlign(align); // must use setter here to validate value
        GreenfootImage img;
        if (this.outline == null) {
            img = new GreenfootImage(text, size, foreground, background);
        } else {
            img = new GreenfootImage(text, size, foreground, background, outline);
        }
        if (getImage() != null) {
            img.setColor(getImage().getColor());
            img.setFont(getImage().getFont());
            img.setTransparency(getImage().getTransparency());
        }
        setImage(img);
    }
    
    /**
     * Sets the number, size, foreground color, background color, outline color, and alignment.
     * @param num the number to display
     * @param size the font size of the text
     * @param foreground the foreground color (text color - null means black)
     * @param background the background color (null means clear)
     * @param outline outline color (null means no outline)
     * @param align the alignment of the text. valid values are: ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT
     */
    public void setText(Number num, int size, Color foreground, Color background, Color outline, int align) {
        setText(num.toString(), size, foreground, background, outline, align);
    }

    /**
     * Sets the image and adjusts the location according to the new size of the image and the alignment settings
     * @param image the new image
     */
    @Override
    public void setImage(GreenfootImage image) {
        GreenfootImage curImg = getImage();
        if (curImg != null && getWorld() != null && getAlign() != ALIGN_CENTER) {
            int curWidth = curImg.getWidth();
            int nextWidth = image.getWidth();
            int x = getX();
            if (curWidth != nextWidth) {
                if (getAlign() == ALIGN_LEFT) {
                    int curLeftX = x - curWidth / 2;
                    x = curLeftX + nextWidth / 2;
                } else {
                    int curRightX = x + curWidth / 2;
                    x = curRightX - nextWidth / 2;
                }
            }
            setLocation(x, getY());
        }
        super.setImage(image);
    }

    /**
     * Sets the alignment.
     * @param align the alignment of the text. valid values are: ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT
     */
    public void setAlign(int align) {
        if (align != ALIGN_LEFT && align != ALIGN_RIGHT && align != ALIGN_CENTER) {
            align = ALIGN_CENTER;
        }
        this.align = align;
    }

    /**
     * returns the alignment.
     * @return the alignment
     */
    public int getAlign() {
        return align;
    }

    /**
     * returns the text.
     * @return the text
     */
    public String getText() {
        return text;
    }
    
    /**
     * returns the text parsed as an int
     * @return the text parsed as an int
     * @throws NumberFormatException if the text is not a parsable integer.
     */
    public int getIntVal() throws NumberFormatException {
        return Integer.parseInt(text);
    }

    /**
     * returns the text parsed as a double
     * @return the text parsed as a double
     * @throws NumberFormatException if the text is not a parsable double.
     */
    public double getDoubleVal() throws NumberFormatException {
        return Double.parseDouble(text);
    }

    /**
     * returns the text parsed as a long
     * @return the text parsed as a long
     * @throws NumberFormatException if the text is not a parsable long.
     */
    public double getLongVal() throws NumberFormatException {
        return Long.parseLong(text);
    }

    /**
     * sets the font size.
     * @param size the font size
     */
    public void setSize(int size) {
        this.size = size;
        setText(getText());
    }

    /**
     * returns the font size.
     * @return the font size
     */
    public int getSize() {
        return size;
    }

    /**
     * sets the foreground (text) color.
     * @param foreground the foreground color
     */
    public void setForeground(Color foreground) {
        this.foreground = foreground != null ? foreground : Color.BLACK;
        setText(getText());
    }

    /**
     * returns the foreground (text) color.
     * @return the foreground color
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * sets the background color.
     * @param background the background color
     */
    public void setBackground(Color background) {
        this.background = background != null ? background : new Color(0, 0, 0, 0);
        setText(getText());
    }

    /**
     * returns the background color.
     * @return the background color
     */
    public Color getBackground() {
        return background;
    }

    /**
     * sets the outline color.
     * @param outline the outline color
     */
    public void setOutline(Color outline) {
        this.outline = outline;
        setText(getText());
    }

    /**
     * returns the outline color.
     * @return the outline color
     */
    public Color getOutline() {
        return outline;
    }
}
