package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.relations.PrivacyChatMode;
import io.battlerune.net.packet.OutgoingPacket;

public final class SendChatOption extends OutgoingPacket {
	
	private final PrivacyChatMode publicChat;
	private final PrivacyChatMode privateChat;
	private final PrivacyChatMode clanChat;
	private final PrivacyChatMode tradeChat;
	
	public SendChatOption(PrivacyChatMode publicChat, PrivacyChatMode privateChat, PrivacyChatMode clanChat, PrivacyChatMode tradeChat) {
		super(206, 4);
		this.publicChat = publicChat;
		this.privateChat = privateChat;
		this.clanChat = clanChat;
		this.tradeChat = tradeChat;
	}
	
	@Override
	protected boolean encode(Player player) {
		builder.writeByte(publicChat.getCode()).writeByte(privateChat.getCode()).writeByte(clanChat.getCode()).writeByte(tradeChat.getCode());
		return true;
	}
}
