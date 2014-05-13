package life;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import world.Direction;
import world.Environment;
import world.Food;


public class CritterUtil {
	
	/** gets a list of all food items the Critter can see*/
	public static ArrayList<Food> getViewableFood(Critter self, Environment envi){
		synchronized(envi.lock_food){		
		Arc2D arc = getView(self);
		Ellipse2D circle = getCloseView(self);
			
		ArrayList<Food> viewable = new ArrayList<Food>();
	
			Iterator<Food> iter = envi.getFood().listIterator();
			while(iter.hasNext()){
				Food temp = iter.next();
				if(arc.contains(temp.getPosition().x, temp.getPosition().y)){
					viewable.add(temp);
				}
				else if(circle.contains(temp.getPosition().x, temp.getPosition().y)){
					viewable.add(temp);
				}
			}
			return viewable;
		}
	}
	
	/** returns the closest food item*/
	public static Food getNearestFood(Critter self, Environment envi){
		synchronized(envi.lock_food){		
			List<Food> food = getViewableFood(self, envi); 		
			
			if(food.isEmpty()){
				return null;
			}
			Food closest = null;
			double min = 10000000;
		
			Iterator<Food> iter = food.listIterator();
			while(iter.hasNext()){
				Food temp = iter.next();
				double distance = Math.sqrt(Math.pow((self.getPosition().x - temp.getPosition().x), 2) + 
						Math.pow((self.getPosition().y - temp.getPosition().y), 2));
				if (distance < min){
					min = distance;
					closest = temp;
				}
			}
			
			return closest;
		}
	}
	
	
	/** gets a list of all Critters the Critter can see */
	public static ArrayList<Critter> getViewableCritters(Critter self, Environment envi){
		synchronized(envi.lock_critter){
		
		Arc2D arc = getView(self);
		Ellipse2D circle = getCloseView(self);
			
		ArrayList<Critter> viewable = new ArrayList<Critter>();
	
			Iterator<Critter> iter = envi.getCritters().listIterator();
			while(iter.hasNext()){
				Critter temp = iter.next();
				if(temp.toString().equals(self.toString())){
					continue;
				}
				if(arc.contains(temp.getPosition().x, temp.getPosition().y)){
					viewable.add(temp);
				}
				else if(circle.contains(temp.getPosition().x, temp.getPosition().y)){
					viewable.add(temp);
				}
			}
			return viewable;
		}
	}
	
	

	/** returns the closest herbivore Critter */
	public static Critter getNearestPrey(Critter self, Environment envi){
		synchronized(envi.lock_critter){
		
		List<Critter> critters = getViewableCritters(self, envi);
		if(critters.isEmpty()){
			return null;
		}
		
		Critter closest = null;
		double min = 100000000;
		
			Iterator<Critter> iter = critters.listIterator();
			while(iter.hasNext()){
				Critter temp = iter.next();
				double distance = Math.sqrt(Math.pow(self.getPosition().x - temp.getPosition().x, 2) + 
						Math.pow(self.getPosition().y - temp.getPosition().y, 2));
				if (distance == 0){
					continue;
				}
				if(self.stamina >= (self.stamina_stat/10) && temp.getFood() == 1){ //will eat carn if stamina is too low
					continue;
				}
				if(distance < min){
					min = distance;
					closest = temp;
				}
			}
			
		return closest;
		}
	}

	/** */
	public static Ellipse2D getCloseView(Critter self){
		
		self.circ_height = self.circ_width = (self.eyes_stat * 10) + 20;
		self.setCircleX(self.getPosition().x - (self.circ_width/2));
		self.setCircleY(self.getPosition().y - (self.circ_height/2));
		
		Ellipse2D circle = new Ellipse2D.Double(self.getCircleX(), self.getCircleY(), self.circ_width, self.circ_height);
		return circle;
	}
	
