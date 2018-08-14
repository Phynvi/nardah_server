package io.battlerune.game.world.entity.mob.player;

import io.battlerune.Config;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.data.LockType;
import io.battlerune.game.world.entity.mob.player.profile.Profile;
import io.battlerune.game.world.entity.mob.player.profile.ProfileRepository;

import java.util.Arrays;
import java.util.Optional;

/**
 * Handles account security.
 * @author Daniel
 */
public class AccountSecurity {
	
	/**
	 * The player instance.
	 */
	private Player player;
	
	/**
	 * Constructs a new <code>AccountSecurity<code>.
	 */
	AccountSecurity(Player player) {
		this.player = player;
	}
	
	/**
	 * Handles account login.
	 */
	public void login() {
		String name = player.getName();
		String host = player.lastHost;
		
		if(!player.hostList.contains(host))
			player.hostList.add(host);
		
		ProfileRepository.put(new Profile(name, player.getPassword(), host, player.hostList, player.right));
		
		if(!AccountData.forName(name).isPresent()) {
			if(player.right == PlayerRight.MODERATOR || player.right == PlayerRight.OWNER || player.right == PlayerRight.ADMINISTRATOR || player.right == PlayerRight.DEVELOPER) {
				player.right = PlayerRight.PLAYER;
				player.inventory.clear();
				player.equipment.clear();
				player.pkPoints = 0;
				player.skillingPoints = 0;
				player.bossPoints = 0;
				player.triviaPoints = 0;
				player.expRate = 1;
				player.votePoints = 0;
				player.donation.setCredits(0);
				player.pestPoints = 0;
				player.kolodionPoints = 0;
				player.bank.clear();
				player.setVisible(true);
			} else if(PlayerRight.isDonator(player)) {
				player.setVisible(true);
				player.donation.updateRank(true);
			}
			return;
		}
		
		player.interfaceManager.close();
		AccountData account = AccountData.forName(name).get();
		player.setVisible(true);
		
		if(!Config.LIVE_SERVER || host.equals("127.0.0.1")) {
			return;
		}
		
		if(account.getName().equalsIgnoreCase(name)) {
			if((account.getRight() == PlayerRight.OWNER || account.getRight() == PlayerRight.DEVELOPER) && player.right != account.right)
				player.right = account.right;
			
			for(String hosts : account.getHost()) {
				if(host.equalsIgnoreCase(hosts))
					return;
			}
			
			if(account.getKey().isEmpty()) {
				return;
			}
			
			player.locking.lock(LockType.MASTER_WITH_COMMANDS);
			player.message("<col=F03541>You have logged in with an un-authorized IP address. Your account was locked. Please");
			player.message("<col=F03541>enter your security key by command. ::key 12345");
			World.sendStaffMessage("<col=E02828>[AccountSecurity] Un-recognized staff host address : " + player.getName() + ".");
		}
	}
	
	/**
	 * Holds all the account security data for the management team.
	 */
	public enum AccountData {
		/* Owner */
		
		ADAM(PlayerRight.DEVELOPER, "Adam", "", "127.0.0.1"), HARRYL(PlayerRight.DEVELOPER, "Harryl", "", "127.0.0.1"), MERADJ(PlayerRight.DEVELOPER, "Meradj", "", "213.127.118.249", "195.169.28.13"), RED(PlayerRight.DEVELOPER, "Red", "127.0.0.1", ""),
		
		/* Developer */
		ASHPIRE(PlayerRight.DEVELOPER, "Ashpire", "", "", ""), ETHAN(PlayerRight.DEVELOPER, "Ethan", "", "", ""), JORDAN(PlayerRight.DEVELOPER, "JordanRSPS", "", "", ""),
		// NSHUSA(PlayerRight.DEVELOPER, "Nshusa", "320 406 7557", "97.88.20.251"),
		
		/* Administrator */
		// SICK(PlayerRight.ADMINISTRATOR, "Sick", "532268532268", "24.185.138.252",
		// "70.44.114.95"),
		// SAM(PlayerRight.ADMINISTRATOR, "Osrssam", "462446244624", "162.104.166.126"),
		// PROJECT123(PlayerRight.ADMINISTRATOR, "Project123", "667199815671",
		// "72.223.36.170"),
		// GOAT(PlayerRight.ADMINISTRATOR, "Goat", "193618193618", "24.19.102.254"),
		
		/* Moderator */
		// YAN(PlayerRight.MODERATOR, "Yan", "484090327761", "207.134.50.158"),
		// SANTA(PlayerRight.MODERATOR, "Santa", "5283416073", "84.192.208.37"),
		// MEERSTER(PlayerRight.MODERATOR, "Meerster", "137137137137",
		// "75.184.113.154"),
		
		/* Helper */
		ADAM1(PlayerRight.HELPER, "Adam1", "", "104.197.52.162"), // TRANQUILLO(PlayerRight.HELPER, "Tranquillo", "", "87.214.74.143"),
		// PVM_BEN(PlayerRight.HELPER, "Pvm Ben", "", "31.205.22.206"),
		// SYZYGY(PlayerRight.HELPER, "Syzygy", "", "70.66.42.221"),
		MULTAK(PlayerRight.PLAYER, "Multak", "", "24.98.63.27");
		
		private final String name;
		private final String key;
		private final PlayerRight right;
		private final String[] host;
		
		AccountData(PlayerRight right, String name, String key, String... host) {
			this.right = right;
			this.name = name;
			this.key = key;
			this.host = host;
		}
		
		public static Optional<AccountData> forName(String name) {
			return Arrays.stream(values()).filter(a -> a.name.equalsIgnoreCase(name)).findAny();
		}
		
		public String getName() {
			return name;
		}
		
		public PlayerRight getRight() {
			return right;
		}
		
		public String getKey() {
			return key;
		}
		
		public String[] getHost() {
			return host;
		}
	}
}
