package assignment5;
/* CRITTERS Critter2.java
 * EE422C Project 4 submission by
 * Timberlon Gray
 * tg22698
 * 16235
 * Raiyan Chowdhury
 * rac4444
 * 16235
 * Slip days used: <0>
 * Spring 2017
 */

import javafx.scene.paint.Color;

public class Critter2 extends Critter {
	private int slowMove = 0; // has the option to move every 5 turns
/**
 * The Sloth moves very infrequently, and reproduces at a 
 * fairly low energy count.
 */
	@Override
	public void doTimeStep() {
		if(getEnergy() > Params.start_energy * 0.4 && getEnergy() >= Params.min_reproduce_energy) {
			Critter2 child = new Critter2();
			reproduce(child, Critter.getRandomInt(8));
		}
		if(slowMove == 0) {
			slowMove = 4;
			int direction = getRandomInt(8);
			if(look(direction, false) != null && look(direction, false).equals("@")) {
				walk(direction);
			}
		} else slowMove--;
	}

/**
 * Because the sloth is vegetarian, it will try to run (well, walk since 
 * they can't run) from any fight that is not with algae.
 */
	@Override
	public boolean fight(String opponent) {
		if (opponent.equals("@")) return true;
		walk(getRandomInt(8));
		return false;
	}
	
	public String toString() {
		return "2";
	}

	@Override
	public CritterShape viewShape() {
		return Critter.CritterShape.DIAMOND;
	}
	@Override
	public Color viewOutlineColor() {
		return Color.GREEN;
	}
	@Override
	public Color viewFillColor() {
		return Color.BROWN;
	}
	
}
