package assignment5;

import java.util.ArrayList;
import java.util.HashMap;

/* CRITTERS Main.java
 * EE422C Project 5 submission by
 * Timberlon Gray
 * tg22698
 * 16235
 * Raiyan Chowdhury
 * rac4444
 * 16235
 * Slip days used: <0>
 * Spring 2017
 */

import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.control.*;

import java.io.File;
import java.lang.reflect.Method;

public class Main extends Application{
	private int maxSimWidth = 900;
	private int maxSimHeight = 700;
	private double simSpeed = 50;
	private boolean isRunning = false;
	private static String myPackage;
	static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * This is what ultimately launches the Critter GUI.
	 * A GridPane for the controller component and a Group for displaying the world
	 * are created here. 
	 * The controller component consists of the options to set a number of timesteps 
	 * to execute, set the seed of the random number generator used by Critter, create
	 * any number of critters desired at will, have time steps occur automatically via 
	 * animation at varying speeds, and have stats displayed for any critter the user wants.
	 * Every time the world is changed in some way -- if critters are added or if a time step
	 * occurs -- Critter.displayWorld() is called and the world view is updated.
	 * @param primaryStage
	 */
	@Override
	public void start(Stage primaryStage) {
		//code for getting class names
		String path = System.getProperty("user.dir");
		String files[] = null;
		try {
			String bin = path + File.separator + "bin" + File.separator + myPackage;
			File f = new File(bin);
			files = f.list();
			for (int i = 0; i < files.length; i++) {
				files[i] = files[i].substring(0, files[i].length()-6);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//get list of Critter subclasses
		List<String> classes = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			Class<?> my_critter = null;
			try {
				my_critter = Class.forName(myPackage + "." + files[i]);
				if (Critter.class.isAssignableFrom(my_critter)
						&& !files[i].equals("Critter$TestCritter") 
						&& !files[i].equals("Critter")) {
					classes.add(files[i]);
				}
			}
			catch (ClassNotFoundException | NoClassDefFoundError e) {
				my_critter = null;
			}
		}
		
		// GridPane for all the buttons and text boxes
		GridPane gridPane = new GridPane();
		
		//Group for world
		int wWidth=Params.world_width;
		int wHeight=Params.world_height;
		int size1 = maxSimWidth/wWidth;
		if (size1 > maxSimHeight/wHeight) size1 = maxSimHeight/wHeight;
		int size = size1;
		Group pane = new Group();
    	Stage stage = new Stage();
		stage.setTitle("World");
		Scene scene = new Scene(pane,wWidth*size, wHeight*size);
		stage.setScene(scene);
		stage.show();
		
		// box for displaying error messages
		Label errorMsg = new Label();
		errorMsg.setPrefWidth(350);
		errorMsg.setTextFill(Color.RED);
		gridPane.add(errorMsg, 0, 10);
		
		// input for critter type, button to display stats, box for displaying the stats
		//text for displaying stats
		Text stats = new Text();
		stats.setWrappingWidth(360);
		stats.setFont(Font.font("Verdana", 12));
		gridPane.add(stats, 0, 5);
	
		MenuButton critterInput = new MenuButton();
		critterInput.setPrefWidth(350);
		critterInput.setText("Choose critter stats to display");
		List<CheckMenuItem> class_items = new ArrayList<CheckMenuItem>();
		HashMap<String, Boolean> display_or_not = new HashMap<String, Boolean>();
		for (String s : classes) {
			class_items.add(new CheckMenuItem(s));
			display_or_not.put(s, false);
		}
		for (CheckMenuItem m : class_items) {
			m.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					if (m.isSelected()) {
						display_or_not.put(m.getText(), true);
					} else {
						display_or_not.put(m.getText(), false);
					}
					try {
						String critter_stats = "";
						for (String critter_class_name : display_or_not.keySet()) {
							if (display_or_not.get(critter_class_name) == true) {
								List<Critter> list_of_instances = Critter.getInstances(critter_class_name);
								String critter_class = myPackage + "." + critter_class_name;
								Class<?> type = Class.forName(critter_class);
								Method method = type.getMethod("runStats", List.class);
								critter_stats = critter_stats + critter_class_name + " stats:\n";
								critter_stats = critter_stats + (String) method.invoke(null, list_of_instances) + "\n\n";
							}
						}
						if (critter_stats.equals("")) {
							stats.setText("");
							errorMsg.setText("Please choose at least one critter type.");
						}
						else {
							stats.setText(critter_stats);
							errorMsg.setText("");
						}
				    } catch (Exception f){
				    	errorMsg.setText("You did not enter a known critter type for stats!");
				    	stats.setText("");
				    }
				}
			});
		}
		critterInput.getItems().addAll(class_items);
		gridPane.add(critterInput, 0, 4);
		
		// Time Step button and text box functionality
		TextField stepInput = new TextField();
		stepInput.setPromptText("Enter the desired amount of time steps");
		stepInput.setPrefWidth(350);
		gridPane.add(stepInput, 0, 0);
		
		Button stepButton = new Button();
		stepButton.setText("Perform Time Steps");
		gridPane.add(stepButton, 2, 0);
		stepButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	int steps = 1;
		    	try {
		    		steps = Integer.parseInt(stepInput.getText());
		    		errorMsg.setText("");
	    		} catch (Exception f){
	    			errorMsg.setText("You did not enter an integer for time steps! Default = 1");
	    		}
		    	while(steps > 0) {
		    		Critter.worldTimeStep();
		    		try {
						String critter_stats = "";
						for (String critter_class_name : display_or_not.keySet()) {
							if (display_or_not.get(critter_class_name) == true) {
								List<Critter> list_of_instances = Critter.getInstances(critter_class_name);
								String critter_class = myPackage + "." + critter_class_name;
								Class<?> type = Class.forName(critter_class);
								Method method = type.getMethod("runStats", List.class);
								critter_stats = critter_stats + critter_class_name + " stats:\n";
								critter_stats = critter_stats + (String) method.invoke(null, list_of_instances) + "\n\n";
							}
						}
						stats.setText(critter_stats);
					 	//errorMsg.setText("");
				    } catch (Exception f){
				    	//errorMsg.setText("You did not enter a known critter type for stats!");
				    	stats.setText("");
				    }
		    		steps--;
		    	}
		    	Critter.displayWorld(pane);
		    }
		});
		
		// Seed input and button to set
		TextField seedInput = new TextField();
		seedInput.setPromptText("Enter a seed if desired");
		seedInput.setPrefWidth(350);
		gridPane.add(seedInput, 0, 1);
		
		Button seedSet = new Button();
		seedSet.setText("Set Seed");
		gridPane.add(seedSet, 2, 1);
		seedSet.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	try {
		    		Critter.setSeed(Integer.parseInt(seedInput.getText()));
		    		errorMsg.setText("");
	    		} catch (Exception f){
	    			errorMsg.setText("You did not enter an integer for the seed!");
	    		}
		    }
		});
		
		// Code to create critters. Includes text box for critter type, amount desired, and button to activate.
				
				final ComboBox<String> critterInput2 = new ComboBox<String>();
 				critterInput2.getItems().addAll(classes);
 				critterInput2.setPrefWidth(350);
 				gridPane.add(critterInput2, 0, 2);
				
				TextField amtToSpawn = new TextField();
				amtToSpawn.setPromptText("How many?");
				amtToSpawn.setPrefWidth(80);
				gridPane.add(amtToSpawn, 1, 2);
				
				Button createCritters = new Button();
				createCritters.setText("Create Critters");
				gridPane.add(createCritters, 2, 2);
				createCritters.setOnAction(new EventHandler<ActionEvent>() {
				    @Override public void handle(ActionEvent e) {
				    	int intToSpawn = 0;
				    	try {
				    		intToSpawn = Integer.parseInt(amtToSpawn.getText());
				    		errorMsg.setText("");
			    		} catch (Exception f){
			    			if (critterInput2.getValue() == null) 
			    				errorMsg.setText("Please specify a critter type and a valid number of critters.");
			    			else if (amtToSpawn.getText().equals(""))
			    				errorMsg.setText("Please specify a valid number of critters.");
			    			else
			    				errorMsg.setText("Number of critters specified is not an integer!");
			    		}
				    	try {
				    		while(intToSpawn > 0) {
			    				Critter.makeCritter(critterInput2.getValue());
			    				intToSpawn--;
			    			}
				    	} catch (Exception g) {
				    		errorMsg.setText("Please specify a critter type.");
				    	}
				    	Critter.displayWorld(pane);
				    }
				});
				
				// Button to quit the program
				Button quit = new Button();
				quit.setText("End Simulation");
				gridPane.add(quit, 0, 11);
				quit.setOnAction(new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent e) {
						System.exit(0);
					}
				});
				
		// Button and function for the animation
		AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	for(int i = 0; i <= (1000000000-(simSpeed)*10000000); i++) {}
            	Critter.worldTimeStep();
            	try {
					String critter_stats = "";
					for (String critter_class_name : display_or_not.keySet()) {
						if (display_or_not.get(critter_class_name) == true) {
							List<Critter> list_of_instances = Critter.getInstances(critter_class_name);
							String critter_class = myPackage + "." + critter_class_name;
							Class<?> type = Class.forName(critter_class);
							Method method = type.getMethod("runStats", List.class);
							critter_stats = critter_stats + critter_class_name + " stats:\n";
							critter_stats = critter_stats + (String) method.invoke(null, list_of_instances) + "\n\n";
						}
					}
					stats.setText(critter_stats);
				 	errorMsg.setText("");
			    } catch (Exception f){
			    	//errorMsg.setText("You did not enter a known critter type for stats!");
			    	stats.setText("");
			    }
            	Critter.displayWorld(pane);
            }
        };
        Slider animationSpeed = new Slider();
        animationSpeed.setMin(0);
        animationSpeed.setMax(100);
        animationSpeed.setValue(50);
        animationSpeed.setShowTickMarks(true);
        animationSpeed.setBlockIncrement(5);
		gridPane.add(animationSpeed, 0, 3);
		
		Button animationToggle = new Button();
		animationToggle.setText("Toggle Animation");
		gridPane.add(animationToggle, 2, 3);
		animationToggle.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if(isRunning) {
		    		isRunning = false;
		    		animationSpeed.setDisable(false);
		    		createCritters.setDisable(false);
		    		amtToSpawn.setDisable(false);
		    		critterInput2.setDisable(false);
		    		critterInput.setDisable(false);
		    		stats.setDisable(false);
		    		seedSet.setDisable(false);
		    		seedInput.setDisable(false);
		    		stepInput.setDisable(false);
		    		stepButton.setDisable(false);
		    		quit.setDisable(false);
		    		timer.stop();
		    	} else {
		    		simSpeed = animationSpeed.getValue();
		    		animationSpeed.setDisable(true);
		    		createCritters.setDisable(true);
		    		amtToSpawn.setDisable(true);
		    		critterInput2.setDisable(true);
		    		critterInput.setDisable(true);
		    		stats.setDisable(true);
		    		seedSet.setDisable(true);
		    		seedInput.setDisable(true);
		    		stepInput.setDisable(true);
		    		stepButton.setDisable(true);
		    		quit.setDisable(true);
		    		isRunning = true;
		    		timer.start();
		    	}
		    }
		});
        
		primaryStage.setScene(new Scene(gridPane, 560, 700));
		primaryStage.show();
		Critter.displayWorld(pane);
	}
}



