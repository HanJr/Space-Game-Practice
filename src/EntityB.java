import java.awt.Graphics;
import java.awt.Rectangle;

public interface EntityB {

	public double getX();
	public double getY();
	public void tick();
	public void render(Graphics g);
	public Rectangle getBounds();
	
	
}
