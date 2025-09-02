package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Entity {
	
	protected int state;
	protected int aniTick, aniIndex;
	protected float x, y;
	protected int width, height;
	protected Rectangle2D.Float hitbox;
	protected float airSpeed;
	protected boolean inAir = false;
	
	protected float walkSpeed;
	
	protected int maxHealth;
	protected int currentHealth;
	protected Rectangle2D.Float attackBox;
	
	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	// debugging purposes
	protected void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.PINK);
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	protected void initHitbox(int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
		
	}
	
	protected void drawAttackBox(Graphics g, int lvlOffsetX) {
		g.setColor(Color.RED);
		g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}
	
	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}
	
	public int getState() {
		return state;
	}
	
	public int getAniIndex() {
		return aniIndex;
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
}
