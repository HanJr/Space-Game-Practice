import java.awt.image.BufferedImage;

public class Texture {
	public BufferedImage player, bullet, enemy;
	private SpriteSheet ss;
	
	public Texture(Game game) {
		ss = game.getSpriteSheet();
	
		getTextures();
	}
	
	public void getTextures() {
		player = ss.grabImage(1,1,32,32);
		bullet = ss.grabImage(2,1,32,32);
		enemy = ss.grabImage(3,1,32,32);
	}
}
