package world;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import life.Critter;
import life.CritterUtil;



public class Main extends JApplet{

	Environment planet;
	int dimension;
	
	public void init(){
		
		dimension = 750;	
		this.setSize(dimension, dimension);
	
		try{
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override
				public void run() {
					startMenu();				
					
				}				
			});
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		
	}
	
	/** creates the GUI shown during initial startup */
	public void startMenu(){
		final JPanel startscreen = new JPanel();
		final JButton auto_button = new JButton("Watch an entirely random simulation.");
		final JButton game_button = new JButton("Create your own species to fight for survival!");
		
		auto_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remove(startscreen);			
				if(planet != null){
					remove(planet);
				}		
				buildToolbar();
				validate();
				planet = new Environment(dimension,dimension);
				add(planet);			
			}		
		});
		
		game_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(startscreen);
				
				setLayout(new GridBagLayout());
				GridBagConstraints constraints = new GridBagConstraints();
				
				final ScreenBuilder screen = new ScreenBuilder().buildCritterScreen();
				constraints.gridx = 0;
				constraints.gridy = 0;
				add(screen, constraints);
				
				final JButton launch = new JButton("Launch the Simulation");
				launch.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						Critter crit = screen.generateCritter(planet);
						crit.initializeToBounds(dimension, dimension);
						remove(screen);
						remove(launch);
						buildToolbar();
						validate();
						
						crit.envi = planet = new Environment(crit,dimension,dimension);
						
						add(planet);
						
					}				
				});
				constraints.gridx = 0;
				constraints.gridy = 1;
				launch.setSize(100, 300);
				add(launch, constraints);
				
				validate();	
				repaint();
			}		
		});
		
		
		auto_button.setSize((int)Math.floor(dimension/2), (int)dimension);
		
		startscreen.add(auto_button);
		startscreen.add(game_button);
		
		add(startscreen);	
	}
	
	
	
	/** Creates the GUI toolbar options shown during the simulation*/
	public void buildToolbar(){
		final JMenuBar menubar = new JMenuBar();
		JMenu display_menu = new JMenu("Display Options");
		
		final JCheckBoxMenuItem view = new JCheckBoxMenuItem("Show Critter views");
		view.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(view.isSelected()){
					planet.display_views = true;
					view.setText("Hide Critter views");
				}
				else{
					planet.display_views = false;
					view.setText("Show Critter views");
				}					
			}
			
		});
		display_menu.add(view);
		
		final JCheckBoxMenuItem sprites = new JCheckBoxMenuItem("Show detailed Critters");
		sprites.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(sprites.isSelected()){
					sprites.setText("Show simple Critters");
					planet.paint_detailed = true;
					
				}
				else{
					sprites.setText("Show detailed Critters");
					planet.paint_detailed = false;
					CritterUtil.massApplySimpleSprite(planet);
					
				}					
			}
			
		});
		display_menu.add(sprites);
		
		menubar.add(display_menu);
		
		
		setJMenuBar(menubar);
	}

}



