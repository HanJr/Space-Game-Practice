import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject implements EntityA{
	
	
	Texture tex;
	
	public Bullet(double x, double y, Texture tex) {
		super(x,y);
		this.tex = tex;
	}
	
	public void tick() {
		y -= 10;
	}
	
	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}	
	
	public void render(Graphics g) {
		g.drawImage(tex.bullet, (int)x, (int)y, null); //�� null�� ���°�? width�� height�� �־����� �ʾҴ�.
	}
}
