package assignment5;

import java.awt.Point;

/* CRITTERS Critter.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class Critter {
	/* NEW FOR PROJECT 5 */
	public enum CritterShape {
		CIRCLE,
		SQUARE,
		TRIANGLE,
		DIAMOND,
		STAR
	}
	/* the default color is white, which I hope makes critters invisible by default
	 * If you change the background color of your View component, then update the default
	 * color to be the same as you background 
	 * 
	 * critters must override at least one of the following three methods, it is not 
	 * proper for critters to remain invisible in the view
	 * 
	 * If a critter only overrides the outline color, then it will look like a non-filled 
	 * shape, at least, that's the intent. You can edit these default methods however you 
	 * need to, but please preserve that intent as you implement them. 
	 */
	public javafx.scene.paint.Color viewColor() { 
		return javafx.scene.paint.Color.WHITE; 
	}
	
	public javafx.scene.paint.Color viewOutlineColor() { return viewColor(); }
	public javafx.scene.paint.Color viewFillColor() { return viewColor(); }
	
	public abstract CritterShape viewShape(); 
	
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	/**
	 * Lets a critter see if and what critter exists at a location near it.
	 * @param direction - what direction the critter is looking in; integer from 0 to 7.
	 * @param steps - false = 1 square away, true = 2 squares away
	 * @return - the toString() identifier of the critter at the location, or null if no critter is there.
	 */
	protected final String look(int direction, boolean steps) {
		energy -= Params.look_energy_cost;
		int w = Params.world_width;
		int h = Params.world_height;
		int x = 0,y = 0,steps_num;
		if (!steps) steps_num = 1;
		else steps_num = 2;
		
		switch(direction) {
			case 0://right
				x = (((x_coord + steps_num) % w) + w) % w;
				y = y_coord;
				break;
			case 1://up-right
				x = (((x_coord + steps_num) % w) + w) % w; 
				y = (((y_coord - steps_num) % h) + h) % h; 
				break;
			case 2://up
				x = x_coord;
				y = (((y_coord - steps_num) % h) + h) % h; 
				break;
			case 3://up-left
				x = (((x_coord - steps_num) % w) + w) % w; 
				y = (((y_coord - steps_num) % h) + h) % h;
				break;
			case 4://left
				x = (((x_coord - steps_num) % w) + w) % w;
				y = y_coord;
				break;
			case 5://down-left
				x = (((x_coord - steps_num) % w) + w) % w;
				y = (((y_coord + steps_num) % h) + h) % h;
				break;
			case 6://down
				x = x_coord;
				y = (((y_coord + steps_num) % h) + h) % h;
				break;
			case 7://down-right
				x = (((x_coord + steps_num) % w) + w) % w;
				y = (((y_coord + steps_num) % h) + h) % h;
				break;
		}
		
		if (getEncounterStatus()) {
			for (Critter c : population) {
				if (x == c.x_coord && y == c.y_coord) {
					return c.toString();
				}
			}
			return null;
		} else {
			for (Critter c : population) {
				if (x == c.old_x && y == c.old_y) {
					return c.toString();
				}
			}
			return null;
		}
	}
	
	/* rest is unchanged from Project 4 */
	
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	private int old_x;
	private int old_y;

	private boolean already_moved;
	private static boolean pre_encounter_movements_done;
	
	private static void setEncounterStatus(boolean b) {
		pre_encounter_movements_done = b;
	}
	
	private static boolean getEncounterStatus() {
		return pre_encounter_movements_done;
	}
	
	
	/**
	 * checks if the critter already moved, then calls move() below for 1 step. 
	 * @param direction
	 */
	protected final void walk(int direction) {
		if (!already_moved) {
			already_moved = true;
			move(direction, 1);
		}
		energy -= Params.walk_energy_cost;
	}
	/**
	 * checks if the critter already moved, then calls move() below for 2 steps. 
	 * @param direction
	 */
	protected final void run(int direction) {
		if (!already_moved) {
			already_moved = true;
			move(direction, 2);
		}
		energy -= Params.run_energy_cost;
	}
	
	/**
	 * Moves a critter according to the direction and steps values input.
	 * @param direction - direction that the critter will move in.
	 * @param steps - how many spaces the critter will move in that direction.
	 */
	private final void move(int direction, int steps) {
		int w = Params.world_width;
		int h = Params.world_height;
		int x=0, y=0;
		switch(direction) {
		//(a % b + b) % b handles negative numbers
			case 0://right
				x = (((x_coord + steps) % w) + w) % w;
				y = y_coord;
				break;
			case 1://up-right
				x = (((x_coord + steps) % w) + w) % w; 
				y = (((y_coord - steps) % h) + h) % h; 
				break;
			case 2://up
				x = x_coord;
				y = (((y_coord - steps) % h) + h) % h; 
				break;
			case 3://up-left
				x = (((x_coord - steps) % w) + w) % w; 
				y = (((y_coord - steps) % h) + h) % h;
				break;
			case 4://left
				x = (((x_coord - steps) % w) + w) % w;
				y = y_coord;
				break;
			case 5://down-left
				x = (((x_coord - steps) % w) + w) % w;
				y = (((y_coord + steps) % h) + h) % h;
				break;
			case 6://down
				x = x_coord;
				y = (((y_coord + steps) % h) + h) % h;
				break;
			case 7://down-right
				x = (((x_coord + steps) % w) + w) % w;
				y = (((y_coord + steps) % h) + h) % h;
				break;
		}
		if (moveOK(x,y)) {
			x_coord = x;
			y_coord = y;
		}
	}
	
	/**
	 * Makes sure that the place the Critter is trying ot move into isn't already occupied.
	 * @param x - coordinate to be moved into
	 * @param y - coordinate to be moved into
	 * @return - whether its ok or not
	 */
	private boolean moveOK(int x, int y) {
		boolean ok = true;//it's OK to move by default
		//if we don't have to check for critters then the function returns true
		if (getEncounterStatus()) {
			for (Critter c : population) {
				//if a critter is found in our spot then we return false
				if (x == c.x_coord && y == c.y_coord && c.energy > 0) {
					ok = false;
					break;
				}
			}
		}
		return ok;
	}
	
	/**
	 * Takes a newly initialized offspring and places it into the world according 
	 * to the parent's location and the direction input. Then changes the energy 
	 * values of the parent and offspring as stated in the assignment info.
	 * @param offspring - newly initialized Critter
	 * @param direction - direction in relation to the parent that the critter 
	 * should be placed.
	 */
	protected final void reproduce(Critter offspring, int direction) {
		// check if parent has enough energy
		if(this.getEnergy() < Params.min_reproduce_energy) return;
		// set energy of parent/child
		offspring.energy = this.energy / 2;
		this.energy = this.energy/2 + this.energy%2;
		// place child in world
		offspring.x_coord = this.x_coord;
		offspring.y_coord = this.y_coord;
		offspring.move(direction, 1);
		// add child to babies list
		babies.add(offspring);
	}

	public abstract void doTimeStep();
	public abstract boolean fight(String oponent);
	
	/**
	 * Performs all necessary work associated with each "step" done across 
	 * the whole simulation. See notes in code for details. 
	 */
	public static void worldTimeStep() {
		setEncounterStatus(false);
		
		for (Critter c : population) {
			c.old_x = c.x_coord;
			c.old_y = c.y_coord;
		}
		//do time steps
		for (Critter c : population) {
			c.doTimeStep();
		}
		
		removeDeadCritters();
		
		setEncounterStatus(true);
		
		//resolve encounters
		doEncounters();
		removeDeadCritters();
		
		//update rest energy
		for (Critter c : population) {
			c.energy -= Params.rest_energy_cost;
		}
		
		//generate algae
		for (int i = 0; i < Params.refresh_algae_count; i++) {
			Critter a = new Algae();
			a.energy = Params.start_energy;
			a.x_coord = getRandomInt(Params.world_width);
			a.y_coord = getRandomInt(Params.world_height);
			population.add(a);
		}
		
		//add babies to population
		population.addAll(babies);
		babies.clear();
		
		//set already_moved variables to false for next time step
		for (Critter c: population) {
			c.already_moved = false;
		}
		
		//remove dead critters
		removeDeadCritters();
	}
	
	/**
	 * Fills a Group with a grid and all the live critters to be displayed.
	 * @param display - the Group that holds everything.
	 */
	public static void displayWorld(Group display) {
		int worldWidth = Params.world_width;	// dimensions of grid
		int worldHeight = Params.world_height;
		int maxSimWidth = 900;
		int maxSimHeight = 700;
		int size = maxSimWidth/worldWidth;
		if (size > maxSimHeight/worldHeight) size = maxSimHeight/worldHeight;
		int worldPixelWidth = size*worldWidth;	// dimensions in pixels
		int worldPixelHeight = size*worldHeight;
		
		// prints white bg and edges
		Shape t = new Rectangle(worldPixelWidth, worldPixelHeight);
		t.setFill(Color.WHITE);
		t.setStroke(Color.LIGHTGRAY);
		display.getChildren().add(t);
		// prints vertical grid lines
		for (int i = 1; i < worldWidth; i++) {
			Shape s = new Rectangle(1, worldPixelHeight);
			s.setFill(Color.LIGHTGRAY);
			s.setTranslateX(i*size);
			display.getChildren().add(s);
		}
		// prints horizontal grid lines
		for (int i = 1; i < worldHeight; i++) {
			Shape s = new Rectangle(worldPixelWidth, 1);
			s.setFill(Color.LIGHTGRAY);
			s.setTranslateY(i*size);
			display.getChildren().add(s);
		}
		
		//record each critter position here
		Critter[][] c_array = new Critter[worldWidth][worldHeight];
		for (Critter c : population) {
			int x = c.x_coord;
			int y = c.y_coord;
			c_array[x][y] = c;
		}
		Shape s = null;
		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				if (c_array[x][y]!=null) {
					s = getIcon(c_array[x][y], size-1);
					switch(c_array[x][y].viewShape()){
					case CIRCLE:
						s.setTranslateX(x*size + size/2 + 1);
						s.setTranslateY(y*size + size/2 + 1);
						break;
					default:
						s.setTranslateX(x*size + 1);
						s.setTranslateY(y*size + 1);
						break;
					}
					display.getChildren().add(s);
				}
			}
		}
	}
	
	/**
	 * Returns the Shape speficied by the class of the critter type specified
	 * @param c - critter type specified
	 * @param size - how big the shape should be (in pixels)
	 * @return - shape of the correct type and size
	 */
	private static Shape getIcon(Critter c, int size) {
		Shape s = null;
		double sz = (double) size;
		switch(c.viewShape()) {
			case CIRCLE:
				s = new Circle(size/2);
				break;
			case SQUARE:
				s = new Rectangle(size,size);
				break;
			case TRIANGLE:
				s = new Polygon();
				((Polygon)s).getPoints().addAll(new Double[]{
						sz/2, 0.0,
						0.0, sz,
						sz, sz
				});
				break;
			case DIAMOND:
				s = new Polygon();
				((Polygon)s).getPoints().addAll(new Double[]{
						sz/2, 0.0,
						0.0, sz/2,
						sz/2, sz,
						sz, sz/2
				});
				break;
			case STAR:
				s = new Polygon();
				((Polygon)s).getPoints().addAll(new Double[]{
						sz*0.5, 0.0,
						sz*0.375, sz*0.4,
						0.0, sz*0.4,
						sz*0.31, sz*0.625,
						sz*0.2, sz,
						sz*0.5, sz*0.775,
						sz*0.8, sz,
						sz*0.69, sz*0.625,
						sz, sz*0.4,
						sz*0.625, sz*0.4
				});
				break;
		}
		s.setFill(c.viewFillColor());
		s.setStroke(c.viewOutlineColor());
		return s;
	}
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name - name of critter
	 * @throws InvalidCritterException
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		Class<?> my_critter = null;
		Constructor<?> constructor = null;
		Object instance_of_my_critter = null;
		
		critter_class_name = myPackage + "." + critter_class_name;
		
		try {
			my_critter = Class.forName(critter_class_name);
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			throw new InvalidCritterException(critter_class_name);
		}
		//check if subclass of Critter
		if (!Critter.class.isAssignableFrom(my_critter)) {
			throw new InvalidCritterException(critter_class_name);
		}
		try { 
			constructor = my_critter.getConstructor();
			instance_of_my_critter = constructor.newInstance();
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InvalidCritterException(critter_class_name);
		}
		
		Critter c = (Critter) instance_of_my_critter;
		c.energy = Params.start_energy;
		c.x_coord = getRandomInt(Params.world_width);
		c.y_coord = getRandomInt(Params.world_height);
		c.already_moved = false;
		population.add(c);
	}

