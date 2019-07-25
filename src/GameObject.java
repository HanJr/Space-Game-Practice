import java.awt.Rectangle;

public class GameObject {
	public double x; //private ���� �ϴϱ� Player�� Bullet, Entity�� subclass���� �ν��� ���ߴ�. 
	public double y;
	
	public GameObject(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int)x,(int)y,32,32);
	}
}
