package plugin.command;

import com.nardah.game.plugin.extension.CommandExtension;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.actor.player.command.Command;
import com.nardah.game.world.entity.actor.player.command.CommandParser;

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
