package entities;

import static utils.Constants.PlayerConstants.GetSpriteAmount;


import static utils.Constants.PlayerConstants.*;
import static utils.Constants.*;


import static utils.HelpMethods.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import main.Game;
import utils.LoadSave;

public class Player extends Entity {
	
	private BufferedImage[][] animations;
	
	private boolean moving = false, attacking = false;
	private boolean left, right, jump;
	private int[][] lvlData;
	private float xDrawOffset = 21 * Game.SCALE; // calculated new hitbox x coord
	private float yDrawOffset = 4 * Game.SCALE; // calculated new hitbox y coord
	private int spriteWidth = 20;
	private int spriteHeight = 27;
	
	// jumping and gravity
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	
	// Status Bar UI
	private BufferedImage statusBarImg;
	
	// Calculates pixels of elements on screen
	private int statusBarWidth = (int) (192 * Game.SCALE);
	private int statusBarHeight = (int) (58 * Game.SCALE);
	private int statusBarX = (int) (10 * Game.SCALE);
	private int statusBarY = (int) (10 * Game.SCALE);

	private int healthBarWidth = (int) (150 * Game.SCALE);
	private int healthBarHeight = (int) (4 * Game.SCALE);
	private int healthBarXStart = (int) (34 * Game.SCALE);
	private int healthBarYStart = (int) (14 * Game.SCALE);
	private int healthWidth = healthBarWidth;
	
	private int powerBarWidth = (int) (104 * Game.SCALE);
	private int powerBarHeight = (int) (2 * Game.SCALE);
	private int powerBarXStart = (int) (44 * Game.SCALE);
	private int powerBarYStart = (int) (34 * Game.SCALE);
	private int powerWidth = powerBarWidth;
	private int powerMaxValue = 200;
	private int powerValue = powerMaxValue;
	
	// Attack Box
	private int attackBoxOffset = (int) (10 * Game.SCALE);
	
	private int flipX = 0;
	private int flipW = 1;
	
	private boolean attackChecked;
	
	private Playing playing;

	private int tileY = 0;
	
	private boolean powerAttackActive;
	private int powerAttackTick;
	private int powerGrowSpeed = 15;
	private int powerGrowTick;
	
