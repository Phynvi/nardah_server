package plugin.command;

import java.util.*;

import io.battlerune.Config;
import io.battlerune.content.DropDisplay;
import io.battlerune.content.DropSimulator;
import io.battlerune.content.RoyaltyProgram;
import io.battlerune.content.Yell;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.activity.impl.battlerealm.BattleRealm;
import io.battlerune.content.activity.impl.kraken.KrakenActivity;
import io.battlerune.content.activity.impl.school.SchoolActivity;
import io.battlerune.content.clanchannel.channel.ClanChannelHandler;
import io.battlerune.content.emote.EmoteHandler;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.content.teleport.TeleportHandler;
import io.battlerune.content.triviabot.TriviaBot;
import io.battlerune.game.plugin.extension.CommandExtension;
import io.battlerune.game.service.DonationService;
import io.battlerune.game.service.VoteService;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.strategy.player.special.CombatSpecial;
import io.battlerune.game.world.entity.mob.UpdateFlag;
import io.battlerune.game.world.entity.mob.player.AccountSecurity;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.game.world.entity.mob.player.command.Command;
import io.battlerune.game.world.entity.mob.player.command.CommandParser;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.game.world.items.containers.ItemContainer;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.*;
import io.battlerune.util.MessageColor;
import io.battlerune.util.Utility;

public class PlayerCommandPlugin extends CommandExtension {

