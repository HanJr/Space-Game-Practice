import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

//�� ������Ʈ�� ���߳�Ʈ�� ����. Synchronized example���� ���� ������ �ľ� �� �� �ִ�. 
//Canvas�� �����ΰ�? JFrame, JPanel�� �ٸ����� �����ΰ�? �� Canvas�� �����ؾ��ϴ°�? Thread(a class)�� Runnable(an interface)�� ����
public class Game extends Canvas implements Runnable{
	
	public static final int WIDTH = 320;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 2; //scale�� ���� �÷����ƶ�, ��ǻ� �������� ���ǹ��ϴ�. 
	public final String TITLE = "2D SPACE GAME";
	
	private boolean running = false;
	private Thread thread;
	private SpriteSheet ss;
	private Player player;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//BufferedImage = ���� ���۸��� ���ٰ� ���� �ȴ�. �̹����� �ڿ��� �ε��ϰ� ���. �ٵ� �� ����� WIDTH�� HEIGHT�� �״�� ���̰�, setPreferredSize���� ���̴� ������ scale�� ������?
	private Controller objController;
	private Texture tex;
	
	private BufferedImage spriteSheet = null;
	private BufferedImage bg = null;
	
	private boolean isShooting = false;
	private int numOfEnemies = 5;
	private int numOfKilledEnemies = 0;
	
	public static enum STATE {GAME, MENU}; //ENUM ����. ��Ʈ���� �ƴ�.
	private STATE state = STATE.MENU;
	private Menu menu;
	
	private int health = 200;
	private boolean playerIsDead = false;
	
	public void init() {
		requestFocus();
		BufferedImageLoader loader = new BufferedImageLoader(); // �� Ŭ������ ���� �ʿ��Ѱ�?
		try {
			spriteSheet = loader.loadImage("/res/spriteSheet.png");//�δ��� ���״��
			//res ������ ���� ���� GalagLike ������ ��ť��Ʈ �������� ������� �� �۵����� �ʾҴ�.
			//src �ۿ� res�� ���� �� �۵����� �ʾҴ�.  
			bg = loader.loadImage("/res/bg.png");
		}catch(IOException e) {
			e.printStackTrace();
		}
		

		
		ss = new SpriteSheet(spriteSheet);//�δ��� ���״�� �̹����� ������°�. �������� ���� �κ����� �̹����� ����ü��ְ� �����ش�. 
		menu = new Menu();
		addMouseListener(new MouseInputListener(this));
		tex = new Texture(this);
		objController = new Controller(tex, this);
		objController.createEnemies(numOfEnemies);
		player = new Player(100, 100, tex);
		objController.addEntityA(player);
		addKeyListener(new KeyInput(this));
		//player = ss.grabImage(1,1,32,32); //���⼭ ���� render�� image�� ���� ����ؾ��Ѵٴ� ���� ��Ծ���. 
	}
	
	//synchronized?
	private synchronized void start() {
		if(running)
			return;
	
		running = true;
		thread = new Thread(this);//��� ����� Ư������ �ָ�, �����θ� �ƱԸ�Ʈ�� �ѱ�
 		thread.start();//Causes this thread to begin execution; the Java Virtual Machine calls the run method of this thread.
		
		//run()
		//If this thread was constructed using a separate Runnable run object, then that Runnable object's run method is called; otherwise, this method does nothing and returns.
	}
	
	private synchronized void stop() {
		if(!running)
			return;
		
		running = false;
		try {
			thread.join();//Waits for this thread to die.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(1);//�� 1�� ���?
		
		//System.exit(0); ---> OK 
		//System.exit(-1); ---> analogues to Exception 
		//System.exit(1); ---> analogues to Error 
	}
	
	public void setGameState(STATE state) {
		this.state = state;
	}

	public STATE getGameState() {
		return state;
	}
	
	public SpriteSheet getSpriteSheet() {
		return ss;
	}
	
	public int getNumOfEnenmies() {
		return numOfEnemies;
	}

	public void setNumOfEnenmies(int numOfEnenmies) {
		this.numOfEnemies = numOfEnenmies;
	}

	public int getNumOfKilledEnemies() {
		return numOfKilledEnemies;
	}

	public void setNumOfKilledEnemies(int numOfKilledEnemies) {
		this.numOfKilledEnemies = numOfKilledEnemies;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getHealth() {
		return health;
	}
	
	//Runnable���� �䱸�ϴ� �޼ҵ�
	//Runnable�� �����尡 �ٸ���?
	public void run() {
		init();
		long lastTime = System.nanoTime(); //�� ���� ���� ������ �ð��� ����ϱ����� ����
		final double amountOfTicks = 60.0; // 1�ʴ� ���ϴ� ������Ʈ�� ��
		double ns = 1000000000 / amountOfTicks; // 1�ʸ� ���ϴ� ������Ʈ�� ����ŭ���� ���� �� = �ʴ� ������Ʈ���� �������, �� ������Ʈ�� ����Ǿ��ϴ� �ִ� �ð��� ����ϴ� ��
		double delta = 0; // ������ ������Ʈ ���ķ�, �Ǵٸ� ������Ʈ�� ������ �ð��� �����Ǿ������� ���� ���� �����ϴ� ����
		int updates = 0; //1�ʸ��� ������Ʈ�� ���� ���
		int frames = 0; //1�ʸ��� ������Ʈ�� �������� ���
		long timer = System.currentTimeMillis(); //1�ʸ��� ������Ʈ�� ���� �������� ������Ʈ�� ���� ����ϱ�����
		
		while(running) {
			long now = System.nanoTime();//���� ���ο� ���� ������ �ð�
			delta += (now - lastTime) / ns; //���ݰ� ���������� ���� �� �� �ð��� �� ���� �� ������Ʈ�� �Ҵ� �� �ִ� �ð����� ������ �� ������Ʈ�� ���� �ؾ��ϴ��� ã�� �� �ְԵȴ�.
			lastTime = now;
			//1�̶�� ���ڴ� ���ݰ� ������ ������Ʈ ���̿� ������ ������Ʈ �ִ�ð��� ������ ������Ŵ�� �ǹ��Ѵ�. ��, ������Ʈ�� �ؾ� �� �ð��� �Ǿ��ٴ� �ǹ�. 
			if(delta >= 1) {
				tick();
				updates++;
				delta--; //�ִ�ð��� ��������� �ٽ� ���½��Ѽ� ���� ������Ʈ Ÿ�̹��� ����Ѵ�. 
			}
			render();//rending�� ������Ʈ Ƚ���� ������� �츶�� ���ư���. 
			frames++;//
			
			//�� �ʸ��� ������Ʈ�� �����Ӽ��� �����ϰ�, ���ʸ��� �󸶸�ŭ�� ������Ʈ�� �������� �ٲ������ ī��Ʈ �� �� �ְ� ���ش�. 
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + " Ticks, Fps" + frames);
				updates = 0;
				frames = 0;
			}
		}
		
		stop(); // ������ ������ �����带 ������
	}
	
