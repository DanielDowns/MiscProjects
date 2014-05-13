package life;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

import world.Direction;
import world.Environment;
import world.Food;

public class Critter implements Runnable{


	//stats (that are never modified)//////////////////
	public int stamina_stat;
	public int health_stat;
	public int legs_stat;
	public int eyes_stat;
	public int claws_stat;
	
	public int food; //0 = herb, 1 = carn
	public int move_distance; //how far it can move
	
	public Environment envi; //the environment it inhabits
	
	//stats (that are modified)////////////////////////
	public int stamina = 1;
	public int health = 1;
	public int legs = 1;
	public int eyes = 1;
	public int claws = 1;	

	Direction view = Direction.N;  //direction sprite is facing
	
	private Point position = new Point();      //where it is
	private Point last_position = new Point(); //where it was
	
	public BufferedImage image; //what it looks like
	
	//Viewing arc data (used for GUI)////////////////////////////////
	public double arc_degrees;
	public double arc_width;
	public double arc_height;
	public double arc_start;
	private double arc_x;
	private double arc_y;
	
	//Viewing circle data (used for GUI)////////////////////////////////
	public double circ_height;
	public double circ_width;
	public double circ_start;
	private double circ_x;
	private double circ_y;
	
	/////////////////////////////////////////
	
