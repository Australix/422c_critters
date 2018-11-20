package assignment5;
/* CRITTERS Critter1.java
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


public class Critter1 extends Critter {
/**
 * The Berserker always runs and will (have to) fight whenever it 
 * encounters another Critter. It will reproduce only with a significant 
 * amount of energy head room.
 */
	@Override
	public void doTimeStep() {
		if(getEnergy() > Params.start_energy * 2.5 && getEnergy() >= Params.min_reproduce_energy) {
			Critter1 child = new Critter1();
			reproduce(child, Critter.getRandomInt(8));
		}
		run(getRandomInt(8));
	}
/**
 * Since the Berserker always runs on his turn, he will always have 
 * to fight regardless of the code here. 
 */
	@Override
	public boolean fight(String opponent) {
		return true;
	}
	
	public String toString() {
		return "1";
	}
	@Override
	public CritterShape viewShape() {
		return Critter.CritterShape.STAR;
	}
	@Override
	public Color viewOutlineColor() {
		return Color.RED;
	}
	@Override
	public Color viewFillColor() {
		return Color.RED;
	}
}
