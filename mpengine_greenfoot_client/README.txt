To build a jar, simply double click (open) the build_mpengine_greenfoot_client.jardesc file in eclipse.
You should update the version number when building a new version.

To run javadocs:
1. right click on module-info.java and refactor -> rename to something temporary such as module-info.txt (see below for the reason)
2. right click on the ant file mpengine_greenfoot_client_javadoc.xml and Run As... -> Ant Build.
3. restore the name of module-info.java

Reason for renaming module-info.java while running javadoc with ant:
1. We don't need module-info.java because the modules are already included in the --module-path of the ant build file.
2. If there is a module-info.java, ant insists on using the module-info.java, but then fails to find the mpengine.client module
3. By not having a module-info.java file it forces ant to use the --module-path in the xml file.

While it is possible to run ant from commandline, it actually has the same issue as in eclipse so it is not worth it.