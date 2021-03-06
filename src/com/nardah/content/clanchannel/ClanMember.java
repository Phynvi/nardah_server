package com.nardah.content.clanchannel;

import com.nardah.content.clanchannel.channel.ClanChannel;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.relations.PrivacyChatMode;
import com.nardah.net.packet.out.SendClanDetails;

import java.util.Objects;
import java.util.Optional;

/**
 * Handles the clan channel member.
 * @author Daniel
 * @author Michael
 */
public class ClanMember {
	/**
	 * The name of the clan member.
	 */
	public final String name;
	
	/**
	 * The rank of the clan member.
	 */
	public ClanRank rank;
	
	/**
	 * The total level of the clan member.
	 */
	public int totalLevel;
	
	/**
	 * The join date of the clan member.
	 */
	public String joined;
	
	/**
	 * The experienced gained by the clan member.
	 */
	public double expGained;
	
	/**
	 * The mobs killed by the clan member.
	 */
	public int npcKills;
	
	/**
	 * The players killed by the clan member.
	 */
	public int playerKills;
	
	/**
	 * The player instance of the clan member .
	 */
	public Optional<Player> player;
	
	/**
	 * Constructs a new <code>ClanMember</code>.
	 */
	public ClanMember(String name) {
		this.name = name;
		this.rank = ClanRank.MEMBER;
		this.player = Optional.empty();
	}
	
	/**
	 * Constructs a new <code>ClanMember</code>.
	 */
	public ClanMember(Player player) {
		this.name = player.getName();
		this.player = Optional.of(player);
		this.rank = ClanRank.MEMBER;
	}
	
	/**
	 * Handles messaging the clan member.
	 */
	public void message(Object... messages) {
		player.ifPresent(p -> {
			ClanChannel channel = p.clanChannel;
			for(Object message : messages) {
				p.send(new SendClanDetails(String.valueOf(message), channel.getName(), ClanRank.SYSTEM));
			}
		});
	}
	
	/**
	 * Handles messaging the clan member.
	 */
	public void chat(ClanMember speaker, Object message) {
		player.ifPresent(p -> {
			final ClanChannel channel = p.clanChannel;
			
			final Optional<Player> result = speaker.player;
			
			if(!result.isPresent()) {
				return;
			}
			
			final Player playerTalking = result.get();
			
			if(p.relations.getClanChatMode() == PrivacyChatMode.OFF || p.relations.getClanChatMode() == PrivacyChatMode.FRIENDS_ONLY && !p.relations.isFriendWith(playerTalking.getName())) {
				return;
			}
			
			p.send(new SendClanDetails(speaker.name, String.valueOf(message), channel.getName(), speaker.rank));
		});
	}
	
	/**
	 * Gets the value of the clan member based on their contribution.
	 */
	public int getValue() {
		return (int) expGained;
	}
	
	public boolean hasContributed() {
		return !(npcKills == 0 && expGained == 0 && playerKills == 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof ClanMember) {
			ClanMember other = (ClanMember) obj;
			return Objects.equals(name, other.name) && Objects.equals(rank, other.rank) && Objects.equals(totalLevel, other.totalLevel) && Objects.equals(joined, other.joined) && Objects.equals(expGained, other.expGained) && Objects.equals(npcKills, other.npcKills) && Objects.equals(playerKills, other.playerKills);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name + " " + rank;
	}
	
}
