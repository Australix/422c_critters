package assignment5;
/* CRITTERS Critter3.java
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

import javafx.scene.paint.Color;

public class Critter3 extends Critter {
	
	private int dir;
	private int time_to_move;
	
	public Critter3() {
		dir = Critter.getRandomInt(8);
		time_to_move = 3;
	}

	@Override
	/**
	 * This critter will move every 3 turns in search of food (i.e. other critters),
	 * but otherwise does not move as it is typically rather lazy, even if it is
	 * very hungry (i.e. low on energy).
	 * It is however very interested in carrying on its lineage, and will always reproduce
	 * as long as it meets the minimum energy requirement.
	 */
	public void doTimeStep() {
		if (getEnergy() >= Params.min_reproduce_energy) {
			Critter3 child = new Critter3();
			reproduce(child, Critter.getRandomInt(8));
		}
		if (time_to_move == 0) {
			time_to_move = 3;
			walk(dir);
			dir = (dir*Critter.getRandomInt(8)) % 8;
		} else {
			time_to_move -= 1;
		}
	}

	@Override
	/**
	 * This critter is a carnivore that unconditionally fights (hunts) non-Algae critters.
	 * Unfortunately, it is allergic to plants and has a 50/50 chance of dying should 
	 * it encounter Algae and fail to escape from it. If it survives, it gains newfound
	 * energy from its near-death experience. What doesn't kill you makes you stronger.
	 * @param String representation of opponent
	 */
	public boolean fight(String opponent) {
		if (!opponent.equals("@")) {
			return true;
		}
		if (dir%2 == 0) {
			dir = (dir+1)%8;
		}
		run(dir);
		return false;
	}
	
	public String toString() {
		return "3";
	}

	@Override
	public CritterShape viewShape() {
		return Critter.CritterShape.TRIANGLE;
	}
	@Override
	public Color viewOutlineColor() {
		return Color.GRAY;
	}
	@Override
	public Color viewFillColor() {
		return Color.ORANGE;
	}
	
}