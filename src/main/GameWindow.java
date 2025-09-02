package main;

import java.awt.event.WindowEvent;

import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class GameWindow {
	
	private JFrame jframe;
	
	
	/**
	 * initializes preferences for GameWindow, taking GamePanel
	 * 
	 */
	public GameWindow(GamePanel gamePanel) {
		
		jframe = new JFrame();
		
		jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
		jframe.add(gamePanel);
		jframe.setResizable(false);
		jframe.pack();
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
		jframe.addWindowFocusListener(new WindowFocusListener() {
			
			/**
			 * changes game's focus if window loses focus
			 * 
			 */
			@Override
			public void windowLostFocus(WindowEvent arg0) {
				gamePanel.getGame().windowFocusLost();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				
			}
		});
		
	}
	
}
