package mp_demo_game;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.tinocs.mp.client.Client;
import com.tinocs.mp.javafxengine.JavafxEngineEventHandler;
import com.tinocs.mp.javafxengine.MPWorld;

public class DemoEngineEventHandler extends JavafxEngineEventHandler {
	
	private static final String CMD_COLORS = "COLORS";
	
	private ConcurrentHashMap<String, String> colorFilters = new ConcurrentHashMap<>();

	public DemoEngineEventHandler(MPWorld world) {
		super(world);
	}
	
	public String getColorFilterForClient(String clientId) {
		return colorFilters.get(clientId);
	}
	
	@Override
    public void handleCommand(String command, Client client) {
		Scanner reader = new Scanner(command);
		String senderId = reader.next();
		String cmd = reader.next();
		if (cmd.equals(CMD_COLORS)) {
			colorFilters.put(senderId, reader.nextLine());
		}
		reader.close();
		super.handleCommand(command, client);
	}
	
	@Override
	public void handleOtherClientJoined(String clientId, Client client) {
		Player p = (Player)getWorld().getClientActors(client.getId()).get(0);
		client.sendMessage(CMD_COLORS + " " + p.getRGBFilter()[0] + " " + p.getRGBFilter()[1] + " " + p.getRGBFilter()[2] + " " + p.getBlueMultiplier(), clientId);
		super.handleOtherClientJoined(clientId, client);
	}
	

	@Override
	public void onIdAssigned(String clientId, Client client) {
		super.onIdAssigned(clientId, client);
		Player p = new Player(client);
		client.broadcastMessage(CMD_COLORS + " " + p.getRGBFilter()[0] + " " + p.getRGBFilter()[1] + " " + p.getRGBFilter()[2] + " " + p.getBlueMultiplier());
		p.setX(getWorld().getWidth() / 2);
		p.setY(getWorld().getHeight() - p.getHeight());
		getWorld().add(p);
	}
}
