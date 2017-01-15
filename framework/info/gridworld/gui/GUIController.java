/* 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2002-2006 College Entrance Examination Board 
 * (http://www.collegeboard.com).
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * @author Julie Zelenski
 * @author Cay Horstmann
 */

package info.gridworld.gui;

import info.gridworld.actor.Actor;
import info.gridworld.grid.*;
import info.gridworld.world.World;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;

/**
 * The GUIController controls the behavior in a WorldFrame. <br />
 * This code is not tested on the AP CS A and AB exams. It contains GUI
 * implementation details that are not intended to be understood by AP CS
 * students.
 */

public class GUIController<T>
{
	public static final int INDEFINITE = 0, FIXED_STEPS = 1, PROMPT_STEPS = 2;

	private static final int MIN_DELAY_MSECS = 10, MAX_DELAY_MSECS = 1000;
	private static final int INITIAL_DELAY = MIN_DELAY_MSECS
			+ (MAX_DELAY_MSECS - MIN_DELAY_MSECS) / 2;

	private Timer timer;
	private JButton stepButton, runButton, stopButton, slowStepButton, solutionButton, directionButton, musicButton, exitButton;
	private int count = 0, musicCount = 0;
	private File file;
	private Clip clip;
	private JComponent controlPanel;
	private GridPanel display;
	private WorldFrame<T> parentFrame;
	private int numStepsToRun, numStepsSoFar;
	private boolean slowStepping = false;
	private int slowNum = 0;
	private ArrayList<Actor> slowActors = new ArrayList<Actor>();
	private JSlider speedSlider;
	private JLabel speed, flavor, deaths;
	private ResourceBundle resources;
	private DisplayMap displayMap;
	private boolean running;
	private Set<Class> occupantClasses;

	/**
	 * Creates a new controller tied to the specified display and gui
	 * frame.
	 * @param parent the frame for the world window
	 * @param disp the panel that displays the grid
	 * @param displayMap the map for occupant displays
	 * @param res the resource bundle for message display
	 */
	public GUIController(WorldFrame<T> parent, GridPanel disp,
			DisplayMap displayMap, ResourceBundle res)
	{
		resources = res;
		display = disp;
		parentFrame = parent;
		this.displayMap = displayMap;
		makeControls();

		occupantClasses = new TreeSet<Class>(new Comparator<Class>()
				{
			public int compare(Class a, Class b)
			{
				return a.getName().compareTo(b.getName());
			}
				});

		World<T> world = parentFrame.getWorld();
		Grid<T> gr = world.getGrid();
		for (Location loc : gr.getOccupiedLocations())
			addOccupant(gr.get(loc));
		for (String name : world.getOccupantClasses())
			try
		{
				occupantClasses.add(Class.forName(name));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		timer = new Timer(INITIAL_DELAY, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if(slowStepping){
					if(slowNum<slowActors.size()){
						Actor a = slowActors.get(slowNum);
						if (a.getGrid() == parentFrame.getWorld().getGrid()){
							setLine("Slow Stepping... Current actor: "+a.getClass().getSimpleName()+" @ ("+a.getLocation().getRow()+","+a.getLocation().getCol()+")");
							a.act();
						}
						parentFrame.repaint();

						slowNum++;
					}else{
						setLine("Done...");
						slowStepping = false;
						timer.stop();
						display.setToolTipsEnabled(false);
						parentFrame.setRunMenuItemsEnabled(true);
						stopButton.setEnabled(false);
						stepButton.setEnabled(true);
						runButton.setEnabled(true);
						solutionButton.setEnabled(true);
						exitButton.setEnabled(true);
						directionButton.setEnabled(true);
						slowStepButton.setEnabled(true);
						speedSlider.setEnabled(true);
						setLine("Ready.");
					}
				}else step();
			}
		});

