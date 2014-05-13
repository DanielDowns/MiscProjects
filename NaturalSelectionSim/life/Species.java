package life;

import java.util.ArrayList;

public enum Species {
	CARN1,
	CARN2,
	CARN3,
	CARN4,
	CARN5,
	HERB1,
	HERB2,
	HERB3,
	HERB4,
	HERB5;
	
	int stamina;
	int health;
	int legs;
	int eyes;
	int claws;
	int food; //0 = herb, 1 = carn
	
	private ArrayList<Species> herbs = new ArrayList<Species>();
	private ArrayList<Species> carns = new ArrayList<Species>();
	
	/** Randomly generated Species */
	Species(){
		int talentpool;
		
		
	}
	
	/** Player created species */
	Species(int s, int h, int l, int e, int c, int f){
		stamina = s;
		health = h;
		legs = l;
		eyes = e;
		claws = c;
		food = f;
	}
	
	/** returns an Herbavore Critter currently not in use */
	public Species getUnusedHerbavore(){
		
		int index = (int) (Math.random() * (herbs.size() + 1));
		return herbs.remove(index);
	}
	
	/** returns an Carnavore Critter currently not in use */
	public Species getUnusedCarnavore(){
		
		int index = (int) (Math.random() * (carns.size() + 1));
		return carns.remove(index);
	}
	
	public void freeSpecies(Species s){
		
	}

}
