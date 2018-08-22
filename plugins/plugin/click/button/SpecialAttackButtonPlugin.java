package plugin.click.button;


import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class SpecialAttackButtonPlugin extends PluginContext {

	/**
	 * @author adameternal123
	 * handles clicking on the special attack button
	 */
    @Override
    protected boolean onClick(Player player, int button) {

    	if(button == 1998) {
           
    		if (player.getCombatSpecial()!= null) {
    			if (!player.isSpecialActivated()) {
    			                 player.getCombatSpecial().enable(player);
    			             } else {
    			                 player.getCombatSpecial().disable(player, true);
    			             }
    			}
    			
    		
    	}
        return false;
    }
}