	private void tick() {
		
		if(state == STATE.GAME) {	
			player.tick();
			objController.tick();
			
			if(numOfKilledEnemies >= numOfEnemies) {
				numOfEnemies = numOfEnemies + 1;
				numOfKilledEnemies = 0;
				objController.createEnemies(numOfEnemies);
			}
			
			if(playerIsDead) {
				
				try {
					Thread.sleep(5000);
					this.stop();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();//return null is BufferedStrategy is not created yet. this�� �ָ�.
		
		//�̴�εθ�, �� ����ų� ������Ʈ��Ʈ�� ���������� �������. bs�� �Ź� �ٽ� �� �޼ҵ�ȿ��� define�Ǳ� �����̴�. 
		if(bs == null) {
			//createBufferStrategy�� ��� ���� �޼ҵ��ϱ�?
			this.createBufferStrategy(3);//MAX 30���� ����. 3�� ���۸��� ����� ����, 3�� ������ ������?
			return;
		}
		
		Graphics g = bs.getDrawGraphics();


		
		/*��� ���� �̹����� ���� �Ͼ������ �ٲ� �� ������? Graphics�� Image�� ��� ��ȣ�ۿ��ϴ��� �����ؾ��Ѵ�.
		Graphics g2 = image.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0,0, this.getWidth(),this.getHeight());
		*/
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);

		
		if(state == STATE.GAME) {
			g.drawImage(bg, 0, 0, null);
			player.render(g);
			objController.render(g);
			
			g.setColor(Color.GRAY);
			g.fillRect(5,5,200,50);
			g.setColor(Color.GREEN);
			g.fillRect(5,5,health,50);	
			
			if(health <= 0) {
				playerIsDead = true;
				g.setColor(Color.RED);
				g.drawString("Game Over", WIDTH - 50, 200);
			}
		}else {
			menu.render(g);
		}
			
		g.dispose();//Disposes of this graphics context and releases any system resources that it is using. ��� ������ ���� ������, ������ �� �� ����.
		bs.show();//Makes the next available buffer visible by either copying the memory (blitting) or changing the display pointer (flipping). :Blitting / flipping�� �ñ��ϸ� apiã�ƺ���
		//bs�� ����Ҵٸ�, �� show�� ������ �ʿ��� �Ͱ��� �����ϱ� �۵� x
		
		
	}
	
	public Controller getController() {
		return objController;
	}
	
	public void keyPressed(KeyEvent e) {
	
		if(state == STATE.GAME) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
			//player.setY(player.getY() - 2); �� ��İ� velX�� velY�� ����ϴµ� �־ ��������, ���ڴ� ���� �÷��̾��� �������� �� �ʿ䰡 ���⶧���� �ӵ��� �� �����ٴ� ��
			player.setVelY(-5);
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
			//player.setY(player.getY() + 2);
			player.setVelY(5);
		}
		else if(e.getKeyCode() == KeyEvent.VK_A) {
			//player.setX(player.getX() - 2);
			player.setVelX(-5);
		}
		else if(e.getKeyCode() == KeyEvent.VK_D) {
			//player.setX(player.getX() + 2);
			player.setVelX(5);
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE && !isShooting) {			
			isShooting = true;
			objController.addEntityA(new Bullet(player.getX(), player.getY(), tex));
		}
		}
	}
	//���۽����� ���� �ϸ� ���ߴ�����, keyReleased method�� ���ľ��ϱ� ������. Ű�� �����ʰ� ���� Ű�� ������ �ڿ��������� �̰��� ����
	public void keyReleased(KeyEvent e) {
		if(state == STATE.GAME) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
			player.setVelY(0);
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
			player.setVelY(0);
		}
		if(e.getKeyCode() == KeyEvent.VK_A) {
			player.setVelX(0);
		}
		else if(e.getKeyCode() == KeyEvent.VK_D) {
			player.setVelX(0);
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			isShooting = false;
		}
		}
	}	
	
	public static void main(String[] args) {
		Game game = new Game();
		
		game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		JFrame frame = new JFrame(game.TITLE);
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.start();
	}
}