	/** returns a 2D arc that represents what the critter can see */
	public static Arc2D getView(Critter self){
		
		int degrees = 80;
		double start = 0;
		double width, height, x = 0, y = 0;
		
		width = height = self.eyes*100;
		x = self.getPosition().x;
		y = self.getPosition().y;
		
		if(self.view == Direction.NE){
			start = 22.25 - 17.5;

		}
		else if(self.view == Direction.N){
			start = 67.25 - 17.5;

		}
		else if(self.view == Direction.NW){
			start = 112.25 - 17.5;

		}
		else if(self.view == Direction.W){
			start = 157.25 - 17.5;

		}
		else if(self.view == Direction.SW){
			start = 202.25 - 17.5;

		}
		else if(self.view == Direction.S){
			start = 247.25 - 17.5;

		}
		else if(self.view == Direction.SE){
			start = 292.25 - 17.5;

		}
		else if(self.view == Direction.E){
			start = 337.25 - 17.5;

		}
		
		self.arc_degrees = degrees;
		self.arc_start = start;
		self.arc_width = width;
		self.arc_height = height;
		self.setArcX(x);
		self.setArcY(y);
		
		Arc2D arc = new Arc2D.Double(x-(width/2), y-(height/2), width, height, start, degrees, Arc2D.PIE);
		return arc;
	}
	
	
	
	/** determines if two points are close enough to count as a collision */
	public static boolean collide(Point self, Point other){
		double distance = Math.sqrt(Math.pow(self.x - other.x, 2) + Math.pow(self.y - other.y, 2));
		if(distance <= 7){
			return true;
		}
		return false;
	}

	
	/** assigns to the correct sprite and color to each Critter*/
	public static void applyDetailedSprite(Critter crit){
		try {
			String where = new String("C:\\Users\\Daniel\\EclipseWorkspace\\Evo\\sprites\\");
			if(crit.view == Direction.N){
				where+="Ncrit.png";
			}
			else if(crit.view == Direction.S){
				where+="Scrit.png";
			}
			else if(crit.view == Direction.W){
				where+="Wcrit.png";
			}
			else if(crit.view == Direction.E){
				where+="Ecrit.png";
			}
			else if(crit.view == Direction.NE){
				where+="NEcrit.png";
			}
			else if(crit.view == Direction.SE){
				where+="SEcrit.png";
			}
			else if(crit.view == Direction.NW){
				where+="NWcrit.png";
			}
			else if(crit.view == Direction.SW){
				where+="SWcrit.png";
			}
			
			
			crit.image = ImageIO.read(new FileInputStream(where));
			
			for(int i = 0; i < crit.image.getWidth(); i++){
				for(int j = 0; j < crit.image.getHeight(); j++){
					if(crit.image.getRGB(i, j) != Color.WHITE.getRGB()){
						if(crit.food == 1){
							crit.image.setRGB(i, j, Color.RED.getRGB());						
						}
						else{
							crit.image.setRGB(i, j, Color.GREEN.getRGB());	
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/** applies simple sprite to all Critters
	 * 
	 * Is the more efficient than rechoosing each step
	 * */
	public static void massApplySimpleSprite(Environment envi){
		synchronized(envi.lock_critter){
			Iterator<Critter> iter = envi.getCritters().listIterator();
			while(iter.hasNext()){
				Critter crit = iter.next();
			
				try {
					crit.image = ImageIO.read(new FileInputStream("C:\\Users\\Daniel\\EclipseWorkspace\\Evo\\sprites\\Omnicrit.png"));
					for(int i = 0; i < crit.image.getWidth(); i++){
						for(int j = 0; j < crit.image.getHeight(); j++){
							if(crit.image.getRGB(i, j) != Color.WHITE.getRGB()){
								if(crit.food == 1){
									crit.image.setRGB(i, j, Color.RED.getRGB());						
								}
								else{
									crit.image.setRGB(i, j, Color.GREEN.getRGB());	
								}
							}
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
