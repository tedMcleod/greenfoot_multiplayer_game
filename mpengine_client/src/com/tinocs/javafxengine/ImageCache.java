package com.tinocs.javafxengine;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class ImageCache {
	
	private static Map<String, Image> imgCache = new HashMap<>();

	/**
     * Return the image at the given url.
	 * If the url is not already associated with a cached image, create the image and cache it.
     * In either case, return the cached image.
     * See {@link javafx.scene.image.Image#Image(String)}
     * @param url the path to the image resource. For example, if the image is in the
     *        images package and is named pic.png, the url would be "images/pic.png"
     * @return the image at the given url.
     */
    public static Image getImage(String url) {
    	if (!imgCache.containsKey(url)) imgCache.put(url, new Image(url));
    	return imgCache.get(url);
    }
    
    public static void clearCache() {
    	imgCache = new HashMap<>();
    }
}
