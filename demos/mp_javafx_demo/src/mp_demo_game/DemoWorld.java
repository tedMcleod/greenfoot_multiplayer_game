package mp_demo_game;

import com.tinocs.mp.javafxengine.MPWorld;

public class DemoWorld extends MPWorld {
	
	public DemoWorld() {
		setPrefSize(300, 400);
	}
	
	@Override
	public void onDimensionsInitialized() {
		start();
	}

	@Override
	public void act(long now) {
		
	}

	
}