	/** Standard Constructor */
	public Critter(Environment E){
		
		Random rand = new Random();
		envi = E;
		
		stamina += (rand.nextInt(5) + 1) * 50;
		health += (rand.nextInt(5) + 2);
		legs += (rand.nextInt(5) + 1);
		eyes += (rand.nextInt(5) + 1);
		claws += (rand.nextInt(5) + 1);
		
		stamina_stat = stamina;
		health_stat = health; 
		legs_stat = legs;
		eyes_stat = eyes;
		claws_stat = claws;
			
		move_distance = legs;
		
		food = rand.nextInt(2);
		
		position.x = rand.nextInt(envi.x_width);
		position.y = rand.nextInt(envi.y_height);
		
		try {
			//change this to the correct location on your computer
			image = ImageIO.read(new File("C:\\Users\\Daniel\\EclipseWorkspace\\Evo\\sprites\\omnicrit.png"));
			
			for(int i = 0; i < image.getWidth(); i++){
				for(int j = 0; j < image.getHeight(); j++){
					if(image.getRGB(i, j) != Color.WHITE.getRGB()){
						if(food == 1){
							image.setRGB(i, j, Color.RED.getRGB());						
						}
						else{
							image.setRGB(i, j, Color.GREEN.getRGB());	
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(food == 1)
			System.out.println(this.toString()+" created");
		
	} /// END STANDARD CONSTRUCTOR
	
	
	/** reproduction constructor */
	private Critter(Environment E, Critter mother){	
		
		envi = E;
		
		image = mother.image;
		
		stamina = stamina_stat = mother.stamina_stat;
		health = health_stat = mother.health_stat;
		eyes = eyes_stat = mother.eyes_stat;
		claws = claws_stat = mother.claws_stat;
		legs = legs_stat = mother.legs_stat;
		food = mother.food;
		position.x = mother.getPosition().x+5;
		position.y = mother.getPosition().y+5;
		move_distance = mother.legs;
		
		if(food == 1)
		System.out.println(this.toString()+" created");
	}
	
	/** creates a Critter based on user specs
	 * 
	 *  initializeToBounds() MUST BE CALLED
	 *  */
	public Critter(Environment E, int s, int h, int l, int c, int e, int f){
		envi = E;
		
		stamina_stat = stamina = (s * 50);
		health_stat = health = h;
		legs_stat = legs = move_distance = l;
		claws_stat = claws = c;
		eyes_stat = eyes = e;
		food = f;
		
		try {
			//change this to the correct location on your computer
			image = ImageIO.read(new File("C:\\Users\\Daniel\\EclipseWorkspace\\Evo\\sprites\\omnicrit.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		for(int i = 0; i < image.getWidth(); i++){
			for(int j = 0; j < image.getHeight(); j++){
				if(image.getRGB(i, j) != Color.WHITE.getRGB()){
					if(food == 1){
						image.setRGB(i, j, Color.RED.getRGB());						
					}
					else{
						image.setRGB(i, j, Color.GREEN.getRGB());	
					}
				}
			}
		}
		
	}
	
	///GETTERS
	public BufferedImage getGraphic() {
		return image;
	}
	
	public Point getPosition(){
		return position;
	}
	
	public int getFood(){
		return food;
	}
	
	public double getArcX(){
		return arc_x;
	}
	
	public double getArcY(){
		return arc_y;
	}
	
	public double getCircleX(){
		return circ_x;
	}
	
	public double getCircleY(){
		return circ_y;
	}
	///END GETTERS
	
	///SETTERS
	public void setFood(int f){
		food = f;
		for(int i = 0; i < image.getWidth(); i++){
			for(int j = 0; j < image.getHeight(); j++){
				if(image.getRGB(i, j) != Color.WHITE.getRGB()){
					if(food == 1){
						image.setRGB(i, j, Color.RED.getRGB());						
					}
					else{
						image.setRGB(i, j, Color.GREEN.getRGB());	
					}
				}
			}
		}
	}
	
	public void initializeToBounds(int xbound, int ybound){
		Random rand = new Random();
		position.x = rand.nextInt(xbound);
		position.y = rand.nextInt(ybound);
	}
	
	public void setArcX(double x){
		arc_x = x;
	}
	
	public void setArcY(double y){
		arc_y = y;
	}
	
	public void setCircleX(double x){
		circ_x = x;
	}
	
	public void setCircleY(double y){
		circ_y = y;
	}
	///END SETTERS
	
	/** the life of the critter*/
	@Override
	public void run() {
		int regen_marker = stamina;
		while(true){
			
			if(health >= 1){						//hunts
				if(!hunt()){
					wander();
				}
			}
			else{
				wander();
			}

			if(stamina > stamina_stat *2){     //reproduces
				reproduce();
			}
			
			stamina--;                         //loses energy and heals
			if((regen_marker - stamina) >= 50 && health < health_stat){
				health++;
			}
			
			face();
			if(health <= 0 || stamina <= 0){   //dies
				break; 
			}
		}
		envi.destroyCritter(this);
	}
	
	/** moves toward and attempts to eat the nearest viable food source*/
	private boolean hunt(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(food == 0){
			Food target = CritterUtil.getNearestFood(this, envi);
			if(target == null){
				return false;
			}
			moveTo(target.getPosition());
			if(CritterUtil.collide(this.getPosition(), target.getPosition())){
				envi.destroyFood(target);
				stamina+=100;
			}
		}
		else{
			Critter target = CritterUtil.getNearestPrey(this, envi);
			if(target == null){
				return false;
			}
			moveTo(target.position);
			if(CritterUtil.collide(this.getPosition(), target.getPosition())){
				target.health -= this.claws;
				if(target.health <= 0){
					if(target.food == 1){ //spawning and eating the copy makes self sustaining populations.
						stamina+=75;      //this prevents that.
					}
					else{
						stamina+=100;
					}
					
				}
				else{
//					health--;     //damage from target "fighting back"
				}
				try {
					Thread.sleep(claws*100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/** slowly moves and looks around randomly*/
	private void wander(){
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		last_position.setLocation(position.x, position.y);
		
		int distance = legs * 2;
		Random rand = new Random();
		int x = rand.nextInt(distance) - (int) Math.ceil((float)distance/2);
		int y = rand.nextInt(distance) - (int) Math.ceil((float)distance/2);
				
		position.x += x;
		position.y += y;
	}
	
	/** creates a new copy of the critter once it has double the starting stamina*/
	private void reproduce(){
		Critter baby = new Critter(envi, this);
		stamina /= 2;
		envi.addCritter(baby);
	}

	
	/** travels to the given point*/
	private void moveTo(Point point){
		last_position.setLocation(position.x, position.y);
		
		float x_slope = point.x - position.x;
		float y_slope = point.y - position.y;
		
		float slope = Math.abs(y_slope/x_slope);	
		
		float tape = move_distance;
		int small_move = (int) Math.ceil(tape/(slope+1));	
			
		if(position.x <= point.x && position.y <= point.y){ 
			position.x += small_move; 
			position.y += (tape - small_move);
		}
		else if(position.x <= point.x && position.y >= point.y){ 
			position.x += small_move; 
			position.y -= (tape - small_move);
		}
		else if(position.x >= point.x && position.y <= point.y){ 
			position.x -= small_move; 
			position.y += (tape - small_move);
		}
		else if(position.x >= point.x && position.y >= point.y){
			position.x -= small_move; 
			position.y -= (tape - small_move);	
		}
		
	}

    /** points the critter in the direction its moving*/
	private void face(){
		try{
			double slope = (double)(position.y - last_position.y)/(double)(position.x - last_position.x);
			
			//change slope to angle
			slope = Math.toDegrees(Math.atan(slope));
			
			
			if(slope < 0){
				slope = Math.abs(slope);
			}
			
			if((position.x > last_position.x && position.y > last_position.y) || (position.x < last_position.x && position.y < last_position.y)){
				slope = 0 - slope;
			}
			
			
			if(slope == Double.POSITIVE_INFINITY){
				if(position.x > last_position.x){
					view = Direction.E;
				}
			}
			else if(slope <= 90 && slope >= 67.25){
				if(position.y < last_position.y){
					view = Direction.N;
				}
				else{
					view = Direction.S;
				}
			}
			else if(slope <= 67.25 && slope >= 22.25){
				if(position.x > last_position.x){
					view = Direction.NE;
				}
				else{
					view = Direction.SW;
				}
			}
			else if(slope <= 22.25 && slope >= -22.25){
				if(position.x > last_position.x){
					view = Direction.E;
				}
				else{
					view = Direction.W;
				}
			}
			else if(slope <= -22.25 && slope >= -67.25){
				if(position.x > last_position.x){
					view = Direction.SE;
				}
				else{
					view = Direction.NW;
				}
			}
			else if(slope <= -67.25 && slope >= -90){
				if(position.y < last_position.y){
					view = Direction.N;
				}
				else{
					view = Direction.S;
				}
			}
		}
		catch(ArithmeticException e){
			if(position.y < last_position.y){
				view = Direction.N;
			}
			else{
				view = Direction.S;
			}
		}
		
		if(envi == null){
			System.out.println(food);
			System.out.println(position.x +"/"+ position.y);
			System.out.println(this.toString());
		}
		if(envi.paint_detailed == true){
			CritterUtil.applyDetailedSprite(this);
		}

		envi.repaint();
	}

	public void setEqual(Critter c){
		stamina_stat = c.stamina_stat;
		health_stat = c.health_stat;
		legs_stat = c.legs_stat;
		eyes_stat = c.eyes_stat;
		claws_stat = c.claws_stat;
		
		food = c.food; //0 = herb, 1 = carn
		move_distance = c.move_distance; //how far it can move
		
		envi = c.envi; //the environment it inhabits
		
		//stats (that are modified)////////////////////////
		stamina = c.stamina;
		health = c.health;
		legs = c.legs;
		eyes = c.eyes;
		claws = c.claws;	

		view = c.view;
		
		position.x = c.position.x;
		position.y = c.position.y;
		
		last_position.x = c.last_position.x;
		last_position.y = c.last_position.y;
		
		image = c.image; //what it looks like
		
		//Viewing arc data (used for GUI)////////////////////////////////
		arc_degrees = c.arc_degrees;
		arc_width = c.arc_width;
		arc_height = c.arc_height;
		arc_start = c.arc_start;
		arc_x = c.arc_x;
		arc_y = c.arc_y;
		
		//Viewing circle data (used for GUI)////////////////////////////////
		circ_height = c.circ_height;
		circ_width = c.circ_width;
		circ_start = c.circ_start;
		circ_x = c.circ_x;
		circ_y = c.circ_y;
	}
}
