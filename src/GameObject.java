import java.awt.Rectangle;

public class GameObject {
	public double x; //private 으로 하니까 Player나 Bullet, Entity등 subclass에서 인식을 못했다. 
	public double y;
	
	public GameObject(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int)x,(int)y,32,32);
	}
}
