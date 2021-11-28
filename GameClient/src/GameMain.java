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
	///////////////////////////////////////////////////////////////////////////////////////////////////////
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
					
					//processExplosion(bombLabel, bomb_ex);
				
			
			TimerTask task2 = new TimerTask(){
				public void run() {
					
					for (int i=0; i< 5 ; i++) {
					bomb_ex[i].setCoordinates(0,0);
					bombLabel[i].setLocation(bomb_ex[i].getX(), bomb_ex[i].getY());
					bombLabel[i].setIcon(emptyImage);
					}
					
					//hide enemy if they are caught by explosion
					
					for (int i=0; i< 4 ; i++) {
						if (enemy[i].getEnemyAlive()==false) { 
							enemy[i].hide();
							if (enemy_down[i]==0) {
								points1 = points1 + 1000;
								scores1Label.setText(String.valueOf(points1));
								scorePoints(points1);
								enemy_down[i]=1;
							}
						}
					} 
					
					//send message if bomberman dies
					if (player1flag==1) {
						JOptionPane.showMessageDialog(null, "You died! Better luck next time!", "GAME OVER!", JOptionPane.INFORMATION_MESSAGE);
						displayAllScores();
						bombermanLabel.setVisible(false);
					}
					
					//message if all enemies are down
					if(enemy[0].getEnemyAlive()==false && enemy[1].getEnemyAlive()==false && enemy[2].getEnemyAlive()==false && enemy[3].getEnemyAlive()==false) {
						JOptionPane.showMessageDialog(null, "YOU WON!", "CONGRATULATIONS!", JOptionPane.INFORMATION_MESSAGE);
						displayAllScores();
						bombermanLabel.setVisible(false);
						
					}
					
					for (int i=0; i< 4 ; i++) {
						enemyLabel[i].setVisible(enemy[i].getVisible());
					}
				
				}
			};
			
			//Process: 
			//Change image for the initial bomb 
			//bombImage = new ImageIcon( getClass().getResource( bomb_ex[0].getFilename() ) );
			//bombLabel[0].setIcon(bombImage);
			//Place the bomb in the same place the bomberman is located
			//bombLabel[0].setLocation(bomberman.getX(), bomberman.getY());
			//giving values X and Y so it can recognize collision with rectangles
			//bomb_ex[0].setCoordinates(bomberman.getX(),bomberman.getY());
			
			// explosion and collisions with walls and bomberman
			//timer.schedule(task, 2000);
			
			// reseting the bomb image
			timer.schedule(task2, 2000);
			
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
			
			try {
				Class.forName("org.sqlite.JDBC");
				System.out.println("Database Driver Loaded");
				
				String dbURL = "jdbc:sqlite:product.db";
				conn = DriverManager.getConnection(dbURL);
				
				if (conn != null) {
					System.out.println("Connected to database");
					conn.setAutoCommit(false);
					
					stmt = conn.createStatement();
										
					//String sql = "DROP TABLE SCORES";
					//stmt.executeUpdate(sql);
					//conn.commit();
					
					String sql = "CREATE TABLE IF NOT EXISTS SCORES " +
					             "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
							     " NAME TEXT NOT NULL, " + 
					             " SCORE INT NOT NULL) ";
					
					stmt.executeUpdate(sql);
					conn.commit();
					System.out.println("Table Created Successfully");
					
					sql = "INSERT INTO SCORES (NAME, SCORE) VALUES " + 
	                        "('"+ player1_name+"', 0)";
					stmt.executeUpdate(sql);
					conn.commit();
										
					conn.close();
				}
				
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
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
			
			startButton.setText("Re-start");
			
			
			try {
				startEnemyCommand(0);
				startEnemyCommand(1);
				startEnemyCommand(2);
				startEnemyCommand(3);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			//for (int i=0; i< 4 ; i++) {
			//	if (!enemy[i].getMoving()) { //check and make enemies move
			//		//start moving
			//		enemy[i].moveEnemy();
			//	}
			//}
			
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

///////////////////////////////////////////////////DATABASE///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void scorePoints(int current_score) {
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="UPDATE SCORES SET SCORE = "+current_score+" WHERE NAME='"+player1_name +"'"; 
                stmt.executeUpdate(sql);
				conn.commit();
								
				conn.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
//////////////////////////////////////////////////////INTERACTION WITH THE BOMBS/////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void processExplosion(JLabel[] bomb,  bomb[] bomb_ex) {
		
		// checking if there is an unbreakable wall besides the bomb, if not then there is an explosion
		for (int i=0; i< map.length ; i++) {
			for (int j=0; j< map[i].length ; j++) {
				
				//bomb explosions check spaces beside the bomb
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()-25)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()-100))
						&& map[i][j]!=2) {
					bomb[1].setIcon(bombImage); 
					bomb[1].setLocation(bomb[0].getX(), bomb[0].getY()-100);
					bomb_ex[1].setCoordinates(bomb[0].getX(),bomb[0].getY()-100);
					}
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()-25)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()+100))
						&& map[i][j]!=2) {
					bomb[2].setIcon(bombImage); 
					bomb[2].setLocation(bomb[0].getX(), bomb[0].getY()+100);
					bomb_ex[2].setCoordinates(bomb[0].getX(),bomb[0].getY()+100);
					} 
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()+75)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()))
						&& map[i][j]!=2) {
					bomb[3].setIcon(bombImage);
					bomb[3].setLocation(bomb[0].getX()+100, bomb[0].getY());
					bomb_ex[3].setCoordinates(bomb[0].getX()+100,bomb[0].getY());	
					}
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()-125)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()))
					&& map[i][j]!=2) {
					bomb[4].setIcon(bombImage);
					bomb[4].setLocation(bomb[0].getX()-100,bomb[0].getY() );
					bomb_ex[4].setCoordinates(bomb[0].getX()-100,bomb[0].getY());
				}
				
			}
		}

		//Erase walls in case there was an explosion		
		for (int i=0; i< map.length ; i++) {
			for (int j=0; j< map[i].length ; j++) {
				
				//bomb explosions check spaces beside the bomb
				for (int k=1; k< 5 ; k++) {
					if ((brickLabel[i][j].getLocation().getX() == (bomb[k].getLocation().getX()-25)) 
							&& (brickLabel[i][j].getLocation().getY() == (bomb[k].getLocation().getY())) 
							&& map[i][j]!=2) {
						brickLabel[i][j].setIcon(emptyImage);
						map[i][j]=0;
					}
				}
				
				for (int k=0; k< 5 ; k++) {
					//check if bomberman was caught in the explosions 
					if (((bombermanLabel.getLocation().getX()-25) == (bomb[k].getLocation().getX()-25)) 
							&& (bombermanLabel.getLocation().getY() == (bomb[k].getLocation().getY()))) {
						bombermanLabel.setIcon(bombermanDownImage); 
						player1flag=1;
					}
					
				}
											
			}
		}
	}
	
