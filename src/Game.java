import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

//이 프로젝트의 개발노트를 보라. Synchronized example에서 나의 약점을 파악 할 수 있다. 
//Canvas는 무엇인가? JFrame, JPanel과 다른것이 무엇인가? 왜 Canvas를 선택해야하는가? Thread(a class)와 Runnable(an interface)의 관계
public class Game extends Canvas implements Runnable{
	
	public static final int WIDTH = 320;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 2; //scale의 값을 올려보아라, 사실상 스케일은 무의미하다. 
	public final String TITLE = "2D SPACE GAME";
	
	private boolean running = false;
	private Thread thread;
	private SpriteSheet ss;
	private Player player;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//BufferedImage = 더블 버퍼링과 같다고 보면 된다. 이미지를 뒤에서 로드하고 쏜다. 근데 왜 여기는 WIDTH와 HEIGHT이 그대로 쓰이고, setPreferredSize에서 쓰이는 디멘션은 scale을 곱하지?
	private Controller objController;
	private Texture tex;
	
	private BufferedImage spriteSheet = null;
	private BufferedImage bg = null;
	
	private boolean isShooting = false;
	private int numOfEnemies = 5;
	private int numOfKilledEnemies = 0;
	
	public static enum STATE {GAME, MENU}; //ENUM 사용법. 스트링이 아님.
	private STATE state = STATE.MENU;
	private Menu menu;
	
	private int health = 200;
	private boolean playerIsDead = false;
	
	public void init() {
		requestFocus();
		BufferedImageLoader loader = new BufferedImageLoader(); // 이 클래스가 굳이 필요한가?
		try {
			spriteSheet = loader.loadImage("/res/spriteSheet.png");//로더는 말그대로
			//res 폴더가 내가 직접 GalagLike 폴더를 다큐먼트 폴더에서 만들었을 때 작동하지 않았다.
			//src 밖에 res가 있을 때 작동하지 않았다.  
			bg = loader.loadImage("/res/bg.png");
		}catch(IOException e) {
			e.printStackTrace();
		}
		

		
		ss = new SpriteSheet(spriteSheet);//로더는 말그대로 이미지를 갖고오는것. 스프라잇 쉿은 부분적인 이미지를 갖고올수있게 도와준다. 
		menu = new Menu();
		addMouseListener(new MouseInputListener(this));
		tex = new Texture(this);
		objController = new Controller(tex, this);
		objController.createEnemies(numOfEnemies);
		player = new Player(100, 100, tex);
		objController.addEntityA(player);
		addKeyListener(new KeyInput(this));
		//player = ss.grabImage(1,1,32,32); //여기서 나는 render에 image를 직접 출력해야한다는 것을 까먹었다. 
	}
	
	//synchronized?
	private synchronized void start() {
		if(running)
			return;
	
		running = true;
		thread = new Thread(this);//사용 방법이 특이한점 주목, 스스로를 아규먼트로 넘김
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
		System.exit(1);//왜 1을 사용?
		
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
	
	//Runnable에서 요구하는 메소드
	//Runnable과 스레드가 다른점?
	public void run() {
		init();
		long lastTime = System.nanoTime(); //전 룹이 돌기 시작한 시간을 기록하기위한 변수
		final double amountOfTicks = 60.0; // 1초당 원하는 업데이트의 수
		double ns = 1000000000 / amountOfTicks; // 1초를 원하는 업데이트의 수만큼으로 나눈 것 = 초당 업데이트수를 기반으로, 각 업데이트가 진행되야하는 최대 시간을 계산하는 것
		double delta = 0; // 마지막 업데이트 이후로, 또다른 업데이트를 진행할 시간이 충족되었는지를 계산된 값을 저장하는 변수
		int updates = 0; //1초마다 업데이트된 수를 기록
		int frames = 0; //1초마다 업데이트된 프레임을 기록
		long timer = System.currentTimeMillis(); //1초마다 업데이트된 수와 프레임이 업데이트된 수를 계산하기위함
		
		while(running) {
			long now = System.nanoTime();//현재 새로운 룹이 시작한 시간
			delta += (now - lastTime) / ns; //지금과 마지막으로 룹이 돈 된 시간을 뺀 것을 각 업데이트당 할당 된 최대 시간으로 나누면 각 업데이트를 언제 해야하는지 찾을 수 있게된다.
			lastTime = now;
			//1이라는 숫자는 지금과 마지막 업데이트 사이에 간격이 업데이트 최대시간의 간격을 충족시킴을 의미한다. 곧, 업데이트를 해야 할 시간이 되었다는 의미. 
			if(delta >= 1) {
				tick();
				updates++;
				delta--; //최대시간을 사용했으니 다시 리셋시켜서 다음 업데이트 타이밍을 계산한다. 
			}
			render();//rending은 업데이트 횟수와 관계없이 룹마다 돌아간다. 
			frames++;//
			
			//매 초마다 업데이트와 프레임수를 리셋하고, 매초마다 얼마만큼의 업데이트와 프레임이 바뀌었는지 카운트 할 수 있게 해준다. 
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + " Ticks, Fps" + frames);
				updates = 0;
				frames = 0;
			}
		}
		
		stop(); // 게임이 끝나면 스레드를 끝낸다
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
		BufferStrategy bs = this.getBufferStrategy();//return null is BufferedStrategy is not created yet. this를 주목.
		
		//이대로두면, 이 컨디셔널 스테이트먼트의 존재이유가 사라진다. bs는 매번 다시 이 메소드안에서 define되기 때문이다. 
		if(bs == null) {
			//createBufferStrategy는 어디에 속한 메소드일까?
			this.createBufferStrategy(3);//MAX 30까지 가능. 3중 버퍼링을 만드는 것임, 3중 버퍼의 장점은?
			return;
		}
		
		Graphics g = bs.getDrawGraphics();


		
		/*어떻게 버퍼 이미지의 색을 하얀색으로 바꿀 수 있을까? Graphics와 Image가 어떻게 상호작용하는지 이해해야한다.
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
			
		g.dispose();//Disposes of this graphics context and releases any system resources that it is using. 없어도 문제는 되지 않으나, 무리가 올 것 같다.
		bs.show();//Makes the next available buffer visible by either copying the memory (blitting) or changing the display pointer (flipping). :Blitting / flipping이 궁금하면 api찾아보길
		//bs를 사용할다면, 이 show가 무조건 필요한 것같다 없으니까 작동 x
		
		
	}
	
	public Controller getController() {
		return objController;
	}
	
	public void keyPressed(KeyEvent e) {
	
		if(state == STATE.GAME) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
			//player.setY(player.getY() - 2); 이 방식과 velX나 velY를 사용하는데 있어서 차이점은, 후자는 현재 플레이어의 포지션을 알 필요가 없기때문에 속도가 더 빠르다는 것
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
	//갑작스러운 턴을 하면 멈추는이유, keyReleased method를 거쳐야하기 때문에. 키를 놓지않고 다음 키를 누르면 자연스러움이 이것을 증명
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
