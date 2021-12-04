import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class enemy extends Sprite {
	
	private Boolean moving, visible, enemyAlive, bombermanAlive,horizontal, direction; 
	private Thread t;
	private JLabel enemyLabel, bombermanLabel, bombLabel;
	private int limit = 0,flag=0;
	private JButton animationButton;
	private bomber bomberman;
	private bomb bomb, bomb_ex_right, bomb_ex_left, bomb_ex_up, bomb_ex_down;
	private Connection conn = null;
	private Statement stmt = null;
	
	public Boolean getMoving() {return moving;}
	public Boolean getEnemyAlive() {return enemyAlive;} 
	public int getLimit() {return limit;}
	public int getFlag() {return flag;}
	public Boolean getBombermanAlive() {return bombermanAlive;}
	public Boolean getVisible() {return visible;}
	
	public void setMoving(Boolean moving) {	this.moving = moving;}
	
	//Work with bomb, bomb explosion and bomberman features
	public void setBomberman (bomber temp) {this.bomberman=temp;}
	public void setFlag (int temp) {this.flag=temp;}
	public void setBomb(bomb temp) {this.bomb= temp;}
	public void setVisible(Boolean visible) {this.visible = visible;}
	public void setEnemyAlive(Boolean temp) {this.enemyAlive=temp;}
	public void setBombermanAlive(Boolean temp) {this.bombermanAlive=temp;}
	
	public void setBombEx(bomb temp, bomb temp2, bomb temp3, bomb temp4) {
		this.bomb_ex_right= temp;
		this.bomb_ex_left= temp2;
		this.bomb_ex_up= temp3;
		this.bomb_ex_down= temp4;
	}
	
	public void setEnemyLabel(JLabel temp) {this.enemyLabel = temp;}
	public void setBombermanLabel(JLabel temp) {this.bombermanLabel = temp;}
	public void setBombLabel(JLabel temp) {this.bombLabel= temp;}
	public void setAnimationButton(JButton temp) {this.animationButton = temp;}
	public void setLimit(int temp) {this.limit = temp;}
	
	public void hide() { this.visible= false; }
	public void show() { this.visible= true; }
	
	public enemy() {
		   super(75,100,"enemy.png");
		   this.moving=false;
		   this.direction=true;
		   this.enemyAlive=true;
		   this.visible=false;
		   this.horizontal=true;
		   this.bombermanAlive=true;
	}
	
	public enemy(Boolean horizontal) {
		   super(75,100,"enemy.png");
		   this.moving=false;
		   this.direction=true;
		   this.enemyAlive=true;
		   this.visible=false;
		   this.horizontal=horizontal;
		   this.bombermanAlive=true;
	}
	
	public enemy(JLabel temp) {
		   super(75,100,"enemy.png");
		   this.moving=false;
		   this.direction=true;
		   this.enemyAlive=true;
		   this.enemyLabel= temp;
		   this.horizontal=true;
		   this.visible=false;
		   this.bombermanAlive=true;
	 }
	
		
	
}
