package plugin.click.button;


import com.nardah.Config;
import com.nardah.content.StarterKit;
import com.nardah.content.clanchannel.channel.ClanChannelHandler;
import com.nardah.content.dialogue.Expression;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.actor.player.persist.PlayerSerializer;
import com.nardah.game.world.entity.combat.strategy.player.special.CombatSpecial;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendRunEnergy;
import com.nardah.net.packet.out.SendSpecialAmount;
import com.nardah.util.MessageColor;
import com.nardah.util.Utility;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.nardah.content.StarterKit.refresh;


public class StarterKitButtonPlugin extends PluginContext {

    private static final int[] STARTER_BUTTON = {-8028, -8024, -8027, -8023, -8026, -8022, -8025, -8021, -8020};

    public static boolean isButton(int button) {
        for (int b : STARTER_BUTTON) {
            if (button == b)
                return true;
        }
        return false;
    }

    @Override
    protected boolean onClick(Player player, int button) {
        if (!player.interfaceManager.isInterfaceOpen(57500)) {
            return false;
        }
        switch (button) {
            case -8028:
            case -8024:
                refresh(player, StarterKit.KitData.NORMAL);
                return true;
            case -8027:
            case -8023:
                refresh(player, StarterKit.KitData.IRONMAN);
                return true;
            case -8026:
            case -8022:
                refresh(player, StarterKit.KitData.ULTIMATE_IRONMAN);
                return true;
            case -8025:
            case -8021:
                refresh(player, StarterKit.KitData.HARDCORE_IRONMAN);
                return true;
            case -8020:
                confirm(player);
                return true;
        }
        return false;
    }

    /** Handles the confirmation of the starter kit. */
    private static void confirm(Player player) {
        if (!player.buttonDelay.elapsed(1, TimeUnit.SECONDS)) {
            return;
        }

        StarterKit.KitData kit = player.attributes.get("STARTER_KEY", StarterKit.KitData.class);

        String name = Utility.formatEnum(kit.name());
        player.interfaceManager.close();
        player.newPlayer = false;
        player.needsStarter = false;
        //ClanChannelHandler.connect(player, "help");
        //player.clanTag = "help";
        //player.clanChannel.
        player.right = kit.getRight();
        CombatSpecial.restore(player, 100);
        player.send(new SendSpecialAmount());
        Arrays.stream(kit.getItems()).forEach(player.inventory::add);

        if (kit.getRight() != PlayerRight.PLAYER) {
            player.settings.acceptAid = false;
        }

        for (int index = 0; index < 6; index++) {
            player.achievedSkills[index] = index == 3 ? 10 : 1;
        }

        player.setVisible(true);
        player.locking.unlock();
        player.playerAssistant.setSidebar(false);
        player.runEnergy = 100;
        player.send(new SendRunEnergy());

       /* player.dialogueFactory.sendNpcChat(306, Expression.HAPPY, "As a new player, you can sign up for Brutal Mode.",
                "This mode enables open-world PvP and sets you at 3x less exp.",
                "Are you interested?").sendOption("That sounds like me!",
                () -> {
                    player.brutalMode = true;
                    player.expRate = 0.34;
                    player.message("That was a mistake! (Brutal Mode selected).");
                }, "What? No!",
                () -> {
                    //Do nothing!
                }).execute();*/

        World.sendMessage("Welcome to Nardah @blu@" + player.getName() + "");
        player.send(new SendMessage("You will now be playing as " + Utility.getAOrAn(name) + " @blu@" + name + "@bla@ player."));
        player.send(new SendMessage("@red@Tutorial Tip@bla@ You can train your combat ::train, or make money thieving or ::barrows"));
        player.send(new SendMessage("@red@Tutorial Tip@bla@ Pking is also a very good method to make money."));
        player.runEnergy =+ 100;
        player.buttonDelay.reset();
        ClanChannelHandler.connect(player, "help");
         
        
        if (Config.LIVE_SERVER /*&& !Config.FORUM_INTEGRATION*/) {
            player.bank.clear();
            
//            player.bank.addAll(Config.BANK_ITEMS);
            System.arraycopy(Config.TAB_AMOUNT, 0, player.bank.tabAmounts, 0, Config.TAB_AMOUNT.length);
            player.bank.shift();
//            player.skills.master();
//            AchievementHandler.completeAll(player);
//            EmoteHandler.unlockAll(player);
           player.send(new SendMessage("Your account is now maxed out.", MessageColor.BLUE));
//           player.inventory.add(13194 , 1);
           player.send(new SendMessage("Do ::defaultbank for max gear", MessageColor.BLUE));


        }

        // this is needed for logs so players are entered into the db upon first account creation
        PlayerSerializer.save(player);
    }
}