/**
 * gets a list of critters of a specific type.
 * @param critter_class_name - the critter specified
 * @return list of critters
 * @throws InvalidCritterException
 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();
		critter_class_name = myPackage + "." + critter_class_name;
		for(Critter current : population) {
			Class<?> type;
			try{
				type = Class.forName(critter_class_name);
			} catch (ClassNotFoundException c) {
				throw new InvalidCritterException(critter_class_name);
			}
		    if(type.isAssignableFrom(current.getClass())) {
		    	result.add(current);
		    } 
		}
		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static String runStats(List<Critter> critters) {
		String stats = "";
		stats = stats + critters.size() + " critters as follows -- ";
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			stats = stats + prefix + s + ":" + critter_count.get(s);
			prefix = ", ";
		}
		return stats;
	}
	
	/*
	 * Carries out each Encounter i.e. whenever two Critters are in the same location. 
	 */
	private static void doEncounters() {
		//HashMap<Integer, Integer> critterLocations = new HashMap<Integer, Integer>();
		HashSet<Point> critterLocations = new HashSet<Point>();
		// adds the location of each critter to a hashmap (the actual command that does 
		// this is at the bottom of the for-each).
		for (Critter a : population) {
			if (a.energy > 0) {
				//int coords = a.x_coord + (a.y_coord * 1000000);
				Point coords = new Point(a.x_coord, a.y_coord);
				// checks if the location is already there ie there are two critters there.
				if(critterLocations.contains(coords)) {
					// finds the other critter
					for(Critter b : population) {
						if (b.energy> 0) {
							//int coords2 = b.x_coord + (b.y_coord * 1000000);
							Point coords2 = new Point(b.x_coord, b.y_coord);
							if(coords.equals(coords2)) {
								// same code as before
								//see if the critters want to fight
								boolean fight_a = a.fight(b.toString());
								boolean fight_b = b.fight(a.toString());
								Point a_coords = new Point(a.x_coord, a.y_coord);
								Point b_coords = new Point(b.x_coord, b.y_coord);
								//int a_coords = a.x_coord + (a.y_coord*1000000);
								//int b_coords = b.x_coord + (b.y_coord*1000000);
								
								//critters fight if these conditions are met
								if (a.energy > 0 && b.energy > 0
										&& a_coords.equals(coords) && b_coords.equals(coords)) {
									
									int rand_a, rand_b;
									
									if (fight_a)
										rand_a = getRandomInt(a.energy);
									else
										rand_a = 0;
									
									if (fight_b)
										rand_b = getRandomInt(b.energy);
									else
										rand_b = 0;
										
									if (rand_a > rand_b) {
										a.energy += (b.energy/2);
										b.energy = 0;
									} else {
										b.energy += (a.energy/2);
										a.energy = 0;
									}
								}
								// check if both moved/died
								if((a.energy <= 0 || !a_coords.equals(coords)) 
										&& (b.energy <= 0 || !b_coords.equals(coords))) {
									critterLocations.remove(coords);
								}
								break;
							}
						}
					}
				}
				else {
					//critterLocations.put(coords, 0);
					critterLocations.add(coords);
				}
			}
		}
	}

	/**
	 * Removes "dead" critters i.e. critters with no energy
	 */
	private static void removeDeadCritters() {
		Iterator<Critter> i = population.iterator();
		while (i.hasNext()) {
			Critter c = i.next();
			if (c.energy <= 0) {
				i.remove();
			}
		}
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure thath the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctup update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}
	
	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		population.clear();
		babies.clear();
	}
	
	
}
