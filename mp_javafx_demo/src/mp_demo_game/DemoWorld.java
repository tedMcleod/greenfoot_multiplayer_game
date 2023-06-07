package mp_demo_game;


import engine.World;
import mp_engine.GameWorld;

public class DemoWorld extends GameWorld {
	
	public DemoWorld() {
		setPrefSize(300, 400);
	}
	
	@Override
	public void onDimensionsInitialized() {
		start();
	}

	@Override
	public void act(long now) {
		// TODO Auto-generated method stub
		
	}

	
}
