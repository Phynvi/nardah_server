package com.nardah.game.action.impl;

import com.nardah.content.skill.impl.magic.spell.Spell;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;

/**
 * Handles the spell casting action.
 * @author Daniel
 */
public final class SpellAction extends Action<Player> {
	private final Spell spell;
	private final Item item;

	/**
	 * Creates the <code>SpellAction</code>.
	 */
	public SpellAction(Player player, Spell spell, Item item) {
		super(player, 3);
		this.spell = spell;
		this.item = item;
	}

	@Override
	protected void onSchedule() {
		spell.execute(getMob(), item);
	}

	@Override
	public void execute() {
		cancel();
	}

	@Override
	public String getName() {
		return "spell-action";
	}

	@Override
	public boolean prioritized() {
		return true;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.WALKABLE;
	}

}
