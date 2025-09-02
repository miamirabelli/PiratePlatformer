package objects;

import static utils.Constants.ObjectConstants.*;

import main.Game;

public class GameContainer extends GameObject {

	public GameContainer(int x, int y, int objectType) {
		super(x, y, objectType);
		createHitbox();
	}

	private void createHitbox() {
		if (objectType == BOX) {
			initHitbox(25,18); // 25 x 18 pixels
			
			xDrawOffset = (int) (7 * Game.SCALE); // sprite 7 pixels from side of sprite atlas
			yDrawOffset = (int) (12 * Game.SCALE); // sprite 12 pixels from top of sprite atlas
			
		} else {
			initHitbox(23, 25); // 23 x 25 pixels
			
			xDrawOffset = (int) (8 * Game.SCALE); // sprite 8 pixels from side of sprite atlas
			yDrawOffset = (int) (5 * Game.SCALE); // sprite 6 pixels from top of sprite atlas
		}
		
		// since sprites begin a top of 32x32 tile, we must increase hitbox.y and hitbox.x to touch floor
		
		hitbox.y += yDrawOffset + (int) (Game.SCALE * 2); // difference between sprite and tile = 2 px
		hitbox.x += xDrawOffset / 2; // places sprite in middle of tile
	}
	
	protected void update() {
		if (doAnimation) {
			updateAnimationTick();
		}
	}

}