    @Override
    protected void register() {

        commands.add(new Command("commands", "command") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.send(new SendString("Commands List", 37103));
                player.send(new SendString("", 37107));

                // reset
                for (int i = 0; i < 50; i++) {
                    player.send(new SendString("", i + 37111));
                }

                final Set<String> set = new HashSet<>();
                int count = 0;

                for (CommandExtension extension : extensions) {

                    if (!extension.canAccess(player)) {
                        continue;
                    }

                    final String clazzName = extension.getClass().getSimpleName().replace("CommandPlugin", "");

                    player.send(new SendString(clazzName + " Commands", count + 37111));
                    count++;

                    for (Map.Entry<String, Command> entry : extension.multimap.entries()) {
                        if (count >= 100) {
                            break;
                        }

                        if (set.contains(entry.getKey())) {
                            continue;
                        }

                        final Command command = entry.getValue();

                        final StringBuilder builder = new StringBuilder();

                        for (int i = 0; i < command.getNames().length; i++) {
                            String name = command.getNames()[i];
                            builder.append("::");
                            builder.append(name);
                            if (i < command.getNames().length - 1) {
                                builder.append(", ");
                            }
                        }

                        player.send(new SendString(builder.toString(), count + 37111));

                        set.addAll(Arrays.asList(command.getNames()));

                        count++;
                    }
                }

                player.send(new SendScrollbar(37100, count * 22));
                player.interfaceManager.open(37100);

            }
        });
        
        commands.add(new Command("defaultbank") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.bank.clear();
                player.bank.addAll(Config.BANK_ITEMS);
                System.arraycopy(Config.TAB_AMOUNT, 0, player.bank.tabAmounts, 0, Config.TAB_AMOUNT.length);
                player.bank.shift();
                player.bank.open();
            }
        });
        
        commands.add(new Command("resetskills", "resetskill", "reset") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (int index = 0; index < Skill.SKILL_COUNT; index++) {
                    if (index < 7)
                        player.achievedSkills[index] = index == 3 ? 10 : 1;
                    player.skills.setMaxLevel(index, index == 3 ? 10 : 1);
                }
                player.skills.calculateCombat();
                player.updateFlags.add(UpdateFlag.APPEARANCE);
                player.send(new SendMessage("You have reset all your skills."));
            }
        });
        
        commands.add(new Command("find", "give") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String name = parser.nextLine();
                    ItemContainer container = new ItemContainer(400, ItemContainer.StackPolicy.ALWAYS);
                    int count = 0;
                    for (final ItemDefinition def : ItemDefinition.DEFINITIONS) {
                        if (def == null || def.getName() == null || def.isNoted())
                            continue;
                        if (def.getName().toLowerCase().trim().contains(name)) {
                            container.add(new Item(def.getId()));
                            count++;
                            if (count == 400)
                                break;
                        }
                    }
                    player.send(new SendString("Search: <col=FF5500>" + name, 37506));
                    player.send(new SendString(String.format("Found <col=FF5500>%s</col> item%s", count, count != 1 ? "s" : ""), 37507));
                    player.send(new SendScrollbar(37520, count / 8 * 52 + ((count % 8) == 0 ? 0 : 52)));
                    player.send(new SendItemOnInterface(37521, container.getItems()));
                    player.interfaceManager.open(37500);
                    player.send(new SendMessage(String.format("Found %s item%s containing the key '%s'.", count, count != 1 ? "s" : "", name)));
                }
            }
        });

        
        commands.add(new Command("master") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.skills.master();
                AchievementHandler.completeAll(player);
                EmoteHandler.unlockAll(player);
                player.send(new SendMessage("Your account is now maxed out.", MessageColor.BLUE));
            }
        });
        
        commands.add(new Command("gano") {
            @Override
            public void execute(Player player, CommandParser parser) {
      
              
            }
        });

        /*commands.add(new Command("ffa") {
            @Override
            public void execute(Player player, CommandParser parser) {
              if (FreeForAll.game.containsKey(player)) {
        			player.message("@or2@[Tournament] You are already in the game!");
        			return;
        	   }
               if(!FreeForAll.startTournament) {
            	  player.message("@or2@[Tournament] Please wait till the tournament has been activated!");
            	  return;
               }
               player.send(new SendFadeScreen("@or2@Joining The Tournament, Please Wait!", 1, 3));
               World.schedule(5, () -> {
               FreeForAll.joinLobby(player);
               });
            }
        });
        */
        commands.add(new Command("royaltyprogram", "royaltyrewards") {
            @Override
            public void execute(Player player, CommandParser parser) {
                RoyaltyProgram.open(player);
            }
        });
        
   
        
        
        commands.add(new Command("pouch") {
            @Override
            public void execute(Player player, CommandParser parser) {
            	//player.bankVault.value();
          
             
          if(player.getCombat().isUnderAttack() || player.getCombat().isAttacking() || Area.inWilderness(player) || Area.inWilderness(player) || Area.inWildernessCourse(player)
        		  || Area.inWildernessResource(player)) {
        	  player.message("You cannot access your Vault whilst in combat! or in the wilderness!");
          } else {
        	  
                player.send(new SendInputAmount("Enter the amount of coins you want to withdraw:", 10, input -> player.bankVault.withdraw(Long.parseLong(input))));
          }
            
            }
        });
        
   
        commands.add(new Command("vault", "vaultamount") {
            @Override
            public void execute(Player player, CommandParser parser) {
            	player.bankVault.value();
            }
        });

        commands.add(new Command("drops", "drop", "droplist", "droptable") {
            @Override
            public void execute(Player player, CommandParser parser) {
                //RoyaltyProgram.open(player);
               // DropSimulator.open(player);
                DropDisplay.open(player);
            }
        });
        commands.add(new Command("gen", "simulate", "simulator", "dropsim") {
            @Override
            public void execute(Player player, CommandParser parser) {
                //RoyaltyProgram.open(player);
                DropSimulator.open(player);
               // DropDisplay.open(player);
            }
        });
        commands.add(new Command("vote") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.send(new SendURL("https://www.nearreality.io/vote"));
            }
        });
        commands.add(new Command("train", "tran", "start") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.TRAIN_ZONE, 20, () -> {
                });
            }
        });
        commands.add(new Command("dice", "gamble", "dp") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DICE_ZONE, 20, () -> {
                    player.send(new SendMessage("@or2@Welcome to Gamble, " + player.getName() + "!"));
                    player.send(new SendMessage("@red@Make sure you record at ALL times"));
                    player.send(new SendMessage("@red@No refunds will be given out without any kind of video proof"));
                    ClanChannelHandler.connect(player, "Dice");
                    player.message("You've attempted to join Dice clan chat.");

                });
            }
        });
        
        commands.add(new Command("arena", "arenazone", "Arenaboss") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.ARENA_ZONE, 20, () -> {
           		// player.send(new SendFadeScreen("@or2@Entering The Arena Lair!", 1, 3));
                    player.send(new SendMessage("Welcome To The Arena Zone, " + player.getName() + "!"));
                });
            }
        });
        
        commands.add(new Command("edge") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.EDGEVILLE, 20, () -> {
                    player.send(new SendMessage("@or2@Welcome to the Edville PK area, " + player.getName() + "!"));
                    player.send(new SendMessage("@or2@Goodluck, you might need it."));
               });
                
            }
            
        });
        commands.add(new Command("duel") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DUEL, 20, () -> {
                    player.send(new SendMessage("@or2@Welcome to the Duel Arena, " + player.getName() + "!"));
                    player.send(new SendMessage("@or2@Here comes the money!"));
               });
                
            }
            
        });
        commands.add(new Command("barrows") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.BARROWS, 20, () -> {
                    player.send(new SendMessage("@or2@Welcome to Barrows, " + player.getName() + "!"));
                    player.send(new SendMessage("@red@Goodluck with grinding!"));
               });
                
            }
        });
        commands.add(new Command("kbd") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.KBD, 20, () -> {
                    player.send(new SendMessage("@or2@Welcome to KBD, Goodluck " + player.getName() + "!"));
                });
            }
        });
        
        commands.add(new Command("dusties") {
        	@Override
        	public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DUSTIES, 20, () -> {
                    player.send(new SendMessage("@or2@You have been teleported to the Dusties"));
                });
            }
        });
        
        
        commands.add(new Command("dks") {
        	@Override
        	public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DKS, 20, () -> {
                    player.send(new SendMessage("@or2@You have teleported to the Dagganoth Lair"));
                });
            }
        });
        
        commands.add(new Command("skull") {
        	@Override
        	public void execute(Player player, CommandParser parser) {
        		player.skulling.skull();
        	}
        });
        
  

        
        commands.add(new Command("tav") {
        	@Override
        	public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DUSTIES, 20, () -> {
                    player.send(new SendMessage("@or2@You have teleported to Taverly Dungeon"));
                });
            }
        });
        commands.add(new Command("bandos") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.BANDOS, 20, () -> {
                    player.send(new SendMessage("@or2@You have been teleported to the General Graardor boss."));
                });
            }
        });
        commands.add(new Command("zamorak") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.ZAMORAK, 20, () -> {
                	player.send(new SendMessage("@or2@You have been teleported to the K'ril Tsutsaroth boss."));
                });
            }
        });
    
    commands.add(new Command("armadyl") {
        @Override
        public void execute(Player player, CommandParser parser) {
            Teleportation.teleport(player, Config.ARMADYL, 20, () -> {
                player.send(new SendMessage("@or2@You have been teleported to the Kree'arra boss."));
            });
        }
    });
    commands.add(new Command("saradomin") {
        @Override
        public void execute(Player player, CommandParser parser) {
            Teleportation.teleport(player, Config.SARADOMIN, 20, () -> {
                player.send(new SendMessage("@or2@You have been teleported to the Commander Zilyana boss."));
            });
        }
    });
        commands.add(new Command("skill") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.SKILL_ZONE, 20, () -> {
                    player.send(new SendMessage("@or2@Welcome to the Skilling Zone, " + player.getName() + "!"));
                });
            }
        });

        commands.add(new Command("donate", "webstore") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.send(new SendURL("https://www.nearreality.io/store"));
            }
        });
        commands.add(new Command("Disord", "discord") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.send(new SendURL("https://www.nearreality.io/store"));
            }
        });

        commands.add(new Command("voted", "claimvote", "claimvotes") {
            @Override
            public void execute(Player player, CommandParser parser) {
                VoteService.claimReward(player);
            }
        });

        commands.add(new Command("donated") {
            @Override
            public void execute(Player player, CommandParser parser) {
                DonationService.claimDonation(player);
            }
        });
        commands.add(new Command("stuck") {
            @Override
            public void execute(Player player, CommandParser parser) {
            World.sendStaffMessage("[STUCK]" + player.getName() + " Is Stuck!");
            player.message("Staff team have been alerted!");
            }
        });

        commands.add(new Command("teleport") {
            @Override
            public void execute(Player player, CommandParser parser) {
                TeleportHandler.open(player);
            }
        });

        commands.add(new Command("staff", "staffonline", "staffon", "help") {
            @Override
            public void execute(Player player, CommandParser parser) {
                List<Player> staffs = World.getStaff();
                int length = staffs.size() < 10 ? 10 : staffs.size();

                player.send(new SendString("", 37113));
                player.send(new SendString("", 37107));
                player.send(new SendString("NR Online Staff", 37103));
                player.send(new SendScrollbar(37110, length * 20));

                for (int index = 0, string = 37112; index < length; index++, string++) {
                    if (index < staffs.size()) {
                        Player staff = staffs.get(index);
                        player.send(new SendString(PlayerRight.getCrown(staff) + " " + staff.getName() + "    (<col=255>" + staff.right.getName() + "</col>)", string));
                    } else {
                        player.send(new SendString("", string));
                    }
                }

                player.send(new SendItemOnInterface(37199));
                player.interfaceManager.open(37100);
                World.sendStaffMessage("[HELP]" + player.getName() + " Has Requested Help! Find out why!");
                
            }
        });

        commands.add(new Command("home") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.DEFAULT_POSITION);
            }
        });
        
        commands.add(new Command("wests") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.WESTS);
                player.send(new SendMessage("@or2@Goodluck, " + player.getName() + "!"));
                player.send(new SendMessage("@or2@You might need it.."));
            }
        });
        commands.add(new Command("easts") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.EASTS);
                player.send(new SendMessage("@or2@Goodluck, " + player.getName() + "!"));
                player.send(new SendMessage("@or2@You might need it.."));
            }
        });
        commands.add(new Command("mb") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.MB);
                player.send(new SendMessage("@or2@Welcome to Mage Bank, " + player.getName() + "!"));
            }
        });
        commands.add(new Command("gdz") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, Config.GDZ);
                player.send(new SendMessage("@or2@Goodluck, " + player.getName() + "!"));
                player.send(new SendMessage("@or2@You might need it.."));
            }
        });
        commands.add(new Command("task", "slayertask") {
            @Override
            public void execute(Player player, CommandParser parser) {              
            	player.message("You have " + player.slayer.getAmount() + " of " + player.slayer.getTask() +  "!");
       	
            }
        });
        
        commands.add(new Command("players") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.send(new SendMessage("There are currently " + World.getPlayerCount() + " players playing Near Reality!", MessageColor.RED));
            }
        });

        commands.add(new Command("empty", "emptyinventory", "clearinventory") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (Area.inWilderness(player) || player.getCombat().inCombat() || Area.inDuelArena(player) || player.playerAssistant.busy()) {
                    player.message("@or2@You can not clear your inventory at this current moment.");
                    return;
                }

                if (player.inventory.isEmpty()) {
                    player.message("@or2@You have nothing to empty!");
                    return;
                }

                String networth = Utility.formatDigits(player.playerAssistant.networth(player.inventory));
                player.dialogueFactory.sendStatement("Are you sure you want to clear your inventory? ",
                        "Container worth: <col=255>" + networth + " </col>coins.");
                player.dialogueFactory.sendOption("Yes", () -> {
                    player.inventory.clear(true);
                    player.dialogueFactory.clear();
                }, "Nevermind", () -> player.dialogueFactory.clear());
                player.dialogueFactory.execute();
            }
        });

        commands.add(new Command("yell") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    try {
                        StringBuilder message = new StringBuilder(parser.nextString());
                        while (parser.hasNext()) {
                            message.append(" ").append(parser.nextString());
                        }
                        Yell.yell(player, message.toString().trim());
                    } catch (final Exception e) {
                        player.send(new SendMessage("@or2@Invalid yell format, syntax: -messsage"));
                    }
                }
            }
        });

        commands.add(new Command("ans", "answer", "answers") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder answer = new StringBuilder();
                    while (parser.hasNext()) {
                        answer.append(parser.nextString()).append(" ");
                    }
                    TriviaBot.answer(player, answer.toString().trim());
                }
            }
        });

        commands.add(new Command("canceltask", "cleartask") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.message("Received canceltask command");
                if (!player.right.isDeveloper(player) || !player.right.isManagement(player))
                {
                    player.message("You must be a developer to use this!");
                    return;
                }

                if (parser.hasNext())
                {
                    System.out.println("Here");
                    String t = parser.nextString();

                    Player cancelMe = World.getPlayers().findFirst(o -> o.getName().toLowerCase().equals(t.toLowerCase())).get();

                    System.out.println(cancelMe.getName() + " should say " + t);
                    cancelMe.slayer.forceCancel();
                }
                else
                {
                    player.slayer.forceCancel();
                }
            }
        });

        commands.add(new Command("settask") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.message("Received settask command");
                if (!player.right.isDeveloper(player) || !player.right.isManagement(player))
                {
                    player.message("You must be a developer to use this!");
                    return;
                }

                player.slayer.setAmount(2);
            }
        });

        commands.add(new Command("battlerealm", "realm") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.message("Entering you to the BattleRealm");
                BattleRealm.enter(player);
            }
        });

        commands.add(new Command("brutal", "brutalMode") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.brutalMode = !player.brutalMode;
                player.message("BrutalMode status is now " + player.brutalMode);
            }
        });

        commands.add(new Command("instance") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.message("instance is " + player.instance);
            }
        });

        commands.add(new Command("rest") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int amount = 1000;
                if (parser.hasNext()) {
                    amount = parser.nextInt();
                }
                CombatSpecial.restore(player, amount);
            }
        });

        commands.add(new Command("region") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.message("Region: " + player.getRegion());
            }
        });

        commands.add(new Command("key", "pin") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (player.right != PlayerRight.HELPER && !PlayerRight.isManagement(player)) {
                    return;
                }

                if (parser.hasNext()) {
                    String key = parser.nextString();

                    if (!player.locking.locked()) {
                        player.send(new SendMessage("Your account is not locked, there is no need to enter a key.", MessageColor.RED));
                        
                        return;
                    }

                    if (AccountSecurity.AccountData.forName(player.getName()).isPresent()) {
                        AccountSecurity.AccountData account = AccountSecurity.AccountData.forName(player.getName()).get();

                        if (account.getKey().equalsIgnoreCase(key)) {
                            player.send(new SendMessage("You have entered the assigned security key for this account. Your session was", MessageColor.RED));
                            player.send(new SendMessage("activated.", MessageColor.RED));
                            player.move(Config.DEFAULT_POSITION);
                            player.locking.unlock();
                            player.setVisible(true);
                        } else {
                            player.send(new SendMessage("You have entered the wrong key! Information has been logged and all available staff", MessageColor.RED));
                            player.send(new SendMessage("gameMembers were notified.", MessageColor.RED));
                        }
                    }
                }
            }
        });

        commands.add(new Command("pos", "mypos", "coords") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.send(new SendMessage("Your location is: " + player.getPosition() + "."));
                System.out.println("Your location is: " + player.getPosition() + ".");
            }
        });

        commands.add(new Command("thousandhp") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.skills.modifyLevel(hp -> 1000, Skill.HITPOINTS, 0, 1000);
                World.sendMessage(player + " is cheating. Someone check him out.");
            }
        });

        commands.add(new Command("brain", "brainmini", "math") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, new Position(1770, 5088, 2), 20, () -> SchoolActivity.create(player));
                player.send(new SendMessage("You have teleported to the Brain Game!"));
            }
        });


    }

    @Override
    public boolean canAccess(Player player) {
        return true;
    }

}
