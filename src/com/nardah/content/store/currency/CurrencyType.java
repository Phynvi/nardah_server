package com.nardah.content.store.currency;

import com.google.common.collect.ImmutableSet;
import com.nardah.content.store.currency.impl.*;
import io.battlerune.content.store.currency.impl.*;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.util.Utility;

import java.util.Arrays;
import java.util.Optional;

/**
 * The enumerated type whom holds all the currencies usable for a server.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 4-1-2017.
 */
public enum CurrencyType {

	COINS(0, new ItemCurrency(995)), TOKKUL(1, new ItemCurrency(6529)), DONATOR_POINTS(2, new DonatorPointCurrency()), PVP_POINTS(3, new PlayerKillingPointCurrency()), SLAYER_POINTS(4, new SlayerPointCurrency()), PEST_POINTS(5, new PestPointCurrency()), CLAN_POINTS(6, new ClanPointCurrency()), VOTE_POINTS(7, new VotePointCurrency()), PRESTIGE_POINTS(8, new PrestigePointCurrency()), KOLODION_POINTS(9, new KolodionsPointCurrency()), GRACEFUL_TOKEN(10, new ItemCurrency(11849)), PET_TOKENS(10, new ItemCurrency(20527)), SKILLING_POINTS(11, new SkillingPointCurrency()), BOSS_POINTS(12, new BossPointCurrency()), TRIVIA_POINTS(13, new TriviaPointCurrency()),

	;

	private static final ImmutableSet<CurrencyType> VALUES = ImmutableSet.copyOf(values());

	public final Currency currency;

	public final int id;

	CurrencyType(int id, Currency currency) {
		this.id = id;
		this.currency = currency;

	}

	public static Optional<CurrencyType> lookup(int id) {
		return Arrays.stream(values()).filter(it -> it.getId() == id).findFirst();
	}

	public int getId() {
		return id;
	}

	public static boolean isCurrency(int id) {
		return VALUES.stream().filter(i -> i.currency.tangible()).anyMatch(i -> ((ItemCurrency) i.currency).itemId == id);
	}

	public static String getValue(Player player, CurrencyType currency) {
		String value = "";
		switch(currency) {
			case COINS:
				value = Utility.formatDigits(player.inventory.contains(995) ? player.inventory.computeAmountForId(995) : 0);
				break;
			case TOKKUL:
				value = Utility.formatDigits(player.inventory.contains(6529) ? player.inventory.computeAmountForId(6529) : 0);
				break;
			case GRACEFUL_TOKEN:
				value = Utility.formatDigits(player.inventory.contains(11849) ? player.inventory.computeAmountForId(11849) : 0);
				break;
			case PET_TOKENS:
				value = Utility.formatDigits(player.inventory.contains(20527) ? player.inventory.computeAmountForId(20527) : 0);
				break;
			case KOLODION_POINTS:
				value = Utility.formatDigits(player.kolodionPoints);
				break;
			case PVP_POINTS:
				value = Utility.formatDigits(player.pkPoints);
				break;
			case SKILLING_POINTS:
				value = Utility.formatDigits(player.skillingPoints);
				break;
			case SLAYER_POINTS:
				value = Utility.formatDigits(player.slayer.getPoints());
				break;
			case VOTE_POINTS:
				value = Utility.formatDigits(player.votePoints);
				break;
			case PEST_POINTS:
				value = Utility.formatDigits(player.pestPoints);
				break;
			case BOSS_POINTS:
				value = Utility.formatDigits(player.bossPoints);
				break;
			case TRIVIA_POINTS:
				value = Utility.formatDigits(player.triviaPoints);
				break;
			case PRESTIGE_POINTS:
				value = Utility.formatDigits(player.prestige.getPrestigePoint());
				break;
			case DONATOR_POINTS:
				value = Utility.formatDigits(player.donation.getCredits());
				break;
			case CLAN_POINTS:
				if(player.clanChannel == null) {
					value = "0";
				} else {
					value = Utility.formatDigits(player.clanChannel.getDetails().points);
				}
				break;
		}
		return value.equals("0") ? "None!" : value;
	}

	@Override
	public String toString() {
		return name().toLowerCase().replace("_", " ");
	}
}
