import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import boid.FlockingBoid;
import boid.FoodBoid;
import boid.PredatorBoid;
import boid.StaticPredatorBoid;
import boid.Boid;
import drawing.Canvas;
import tools.Utils;

/**
 * @author Y3848937
 */
public class FlockingMain {
    // **************************************************
    // Fields
    // **************************************************
	//Polymorphic array of boids
	private List<Boid> boids = new ArrayList<Boid>();
	private final int WINDOW_X_SIZE = Utils.SCREEN_X_SIZE;
	private final int WINDOW_Y_SIZE = Utils.SCREEN_Y_SIZE;
	

	
	//Enables static predators or food to be drawn on mouse click
	private boolean staticPredatorEnable = false;
	private boolean foodEnable = false;

	//Initial control values
	static double initialAlignment = .5f;
	static double initialCohesion = .8;
	static double initialSeparation = 0.3;
	static int initialRadius = 500;
	static int initialSpeed = 400;
	

	static float sliderResolution = 1000; //Needed to scale the slider values
	static int sliderSensitivity = 1;
	static int sliderMax = (int) (sliderSensitivity * sliderResolution);
	static int sliderMin = 0;

	public FlockingMain() {
		//boids list is synchronised to avoid multiple threads interacting with it - Contains all boids active in the simulation
		boids = (List<Boid>) Collections.synchronizedList(boids);
		
		// **************************************************
		// Frames, Canvas, and JPanel Initialisation
		// **************************************************
		//JFrame needed to support the GUI - Contains the simulation and control panels
		JFrame frame = new JFrame();
		//Simulation is run on the canvas
		Canvas canvas = new Canvas();
		
		//**********Frame is initialised and canvas added***********
		frame.setTitle("Boids");
		frame.setSize(WINDOW_X_SIZE, WINDOW_Y_SIZE);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.add(canvas);

		//**********Canvas initialised***********
		//Background added - simulation will run on top of it
		ImageIcon backgroundImage = new ImageIcon("pond.png");
		JLabel backgroundJLabel = new JLabel(backgroundImage);
		canvas.add(backgroundJLabel);
		canvas.revalidate();

		//**********Control Panels added to the frame***********
		//Control panels implemented on top of the simulation canvas
		JPanel upperControlPanel = new JPanel(new FlowLayout());
		frame.add(upperControlPanel, BorderLayout.NORTH);

		JPanel lowerControlPanel = new JPanel(new FlowLayout());
		frame.add(lowerControlPanel, BorderLayout.SOUTH);

		
		// **************************************************
		// Upper Control Panel Contents
		// **************************************************
		
		//Location on the control panel is dependent on the order of the following instantiations:
		//Layout order: [		1. 2. 3. 4. 5.	 	] (Aligned Centrally)
		
		//1.
		//**********SAVE/LOAD ComboBox***********
		//Contains the options to save or load the flocking controls
		String[] saveLoad = new String[] { "Save Controls", "Load Controls" };
		JComboBox<String> fileOptions = new JComboBox<>(saveLoad);
		upperControlPanel.add(fileOptions);

		//2.
		//**********Add Button***********
		//Used to add a single flocking boid to the simulation
		JButton addButton = new JButton("Add Flocking Boid");
		addButton.setBounds(300, 200, 100, 30);
		upperControlPanel.add(addButton);

		//3.
		//**********Add ComboBox***********
		//Contains the options to add different types of boids, or multiple flocking boids to the simulation
		String[] thingsToAdd = new String[] { "Add Predator", "Add Food", "Add Static Predator",
											  "Add 10X Flocking Boids" };
		JComboBox<String> addOptions = new JComboBox<>(thingsToAdd);
		upperControlPanel.add(addOptions);
		
		//4.
		//**********Clear Button***********
		//Clears the screen
		JButton clearButton = new JButton("Clear Screen");
		clearButton.setBounds(300, 200, 100, 30);
		upperControlPanel.add(clearButton);

		//5.
		//**********Speed Slider***********
		//Adjusts the speed of flocking boid movements in the simulation
		JSlider speedSlider = new JSlider(sliderMin, sliderMax, initialSpeed);
		upperControlPanel.add(speedSlider);
		//Label stored in a Hashtable 
		Hashtable<Integer, JLabel> labelTableSpeed = new Hashtable<Integer, JLabel>();
		//To avoid crashing, slider is instantiated with a range of 0-1000. Therefore to limit the value to between 0 and 250,
		//speeds are divided by 4.
		JLabel speedLabel = new JLabel("Speed " + initialSpeed/4);
		labelTableSpeed.put(0, speedLabel);
		speedSlider.setValue(initialSpeed);
		speedSlider.setLabelTable(labelTableSpeed);
		speedSlider.setPaintLabels(true);

		// **************************************************
		// Lower Control Panel Contents
		// **************************************************
		
		//Location on the control panel is dependent on the order of the following instantiations:
		//Layout order: [		1. 2. 3. 4.	 	] (Aligned Centrally)
		
		//1.
		//**********Radius Slider***********
		//Determines the radius used by boids to search for those nearby
		JSlider radiusSlider = new JSlider(sliderMin, sliderMax, initialRadius);
		lowerControlPanel.add(radiusSlider);
		Hashtable<Integer, JLabel> labelTableRadius = new Hashtable<Integer, JLabel>();
		JLabel radiusLabel = new JLabel("Radius " + initialRadius/4);
		labelTableRadius.put(0, radiusLabel);
		radiusSlider.setLabelTable(labelTableRadius);
		radiusSlider.setPaintLabels(true);

		//2.
		//**********Cohesion Slider***********
		//Determines the magnitude of the cohesion
		JSlider cohesionSlider = new JSlider(sliderMin, sliderMax, (int) (initialCohesion * sliderResolution));
		lowerControlPanel.add(cohesionSlider);
		Hashtable<Integer, JLabel> cohesionLabelTable = new Hashtable<Integer, JLabel>();
		JLabel cohesionLabel = new JLabel("Cohesion " + initialCohesion);
		cohesionLabelTable.put(new Integer(250), cohesionLabel);
		cohesionSlider.setLabelTable(cohesionLabelTable);
		cohesionSlider.setPaintLabels(true);

		//3.
		//**********Separation Slider***********
		//Determines the magnitude of the separation
		JSlider separationSlider = new JSlider(sliderMin, sliderMax, (int) (initialSeparation * sliderResolution));
		lowerControlPanel.add(separationSlider);
		Hashtable<Integer, JLabel> separationLabelTable = new Hashtable<Integer, JLabel>();
		JLabel separationLabel = new JLabel("Separation " + initialSeparation);
		separationLabelTable.put(0, separationLabel);
		separationSlider.setLabelTable(separationLabelTable);
		separationSlider.setPaintLabels(true);

		//4.
		//**********Alignment Slider***********
		//Determines the magnitude of the alignment
		JSlider alignmentSlider = new JSlider(sliderMin, sliderMax, (int) (initialAlignment * sliderResolution));
		lowerControlPanel.add(alignmentSlider);
		Hashtable<Integer, JLabel> labelTableAlignment = new Hashtable<Integer, JLabel>();
		JLabel alignmentLabel = new JLabel("Alignment " + initialAlignment);
		labelTableAlignment.put(0, alignmentLabel);
		alignmentSlider.setLabelTable(labelTableAlignment);
		alignmentSlider.setPaintLabels(true);

		// **************************************************
		// Upper Control Panel Listeners
		// **************************************************
		
		
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				for (Boid s : boids) {
					if (s.getClass() == FlockingBoid.class) {
						s.setSpeed(speedSlider.getValue()/4);
						//String is shortened to avoid bug where some of the value isn't displayed
						speedLabel.setText("Sp: " + speedSlider.getValue()/4);
						//Speed is limited between 0 and 250 by dividing the sliders resolution by 4.
					}
				}
			}
		});

	
		addButton.addActionListener(new ActionListener() {
			@Override
			//Anonymous class used as action listener for multiple buttons
			public void actionPerformed(ActionEvent e) {
				//Instantiates a new boid at the centre of the screen with the slider values applied to it
				Boid t = new FlockingBoid(canvas, 400, 400);
				boids.add(t);
				t.setRadius(radiusSlider.getValue()/4);
				t.setSpeed(speedSlider.getValue()/4);
				t.setAlignmentControl(alignmentSlider.getValue());
				t.setSeparationControl(separationSlider.getValue());
				t.setcohesionControl(cohesionSlider.getValue());
			}
		});

		
		fileOptions.addActionListener(new ActionListener() {
			//**********FILE FORMAT**********
			//LINE 1: COHESION
			//LINE 2: SEPARATION
			//LINE 3: ALIGNMENT

			@Override
			public void actionPerformed(ActionEvent event) {
				@SuppressWarnings("unchecked") //Not sure about this warning due to declaration in following line. 
				JComboBox<String> combo = (JComboBox<String>) event.getSource();
				String option = (String) combo.getSelectedItem();
				String fileName = "savedSettings.txt";
				//**********SAVING METHOD**********
				if (option.equals("Save Controls")) {
					if (boids.size() > 0) {
						
						double currentCohesion = cohesionSlider.getValue();
						double currentSeparation = separationSlider.getValue();
						double currentAlignment = alignmentSlider.getValue();
						
						//Writer is pointed to the start of the file
						PrintWriter writer = null;
						
						try {
							writer = new PrintWriter(fileName, "UTF-8");
						} catch (FileNotFoundException | UnsupportedEncodingException e1) {
							e1.printStackTrace();
							//if FNFE exception occurs, soft exit occurs
							System.out.println("File not found: \"" + fileName + "\". Terminating!");
							 System.exit(0);
						}//end try/catch
						
						//current values taken from the sliders are written line by line
						writer.println(currentCohesion);
						writer.println(currentSeparation);
						writer.println(currentAlignment);
						writer.close();
						
						displaySavedMessage(currentCohesion, currentSeparation, currentAlignment, frame);
					}//end if

					
					//**********LOADING METHOD**********
				} else if (option.equals("Load Controls")) {
					BufferedReader reader;
					String str;
					List<String> Values = new ArrayList<String>(); //File contents to be loaded into Values array
					
					try {
						reader = new BufferedReader(new FileReader(fileName));
						while ((str = reader.readLine()) != null) {
							Values.add(str);
						}
					} catch (IOException e) {
						e.printStackTrace();
						//if FNFE exception occurs, soft exit occurs
						System.out.println("File not found: \"" + fileName + "\". Terminating!");
						 System.exit(0);
					}//end try/catch
					
					for (Boid s : boids) {
						//Values are rescaled by multiplying by slider resolution
						s.setcohesionControl(Double.valueOf(Values.get(0)) / sliderResolution);
						s.setSeparationControl(Double.valueOf(Values.get(1)) / sliderResolution);
						s.setAlignmentControl(Double.valueOf(Values.get(2)) / sliderResolution);
					}//end for
					
					displayLoadedMessage(Values,frame);
				}//end if
			}
		});

		addOptions.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				@SuppressWarnings("unchecked")
				JComboBox<String> combo = (JComboBox<String>) event.getSource();
				String option = (String) combo.getSelectedItem();

				switch (option) {
				case "Add Predator":
					boids.add(new PredatorBoid(canvas, 300, 400));
					foodEnable = false;
					staticPredatorEnable = false;
					break;
				case "Add Food":
					foodEnable = true;
					// Note - for food and static predators, drawing is only enabled if the relevant
					// dropdown option is the most recently selected
					staticPredatorEnable = false;
					break;
				case "Add Static Predator":
					staticPredatorEnable = true;
					foodEnable = false;
					break;
				case "Add 10X Flocking Boids":
					for (int i = 0; i < 9; i++) {
						Boid t = new FlockingBoid(canvas, 400, 400);
						boids.add(t);
						t.setRadius(radiusSlider.getValue() / 2);
						t.setSpeed(speedSlider.getValue() / 4);
						t.setAlignmentControl(alignmentSlider.getValue());
						t.setSeparationControl(separationSlider.getValue());
						t.setcohesionControl(cohesionSlider.getValue());
					}
					foodEnable = false;
					staticPredatorEnable = false;
					break;
				}

			}
		});

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Boid s : boids) {
					s.undraw();
				}
				boids.clear();
			}

		});

		// **************************************************
		// Lower Control Panel Listeners
		// **************************************************

		//For Cohesion, separation and alignment:
		cohesionSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double val = (double) (cohesionSlider.getValue()) / sliderResolution;
				//Slider resolution used to scale the value to be between 0 and 1
				initialCohesion = val;
				for (Boid s : boids) {
					s.setcohesionControl(val);
					//String truncates to fix bug causing some of the value to not be displayed
					cohesionLabel.setText("C " + String.format("%.2f", val));
				}
			}
		});

		alignmentSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double val = (double) (alignmentSlider.getValue()) / sliderResolution;
				initialAlignment = val;
				for (Boid s : boids) {
					s.setAlignmentControl(val);
					alignmentLabel.setText("A " + String.format("%.2f", val));
				}
			}
		});

		separationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double val = (double) (separationSlider.getValue()) / sliderResolution;
				initialSeparation = val;
				for (Boid s : boids) {
					s.setSeparationControl(val);
					separationLabel.setText("S " + String.format("%.2f", val));
				}
			}
		});

		radiusSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				for (Boid s : boids) {
					if (s.getClass() != PredatorBoid.class) {
						//Division by 4 limits the radius between 0 and 250.
						//Values larger than 250 with separation cause bug in which boids congregate around the top of the canvas
						s.setRadius(radiusSlider.getValue() / 4);
						radiusLabel.setText("R " + radiusSlider.getValue() / 4);
					}
				}
			}
		});

		// **************************************************
		// Canvas Listeners
		// **************************************************
		canvas.addMouseListener(new MouseAdapter() {
			//Draws the relevant boid at the mouse's click location
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				//what gets drawn is dependent on the 2 boolean values state
				if (staticPredatorEnable == true) {
					boids.add(new StaticPredatorBoid(canvas, x, y));
				}
				else if (foodEnable == true) {
					boids.add(new FoodBoid(canvas, x, y));
				}//end if
			}
		});
		
		//******************************
		
		//To determine the time between frames
		int deltaTime = 20;
		gameLoop(deltaTime);
	}

    /**
    * Used to control the methods needed to update the simulation frame by frame
    *
    * @param deltaTime The time used to pause in between frame (milliseconds)
    */
	private void gameLoop(int deltaTime) {
		boolean continueRunning = true;
		
		while (continueRunning) {
			Utils.pause(deltaTime);
			// Synchronisation used to prevent methods interleaving on boids
			synchronized (boids) {
	
				for (Boid s : boids) {
					s.undraw();
				}
				
				for (Boid s : boids) {
					s.flee(boids);
				}
	
				for (Boid s : boids) {
					s.flock(boids);
				}
				for (Boid s : boids) {
					s.hunt(boids);
				}
				for (Boid s : boids) {
					s.update(deltaTime);
				}
				for (int i = 0; i < boids.size(); i++) {
					if (boids.get(i).isEaten() == true) {
						boids.remove(i);
					}
				}
				for (Boid s : boids) {
					s.draw();
				}
			}
		}
	}
	
    /**
    * Displays the dialog box to confirm contents have been successfully saved
    *
    * @param currentCohesion The cohesion value being saved
    * @param currentSeparation The separation value bing saved
    * @param currentAlignment The alignment value being saved
    * @param frame The frame in which the dialog box is being displayed
    */
	private void displaySavedMessage(double currentCohesion, double currentSeparation, double currentAlignment,
			JFrame frame) {
		String displayMessage = String.format("Cohesion: %.2f, Separation: %.2f, Alignment: %.2f successfully Saved!",
				currentCohesion / 1000, currentSeparation / 1000, currentAlignment / 1000); // Division by 1000 to
																							// rescale retrieved values
		JOptionPane.showMessageDialog(frame, displayMessage);

	}
	
    /**
    * Displays the dialog box to confirm contents have been loaded saved
    *
    * @param Values List containing the values being loaded
    * @param frame The frame in which the dialog box is being displayed
    */
	private void displayLoadedMessage(List<String> Values, JFrame frame) {
		String displayMessage = String.format("Cohesion: %.2f, Separation: %.2f, Alignment: %.2f successfully loaded!",
				Double.valueOf(Values.get(0)) / 1000, Double.valueOf(Values.get(1)) / 1000,
				Double.valueOf(Values.get(2)) / 1000);
		JOptionPane.showMessageDialog(frame, displayMessage);
	}

	public static void main(String[] args) {
		new FlockingMain();
	}
}