package plugin.command;

import static io.battlerune.content.skill.impl.magic.teleport.Teleportation.TeleportationData.DONATOR;

import io.battlerune.Config;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.plugin.extension.CommandExtension;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.game.world.entity.mob.player.command.Command;
import io.battlerune.game.world.entity.mob.player.command.CommandParser;
import io.battlerune.net.packet.out.SendMessage;

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
