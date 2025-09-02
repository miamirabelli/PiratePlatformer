package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gamestates.GameState;

import static utils.Constants.Directions.*;

import main.GamePanel;

public class KeyboardInputs implements KeyListener {

	private GamePanel gamePanel;
	
	public KeyboardInputs(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(GameState.state) {
		case MENU:
			gamePanel.getGame().getMenu().keyPressed(e);
		case PLAYING:
			gamePanel.getGame().getPlaying().keyPressed(e);
			break;
		case OPTIONS:
			gamePanel.getGame().getGameOptions().keyPressed(e);
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(GameState.state) {
		case MENU:
			gamePanel.getGame().getMenu().keyReleased(e);
		case PLAYING:
			gamePanel.getGame().getPlaying().keyReleased(e);
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
