package com.nardah.content.skill.impl.hunter.trap;

import com.nardah.game.Animation;
import com.nardah.game.world.items.Item;

/**
 * Hunting traps.
 * @author Viper
 * @version 14/12/2016
 */
public enum Traps {

	BOX("lay", new Item[]{new Item(10008, 1)}, 9380, new int[][]{{5208, -1}, {5208, -1}}, 27), BIRD_SNARE("lay", new Item[]{new Item(10006)}, 9345, new int[][]{{5207, 5207}, {5207, 5207}}, 1), LARUPIA_PITFALL("lay", null, 1, new int[][]{{5208, 9726}, {5208, 9729}}, 31);
	
	/**
	 * The throttle option.
	 */
	private String option;
	
	/**
	 * The components of the trap.
	 */
	private Item[] items;
	
	/**
	 * Laid trap model
	 */
	private int objectId;
	
	/**
	 * Item node animation.
	 */
	private Animation layAnimation, pickUpAnimation;
	
	/**
	 * Trap animation.
	 */
	private Animation trapLayAnimation, trapPickUpAnimation;
	
	/**
	 * Hunter prerequisite to use this trap.
	 */
	private int baseLevel;

	private Traps(String option, Item[] items, int objectId, int[][] animations, int baseLevel) {
		this.option = option;
		this.items = items;
		this.objectId = objectId;
		this.baseLevel = baseLevel;
		layAnimation = animations[0][0] == -1 ? null : new Animation(animations[0][0]);
		pickUpAnimation = animations[1][0] == -1 ? null : new Animation(animations[1][0]);
		trapLayAnimation = animations[0][1] == -1 ? null : new Animation(animations[0][1]);
		trapPickUpAnimation = animations[1][1] == -1 ? null : new Animation(animations[1][1]);
	}
	
	/**
	 * @return the option
	 */
	public String getOption() {
		return option;
	}

	/**
	 * @return the {@link Item} components.
	 */
	public Item[] getItems() {
		return items;
	}
	
	public int getObjectId() {
		return objectId;
	}

	public Animation getPickUpAnimation() {
		return pickUpAnimation;
	}

	public int getBaseLevel() {
		return baseLevel;
	}

	/**
	 * @return the layAnimation
	 */
	public Animation getLayAnimation() {
		return layAnimation;
	}

	/**
	 * @param layAnimation the layAnimation to set
	 */
	public void setLayAnimation(Animation layAnimation) {
		this.layAnimation = layAnimation;
	}

	/**
	 * @return the trapLayAnimation
	 */
	public Animation getTrapLayAnimation() {
		return trapLayAnimation;
	}

	/**
	 * @param trapLayAnimation the trapLayAnimation to set
	 */
	public void setTrapLayAnimation(Animation trapLayAnimation) {
		this.trapLayAnimation = trapLayAnimation;
	}

	/**
	 * @return the trapPickUpAnimation
	 */
	public Animation getTrapPickUpAnimation() {
		return trapPickUpAnimation;
	}

	/**
	 * @param trapPickUpAnimation the trapPickUpAnimation to set
	 */
	public void setTrapPickUpAnimation(Animation trapPickUpAnimation) {
		this.trapPickUpAnimation = trapPickUpAnimation;
	}

}