///////////////////////////////////////////////////////DISPLAY WIN AND LOSE WHEN PLAYER CLEARS THE GAME////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void displayWinner(JLabel[] bomb, bomb[] bomb_ex, int player, JLabel scoreLabel, String player_name) {
		
		int points = 0;
		
		for (int i=0; i< 5 ; i++) {
			bomb_ex[i].setCoordinates(0,0);
			bomb[i].setLocation(bomb_ex[i].getX(), bomb_ex[i].getY());
			bomb[i].setIcon(emptyImage);
			}
			
			//hide enemy if they are caught by explosion
			
			for (int i=0; i< 4 ; i++) {
				if (enemy[i].getEnemyAlive()==false) { 
					enemy[i].hide();
					if (enemy_down[i]==0) {
						if (player==1) {
							points = points1 + 1000;
							points1 = points;
						} 
												
						scoreLabel.setText(String.valueOf(points));
						scorePoints(points);
						enemy_down[i]=1;
					}
				}
			} 
			
			//send message if bomberman dies
			if (player1flag==1 && displayflag1==0) {
				JOptionPane.showMessageDialog(null, "Player 1 Died! Better luck next time!", "Ooops!", JOptionPane.INFORMATION_MESSAGE);
				displayflag1=1;
				bombermanLabel.setLocation(3000,3000);
				bombermanLabel.setVisible(false);
			}
			
			
			//message if all enemies are down
			if(enemy[0].getEnemyAlive()==false && enemy[1].getEnemyAlive()==false && enemy[2].getEnemyAlive()==false && enemy[3].getEnemyAlive()==false) {
				JOptionPane.showMessageDialog(null, "YOU WON!", "CONGRATULATIONS!", JOptionPane.INFORMATION_MESSAGE);
				displayAllScores();
				bombermanLabel.setVisible(false);
				
			}
			
			for (int i=0; i< 4 ; i++) {
				enemyLabel[i].setVisible(enemy[i].getVisible());
			}
	}
	
	////////////////////////////////////////////////////DISPLAY SCORES///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void displayAllScores() {
		
		String[] id_array = new String[1] ;
		String[] name_array = new String[1];
		String[] score_array = new String[1];
		
		int counter= 0;
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM SCORES ORDER BY SCORE DESC"; 
                ResultSet rs = stmt.executeQuery(sql);
				while ( rs.next() ) {
					counter=counter+1;
				}
				rs.close();
			}
			
			if (conn != null) {
				System.out.println("Connected to database");
				conn.setAutoCommit(false);
				
				stmt = conn.createStatement();
				
				String sql ="SELECT * FROM SCORES ORDER BY SCORE DESC"; 
                
				ResultSet rs = stmt.executeQuery(sql);
				
				id_array = new String [counter];
				name_array = new String [counter];
				score_array = new String [counter];
				
				counter = 0;
				
				while ( rs.next() ) {
					
					int id = rs.getInt("id");
					String name = rs.getString("name");
					int score = rs.getInt("score");
					id_array[counter] = String.valueOf(id);
					name_array[counter] = name;
					score_array[counter] = String.valueOf(score);
					counter=counter+1;
				}
				
				rs.close();
				conn.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder(64);
		
		String[] record = new String[5];
		
		if(counter>5) {counter=5;}
		
		for (int i=0; i<counter; i++) {
		
			record[i] = "<td>" + String.valueOf(id_array[i]) + "</td><td>"+ String.valueOf(name_array[i]) + "</td><td>" + String.valueOf(score_array[i]) + "</td>";
		
		}
		
		sb.append("<html><table><tr><td>Player</td><td>Name</td><td>Score</td></tr>");
	    
	    for (int i=0; i<5; i++) {
	    	sb.append("<tr>").append(record[i]).append("</tr>");
	    }
	    
	    sb.append("</table></html>");
	    
		JOptionPane.showMessageDialog(null, sb, "Top Scores", JOptionPane.INFORMATION_MESSAGE);
		
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