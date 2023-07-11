package com.tinocs.javafxengine;

import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.image.Image;

/**
 * A class for caching images in javafx. The cache is thread safe.
 * @author Ted_McLeod
 *
 */
public class ImageCache {
	
	private static final ConcurrentHashMap<String, Image> IMG_CACHE = new ConcurrentHashMap<>();
	
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
    	if (!IMG_CACHE.containsKey(url)) IMG_CACHE.put(url, new Image(url));
    	return IMG_CACHE.get(url);
    }
    
    public static void clearCache() {
    	IMG_CACHE.clear();
    }
}
