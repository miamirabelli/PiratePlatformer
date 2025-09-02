package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.GameState;
import main.Game;
import utils.LoadSave;

public class LevelManager {

	public Game game;
	private BufferedImage[] levelSprite;
	private ArrayList<Level> levels;
	private int lvlIndex = 0;
	
	public LevelManager(Game game) {
		this.game = game;
		importOutsideSprites();
		levels = new ArrayList<>();
		buildAllLevels();
		
	}
	
	public void loadNextLevel() {
		lvlIndex++;
		if(lvlIndex >= levels.size()) {
			lvlIndex = 0;
			System.out.println("Game Completed");
			GameState.state = GameState.MENU;
		}
		
		Level nextLevel = levels.get(lvlIndex);
		game.getPlaying().getEnemyManager().loadEnemies(nextLevel);
		game.getPlaying().getPlayer().loadLvlData(nextLevel.getLevelData());
		game.getPlaying().getObjectManager().loadObjects(nextLevel);
		game.getPlaying().setMaxLvlOffset(nextLevel.getLvlOffset());
	}
	
	private void buildAllLevels() {
		BufferedImage[] allLevels = LoadSave.GetAllLevels();
		for (BufferedImage img : allLevels) {
			levels.add(new Level(img));
		}
	}

	private void importOutsideSprites() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[48]; // 12 wide, 4 tall
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 12; col++) {
				int index = row*12 + col;
				levelSprite[index] = img.getSubimage(col*32, row*32, 32, 32); // 32x32 sprites

			}
		}
		
	}

	public void draw(Graphics g, int lvlOffset) {
		for (int col = 0; col < Game.TILES_IN_HEIGHT; col++) {
			for (int row = 0; row < levels.get(lvlIndex).getLevelData()[0].length; row++) {
				int index = levels.get(lvlIndex).getSpriteIndex(row, col);
				g.drawImage(levelSprite[index], Game.TILES_SIZE*row - lvlOffset, Game.TILES_SIZE*col, Game.TILES_SIZE, Game.TILES_SIZE, null);
			}
		}
	}
	
	public void update() {
		
	}
	
	public Level getCurrentLevel() {
		return levels.get(lvlIndex);
	}
	
	public int getAmountOfLevels() {
		return levels.size();
	}
	
}
