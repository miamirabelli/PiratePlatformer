package entities;

import static utils.Constants.EnemyConstants.*;



import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import static utils.Constants.Directions.*;

import main.Game;

public class Crabby extends Enemy {
	
	// Attack Box
		private int attackBoxOffsetX;

	public Crabby(float x, float y) {
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
		this.state = IDLE;
		initHitbox(22, 19); // 22 x 19 --> size of crab hitbox
		initAttackBox();
	}
	
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (82 * Game.SCALE), (19 * Game.SCALE));
		attackBoxOffsetX = (int)(30 * Game.SCALE);
	}

	public void update(int[][] lvlData, Player player) {
		updateBehavior(lvlData, player);
		updateAnimationTick();
		updateAttackBox();
	}
	
	private void updateAttackBox() {
		attackBox.x = hitbox.x - attackBoxOffsetX;
		attackBox.y = hitbox.y;
	}

	private void updateBehavior(int[][] lvlData, Player player) {
		if (firstUpdate) {
			firstUpdateCheck(lvlData);
		}

		if (inAir) {
			updateInAir(lvlData);	
		} else {
			switch (state) {
			case IDLE:
				newState(RUNNING);
				break;
			case RUNNING:
				
				if(canSeePlayer(lvlData, player)) {
					turnTowardsPlayer(player);
					if (isPlayerCloseForAttack(player)) {
						newState(ATTACKING);
					}
				}
				move(lvlData);
				break;
			case ATTACKING:
				if (aniIndex == 0) {
					attackChecked = false;
				}
				if (aniIndex == 3 && !attackChecked) { // only hitting player at index 3
					checkPlayerHit(attackBox, player);
				}
				break;
			case HIT:
				break;
			}
		}
	}
	
	
	public int flipX() {
		if (walkDir == RIGHT) {
			return width;
		} else {
			return 0;
		}
	}
	
	public int flipW() {
		if (walkDir == RIGHT) {
			return -1;
		} else {
			return 1;
		}
	}
}
