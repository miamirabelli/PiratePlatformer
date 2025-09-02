package objects;

import main.Game;

public class Cannon extends GameObject {
	
	private int tileY;

	public Cannon(int x, int y, int objectType) {
		super(x, y, objectType);
		tileY = y / Game.TILES_SIZE;
		initHitbox(40, 26); // 40 x 26 sprite
		hitbox.x -= (int) (4 * Game.SCALE); // centers cannon in x dir
		hitbox.y += (int) (6 * Game.SCALE); // puts cannon on floor
	}
	
	public void update() {
		if (doAnimation) {
			updateAnimationTick();
		}
	}
	
	public int getTileY() {
		return tileY;
	}

}
