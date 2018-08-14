package io.battlerune.game.plugin.extension;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.data.PacketType;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.command.Command;
import io.battlerune.game.world.entity.actor.player.command.CommandParser;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class CommandExtension extends PluginContext {

	private static final Logger logger = LogManager.getLogger(LoggerType.START_UP);
	protected static final Set<CommandExtension> extensions = new HashSet<>();

	protected final Set<Command> commands = new HashSet<>();
	public final Multimap<String, Command> multimap = ArrayListMultimap.create();

	@Override
	public void onInit() {
		extensions.add(this);
		register();
		multimap.clear();
		for(Command command : commands) {
			if(command.getNames().length == 0) {
				logger.error(String.format("plugin=%s command missing name.", this.getClass().getSimpleName()));
				continue;
			}

			for(String name : command.getNames()) {
				final String key = name.toLowerCase();
				if(multimap.containsKey(key)) {
					logger.error(String.format("plugin=%s duplicate command=%s", this.getClass().getSimpleName(), key));
					continue;
				}

				multimap.put(key, command);
			}
		}
		commands.clear();
	}

	protected abstract void register();

	public abstract boolean canAccess(Player player);

	private static boolean canExecute(Player player, CommandParser parser) {
		return !player.locking.locked(PacketType.COMMANDS) || parser.getCommand().startsWith("key");
	}

	@Override
	protected boolean handleCommand(Player player, CommandParser parser) {
		if(!canExecute(player, parser)) {
			return true;
		}

		for(CommandExtension extension : extensions) {

			if(!extension.canAccess(player)) {
				continue;
			}

			final Collection<Command> collection = extension.multimap.get(parser.getCommand().toLowerCase());

			if(collection == null) {
				continue;
			}

			if(collection.isEmpty()) {
				continue;
			}

			final Command command = collection.toArray(new Command[0])[0];

			// handle error
			try {
				command.execute(player, parser);
			} catch(Exception ex) {
				// but dont care about the exception
			}

			return true;

		}
		return false;
	}

}
