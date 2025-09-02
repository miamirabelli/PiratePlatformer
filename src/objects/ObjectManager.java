package objects;

import java.awt.Graphics;


import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utils.LoadSave;
import static utils.Constants.ObjectConstants.*;
import static utils.Constants.Projectiles.*;
import static utils.HelpMethods.CanCannonSeePlayer;
import static utils.HelpMethods.IsProjectileHittingLevel;

public class ObjectManager {
	
	private Playing playing;
	private BufferedImage[][] potionImgs, containerImgs;
	private BufferedImage spikeImg;
	private BufferedImage[] cannonImgs;
	private BufferedImage cannonBallImg;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Spike> spikes;
	private ArrayList<Cannon> cannons;
	private ArrayList<Projectile> projectiles = new ArrayList<>();
	
	public ObjectManager(Playing playing) {
		this.playing = playing;
		loadImgs();
	}
	
	public void checkSpikesTouched(Player p) {
		for (Spike s : spikes) {
			if(s.getHitbox().intersects(p.getHitbox())) {
				p.kill();
			}
		}
	}
	
	// if player's hitbox overlapped potion's hitbox
	public void checkPotionTouched(Rectangle2D.Float hitbox) {
		for (Potion p : potions) {
			if (p.isActive()) {
				if(hitbox.intersects(p.getHitbox())) {
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
		}
	}
	
	// applies effect based on potion type
	public void applyEffectToPlayer(Potion p) {
		if(p.getObjectType() == RED_POTION) {
			playing.getPlayer().changeHealth(RED_POTION_VALUE);
		} else {
			playing.getPlayer().changePower(BLUE_POTION_VALUE);
		}
	}
	
	public void checkObjectHit(Rectangle2D.Float attackBox) {
		for (GameContainer gc : containers) {
			if(gc.isActive() && !gc.doAnimation) {
				if(gc.getHitbox().intersects(attackBox)) {
					gc.setAnimation(true);
					int type = 0;
					if(gc.getObjectType() == BARREL) {
						type = 1;
					}
					potions.add(new Potion((int) (gc.getHitbox().x + gc.getHitbox().width /2),
							(int) (gc.getHitbox().y - gc.getHitbox().height / 2), type));
					return;
				}
			}
		}
	}
	
	public void loadObjects(Level nextLevel) {
		potions = new ArrayList<>(nextLevel.getPotions());
		containers = new ArrayList<>(nextLevel.getContainers());
		spikes = nextLevel.getSpikes();
		cannons = nextLevel.getCannons();
		projectiles.clear();
	}

	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][7]; // 2 types of potion, 7 ani sprites
	
		for (int col = 0; col < potionImgs.length; col++) {
			for (int row = 0; row < potionImgs[col].length; row++) {
				potionImgs[col][row] = potionSprite.getSubimage(12*row, 16*col, 12, 16); // 12 x 16 image
			}
		}
		
		BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8]; // 2 types of container, 8 ani sprites
	
