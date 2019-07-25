import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Random;

public class Controller {
	
	private EntityA entityA;
	private EntityB entityB;
	private LinkedList<EntityA> entityAList = new LinkedList<EntityA>();
	private LinkedList<EntityB> entityBList = new LinkedList<EntityB>();
	private Random r = new Random();
	private Texture tex;
	private Game game;
	
	public Controller(Texture tex, Game game) {
		this.tex = tex;
		this.game = game;
	}
	
	public void tick() {
		for(int i = 0; i < entityAList.size(); i++) {
			entityA = entityAList.get(i);
			//I initially used entity.getClass().equals("Bullet"), 그러나 이런식으로는 작동되지 않는다
			if(entityA.getY() < 0 && entityA instanceof Bullet) {
				removeEntityA(entityA);
			}			

			entityA.tick();
		}
		for(int i = 0; i < entityBList.size(); i++) {
			entityB = entityBList.get(i);
			
			entityB.tick();
		}
	}
	
	//이 Graphics g 는 서로 어떻게 연결이 되어 있을까
	public void render(Graphics g) {
		for(int i = 0; i < entityAList.size(); i++) {
			entityA = entityAList.get(i);
			entityA.render(g);
		}
		
		for(int i = 0; i < entityBList.size(); i++) {
			entityB = entityBList.get(i);
			entityB.render(g);
		}
	}
	
	public void createEnemies(int numOfEnemies) {
		for(int i = 0; i < numOfEnemies; i++) {
			entityBList.add(new Enemy(r.nextInt(Game.WIDTH * Game.SCALE), -10, tex, game));
		}
	}
	
	public void addEntityA(EntityA entity) {
		entityAList.add(entity);
	}
	
	public void removeEntityA(EntityA entity) {
		entityAList.remove(entity);
	}	

	public void addEntityB(EntityB entity) {
		entityBList.add(entity);
	}
	
	public void removeEntityB(EntityB entity) {
		entityBList.remove(entity);
	}	
	
	public LinkedList<EntityA> getEntityAList(){
		return entityAList;
	}
	
	public LinkedList<EntityB> getEntityBList(){
		return entityBList;
	}
}
