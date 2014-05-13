package world;

public class EnvironmentalThread implements Runnable{

	Environment envi;
	int food_count;
	int food_needed;
	
	EnvironmentalThread(Environment e, int count){
		envi = e;
		food_count = 0;
		food_needed = count;
	}
	
	@Override
	public void run() {
		startFood();
		while(true){			
			moreFood();
			envi.repaint();
			if(envi.getCritters().size() == 0){
				break;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void startFood(){
		synchronized(envi.lock_food){
			int count = 0;
			while(count < 15){
				envi.addFood();
				food_count++;
				count++;
				if(food_count == food_needed){
					break;
				}
				
			}
		}
	}
	
	private void moreFood(){
		synchronized(envi.lock_food){
			food_count = envi.getFood().size();
			int count = 0;
			while(count < 5){
				envi.addFood();
				food_count++;
				count++;
				if(food_count == food_needed){
					break;
				}
				
			}
		}
	}
	
}
