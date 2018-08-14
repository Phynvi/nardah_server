package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.PacketType;

/**
 * Sends the entity feed.
 * @author Daniel
 */
public class SendEntityFeed extends OutgoingPacket {
	
	private final int HP;
	private final int maxHP;
	private String opponent;
	
	public SendEntityFeed(String opponent, int HP, int maxHP) {
		super(175, PacketType.VAR_BYTE);
		this.opponent = opponent == null ? "null" : opponent;
		this.HP = HP;
		this.maxHP = maxHP;
	}
	
	@Override
	public boolean encode(Player player) {
		builder.writeString(opponent).writeShort(HP).writeShort(maxHP);
		return true;
	}
	
}
