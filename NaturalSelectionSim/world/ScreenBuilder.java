package world;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import life.Critter;

public class ScreenBuilder extends JPanel{
	
	static Integer pool = new Integer(10);
	int width = 0;
	int height = 0;
	
	static JTextArea text;
	
	//stats
	static int s = 1;
	static int h = 1;
	static int l = 1;
	static int c = 1;
	static int e = 1;
	static int f = 0;
	
	static int flag = 0;
	

	
	/** creates the Critter builder screen */
	public ScreenBuilder buildCritterScreen(){

		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLabel total = new JLabel("Total Points: ");
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(total,constraints);
		
		text = new JTextArea(pool.toString());
		constraints.gridx = 1;
		constraints.gridy = 0;
		add(text,constraints);
		
		JLabel stamina_label = new JLabel("Stamina: ");
		constraints.gridx = 0;	
		constraints.gridy = 1;
		add(stamina_label,constraints);
		
		final JSpinner stamina_spin = new JSpinner(new SpinnerNumberModel(1,1,5,1));
		stamina_spin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean increase = true;
				int comp = (Integer)stamina_spin.getValue();
				if(comp < s){
					increase = false;
				}
				s = comp;
				spinAction(stamina_spin, increase);					
			}					
		});
		constraints.gridx = 1;
		constraints.gridy = 1;
		add(stamina_spin,constraints);
		
		JLabel health_label = new JLabel("Health: ");
		constraints.gridx = 0;	
		constraints.gridy = 2;
		add(health_label,constraints);
		
		final JSpinner health_spin = new JSpinner(new SpinnerNumberModel(1,1,5,1));
		health_spin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean increase = true;
				int comp = (Integer)health_spin.getValue();
				if(comp < h){
					increase = false;
				}
				h = comp;
				spinAction(health_spin, increase);					
			}					
		});
		constraints.gridx = 1;
		constraints.gridy = 2;
		add(health_spin,constraints);
		
		JLabel leg_label = new JLabel("Legs: ");
		constraints.gridx = 0;	
		constraints.gridy = 3;
		add(leg_label,constraints);
		
		final JSpinner leg_spin = new JSpinner(new SpinnerNumberModel(1,1,5,1));
		leg_spin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean increase = true;
				int comp = (Integer)leg_spin.getValue();
				if(comp < l){
					increase = false;
				}
				l = comp;
				spinAction(leg_spin, increase);					
			}					
		});
		constraints.gridx = 1;
		constraints.gridy = 3;
		add(leg_spin,constraints);
		
		JLabel claw_label = new JLabel("Claws: ");
		constraints.gridx = 0;	
		constraints.gridy = 4;
		add(claw_label,constraints);
		
		final JSpinner claw_spin = new JSpinner(new SpinnerNumberModel(1,1,5,1));
		
		claw_spin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean increase = true;
				int comp = (Integer)claw_spin.getValue();
				if(comp < c){
					increase = false;
				}
				c = comp;
				spinAction(claw_spin, increase);					
			}					
		});
		constraints.gridx = 1;
		constraints.gridy = 4;
		add(claw_spin,constraints);
		
		JLabel eyes_label = new JLabel("Eyes: ");
		constraints.gridx = 0;	
		constraints.gridy = 5;
		add(eyes_label,constraints);
		
		final JSpinner eyes_spin = new JSpinner(new SpinnerNumberModel(1,1,5,1));
		eyes_spin.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean increase = true;
				int comp = (Integer)eyes_spin.getValue();
				if(comp < e){
					increase = false;
				}
				e = comp;
				spinAction(eyes_spin, increase);					
			}					
		});
		constraints.gridx = 1;
		constraints.gridy = 5;
		add(eyes_spin,constraints);
		setVisible(true);
		
		ButtonGroup group = new ButtonGroup();
		
		final JRadioButton herb = new JRadioButton("Herbavore: ");
		herb.setSelected(true);
		herb.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(herb.isSelected()){
					f = 0;
				}
				
			}		
		});
		constraints.gridx = 0;
		constraints.gridy = 6;
		add(herb,constraints);
		
		final JRadioButton carn = new JRadioButton("Carnavore: ");
		carn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(carn.isSelected()){
					f = 1;
				}
			}		
		});
		constraints.gridx = 1;
		constraints.gridy = 6;
		add(carn,constraints);

		group.add(herb);
		group.add(carn);
		
		return this;	
	}
	
	public void spinAction(JSpinner spin, boolean increase){
		if(flag == 0){ 			//calling setValue() calls this method so this avoids recursion
			int value = (Integer)spin.getValue();
			if(increase == true){
				if(pool > 0){
					pool--;
				}
				else{
					flag = 1;
					spin.setValue(--value);
				}
			}
			else{
				if(value > 0){
					pool++;
				}
				else{
					flag = 1;
					spin.setValue(++value);
				}
			}
			text.setText(pool.toString());
		}
		else{
			flag = 0;
		}
		
	}
	
	public Critter generateCritter(Environment envi){
		Critter crit = new Critter(envi,s,h,l,c,e,f);
		return crit;
	}
}
