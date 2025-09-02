package utils;

import static utils.Constants.EnemyConstants.CRABBY;


import static utils.Constants.ObjectConstants.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import main.Game;
import objects.Cannon;
import objects.GameContainer;
import objects.Potion;
import objects.Projectile;
import objects.Spike;

public class HelpMethods {
	
	public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		
		if (!IsSolid(x, y, lvlData)) {
			if (!IsSolid(x + width, y + height, lvlData)) {
				if (!IsSolid(x + width, y, lvlData)) {
					if (!IsSolid(x, y + height, lvlData)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static boolean IsSolid(float x, float y, int[][] lvlData) {
		
		int maxWidth = lvlData[0].length * Game.TILES_SIZE;
		if (x < 0 || x >= maxWidth) {
			return true;
		}
		if (y < 0 || y >= Game.GAME_HEIGHT) {
			return true;
		}
			
			float xIndex = x / Game.TILES_SIZE;
			float yIndex = y / Game.TILES_SIZE;
			
			return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
	}
	
	public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
		int value = lvlData[yTile][xTile];
		
		if (value >= 48 || value < 0 || value != 11) { //48 sprites total; 11 = transparent
			return true;
		}
		return false;
	}
	
public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData) {
		return IsSolid((int) (p.getHitbox().x + p.getHitbox().width/2), (int) (p.getHitbox().y + p.getHitbox().height/2), lvlData);
	}
	
	public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int currentTile = (int) hitbox.x / Game.TILES_SIZE;
		if (xSpeed > 0) { // RIGHT!
			int tileXPos = currentTile * Game.TILES_SIZE;
			int xOffset = (int) (Game.TILES_SIZE - hitbox.width);
			return tileXPos + xOffset - 1; // -1 because 1 pixel not overlapping
		} else { // LEFT!!
			return currentTile * Game.TILES_SIZE;
		}
	}
	
	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int currentTile = (int) hitbox.y / Game.TILES_SIZE;
		if (airSpeed > 0) { // FALLING -- touching floor
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1; // -1 because 1 pixel not overlapping
		} else { // JUMPING -- touching roof
			return currentTile * Game.TILES_SIZE;
		}
	}
	
	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		// checking pixel below bottom left & bottom right
		if (!IsSolid(hitbox.x, hitbox.y+hitbox.height+1, lvlData)) { // +1 pixel bc not overlapping
			if (!IsSolid(hitbox.x+hitbox.width, hitbox.y+hitbox.height+1, lvlData)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
		if(xSpeed > 0) {
			return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData); // checking pixel below hitbox
		} else {
			return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData); // checking pixel below hitbox
		}
	}
	
	public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float hitbox1, Rectangle2D.Float hitbox2, int yTile) {
		int xTile1 = (int) hitbox1.x / Game.TILES_SIZE;
		int xTile2 = (int) hitbox2.x / Game.TILES_SIZE;
		
		if (xTile1 > xTile2) {
			return AreAllTilesClear(xTile1, xTile2, yTile, lvlData);
		} else {
			return AreAllTilesClear(xTile2, xTile1, yTile, lvlData);
		}
	}
	
	public static boolean AreAllTilesClear(int xStart, int xEnd, int yTile, int[][] lvlData) {
		for (int tileNum = 0; tileNum < xEnd - xStart; tileNum++) {
			if(IsTileSolid(xStart + tileNum, yTile, lvlData)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean AreAllTilesWalkable (int xStart, int xEnd, int yTile, int[][] lvlData) {
		if(AreAllTilesClear(xStart, xEnd, yTile, lvlData)) {
			for (int tileNum = 0; tileNum < xEnd - xStart; tileNum++) {
				if(!IsTileSolid(xStart + tileNum, yTile + 1, lvlData)) { // checking tile below --> not solid = not moving towards
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float hitbox1, Rectangle2D.Float hitbox2, int yTile) {
		int xTile1 = (int) hitbox1.x / Game.TILES_SIZE;
		int xTile2 = (int) hitbox2.x / Game.TILES_SIZE;
		
		if (xTile1 > xTile2) {
			return AreAllTilesWalkable(xTile1, xTile2, yTile, lvlData);
		} else {
			return AreAllTilesWalkable(xTile2, xTile1, yTile, lvlData);
		}
	}
	
	public static int[][] GetLevelData(BufferedImage img) {
		int[][] lvlData = new int[img.getHeight()][img.getWidth()]; // total size, not all visible
		for (int col = 0; col < img.getHeight(); col++) {
			for (int row = 0; row < img.getWidth(); row++) {
				Color color = new Color(img.getRGB(row, col));
				int value = color.getRed();
				if (value >= 48) {
					value = 0;
				}
				lvlData[col][row] = value;
			}
		}
		return lvlData;
	}
	
	public static ArrayList<Crabby> GetCrabs(BufferedImage img) {
		ArrayList<Crabby> list = new ArrayList<>();
		for (int col = 0; col < img.getHeight(); col++) {
			for (int row = 0; row < img.getWidth(); row++) {
				Color color = new Color(img.getRGB(row, col));
				int value = color.getGreen();
				if (value == CRABBY) {
					list.add(new Crabby(row*Game.TILES_SIZE, col*Game.TILES_SIZE));
				}
			}
		}
		return list;	
	}
	
	public static Point GetPlayerSpawn(BufferedImage img) {
		for (int col = 0; col < img.getHeight(); col++) {
			for (int row = 0; row < img.getWidth(); row++) {
				Color color = new Color(img.getRGB(row, col));
				int value = color.getGreen();
				if (value == 100) {
					return new Point(row * Game.TILES_SIZE, col * Game.TILES_SIZE);
				}
			}
		}
		return new Point(1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
	}
	
	public static ArrayList<Potion> GetPotions(BufferedImage img) {
		ArrayList<Potion> list = new ArrayList<>();
		for (int col = 0; col < img.getHeight(); col++) {
			for (int row = 0; row < img.getWidth(); row++) {
				Color color = new Color(img.getRGB(row, col));
				int value = color.getBlue();
				if (value == RED_POTION || value == BLUE_POTION) {
					list.add(new Potion(row*Game.TILES_SIZE, col*Game.TILES_SIZE, value));
				}
			}
		}
		return list;	
	}
	
	public static ArrayList<GameContainer> GetContainers(BufferedImage img) {
		ArrayList<GameContainer> list = new ArrayList<>();
		for (int col = 0; col < img.getHeight(); col++) {
			for (int row = 0; row < img.getWidth(); row++) {
				Color color = new Color(img.getRGB(row, col));
				int value = color.getBlue();
				if (value == BOX || value == BARREL) {
					list.add(new GameContainer(row*Game.TILES_SIZE, col*Game.TILES_SIZE, value));
				}
			}
		}
		return list;	
	}

	public static ArrayList<Spike> GetSpikes(BufferedImage img) {
		ArrayList<Spike> list = new ArrayList<>();
		for (int col = 0; col < img.getHeight(); col++) {
			for (int row = 0; row < img.getWidth(); row++) {
				Color color = new Color(img.getRGB(row, col));
				int value = color.getBlue();
				if (value == SPIKE) {
					list.add(new Spike(row*Game.TILES_SIZE, col*Game.TILES_SIZE, SPIKE));
				}
			}
		}
		return list;
	}
	
	public static ArrayList<Cannon> GetCannons(BufferedImage img) {
		ArrayList<Cannon> list = new ArrayList<>();

		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == CANNON_LEFT || value == CANNON_RIGHT)
					list.add(new Cannon(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}
		return list;
	}
	
	
	
}
