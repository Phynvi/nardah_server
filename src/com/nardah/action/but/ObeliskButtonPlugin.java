package com.nardah.action.but;

import com.nardah.action.ActionInitializer;
import com.nardah.action.impl.ButtonAction;
import com.nardah.content.Obelisks;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class ObeliskButtonPlugin extends ActionInitializer {

	@Override
	public void init() {
		ButtonAction action = new ButtonAction() {
			@Override
			public boolean click(Player player, int button) {
				int obelisk = player.attributes.get("OBELISK", Integer.class);
				if (obelisk != -1) {
					switch (button) {
						case -14524:
							Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.getRandom(Obelisks.ObeliskData.LEVEL_44), Obelisks.ObeliskData.LEVEL_44);
							return true;
						case -14523:
							Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.getRandom(Obelisks.ObeliskData.LEVEL_27), Obelisks.ObeliskData.LEVEL_27);
							return true;
						case -14522:
							Obelisks.get().activate(player, obelisk,Obelisks.ObeliskData.getRandom(Obelisks.ObeliskData.LEVEL_35),  Obelisks.ObeliskData.LEVEL_35);
							return true;
						case -14521:
							Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.getRandom(Obelisks.ObeliskData.LEVEL_13), Obelisks.ObeliskData.LEVEL_13);
							return true;
						case -14520:
							Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.getRandom(Obelisks.ObeliskData.LEVEL_19), Obelisks.ObeliskData.LEVEL_19);
							return true;
						case -14519:
							Obelisks.get().activate(player, obelisk, Obelisks.ObeliskData.getRandom(Obelisks.ObeliskData.LEVEL_50), Obelisks.ObeliskData.LEVEL_50);
							return true;
					}
				}
				return false;
			}
		};

		action.register(-14524).register(-14523).register(-14522).register(-14521).register(-14520).register(-14519);
	}
}
