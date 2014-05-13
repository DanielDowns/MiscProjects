package world;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JComponent;
import life.Critter;



public class Environment extends JComponent {

	volatile private ArrayList<Critter> play_pen = new ArrayList<Critter>();
	volatile private ArrayList<Food> food_list = new ArrayList<Food>();
	
	public final Object lock_critter = new Object();
	public final Object lock_food = new Object();
	
	//size of environment
	public int x_width = 100;
	public int y_height = 100;
	
	Executor ex; //launches threads
	
	public boolean display_views;
	public boolean paint_detailed;
	
	/** generates and launches random scenario*/
	Environment(int xsize, int ysize){			
		this.setSize(xsize, ysize);
		
		x_width = xsize;
		y_height = ysize;
		
		display_views = false;
		paint_detailed = false;
		
		int crit_num = 10;
		int food_count = 50;
		
		ex = Executors.newCachedThreadPool(); 		
		for(int i = 0; i < crit_num; i++){				
			Critter c = new Critter(this); 
			c.setFood(0);
			play_pen.add(c);
			ex.execute(c);
		}
		for(int i = 0; i < ((int)Math.ceil(crit_num/2)); i++){			
			Critter c = new Critter(this); 
			c.setFood(1);
			play_pen.add(c);
			ex.execute(c);
		}
		ex.execute(new EnvironmentalThread(this, food_count));
	}
	
	/** creates a scenario with a User Critter*/
	Environment(Critter crit, int xsize, int ysize){
		this.setSize(xsize, ysize);
		
		x_width = xsize;
		y_height = ysize;
		
		display_views = false;
		paint_detailed = false;
		
		int crit_num = 6;
		int food_count = 50;
		
		ex = Executors.newCachedThreadPool(); 	
		if(crit.food == 0){
			for(int i = 0; i < crit_num; i++){				
				Critter c = new Critter(this);
				c.setEqual(crit); 
				c.envi = this;
				c.initializeToBounds(x_width, y_height);
				
				play_pen.add(c);
				ex.execute(c);			
			}
			for(int i = 0; i < crit_num/2; i++){			
				Critter c = new Critter(this); 
				c.setFood(1);
				play_pen.add(c);
				ex.execute(c);	
			}
		}
		else{
			for(int i = 0; i < crit_num/2; i++){				
				Critter c = new Critter(this);
				c.setEqual(crit); 
				c.envi = this;
				c.initializeToBounds(x_width, y_height);
				
				play_pen.add(c);
				ex.execute(c);			
			}
			for(int i = 0; i < crit_num; i++){			
				Critter c = new Critter(this); 
				c.setFood(0);
				play_pen.add(c);
				ex.execute(c);	
			}
		}
		
		ex.execute(new EnvironmentalThread(this, food_count));
	}
	
	//GETTERS 
	public ArrayList<Critter> getCritters(){
		synchronized(lock_critter){
			return play_pen;
		}
	}
	
	public ArrayList<Food> getFood(){
		synchronized(lock_food){
			return food_list;
		}
	}
	
	//END GETTERS
	
	//ADDS NEW ITEMS TO ENVIRONMENT
	public void addFood(){
		synchronized(lock_food){
			food_list.add(new Food(this));
		}
	}
	
	public void addCritter(Critter crit){
		synchronized(lock_critter){
			play_pen.add(crit);
			ex.execute(crit);
		}
	}
	//END ADDERS
	
	/** removes Food item from game*/
	public void destroyFood(Food item){
		synchronized(lock_food){
			ListIterator<Food> iter = food_list.listIterator();
			Food kill = null;
			while(iter.hasNext()){
				Food temp = iter.next();
				if(temp.equals(item)){
					kill = item;
					break;
				}
			}
			if (kill != null){
				food_list.remove(kill);
			}
		}
	}
	
	/** removes the Critter from game*/
	public void destroyCritter(Critter item){
		synchronized(lock_critter){
			ListIterator<Critter> iter = play_pen.listIterator();
			Critter kill = null;
			while(iter.hasNext()){
				Critter temp = iter.next();
				if(temp.equals(item)){
					kill = item;
					break;
				}
			}
			if(kill != null){
				play_pen.remove(kill);
				System.out.println(kill + " dies");
			}
		}
	}
	
	/** paints the environment */
	@Override
	public void paintComponent(Graphics g){
		synchronized(lock_critter){
			 synchronized(lock_food){/////////////////////////////////////////////nested lock
		
				if(display_views == true){
					paintViews(g);
				}
				
				Iterator<Critter> iter = play_pen.listIterator();
				while(iter.hasNext()){
					
					Critter temp = iter.next();
			
					g.drawImage(temp.getGraphic(), temp.getPosition().x, temp.getPosition().y, null);			
					
					char value [] = new String("S:"+Integer.toString(temp.stamina) +"/H:"+temp.health).toCharArray();
					if(temp.food == 0){
						g.setColor(Color.BLUE);
					}
					else{
						g.setColor(Color.RED);
					}
					g.drawChars(value, 0, value.length, temp.getPosition().x, temp.getPosition().y);
				}
				
				Iterator<Food> other_iter = food_list.listIterator();
				while(other_iter.hasNext()){
					Food temp = other_iter.next(); 
					g.drawImage(temp.getGraphic(), temp.position.x, temp.position.y, null);
								
				}
			 }
		}
	}
	
	/** allows painting of the views before anything else to keep the Critters
	 *  viewable at all times
	 *   */
	private void paintViews(Graphics g){
		synchronized(lock_critter){
			Iterator<Critter> iter = play_pen.listIterator();
			while(iter.hasNext()){
			
				Critter temp = iter.next();
				if(temp.food == 1){
					g.setColor(new Color(0xcb,0x60,0x60));
				}
				else{
					g.setColor(new Color(0x75,0xb6,0x77));
				}
				
				g.fillArc((int)(temp.getArcX()-temp.arc_width/2), (int)(temp.getArcY()-temp.arc_height/2), (int)temp.arc_width, 
						(int)temp.arc_height, (int)Math.ceil(temp.arc_start), (int)Math.ceil(temp.arc_degrees));
				g.fillOval((int)temp.getCircleX(), (int)temp.getCircleY(), (int)temp.circ_width, (int)temp.circ_height);
				
			}
		}
	}

}
