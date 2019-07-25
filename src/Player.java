import java.awt.Graphics;

public class Player extends GameObject implements EntityA{

	private double velX;
	private double velY;

	
	Texture tex;
	
	public Player(double x, double y, Texture tex) {
		super(x,y);
		
		this.tex = tex;

	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void tick() {
		x += velX;
		y += velY;
		
		if(x >= 640 - 19)
			x = 640 - 19;
		if(x <= 0)
			x = 0;
		if(y >= 480 - 32)
			y = 480 - 32;	
		if(y <= 0)			
			y = 0;
	}
	
	public void setVelX(double velX) {
		this.velX = velX;
	}
	
	public void setVelY(double velY) {
		this.velY = velY;
	}
	
	//���⼭ �޼ҵ带 ���� �� Graphics�� ������ �ʰ�, �ۿ� �ٸ� Graphics attribute�� ������� �ߴ�. 
	public void render(Graphics g) {
		g.drawImage(tex.player, (int)x, (int)y, null);

	}
}
