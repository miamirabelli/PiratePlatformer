package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import levels.Level;
import utils.LoadSave;
import static utils.Constants.EnemyConstants.*;

public class EnemyManager {
	
	private Playing playing;
	private BufferedImage[][] crabbyArr;
	private ArrayList<Crabby> crabbies = new ArrayList<>();
	
	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
	}
	
	public void loadEnemies(Level level) {
		crabbies = level.getCrabs();
		System.out.println("size of crabs: " + crabbies.size());
	}

	public void update(int[][] lvlData, Player player) {
		boolean isAnyActive = false;
		for(Crabby c : crabbies) {
			if(c.isActive()) {
				c.update(lvlData, player);
				isAnyActive = true;
			}
		}
		if (!isAnyActive) {
			playing.setLevelCompleted(true);
		}
	}
	
	public void draw(Graphics g, int xLvlOffset) {
		drawCrabs(g, xLvlOffset);
	}

	private void drawCrabs(Graphics g, int xLvlOffset) {
		for(Crabby c : crabbies) {
			if(c.isActive()) {
				g.drawImage(crabbyArr[c.getState()][c.getAniIndex()],
						(int) (c.getHitbox().x - CRABBY_DRAWOFFSET_X) - xLvlOffset + c.flipX(), 
						(int) (c.getHitbox().y - CRABBY_DRAWOFFSET_Y), 
						CRABBY_WIDTH * c.flipW(), 
						CRABBY_HEIGHT, null);
				//c.drawAttackBox(g, xLvlOffset);
			}
		}
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for(Crabby c : crabbies) {
			if (c.getCurrentHealth() > 0) {
				if(c.isActive()) {
					if(attackBox.intersects(c.getHitbox())) {
						c.hurt(10); // change later
						return;
					}
				}
			}
		}
	}

	private void loadEnemyImgs() {
		crabbyArr = new BufferedImage[5][9]; // 5 states, max of 9 images per state
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
		for (int state = 0; state < crabbyArr.length; state++) {
			for (int img = 0; img < crabbyArr[state].length; img++) {
				crabbyArr[state][img] = temp.getSubimage(img * CRABBY_WIDTH_DEFAULT, state * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
			}
		}
	}
	
	public void resetAllEnemies() {
		for (Crabby c : crabbies) {
			c.resetEnemy();
		}
	}
}
