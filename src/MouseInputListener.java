
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInputListener extends MouseAdapter{

		Game game;
		
		public MouseInputListener(Game game) {
			this.game = game;
		}
	
		public void mousePressed(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();
			//play game button
			if(x >= Game.WIDTH - 50 && x <= Game.WIDTH - 50 + 100) {
				if(y >= 150 && y <= 200) {
					game.setGameState(Game.STATE.GAME);
				}
			}
			//help button
			if(x >= Game.WIDTH - 50 && x <= Game.WIDTH - 50 + 100) {
				if(y >= 250 && y <= 300) {

				}
			}
			//exit button
			if(x >= Game.WIDTH - 50 && x <= Game.WIDTH - 50 + 100) {
				if(y >= 350 && y <= 400) {
					System.exit(0);
				}
			}
		}
}
