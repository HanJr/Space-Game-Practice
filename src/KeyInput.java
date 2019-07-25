import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{
	
	private Game game;
	
	public KeyInput(Game game) {
		this.game = game;
	}

	//비디오에서는 오버라이드 사용하지 않았다. 꼭 필요한가? 언제? 왜?
	@Override
	public void keyPressed(KeyEvent e) {
		game.keyPressed(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		game.keyReleased(e);
	}
}
