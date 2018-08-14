package plugin.command;

import com.nardah.Config;
import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.game.plugin.extension.CommandExtension;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.actor.player.command.Command;
import com.nardah.game.world.entity.actor.player.command.CommandParser;
import com.nardah.net.packet.out.SendMessage;

public class DonatorCommandPlugin extends CommandExtension {

    @Override
    protected void register() {
        commands.add(new Command( "donorzone", "dzone") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DONATOR_ZONE, 20, () -> {
                    player.send(new SendMessage("Welcome to the donator zone, " + player.getName() + "!"));
                });
            }
        });
       
        
        commands.add(new Command("die") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DONATOR_ZONE, 20, () -> {
                    player.send(new SendMessage("Welcome to the donator zone, " + player.getName() + "!"));
                });
            }
        });
        
        commands.add(new Command("portals", "portal", "portalzone", "pz") {
            @Override
            public void execute(Player player, CommandParser parser) {
               // Teleportation.teleport(player, new Position(3363, 3318, 2));
           
                Teleportation.teleport(player, Config.PORTAL_ZONE, 20, () -> {
                    player.send(new SendMessage("@or2@Welcome to the Portal Zone, " + player.getName() + "."));
                    player.send(new SendMessage("@or2@Each portal teleports you to a different zone"));
                    player.send(new SendMessage("@or2@These are Safe Zones"));
                });
            }
        });
    }      
                
        
    @Override
    public boolean canAccess(Player player) {
        return PlayerRight.isDonator(player);
    }

}
