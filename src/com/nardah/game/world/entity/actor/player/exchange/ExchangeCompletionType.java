package com.nardah.game.world.entity.actor.player.exchange;

/**
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 23-1-2017.
 */
public enum ExchangeCompletionType {
	/**
	 * Determines the items should be restored to the belonging owners.
	 */
	RESTORE,
	
	/**
	 * Determines the items should be disposed.
	 */
	DISPOSE,
	
	/**
	 * Determines the items should be halted.
	 */
	HALT
	
}
