package objects;

import static utils.Constants.ANI_SPEED;
import static utils.Constants.ObjectConstants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public class GameObject {
	
	protected int x, y, objectType ;
	protected Rectangle2D.Float hitbox;
	protected boolean doAnimation, active = true;
	protected int aniTick, aniIndex;
	protected int xDrawOffset, yDrawOffset;
	
	public GameObject(int x, int y, int objectType) {
		this.x = x;
		this.y = y;
		this.objectType = objectType;
	}
	
	protected void updateAnimationTick() {
		aniTick++;
		if(aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(objectType)) {
				aniIndex = 0;
				if (objectType == BARREL || objectType == BOX) {
					doAnimation = false;
					active = false;
				} else if (objectType == CANNON_LEFT || objectType == CANNON_RIGHT) {
					doAnimation = false;
				}
			}
		}
	}
	
	public void reset() {
		aniIndex = 0;
		aniTick = 0;
		active = true;
		
		if (objectType == BARREL || objectType == BOX || objectType == CANNON_RIGHT || objectType == CANNON_LEFT) {
			doAnimation = false;
		} else {
			doAnimation = true;
		}
	}
	
	protected void initHitbox(int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
	}
	
	// for debugging purposes
	public void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.PINK);
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}
	
	public void setAnimation(boolean doAnimation) {
		this.doAnimation = doAnimation;
	}

	public int getObjectType() {
		return objectType;
	}

	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getxDrawOffset() {
		return xDrawOffset;
	}

	public int getyDrawOffset() {
		return yDrawOffset;
	}
	
	public int getAniIndex() {
		return aniIndex;
	}

	public int getAniTick() {
		return aniTick;
	}
	

}
