Build Instructions:
1. Edit the ant_jar_build.xml to change the name of the jar exported so version number is changed.
2. To run, right click on ant_jar_build.xml and Run As... -> Ant Build
3. If you get an error complaining you can't run ant on a version of java less than 11, then
     - Right click on ant_jar_build.xml and Run As... -> External Tools Configurations...
     - Go to the JRE tab and select a Separate JRE that is 11 or higher
     - Your ant build tool will run on that separate JRE, but the code in your jar will still be
       compiled using the JDK chosen in the build settings of your project
       
Run Instructions (Eclipse):
- You can of course run ServerDriver in eclipse which will start a server listening to port 1234
- You can set run configuration and in the arguments tab add a number to program arguments to change the port number.

Run Instructions (BlueJ):
1. In the src folder, make a file name package.bluej
2. double click on package.bluej to open the project
3. right click on ServerDriver and run the main method.
4. When prompted for args, you can just press enter to listen to port 1234
5. If you want to change the port, when prompted for args, type in a port number in the array of args.
   The number needs to be in quotes and you need the curly braces so it will be an array like this:
   {"3612"}

Run Instructions (commandline):
- You can run the jar in commandline by first navigating to the folder containing the jar and then
  entering the command:
  
  java -jar mpengine_server_v1.jar

- The exact jar name could vary, so make sure it matches the actual jar name.
- In the terminal, pressing tab will help auto-complete the name
- if you want to listen at a different port, simply include the port number after the jar name:
  
  java -jar mpengine_server_v1.jar 5387
  
If you want to run the ServerDriver on a remote computer you are connecting to via terminal, you
should use a program like nohup (https://www.digitalocean.com/community/tutorials/nohup-command-in-linux)
so it will keep running even after you close the terminal.


