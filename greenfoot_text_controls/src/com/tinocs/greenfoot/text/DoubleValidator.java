package com.tinocs.greenfoot.text;

/**
 * DoubleValidator considers a string valid if it can be parsed as a double (Double.parseDouble(str) is true)
 * @author Ted_McLeod
 */
public class DoubleValidator implements TextValidator {
	
	/**
	 * returns true if the String can be parsed as a double and false otherwise
	 * @return true if the String can be parsed as a double and false otherwise
	 */
    public boolean isValid(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException err) {
            return false;
        }
    }
}
