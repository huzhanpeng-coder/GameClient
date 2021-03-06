import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameMain extends JFrame implements ActionListener,KeyListener{

	/**
	 * 
	 */
private static final long serialVersionUID = -925518549680460124L;
	
	private bomber bomberman;
	private enemy enemy[] = new enemy[4];
	private bomb bomb_ex[] = new bomb[5];
	
	private walls bricks;
	private int[][] map = { {3,3,1,3,1,1,1,1,1,3,3,3},{3,2,1,3,2,3,1,2,1,3,2,3},{3,1,3,3,3,3,1,1,1,1,3,1},{3,3,3,3,2,1,3,2,1,1,3,1},{3,1,3,3,1,3,3,1,1,1,3,1},{3,2,3,1,2,1,3,2,1,1,2,1},{3,1,3,1,1,1,3,1,1,1,3,1} };
	private int[][]bombermanPosition = new int [7][12];
	private int player1flag=0,displayflag1=0; //flag to identify when the game is over 
	//labels to show the graphics
	private JLabel enemyLabel[] =  new JLabel[4];
	private JLabel bombermanLabel;
	private JLabel bombLabel[] = new JLabel[5];
	private JButton startButton;
	private JLabel[][] brickLabel = new JLabel[7][12];
	private ImageIcon bombermanImage, bombermanDownImage, bricksImage, bricksImage2,emptyImage, bombImage, enemyImage;
	private JLabel  scoreboardLabel,scores1Label,player1Label;
	private String player1_name="";
	private int[] enemy_down = new int[4];
	private JLabel[] asd= new JLabel[2];
	private int points1; 
	//container to hold graphics
	private Container content;
	private Connection conn = null;
	private Statement stmt = null;
	
	//Socket Communication
	final static int CLIENT_PORT = 5656;
	final static int SERVER_PORT = 5556;
	
	//GUI setup
	public GameMain() {
		super("Bomberman");
		setSize(GameProperties.SCREEN_WIDTH, GameProperties.SCREEN_HEIGHT);
		bombermanPosition[0][0]=1;
		
		/////////////////////////////////////////////////Maze///////////////////////////////////////////////////////
		bricks = new walls();
		bricksImage = new ImageIcon( getClass().getResource( bricks.getFilename() ) );
		bricksImage2 = new ImageIcon( getClass().getResource( "walls2.png" ) );
		emptyImage = new ImageIcon( getClass().getResource( "white.png" ) );
		
		
		
		
		//setting values to the bricks label according to the map
		for (int i=0; i< 7 ; i++) {
			
			for (int j=0; j< 12 ; j++) {
				if( map[i][j]==1) {
					brickLabel[i][j] = new JLabel();
					brickLabel[i][j].setText("");
					brickLabel[i][j].setIcon(bricksImage);
					brickLabel[i][j].setSize(bricks.getWidth(),bricks.getHeight());
					}else if( map[i][j]==2) {
						brickLabel[i][j] = new JLabel();
						brickLabel[i][j].setText("");
						brickLabel[i][j].setIcon(bricksImage2);
						brickLabel[i][j].setSize(bricks.getWidth(),bricks.getHeight());
					} else {
							brickLabel[i][j] = new JLabel();
							brickLabel[i][j].setText("");
							brickLabel[i][j].setIcon(emptyImage);
							brickLabel[i][j].setSize(bricks.getWidth(),bricks.getHeight());	
						}
				if( j==0 ) {
					bricks.setX(bricks.getX());
					brickLabel[i][j].setLocation(bricks.getX(), bricks.getY());
				}else {
					bricks.setX(bricks.getX()+100);
					brickLabel[i][j].setLocation(bricks.getX(), bricks.getY());
					}
			}
			bricks.setX(0);
			bricks.setY(bricks.getY()+100);
		}
		
		
		
		/////////////////////////////////////////////////Bomberman///////////////////////////////////////////////////////
		bomberman = new bomber();
		bombermanLabel = new JLabel();
		bombermanImage = new ImageIcon( getClass().getResource( bomberman.getFilename() ) );
		bombermanLabel.setIcon(bombermanImage); 
		bombermanLabel.setSize(bomberman.getWidth(),bomberman.getHeight());	
		bombermanLabel.setVisible(bomberman.getVisible());
		
		bombermanDownImage = new ImageIcon( getClass().getResource( "smallninja2.png" ) );
		
				
		/////////////////////////////////////////////////Bomb///////////////////////////////////////////////////////
		for (int i=0; i<5 ; i++) {
			bomb_ex[i] = new bomb();
			bombLabel[i] = new JLabel();
			bombLabel[i].setIcon(emptyImage); 
			bombLabel[i].setSize(bomb_ex[i].getWidth(),bomb_ex[i].getHeight());
			
		}
		
		
		/////////////////////////////////////////////////Enemy///////////////////////////////////////////////////////
		
		enemy[0] = new enemy();
		enemy[1] = new enemy();
		enemy[2] = new enemy(false);
		enemy[3] = new enemy(false);
		
		for (int i =0; i<4 ; i++) {
			
			enemyImage = new ImageIcon( getClass().getResource( enemy[i].getFilename() ) );
			enemyLabel[i] = new JLabel();
			enemyLabel[i].setIcon(enemyImage); 
			enemyLabel[i].setSize(enemy[i].getWidth(),enemy[i].getHeight());
			
			enemy[i].setEnemyLabel(enemyLabel[i]); 
			//enemy[i].setBomberman(bomberman); //collision with bomberman
			enemy[i].setBombermanLabel(bombermanLabel);
			enemy[i].setBomb(bomb_ex[0]);
			enemy[i].setBombEx(bomb_ex[1],bomb_ex[2],bomb_ex[3], bomb_ex[4]);//for collision with the explosion
			enemyLabel[i].setVisible(enemy[i].getVisible());
			
		}
		
		/////////////////////////////////////////////////Interface///////////////////////////////////////////////////////
		startButton = new JButton("Start Game");
		startButton.setSize(100,100);
		startButton.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-150);
		add(startButton);
		startButton.addActionListener(this);
		startButton.setFocusable(false);
		for (int i =0; i<4 ; i++) {
			enemy[i].setAnimationButton(startButton);
		}
		
		this.addKeyListener(this);
		
		scoreboardLabel = new JLabel("<html><body><center>CURRENT<br>SCORE</center></body></html>");
		scoreboardLabel.setSize(100,50);
		scoreboardLabel.setLocation(GameProperties.SCREEN_WIDTH-130,GameProperties.SCREEN_HEIGHT-700);
		
		player1Label = new JLabel(player1_name);
		player1Label.setSize(100,50);
		player1Label.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-600);
		
		scores1Label = new JLabel();
		scores1Label.setSize(100,50);
		scores1Label.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-550);
		
		add(player1Label);
		add(scoreboardLabel);
		add(scores1Label);
		
		content = getContentPane();
		content.setBackground(Color.white);
		setLayout(null);
		
		enemy[0].setCoordinates(125, 300);
		enemy[1].setCoordinates(425, 200);
		enemy[2].setCoordinates(625, 500);
		enemy[3].setCoordinates(1025, 300);
		bomb_ex[0].setCoordinates(0, 0);
		
		//adding labels and update position for bomberman 		
		add(bombermanLabel);
		bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
		
		//adding labels for enemy 
		for (int i =0; i<4 ; i++) {
			add(enemyLabel[i]);
			enemyLabel[i].setLocation(enemy[i].getX(),enemy[i].getY());
		}
		
		//adding bomb labels
		for (int i=0; i< 5 ; i++) {
			add(bombLabel[i]);
			bombLabel[i].setLocation(bomb_ex[i].getX(), bomb_ex[i].getY());
		}
				
		//adding bricksLabel
		for (int i=0; i< map.length ; i++) {
			for (int j=0; j< map[i].length ; j++) {
				add(brickLabel[i][j]);
			}
		}
		
		try {
		
		final ServerSocket client = new ServerSocket(CLIENT_PORT);
		Thread t1 = new Thread ( new Runnable () {
			public void run ( ) {
				synchronized(this) {
					
					System.out.println("Waiting for server responses...");
					while(true) {
						Socket s2;
						try {
							s2 = client.accept();
							
							GCService myService = new GCService (s2,bomberman,bombermanLabel,player1Label, enemy, enemyLabel,brickLabel,bombLabel,bomb_ex);
																	
							Thread t = new Thread(myService);
							t.start();
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("client connected");
						
					}

					
				}
			}
		});
		t1.start( );

		}
		catch (Exception E)
		{
			E.printStackTrace();
		}
		
		//set up listening server
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main (String[] args) throws IOException{
		GameMain myGame = new GameMain();
		
		myGame.setVisible(true);
	}


	@Override
	////////////////////////////////////BOMBERMAN 1 CONTROLS //////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void keyPressed(KeyEvent e){
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			
			
			try {
				command("down");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//Set the boundaries for bomberman
			
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			
			try {
				command("up");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Set the boundaries for bomberman
			
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			
			try {
				command("right");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Set the boundaries for bomberman
			
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			
			try {
				command("left");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			//Set the boundaries for bomberman
			
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			
			Timer timer = new Timer();
					
			try {
				startBombCommand();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
			
		}
	}	
		
	
////////////////////////////////////////////////////////////////STARTBUTTON///////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == startButton) {
			
			points1 = 0;
			
			for (int i=0; i< 4 ; i++) {
				enemy_down[i]=0;
			}
			
			player1_name = JOptionPane.showInputDialog("Enter the name of player 1");
			scores1Label.setText(String.valueOf(points1));
			////////////////////////////Save player's name into database
			
			try {
				startCommand(player1_name);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			map = new int[][] { {3,3,1,3,1,1,1,1,1,3,3,3},{3,2,1,3,2,3,1,2,1,3,2,3},{3,1,3,3,3,3,1,1,1,1,3,1},{3,3,3,3,2,1,3,2,1,1,3,1},{3,1,3,3,1,3,3,1,1,1,3,1},{3,2,3,1,2,1,3,2,1,1,2,1},{3,1,3,1,1,1,3,1,1,1,3,1} };
			
			//setting values to the bricks label according to the map
			for (int i=0; i< map.length ; i++) {
				for (int j=0; j< map[i].length ; j++) {
					if( map[i][j]==1) {
						brickLabel[i][j].setIcon(bricksImage);
						}else if( map[i][j]==2) {
							brickLabel[i][j].setIcon(bricksImage2);
						} else {
								brickLabel[i][j].setIcon(emptyImage);
						}
				}
			}
			
			bomberman.show();
			bombermanLabel.setVisible(bomberman.getVisible());
			bombermanLabel.setIcon(bombermanImage);
			bomberman.setCoordinates(25, 0);
			bombermanPosition = new int [7][12];
			bombermanPosition[0][0] = 1; 
			bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
			
			player1flag=0;
			displayflag1=0;
			
			for (int i=0; i< 4 ; i++) {
				enemyLabel[i].setIcon(enemyImage); 
				enemy[i].setEnemyLabel(enemyLabel[i]);
				enemy[i].show();
				enemy[i].setEnemyAlive(true);
				enemyLabel[i].setVisible(enemy[i].getVisible());
				enemy[i].setFlag(0);
			}
			
			startButton.setVisible(false);
			
			
			try {
				startEnemyCommand(0);
				startEnemyCommand(1);
				startEnemyCommand(2);
				startEnemyCommand(3);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


////////////////////////////////////////////////////CONNECTIONS///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void command(String direction) throws IOException {
				
		//set up a communication socket
		Socket s = new Socket("localhost", SERVER_PORT);
		
		//Initialize data stream to send data out
		OutputStream outstream = s.getOutputStream();
		PrintWriter out = new PrintWriter(outstream);
		
		if(direction=="up") {
			String command = "PLAYER 1 UP\n";
			System.out.println("Sending: " + command);
			out.println(command);
			out.flush();
		}
		
		if(direction=="down") {
			String command = "PLAYER 1 DOWN\n";
			System.out.println("Sending: " + command);
			out.println(command);
			out.flush();
		}
		
		if(direction=="left") {
			String command = "PLAYER 1 LEFT\n";
			System.out.println("Sending: " + command);
			out.println(command);
			out.flush();
		}
		
		if(direction=="right") {
			String command = "PLAYER 1 RIGHT\n";
			System.out.println("Sending: " + command);
			out.println(command);
			out.flush();
		}
		
		s.close();
		
	}
	
	public void startCommand(String name) throws IOException {
		
		//set up a communication socket
		Socket s = new Socket("localhost", SERVER_PORT);
		
		//Initialize data stream to send data out
		OutputStream outstream = s.getOutputStream();
		PrintWriter out = new PrintWriter(outstream);
		
		String command = "NAME "+name+" \n";
		System.out.println("Sending: " + command);
		out.println(command);
		out.flush();
	
		
		s.close();
		
	}
	
	public void startBombCommand() throws IOException {
		
		//set up a communication socket
		Socket s = new Socket("localhost", SERVER_PORT);
		
		//Initialize data stream to send data out
		OutputStream outstream = s.getOutputStream();
		PrintWriter out = new PrintWriter(outstream);
		
		String command = "BOMB \n";
		System.out.println("Sending: " + command);
		out.println(command);
		out.flush();
	
		
		s.close();
		
	}
	
	public void startEnemyCommand( int i) throws IOException {
		
		//set up a communication socket
		Socket s = new Socket("localhost", SERVER_PORT);
		
		//Initialize data stream to send data out
		OutputStream outstream = s.getOutputStream();
		PrintWriter out = new PrintWriter(outstream);
		
		String command = "MOVE "+i+" \n";
		System.out.println("Sending: " + command);
		out.println(command);
		out.flush();
	
		
		s.close();
		
	}
	
}