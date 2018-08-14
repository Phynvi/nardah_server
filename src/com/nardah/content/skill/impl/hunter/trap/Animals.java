package com.nardah.content.skill.impl.hunter.trap;

import com.nardah.game.Animation;
import com.nardah.game.world.items.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of all the animals that can be hunted.
 * @author Viper
 * @version 1.0 - 05/06/2016
 */
public enum Animals {
	
	/*
	 * Birds
	 */
	CRIMSON_SWIFT(5549, new Item[]{new Item(10088, 3), new Item(526, 1), new Item(9978, 1)}, 1, 34, Traps.BIRD_SNARE, 9349, 9373, 9344, new Animation(6775), new Animation(6774)), GOLDEN_WARBLER(5551, new Item[]{new Item(10090, 3), new Item(526, 1), new Item(9978, 1)}, 5, 47, Traps.BIRD_SNARE, 9376, 9377, 9344, new Animation(6775), new Animation(6774)), COPPER_LONGTAIL(5552, new Item[]{new Item(10091, 3), new Item(526, 1), new Item(9978, 1)}, 9, 61, Traps.BIRD_SNARE, 9378, 9379, 9344, new Animation(6775), new Animation(6774)), CERULEAN_TWITCH(5550, new Item[]{new Item(10089, 3), new Item(526, 1), new Item(9978, 1)}, 11, 64.5, Traps.BIRD_SNARE, 9374, 9375, 9344, new Animation(6775), new Animation(6774)), TROPICAL_WAGTAIL(5548, new Item[]{new Item(10087, 3), new Item(526, 1), new Item(9978, 1)}, 19, 95.8, Traps.BIRD_SNARE, 9347, 9348, 9344, new Animation(6775), new Animation(6774)),
	
	/*
	 * Chinchompa
	 */
	RED_CHINCHOMPA(2911, new Item[]{new Item(10034, 1)}, 1, 265, Traps.BOX, 9381, 9382, 9385, new Animation(5181), new Animation(5181));
	
	private int id;
	
	private int level, animationObject, successfulTransformObjectId, failedTransformObjectId;
	private Item[] item;
	private double xp;
	private Traps hunter;
	private Animation successCatchAnim, failCatchAnim;

	static final Map<Integer, Animals> npc = new HashMap<Integer, Animals>();
	static final Map<Integer, Animals> object = new HashMap<Integer, Animals>();

	public static Animals forID(int id) {
		return npc.get(id);
	}

	static {
		for(Animals npcs : Animals.values())
			npc.put(npcs.id, npcs);
		for(Animals objets : Animals.values())
			object.put(objets.getSuccessfulTransformObjectId(), objets);
	}

	public static Animals forObjectId(int id) {
		return object.get(id);
	}

	private Animals(int npcId, Item[] item, int level, double xp, Traps hunter, int animationObject, int successfulTransformObjectId, int failedTransformObjectId, Animation successCatchAnim, Animation failCatchAnim) {
		this.id = npcId;
		this.item = item;
		this.level = level;
		this.xp = xp;
		this.hunter = hunter;
		this.animationObject = animationObject;
		this.successfulTransformObjectId = successfulTransformObjectId;
		this.failedTransformObjectId = failedTransformObjectId;
		this.successCatchAnim = successCatchAnim;
		this.failCatchAnim = failCatchAnim;
	}

	private Animals(int index) {
		this.id = index;
	}

	public int getID() {
		return id;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the failedTransformObjectId
	 */
	public int getFailedTransformObjectId() {
		return failedTransformObjectId;
	}

	/**
	 * @return the hunter
	 */
	public Traps getHunter() {
		return hunter;
	}

	/**
	 * @return the successCatchAnim
	 */
	public Animation getSuccessCatchAnim() {
		return successCatchAnim;
	}

	/**
	 * @return the failCatchAnim
	 */
	public Animation getFailCatchAnim() {
		return failCatchAnim;
	}

	/**
	 * @return the item
	 */
	public Item[] getItem() {
		return item;
	}

	/**
	 * @return the xp
	 */
	public double getXP() {
		return xp;
	}

	/**
	 * @return the successfulTransformObjectId
	 */
	public int getSuccessfulTransformObjectId() {
		return successfulTransformObjectId;
	}

	/**
	 * @return the animationObject
	 */
	public int getAnimationObject() {
		return animationObject;
	}

}
