import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class GCService implements Runnable {

	private Socket s;
	private Scanner in;
	private int[][] map = { {3,3,1,3,1,1,1,1,1,3,3,3},{3,2,1,3,2,3,1,2,1,3,2,3},{3,1,3,3,3,3,1,1,1,1,3,1},{3,3,3,3,2,1,3,2,1,1,3,1},{3,1,3,3,1,3,3,1,1,1,3,1},{3,2,3,1,2,1,3,2,1,1,2,1},{3,1,3,1,1,1,3,1,1,1,3,1} };
	private bomber bomberman;
	private JLabel bombermanLabel, player1Label;
	private JLabel enemy_Label[]=new JLabel[4];
	private JLabel bomb[]=new JLabel[4];
	private bomb bomb_ex[] = new bomb[4];
	private JLabel brickLabel[][]=new JLabel[7][12];
	private enemy enemy[]= new enemy[4];
	private ImageIcon bombermanImage, bombermanDownImage, bricksImage, bricksImage2,emptyImage, bombImage, enemyImage;
	private walls bricks;
	
	public int[][] getMap(){return map;}
	
	public GCService (Socket aSocket, bomber bomberman, JLabel bombermanLabel, JLabel player1Label, enemy enemy[], JLabel enemy_Label[],JLabel brickLabel[][], JLabel bomb[], bomb bomb_ex[]) {
		this.s = aSocket;
		this.bomberman = bomberman;
		this.bombermanLabel = bombermanLabel;
		this.player1Label = player1Label;
		this.enemy = enemy;
		this.enemy_Label = enemy_Label;
		this.brickLabel=brickLabel;	
		this.bomb_ex=bomb_ex;
		this.bomb=bomb;
	}
	
	public void run() {
		
		
		
		try {
			in = new Scanner(s.getInputStream());
			processRequest( );
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	//processing the requests
	public void processRequest () throws IOException {
		//if next request is empty then return
		while(true) {
			if(!in.hasNext( )){
				return;
			}
			String command = in.next();
			if (command.equals("Quit")) {
				return;
			} else {
				executeCommand(command);
			}
		}
	}
	
	public void executeCommand(String command) throws IOException{
	
		bricks = new walls();
		bricksImage = new ImageIcon( getClass().getResource( bricks.getFilename() ) );
		bricksImage2 = new ImageIcon( getClass().getResource( "walls2.png" ) );
		emptyImage = new ImageIcon( getClass().getResource( "white.png" ) );
		bombImage = new ImageIcon( getClass().getResource( bomb_ex[0].getFilename() ) );
		bombermanDownImage = new ImageIcon( getClass().getResource( "smallninja2.png" ) );
		
		if ( command.equals("PLAYER")) {
			String playerAction = in.next();
			int playerX = in.nextInt();
			int playerY = in.nextInt();
			System.out.println("Player "+" "+playerAction + " "+playerX+", "+playerY);
			
			bomberman.setX(playerX);
			bomberman.setY(playerY);
			bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
		
		}
		
		if (command.equals("BOMB")) {
			int bombNo = in.nextInt();
			int bombX = in.nextInt();
			int bombY = in.nextInt();
			
			System.out.println("bomb" +  bombNo + bombX + bombY);
			
			bomb[bombNo].setIcon(bombImage); 
			bomb[bombNo].setLocation(bombX, bombY);
			bomb_ex[bombNo].setCoordinates(bombX,bombY);
		}
		
		if (command.equals("NAME")) {
			String name = in.next();
			
			player1Label.setText(name);
		}
		
		if (command.equals("BOMBERMAND")) {
			
			bombermanLabel.setIcon(bombermanDownImage);
		}
		
		if (command.equals("WALLS")) {
			int positionX = in.nextInt();
			int positionY = in.nextInt();
			
			brickLabel[positionX][positionY].setIcon(emptyImage);
		}
		
		if (command.equals("ENEMY")) {
			int enemyNo = in.nextInt();
			int enemyX = in.nextInt();
			int enemyY = in.nextInt();
			
			enemy[enemyNo].setX(enemyX);
			enemy[enemyNo].setY(enemyY);
			enemy_Label[enemyNo].setLocation(enemy[enemyNo].getX(),enemy[enemyNo].getY());
			
		}
		
	}
}
