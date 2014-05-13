package world;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;


public class Food {
	Point position = new Point();
	
	BufferedImage image;
	
	public Food(Environment E){
		Random rand = new Random();
		position.x = rand.nextInt(E.getWidth());
		position.y = rand.nextInt(E.getHeight());
		
		try {
			//change this to the correct location on your computer
			image = ImageIO.read(new File("C:\\Users\\Daniel\\EclipseWorkspace\\Evo\\sprites\\omnicrit.png")); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getGraphic(){
		return image;
	}
	
	public Point getPosition(){
		return position;
	}
}
