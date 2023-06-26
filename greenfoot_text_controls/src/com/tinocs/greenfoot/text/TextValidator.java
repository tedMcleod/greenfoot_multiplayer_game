package com.tinocs.greenfoot.text;

/**
 * Classes that implement the TextValidator interface define the rules for determining if a given string
 * is valid.
 * @author Ted_McLeod
 *
 */
public interface TextValidator {
	
	/**
	 * Returns true if the given string is valid and false otherwise.
	 * @param str the string for which validity is being determined.
	 * @return true if the given string is valid and false otherwise.
	 */
    boolean isValid(String str);
}
