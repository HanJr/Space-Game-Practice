import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Menu {

	//fillRect 와 Rectangle을 사용할 때 다른점
	private Rectangle playButton = new Rectangle(Game.WIDTH - 50, 150, 100, 50);
	private Rectangle helpButton = new Rectangle(Game.WIDTH - 50, 250, 100, 50);
	private Rectangle exitButton = new Rectangle(Game.WIDTH - 50, 350, 100, 50);
	
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D g2d = (Graphics2D)g;
		
		Font fnt0 = new Font("ariel", Font.BOLD, 50);
		g.setFont(fnt0);
		g.setColor(Color.WHITE);
		g.drawString("SPACE GAME", Game.WIDTH / 2, 100);
		
		Font fnt1 = new Font("ariel", Font.BOLD, 30);
		g.setFont(fnt1);
		g2d.draw(playButton);
		g.drawString("Play", playButton.x + 19, playButton.y + 35);
		g2d.draw(helpButton);
		g.drawString("Help", helpButton.x + 19, helpButton.y + 35);
		g2d.draw(exitButton);
		g.drawString("Exit", exitButton.x + 19, exitButton.y + 35);
	}

}