		for (int col = 0; col < containerImgs.length; col++) {
			for (int row = 0; row < containerImgs[col].length; row++) {
				containerImgs[col][row] = containerSprite.getSubimage(40*row, 30*col, 40, 30); // 40 x 30 image
	
			}
		}
		spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);
		
		cannonImgs = new BufferedImage[7];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);
		for (int i = 0; i < cannonImgs.length; i++) {
			cannonImgs[i] = temp.getSubimage(i*40, 0, 40, 26);
		}
		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
		
	}
	
	public void update(int[][] lvlData, Player player) {
		for (Potion p : potions) {
			if (p.isActive()) {
				p.update();
			}
		}
		for (GameContainer gc : containers) {
			if( gc.isActive()) {
				gc.update();
			}
		}
		updateCannons(lvlData, player);
		updateProjectiles(lvlData, player);
	}
	
	private void updateProjectiles(int[][] lvlData, Player player) {
		for (Projectile p : projectiles) {
			if (p.isActive()) {
				p.updatePos();
				if (p.getHitbox().intersects(player.getHitbox())) {
					player.changeHealth(-25);
					p.setActive(false);
				} else if (IsProjectileHittingLevel(p, lvlData)) {
					p.setActive(false);
				}
			}
		}
	}

	private void updateCannons(int[][] lvlData, Player player) {
		/** IF CANNON IS NOT ANIMATING!
		 * 1. tileY is same?
		 * 2. player in range?
		 * 3. is player in front?
		 * 4. LOS?
		 * SHOOT!
		 */
		for (Cannon c : cannons) {
			if(!c.doAnimation) {
				if(c.getTileY() == player.getTileY()) {
					if(isPlayerInRange(c, player)) {
						if(isPlayerInFrontOfCannon(c, player)) {
							if(CanCannonSeePlayer(lvlData, player.getHitbox(), c.getHitbox(), c.getTileY())) {
								c.setAnimation(true);
							}
						}
					}
				}
			}
			c.update();
			if (c.getAniIndex() == 4 && c.getAniTick() == 0) {
				shootCannon(c);
			}
		}
	}

	private void shootCannon(Cannon c) {
		int dir = 1;
		if (c.getObjectType() == CANNON_LEFT) {
			dir = -1;
		}
		projectiles.add(new Projectile((int)c.getHitbox().x, (int) c.getHitbox().y, dir));
	}

	private boolean isPlayerInFrontOfCannon(Cannon c, Player player) {
		if(c.getObjectType() == CANNON_LEFT) {
			return c.getHitbox().x > player.getHitbox().x;
		} else {
			return c.getHitbox().x < player.getHitbox().x;
		}
	}

	private boolean isPlayerInRange(Cannon c, Player player) {
		int absValue = (int) Math.abs(player.getHitbox().x - c.hitbox.x);
		return absValue <= Game.TILES_SIZE * 5;
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawPotions(g, xLvlOffset);
		drawContainers(g, xLvlOffset);
		drawTraps(g, xLvlOffset);
		drawCannons(g, xLvlOffset);
		drawProjectiles(g, xLvlOffset);
	}

	private void drawProjectiles(Graphics g, int xLvlOffset) {
		for (Projectile p : projectiles) {
			if (p.isActive()) {
				g.drawImage(cannonBallImg, (int) (p.getHitbox().x - xLvlOffset),
						(int) p.getHitbox().y, CANNON_BALL_WIDTH,
						CANNON_BALL_HEIGHT, null);
			}
		}
		
	}

	private void drawCannons(Graphics g, int xLvlOffset) {
		for (Cannon c : cannons) {
			int x = (int) (c.getHitbox().x - xLvlOffset);
			int width = CANNON_WIDTH;

			if (c.getObjectType() == CANNON_RIGHT) {
				x += width;
				width *= -1;
			}

			g.drawImage(cannonImgs[c.getAniIndex()], x, (int) (c.getHitbox().y), width, CANNON_HEIGHT, null);
		}
	}

	private void drawTraps(Graphics g, int xLvlOffset) {
		for (Spike s : spikes) {
			g.drawImage(spikeImg, (int) (s.getHitbox().x - xLvlOffset),
					(int) (s.getHitbox().y - s.getyDrawOffset()),
					SPIKE_WIDTH, SPIKE_HEIGHT, null);
		}
	}

	private void drawContainers(Graphics g, int xLvlOffset) {
		for (GameContainer gc : containers) {
			if (gc.isActive()) {
				int type = 0;
				if(gc.getObjectType() == BARREL) {
					type = 1;
				}
				g.drawImage(containerImgs[type][gc.getAniIndex()],
							(int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset),
							(int) (gc.getHitbox().y - gc.getyDrawOffset()), 
							CONTAINER_WIDTH, CONTAINER_HEIGHT, null);
				}	
		}
	}

	private void drawPotions(Graphics g, int xLvlOffset) {
		for (Potion p : potions) {
			if (p.isActive()) {
				int type = 0;
				if(p.getObjectType() == RED_POTION) {
					type = 1;
				}
				g.drawImage(potionImgs[type][p.getAniIndex()],
							(int) (p.getHitbox().x - p.getxDrawOffset() - xLvlOffset),
							(int) (p.getHitbox().y - p.getxDrawOffset()), 
							POTION_WIDTH, POTION_HEIGHT, null);
				}	
		}
	}

	public void resetAllObjects() {
		loadObjects(playing.getLevelManager().getCurrentLevel());
		
		for (Potion p : potions) {
			p.reset();
		}
		for (GameContainer gc : containers) {
			gc.reset();
		}
		for (Cannon c : cannons) {
			c.reset();
		}
	}
}
