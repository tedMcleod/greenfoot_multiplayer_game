This is a base project for editing and running a greenfoot program from eclipse.
To use this project, you should:
1. make a copy of this project
2. Create classes in the scenario package. Just like in greenfoot, you will generally make classes
   that inherit from World or Actor, but you are not restricted to that. You can even make other
   packages if you wish. If you make other packages, just be aware that you won't be able to have
   those packages if you were to copy your project into a real greenfoot project.
3. If you want to change which world will be the starting world, open standalone.properties and change
   the main.class property to the full name of your World class. For example, if you have
   a class named TitleWorld in a package named scenario, you would write:
   main.class=scenario.TitleWorld
4. There are other properties that can also be changed, such as hiding the controls or locking the
   speed of the scenario.
5. Just like in Greenfoot, you should put images in the images package and sounds in the sounds package.
   There is one image and one sound included in this base project. Feel free to remove them if you don't
   use them.
6. To set the image of an Actor subclass or background of a World subclass, you should do so in the constructor.

Notes on how I set up this project (you generally won't need to do this if you just copy this project):
1. Create a new java scenario in Greenfoot.
2. Go to scenario --> Share...
3. Select the middle option (standalone application).
4. Uncheck both the lock scenario and hide controls checkboxes
5. Choose a location to save the jar file.
6. change file name extension from .jar to .zip so you can see the contents.
7. On Mac you will have to unzip the zip file, but on windows you can just double click to peek inside.
8. Copy the standalone.properties file to a location outside the zip file.
9. Delete the standalone.properties file from the zip file (or the folder you unzipped it to)
10. Delete MyWorld.class from the zip file (or the folder you unzipped it to)
11. If you unzipped the zip file, then delete the zip file and compress the folder you modified the contents of.
12. You should now have a zip file that contains everything the original jar containedminus MyWorld.class
    and standalone.properties
13. Change the .zip extension back to .jar
14. You now have a jar file that can be used as greenfoot library and you have extracted the standalone.properties
    so you will be able to place it somewhere where it can be edited easily in eclipse.
15. Create a new project in Eclipse using the jdk and javafx libraries used by greenfoot (to ensure version is the same).
    If you use a more recent version and then try to export jar files from this project, they will be compiled in a more
    recent version of java which will not work if used in the greenfoot app. Thus, it is best to use the same version.
    On windows, the jdk and javafx libraries can be found at:
    C:\Program Files\Greenfoot\jdk
    C:\Program Files\Greenfoot\lib\javafx\lib
16. make sure the Java Compiler compliance level is set to 11 since that is the version greenfoot uses.
    Note, this could change in the future, but most likely 11 will be high enough for a long time since
    it would only break if there was a major feature added that is used by greenfoot and that breaks
    compatibility as when modules were added.
16. In module-info.java be sure to require all the javafx modules and open your module (see module-info.java)
17. Copy the jar file you created containing the greenfoot library to your project (I put it in a lib folder
    in the root directory - not in src). Technically you could add it to the modulepath even if its in an outside
    folder, but I prefer to have library jars in the project so it is self-contained.
18. Configure the build path to add the greenfoot library jar to the modulepath
19. In module-info.java add a requires base.greenfoot.proj
20. copy the standalone.properties you saved earlier to the src folder
21. Create a package and a driver class inside that package (since we are using module-info, no classes can be
    in the default package).
22. In the main method of the driver class, call
    GreenfootScenarioApplication.launch(GreenfootScenarioApplication.class, args);
    (you will have to import greenfoot.export.GreenfootScenarioApplication of course)
23. Create a class that extends World (it could be in the same package as the driver or in another package).
    I chose to put it in a package named scenario with the idea that that package will contain all the scenario
    classes. The class that extends World must have a default constructor.
24. Open standalone.properties and change the main.class property to the full name of your World class. For example,
    if you have a class named MyWorld in a package named scenario, you would write:
    main.class=scenario.MyWorld
25. You should be able to run the driver class now.
26. To use images or sounds, you should create an images package and a sounds package and place all images
    or sounds in the corresponding folder. I included one sound and one image and modeled using them in this
    project. You can see in MyWorld.java how to use them.