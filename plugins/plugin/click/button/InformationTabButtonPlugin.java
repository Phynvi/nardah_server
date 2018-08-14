package plugin.click.button;

import io.battlerune.Config;
import io.battlerune.content.DropDisplay;
import io.battlerune.content.DropSimulator;
import io.battlerune.content.achievement.AchievementInterface;
import io.battlerune.content.activity.ActivityType;
import io.battlerune.content.skill.impl.slayer.SlayerTab;
import io.battlerune.content.staff.PanelType;
import io.battlerune.content.staff.StaffPanel;
import io.battlerune.content.tittle.TitleManager;
import io.battlerune.content.writer.InterfaceWriter;
import io.battlerune.content.writer.impl.InformationWriter;
import io.battlerune.content.writer.impl.QuestWriter;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.net.packet.out.SendURL;

public class InformationTabButtonPlugin extends PluginContext {

    /**
     * @author Adam_#6723
     */

    @Override
    protected boolean onClick(Player player, int button) {
        switch (button) {
            case 29404:
                // if (PlayerRight.isManagement(player)) {
                if (PlayerRight.isManagement(player)) {
                    StaffPanel.open(player, PanelType.INFORMATION_PANEL);
                    return true;
                }
                player.send(new SendURL("http://www.nardah.com"));
                return true;

            case 29440:
                InterfaceWriter.write(new InformationWriter(player));
                return true;
		/*case 29420:
           PlayerGuideHandler guide = new PlayerGuideHandler();
           guide.open(player);
			break;*/
            case 29421:
            case 29419:
            case 29423:
                player.dialogueFactory.sendOption("Activity Logger", () -> {
                    player.dialogueFactory.onAction(player.activityLogger::open);
                }, "Game Records", () -> {
                    player.dialogueFactory.onAction(() -> player.gameRecord.display(ActivityType.getFirst()));
                }, "Title Manager", () -> {
                    player.dialogueFactory.onAction(() -> TitleManager.open(player));
                }).execute();
                break;
            case 29429:
                player.slayer.open(SlayerTab.MAIN);
//			player.presetManager.open();
//			PresetInterfaceHandler presets = new PresetInterfaceHandler();
//			presets.open(player);
                break;
            /** here **/
            case 29410:
                InterfaceWriter.write(new AchievementInterface(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 35_000);
                // AchievementInterface.open(player);
                break;
            case 29426:
                player.dialogueFactory.sendOption("Drop display", () -> {
                            player.dialogueFactory.onAction(() -> DropDisplay.open(player));
                        },
                        "Drop Simulator", () -> {
                            player.dialogueFactory.onAction(() -> DropSimulator.open(player));
                        }).execute();
//			DropDisplay.open(player);
                break;
            case 29432:
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 51200);
//                TeleportHandler.open(player, TeleportType.CITIES);
                break;
            case 29411:

                InterfaceWriter.write(new InformationWriter(player));
                return true;
            case -30531:
            case -28219:
            case -30131:
            case -14324:
                InterfaceWriter.write(new InformationWriter(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 29_400);
                return true;
            case 29413:
            case 29405:
            case -30528:
                InterfaceWriter.write(new QuestWriter(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 35_400);
                return true;
            case 29408:
            case -30128:
                InterfaceWriter.write(new AchievementInterface(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 35_000);
                return true;
		/*case 29410:
			player.interfaceManager.setSidebar(Config.QUEST_TAB, 37_300);
			break;*/
            case 29414:
            case -30525:
            case -30125:
                InterfaceWriter.write(new InformationWriter(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 29_400);
			/*player.dialogueFactory.sendOption("Drop display", () -> {
				player.dialogueFactory.onAction(() -> DropDisplay.open(player));
			},

					"Drop Simulator", () -> {
						player.dialogueFactory.onAction(() -> DropSimulator.open(player));
					}, "Activity Logger", () -> {
						player.dialogueFactory.onAction(player.activityLogger::open);
					}, "Game Records", () -> {
						player.dialogueFactory.onAction(() -> player.gameRecord.display(ActivityType.getFirst()));
					}, "Title Manager", () -> {
						player.dialogueFactory.onAction(() -> TitleManager.open(player));
					}).execute();*/
                return true;
        }
        return false;
    }
}