	/**
	 * Player constructor
	 * initializes player, initialzes hitbox/attackbox, and other parameters
	 * 
	 */
	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.state = IDLE;
		this.playing = playing;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		this.walkSpeed = Game.SCALE * 1.0f;
		loadAnimations();
		initHitbox(spriteWidth, spriteHeight);
		initAttackBox();
		
	}
	
	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x; 		// keeps player's hitbox in line with player's position
		hitbox.y = y;
	}
	
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
		resetAttackBox();
	}
	
	/**
	 * update's players health, power, location, and status with every loop
	 * 
	 */
	public void update() {
		updateHealthBar();
		updatePowerBar();
		
		if (currentHealth <= 0) {
			if (state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
			} else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
				playing.setGameOver(true);
			} else {
				updateAnimationTick();
			}
			return;
		}
		
		updateAttackBox();
		updatePosition();
		if (moving) {
			checkPotionTouched();
			checkSpikesTouched();
			tileY = (int) (hitbox.y / Game.TILES_SIZE);
			if (powerAttackActive) {
				powerAttackTick++;
				if(powerAttackTick >= 35) {
					powerAttackTick = 0;
					powerAttackActive = false;
				}
			}
		}
		if(attacking || powerAttackActive) {
			checkAttack();
		}
		updateAnimationTick();
		setAnimation();
	}
	
	private void checkSpikesTouched() {
		playing.checkSpikesTouched(this);
		
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(hitbox);
		
	}

	private void checkAttack() {
		if(attackChecked || aniIndex != 1) { // checks aniIndex for animation flow
			return;
		}
		attackChecked = true;
		
		if (powerAttackActive) {
			attackChecked = false;
		}
		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		
	}

	private void updateAttackBox() {
		if (right && left) { 
			if(flipW == 1) {
				attackBox.x = hitbox.x + hitbox.width + attackBoxOffset;
			} else {
				attackBox.x = hitbox.x - hitbox.width - attackBoxOffset;
			}
		}
		if(right || powerAttackActive && flipW == 1) {
			attackBox.x = hitbox.x + hitbox.width + attackBoxOffset;
		} else if (left || powerAttackActive && flipW == -1) {
			attackBox.x = hitbox.x - hitbox.width - attackBoxOffset;
		}
		attackBox.y = hitbox.y + attackBoxOffset;
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth); // pixels of player health
		
	}
	
	private void updatePowerBar() {
		powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth); // pixels of player power
		
		powerGrowTick++;
		if (powerGrowTick >= powerGrowSpeed) {
			powerGrowTick = 0;
			changePower(1);
		}
	}
	
	/**
	 * draws player
	 * 
	 */
	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[state][aniIndex], 
				(int) (hitbox.x - xDrawOffset) - lvlOffset + flipX, 
				(int) (hitbox.y - yDrawOffset), 
				width * flipW, height, null);
		//drawAttackBox(g, lvlOffset);
		drawUI(g);
	}

	private void drawUI(Graphics g) {
		//background UI
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		
		//heath bar
		g.setColor(Color.RED);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
		
		// power bar
		g.setColor(Color.YELLOW);
		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
	}
	
	/**
	 * loads all kinds of animations with each index into 2D array
	 * 
	 */
	private void loadAnimations() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
		
		animations = new BufferedImage[7][8]; // 7 kinds of animations, 8 per animation (most)
		for (int aniNum = 0; aniNum < animations.length; aniNum++) {
			for (int perAni = 0; perAni < animations[aniNum].length; perAni++) {
				animations[aniNum][perAni] = img.getSubimage(perAni*64, aniNum*40, 64, 40); // 64 x 40 image
			}
		}
		
		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if (!IsEntityOnFloor(hitbox, lvlData)) { 
			// makes player fall down if in air at start
			inAir = true;
		}
	}
	
	
	private void updateAnimationTick() {
		
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}
		}
	}
	
	private void setAnimation() {
		
		int startAni = state; 
		
		if (moving) {
			state = RUNNING;
		} else {
			state = IDLE;
		}
		
		if (inAir) {
			if (airSpeed > 0) {
				state = FALLING;
			} else {
				state = JUMPING;
			}
			
		}
		
		if (powerAttackActive) {
			state = ATTACK;
			aniIndex = 1;
			aniTick = 0;
			return;
		}
		
		if (attacking) {
			state = ATTACK;
			if(startAni != ATTACK) {
				aniIndex = 1;
				aniTick = 0;
				return;
			}
		}
		
		if (startAni != state) {
			resetAniTick();
		}
		
	}
	
	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
		
	}

	private void updatePosition() {
		moving = false;
		
		if (jump) {
			jump();
		}
		
		if (!inAir) {
			if (!powerAttackActive) {
				if ((!left && !right) || (right && left)) {
					return;
				}
			}
		}
		
		float xSpeed = 0;
		
		if (left && !right) {
			xSpeed -= walkSpeed;
			flipX = width;
			flipW = -1;
		} 
		if (right && !left) {
			xSpeed += walkSpeed;
			flipX = 0;
			flipW = 1;
		}
		
		if (!inAir) {
			if (!IsEntityOnFloor(hitbox, lvlData)) {
				inAir = true;
			}
		}
		
		if(powerAttackActive) {
			if((!left && !right )|| (left && right)) {
				if(flipW == -1) {
					xSpeed = -walkSpeed;
				} else {
					xSpeed = walkSpeed;
				}
			}
			xSpeed *= 3;
		}
		
		if (inAir && !powerAttackActive) {
			// can move up/down
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else { // cant move up/down
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0) { 
					// hit floor
					resetInAir();
				} else { 
					// hit roof
					airSpeed = fallSpeedAfterCollision;
				}
				updateXPos(xSpeed);
			}
			
		} else {
			updateXPos(xSpeed);
		}
		moving = true;
	}

	private void jump() {
		if (inAir) {
			return;
		}
		inAir = true;
		airSpeed = jumpSpeed;
		
	}
	
	public void powerAttack() {
		if (powerAttackActive) {
			return;
		} if (powerValue >= 75) {
			powerAttackActive = true;
			changePower(-75);
		}
		
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
		
	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x+xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
			if(powerAttackActive) {
				powerAttackActive = false;
				powerAttackTick = 0;
			}
		}
		
	}
	
	public void changeHealth(int value) {
		currentHealth = currentHealth + value;
		
		if(currentHealth <= 0) {
			currentHealth = 0;
		} else if (currentHealth >= maxHealth) {
			currentHealth = maxHealth;
		}
	}
	
	public void kill() {
		currentHealth = 0;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
	}
	
	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
	
	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}


	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		state = IDLE;
		airSpeed = 0f;
		currentHealth = maxHealth;
		
		hitbox.x = x;
		hitbox.y = y;
		
		if (!IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}
	}
	
	private void resetAttackBox() {
		if(flipW == 1) {
			attackBox.x = hitbox.x + hitbox.width + attackBoxOffset;
		} else {
			attackBox.x = hitbox.x - hitbox.width - attackBoxOffset;
		}
	}

	public void changePower(int value) {
		powerValue += value;
		if (powerValue >= powerMaxValue) {
			powerValue = powerMaxValue;
		} else if (powerValue <= 0) {
			powerValue = 0;
		}
	}

	public int getTileY() {
		return tileY ;
	}
	

}
