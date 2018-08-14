package plugin.command;

import io.battlerune.game.plugin.extension.CommandExtension;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.entity.actor.player.command.Command;
import io.battlerune.game.world.entity.actor.player.command.CommandParser;

public class SupremeCommandPlugin extends CommandExtension {

    @Override
    protected void register() {
        commands.add(new Command("Bank") {
            @Override
            public void execute(Player player, CommandParser parser) {
                
            }
        });
    }
    

    @Override
    public boolean canAccess(Player player) {
        return PlayerRight.isDonator(player);
    }

}
