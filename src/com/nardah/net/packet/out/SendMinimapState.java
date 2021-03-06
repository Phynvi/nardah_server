package com.nardah.net.packet.out;

import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.OutgoingPacket;

public class SendMinimapState extends OutgoingPacket {
	
	private final MinimapState state;
	
	public SendMinimapState(MinimapState state) {
		super(99, 1);
		this.state = state;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeByte(state.getCode());
		return true;
	}
	
	/**
	 * Represents the state a minimap can be in.
	 * @author Seven
	 */
	public enum MinimapState {
		/**
		 * The default state where the map is visible and clicking is enabled.
		 */
		NORMAL(0),
		
		/**
		 * The state where the map is visible, but clicking is disabled.
		 */
		UNCLICKABLE(1),
		
		/**
		 * The state where the map is pitch black, and clicking is disabled.
		 */
		HIDDEN(2);
		
		private final int code;
		
		MinimapState(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
	
}
