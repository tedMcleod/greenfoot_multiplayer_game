import java.util.Scanner;
import greenfoot.*;
import javafx.application.Platform;

/**
 * Write a description of class MyGameClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyGameClient extends GameClient  {

    public MyGameClient(String hostName, int portNumber) {
        super(hostName, portNumber);
    }

    @Override
    public void doCommand(String command) {
        System.out.println("Command: " + command);
        GameWorld gw = (GameWorld)getWorld();
        Scanner scan = new Scanner(command);
        String id = scan.next();
        String cmd = scan.next();
        if (cmd.equals("ID")) {
            Player myPlayer = new Player(id);
            int x = Greenfoot.getRandomNumber(gw.getWidth());
            int y = Greenfoot.getRandomNumber(gw.getHeight());
            gw.addObject(myPlayer, x, y);
            sendMessage("AddPlayer " + x + " " + y);
        }
        else if (cmd.equals("AddPlayer")) {
            OtherPlayer op = new OtherPlayer(id);
            int x = scan.nextInt();
            int y = scan.nextInt();
            gw.addObject(op, x, y);
            GameActor player = gw.getGameActor(getId());
            if (player != null) {
                sendMessage("TO " + id + " AddOP " + player.getX() + " " + player.getY());
            }
        } else if (cmd.equals("AddOP")) {
            OtherPlayer op = new OtherPlayer(id);
            int x = scan.nextInt();
            int y = scan.nextInt();
            gw.addObject(op, x, y);
        } else if (cmd.equals("DC")) {
            GameActor op = gw.getGameActor(id);
            if (op != null) {
                gw.removeObject(op);
            }
            for (GameActor ga : gw.getClientActors(id)) {
                gw.removeObject(ga);
            }
            
        } else if (cmd.equals("MOVE")) {
            String targetId = scan.next();
            GameActor ga = gw.getGameActor(targetId);
            if (ga != null) {
                int x = scan.nextInt();
                int y = scan.nextInt();
                ga.setLocation(x, y);
            }
        } else if (cmd.equals("ROT")) {
            String targetId = scan.next();
            GameActor ga = gw.getGameActor(targetId);
            if (ga != null) {
                int rot = scan.nextInt();
                ga.setRotation(rot);
            }
        } else if (cmd.equals("DESTROY")) {
            String targetId = scan.next();
            GameActor ga = gw.getGameActor(targetId);
            if (ga != null) {
                ga.destroy();
            }
        } else if (cmd.equals("ADDOS")) {
            String shotId = scan.next();
            OtherShot os = new OtherShot(shotId, id);
            int x = scan.nextInt();
            int y = scan.nextInt();
            gw.addObject(os, x, y);
        }
    }
}