		display.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				Grid<T> gr = parentFrame.getWorld().getGrid();
				Location loc = display.locationForPoint(evt.getPoint());
				if (loc != null && gr.isValid(loc) && !isRunning())
				{
					display.setCurrentLocation(loc);
					locationClicked();
				}
			}
		});
		stop();
	}

	/**
	 * Advances the world one step.
	 */
	public void step()
	{
		parentFrame.getWorld().step();
		parentFrame.repaint();
		if (++numStepsSoFar == numStepsToRun)
			stop();
		Grid<T> gr = parentFrame.getWorld().getGrid();

		for (Location loc : gr.getOccupiedLocations())
			addOccupant(gr.get(loc));
	}

	public void slowStep(){
		setLine("Slow Stepping...");
		slowStepping = true;
		display.setToolTipsEnabled(false);
		parentFrame.setRunMenuItemsEnabled(false);
		stopButton.setEnabled(true);
		stepButton.setEnabled(false);
		runButton.setEnabled(false);
		slowStepButton.setEnabled(false);
		speedSlider.setEnabled(true);
		running = false;
		slowNum=0;
		slowActors.clear();
		ArrayList<Location> locs = parentFrame.getWorld().getGrid().getOccupiedLocations();
		for(Location l:locs){
			Actor a = (Actor) parentFrame.getWorld().getGrid().get(l);
			slowActors.add(a);
		}
		timer.start();

	}

	public void showSolution()
	{
		parentFrame.getWorld().showSolution();
	}
	public void hideButtonsAndLabels(){
		solutionButton.hide();
		directionButton.hide();
		musicButton.hide();
		flavor.hide();
		deaths.hide();
	}
	public JButton getExitButton(){
		return exitButton;
	}
	public GridPanel getGridPanel(){
		return display;
	}
	public void setFlavor(String s)
	{
		flavor.setText(s);
	}
	public void setDeaths(String s)
	{
		deaths.setText(s);
	}

	public void exit(){
		if (exitButton.getText().equals("Close"))
			parentFrame.dispose();
		else System.exit(0);
	}
	public void playMusic(){
		if (musicCount == 0){
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			musicCount++;
			musicButton.setText("Stop Music");
		}
		else if (musicCount == 1){
			clip.stop();
			musicCount--;
			musicButton.setText("Resume Music!!!");
		}
	}

	public void showDirections(){
		if (count == 0){
			String answer = "Hahahahahaha I bet you expected to see the directions!\n"
					+ "But no! That's too easy. c:";
			JOptionPane.showMessageDialog(null, answer);
			count++;
		}
		else if (count == 1){
			String answer = "You seem to be getting desperate now....\n"
					+ "Aw c'mon, don't look at me like that...";
			JOptionPane.showMessageDialog(null, answer);
			count++;
		}
		else{
			String answer = "Fine fine, here are the rules:\n"
					+ "Use the arrow keys to move through the maze.\n"
					+ "Red tiles are impassable- you cannot walk on them!\n"
					+ "Yellow tiles are electric- they will electrocute you!\n"
					+ "Green tiles are alarm tiles- if you step on them... \n"
					+ "you will have to answer a tough multiple-choice question!\n"
					+ "Orange tiles are orange scented- "
					+ "they will make you smell delicious!\n"
					+ "Blue tiles are water tiles- swim through them if you like,\n"
					+ "but if you smell like oranges! the piranhas will bite you.\n"
					+ "Also, if a blue tile is next to a yellow tile, "
					+ "the water will also zap you!\n"
					+ "Purple tiles are slippery- you will slide to the next tile!\n"
					+ "However, the slippery soap... smells like lemons!\n"
					+ "Which piranhas do not like, so purple and blue are okay!\n"
					+ "Finally, pink tiles. They don't do anything. Step on them all you like. :3";
			JOptionPane.showMessageDialog(null, answer);
		}
	}

	private void setLine(String message){
		String old = parentFrame.getWorld().getMessage();
		String[] split = old.split("\n");
		String out = "";
		//for(int i=0;i<=2;i++){
		//	out+=split[i]+"\n";
		//}
		out+=message;
		parentFrame.getWorld().setMessage(out);
	}

	private void addOccupant(T occupant)
	{
		Class cl = occupant.getClass();
		do
		{
			if ((cl.getModifiers() & Modifier.ABSTRACT) == 0)
				occupantClasses.add(cl);
			cl = cl.getSuperclass();
		}
		while (cl != Object.class);
	}

	/**
	 * Starts a timer to repeatedly carry out steps at the speed currently
	 * indicated by the speed slider up Depending on the run option, it will
	 * either carry out steps for some fixed number or indefinitely
	 * until stopped.
	 */
	public void run()
	{
		setLine("Running...");
		display.setToolTipsEnabled(false); // hide tool tips while running
		parentFrame.setRunMenuItemsEnabled(false);
		stopButton.setEnabled(true);
		stepButton.setEnabled(false);
		slowStepButton.setEnabled(false);
		runButton.setEnabled(false);
		numStepsSoFar = 0;
		timer.start();
		running = true;
	}

	/**
	 * Stops any existing timer currently carrying out steps.
	 */
	public void stop()
	{
		setLine("Stopping...");
		if(slowStepping){
			timer.stop();
			for(int i = slowNum;i<slowActors.size();i++){
				if (slowActors.get(i).getGrid() == parentFrame.getWorld().getGrid())
					slowActors.get(i).act();
			}
			parentFrame.repaint();
			slowNum=0;
			slowStepping = false;
		}


		display.setToolTipsEnabled(false);
		parentFrame.setRunMenuItemsEnabled(true);
		timer.stop();
		stopButton.setEnabled(false);
		runButton.setEnabled(true);
		stepButton.setEnabled(true);
		running = false;
		slowStepButton.setEnabled(true);
		speedSlider.setEnabled(true);
		setLine("Ready.");
	}

	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Builds the panel with the various controls (buttons and
	 * slider).
	 */
	private void makeControls()
	{
		controlPanel = new JPanel();
		stepButton = new JButton(resources.getString("button.gui.step"));
		runButton = new JButton(resources.getString("button.gui.run"));
		stopButton = new JButton(resources.getString("button.gui.stop"));
		slowStepButton = new JButton("Slow Step");

		solutionButton = new JButton("Show Solution");
		exitButton = new JButton("Exit");
		directionButton = new JButton("Directions");
		musicButton = new JButton("Play music!!!");
		file = new File("TemShop.wav"); 
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file)); 
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		} 

		flavor = new JLabel("Flavor: orange");
		deaths = new JLabel("Deaths: 0");

		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.setBorder(BorderFactory.createEtchedBorder());

		Dimension spacer = new Dimension(5, stepButton.getPreferredSize().height + 15);

		controlPanel.add(Box.createRigidArea(spacer));

		//controlPanel.add(stepButton);
		//controlPanel.add(Box.createRigidArea(spacer));
		//controlPanel.add(slowStepButton);
		//controlPanel.add(Box.createRigidArea(spacer));
		//controlPanel.add(runButton);
		//controlPanel.add(Box.createRigidArea(spacer));
		//controlPanel.add(stopButton);
		//controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(solutionButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(directionButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(musicButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(exitButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(flavor);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(deaths);

		runButton.setEnabled(false);
		stepButton.setEnabled(false);
		stopButton.setEnabled(false);
		solutionButton.setEnabled(true);
		directionButton.setEnabled(true);
		musicButton.setEnabled(true);
		exitButton.setEnabled(true);
		slowStepButton.setEnabled(true);

		//controlPanel.add(Box.createRigidArea(spacer));
		//controlPanel.add(new JLabel(resources.getString("slider.gui.slow")));
		speedSlider = new JSlider(MIN_DELAY_MSECS, MAX_DELAY_MSECS,
				INITIAL_DELAY);
		speedSlider.setInverted(true);
		speedSlider.setPreferredSize(new Dimension(100, speedSlider
				.getPreferredSize().height));
		speedSlider.setMaximumSize(speedSlider.getPreferredSize());

		// remove control PAGE_UP, PAGE_DOWN from slider--they should be used
		// for zoom
		InputMap map = speedSlider.getInputMap();
		while (map != null)
		{
			map.remove(KeyStroke.getKeyStroke("control PAGE_UP"));
			map.remove(KeyStroke.getKeyStroke("control PAGE_DOWN"));
			map = map.getParent();
		}

		//        controlPanel.add(speedSlider);
		//        controlPanel.add(new JLabel(resources.getString("slider.gui.fast")));
		//        controlPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		speed = new JLabel(speedSlider.getValue()+"");
		//        controlPanel.add(speed);

		stepButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setLine("Stepping...");
				step();
				setLine("Ready.");
			}
		});
		runButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				run();
			}
		});
		stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stop();
			}
		});
		solutionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showSolution();
			}
		});
		directionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showDirections();
			}
		});
		musicButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				playMusic();
			}
		});
		exitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				exit();
			}
		});
		slowStepButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				slowStep();
			}
		});
		speedSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				timer.setDelay(((JSlider) evt.getSource()).getValue());
				speed.setText(((JSlider) evt.getSource()).getValue()+"");
			}
		});
	}

	/**
	 * Returns the panel containing the controls.
	 * @return the control panel
	 */
	public JComponent controlPanel()
	{
		return controlPanel;
	}

	/**
	 * Callback on mousePressed when editing a grid.
	 */
	private void locationClicked()
	{
		World<T> world = parentFrame.getWorld();
		Location loc = display.getCurrentLocation();
		if (loc != null && !world.locationClicked(loc))
			editLocation();
		parentFrame.repaint();
	}

	/**
	 * Edits the contents of the current location, by displaying the constructor
	 * or method menu.
	 */
	public void editLocation()
	{
		World<T> world = parentFrame.getWorld();

		Location loc = display.getCurrentLocation();
		if (loc != null)
		{
			T occupant = world.getGrid().get(loc);
			if (occupant == null)
			{
				MenuMaker<T> maker = new MenuMaker<T>(parentFrame, resources,
						displayMap);
				JPopupMenu popup = maker.makeConstructorMenu(occupantClasses,
						loc);
				Point p = display.pointForLocation(loc);
				popup.show(display, p.x, p.y);
			}
			else
			{
				MenuMaker<T> maker = new MenuMaker<T>(parentFrame, resources,
						displayMap);
				JPopupMenu popup = maker.makeMethodMenu(occupant, loc);
				Point p = display.pointForLocation(loc);
				popup.show(display, p.x, p.y);
			}
		}
		parentFrame.repaint();
	}

	/**
	 * Edits the contents of the current location, by displaying the constructor
	 * or method menu.
	 */
	public void deleteLocation()
	{
		World<T> world = parentFrame.getWorld();
		Location loc = display.getCurrentLocation();
		if (loc != null)
		{
			world.remove(loc);
			parentFrame.repaint();
		}
	}
}
