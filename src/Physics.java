import java.util.LinkedList;

public class Physics {

	public static boolean collision(EntityB entityB, EntityA entityA) {
		
		if(entityB.getBounds().intersects(entityA.getBounds()))
			return true;
		
		return false;
	}	
	
	public static boolean collision(EntityA entityA, EntityB entityB) {
		
		if(entityA.getBounds().intersects(entityB.getBounds()))
				return true;
		
		return false;
	}
	

}
