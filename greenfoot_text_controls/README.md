# Greenfoot Text Controls

This library provides components for simple text controls (Text, Button and TextField)
for use in Greenfoot scenarios.

### Adding to Greenfoot as source files

If you want to add the classes to a greenfoot project directly, so they will be visible
in the greenfoot class hierarchy:

1. Copy the java files from the src folder into the root directory of your greenfoot project.
2. You will need to delete the first line in each file that declares the the class is in a package
   (package com.tinocs.greenfoot.text) since all classes in a greenfoot project have to be in the
   default package.
   
### Adding to Greenfoot as library

If you want to just use these classes as a library so they can be used without filling your project
with lots of classes or if you want to simply make these classes automatically available in all
your greenfoot projects, you can add the jar file to the build path where all your greenfoot projects
will be able to access it.

1. In greenfoot, go to tools -> preferences (might be a different route to get there on mac
   but if you get to the preferences/settings where there is a libraries tab at the top then
   you are in the right place.
2. Click on the libraries tab at the top of the window.
3. Click the add file button (on the right). If you don't see if, expand the window.
4. Select the latest version of the jar from the exports folder (should be named something like
   `greenfoot_text_controls_v1.jar` (v1 means version 1, so the number might be higher later on)

### Building the jar file

To create a new jar file from source code:

1. Double click (open) `build_greenfoot_text_controls.jardesc`
2. If you want to change the version number, simply change the number at the end of the name
3. Click Finish
4. The jar will be saved in the exports folder


###Generating Javadocs

To generate the javadocs:

1. right click on the `greenfoot_text_controls_javadoc.xml` and
   Run As... -> Ant Build
2. If you get an error complaining you can't run ant on a version of java less than 11, then


  * Right click on `ant_jar_build.xml` and Run As... -> External Tools Configurations...
  * Go to the JRE tab and select a Separate JRE that is 11 or higher
  * Your ant build tool will run on that separate JRE, but the code in your jar will still be
	  compiled using the JDK chosen in the build settings of your project