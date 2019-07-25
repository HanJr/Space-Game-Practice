import java.awt.Graphics;
import java.util.Random;

public class Enemy extends GameObject implements EntityB{

	private Random r = new Random();
	private double velY = r.nextInt(3) + 1;
	private Texture tex;
	private Controller controller;
	private Game game;
	
	public Enemy(double x, double y, Texture tex, Game game) {
		super(x,y);
		this.tex = tex;
		this.controller = game.getController();
		this.game = game;
	}
	
	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}	
	
	public void tick() {
		y += velY;
		
		if(y > 500) {
			y = -10;
			x = r.nextInt(Game.WIDTH * Game.SCALE);
		}
		
		for(int i = 0; i < game.getController().getEntityAList().size(); i++) {
			if(Physics.collision(this, game.getController().getEntityAList().get(i))) {
				game.setNumOfKilledEnemies(game.getNumOfKilledEnemies() + 1);
				controller.removeEntityB(this);
				if(game.getController().getEntityAList().get(i) instanceof Bullet) {
					game.getController().removeEntityA(game.getController().getEntityAList().get(i));
					System.out.println("The bullet is completely removed from the list");
				}
				else{
					game.setHealth(game.getHealth() - 10);
				}
			}
			
		}
		
	}
	
	public void render(Graphics g) {
		g.drawImage(tex.enemy, (int)x, (int)y, null);
	}
	
	
}
