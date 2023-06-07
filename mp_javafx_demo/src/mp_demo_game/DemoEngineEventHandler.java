package mp_demo_game;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import mp_client_base.GameClient;
import mp_engine.EngineEventHandler;
import mp_engine.GameWorld;

public class DemoEngineEventHandler extends EngineEventHandler {
	
	private static final String CMD_COLORS = "COLORS";
	
	private ConcurrentHashMap<String, String> colorFilters = new ConcurrentHashMap<>();

	public DemoEngineEventHandler(GameWorld world) {
		super(world);
	}
	
	public String getColorFilterForClient(String clientId) {
		return colorFilters.get(clientId);
	}
	
	@Override
    public void handleCommand(String command, GameClient client) {
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
	public void handleOtherClientJoined(String clientId, GameClient client) {
		Player p = (Player)getWorld().getClientActors(client.getId()).get(0);
		client.sendMessage(CMD_COLORS + " " + p.getRGBFilter()[0] + " " + p.getRGBFilter()[1] + " " + p.getRGBFilter()[2] + " " + p.getBlueMultiplier(), clientId);
		super.handleOtherClientJoined(clientId, client);
	}
	

	@Override
	public void onIdAssigned(String clientId, GameClient client) {
		// TODO Auto-generated method stub
		super.onIdAssigned(clientId, client);
		Player p = new Player(client.getId());
		client.broadcastMessage(CMD_COLORS + " " + p.getRGBFilter()[0] + " " + p.getRGBFilter()[1] + " " + p.getRGBFilter()[2] + " " + p.getBlueMultiplier());
		p.setX(getWorld().getWidth() / 2);
		p.setY(getWorld().getHeight() - p.getHeight());
		getWorld().add(p);
	}
}
