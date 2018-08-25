package plugin.command;

import com.nardah.Config;
import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.emote.EmoteHandler;
import com.nardah.content.skill.impl.magic.Spellbook;
import com.nardah.game.plugin.extension.CommandExtension;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.actor.player.command.Command;
import com.nardah.game.world.entity.actor.player.command.CommandParser;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.game.world.items.containers.ItemContainer;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendItemOnInterface;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendScrollbar;
import com.nardah.net.packet.out.SendString;
import com.nardah.util.MessageColor;

public class AdminCommandPlugin extends CommandExtension {

    @Override
    public void register() {
        commands.add(new Command("toggle") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String string = parser.nextString();

                    switch(string.toLowerCase()) {

                        case "hiscores":
                        case "highscores":
                        case "hs":
                            Config.highscoresEnabled = !Config.highscoresEnabled;
                            player.send(new SendMessage(String.format("Highscores are now %s", Config.highscoresEnabled ? "on" : "off")));
                            break;
                    }
                }
            }
        });
/*/
        commands.add(new Command("demote") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        if (PlayerRight.isDeveloper(other)) {
                            return;
                        }
                        other.punishment.unmute();
                        other.dialogueFactory.sendStatement("You have been demoted!").execute();
                        player.message("demote was complete");

                    });
                } else {
                    player.message("Invalid command use; ::demote daniel");
                }
            }
        });
/*/
/*/
        commands.add(new Command("promote") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        if (PlayerRight.isPriviledged(other) && !PlayerRight.isDeveloper(player)) {
                            player.message("You do not have permission to mute this player!");
                            return;
                        }

                        DialogueFactory factory = player.dialogueFactory;
                        factory.sendOption("Helper", () -> {
                            other.right = PlayerRight.HELPER;
                            player.message("You have promoted " + other.getName() + ": " + other.right.getName());
                            other.message("You have been promoted: " + other.right.getName());
                        }, "Moderator", () -> {
                            other.right = PlayerRight.MODERATOR;
                            player.message("You have promoted " + other.getName() + ": " + other.right.getName());
                            other.message("You have been promoted: " + other.right.getName());
                        }, "Admin", () -> {
                            other.right = PlayerRight.ADMINISTRATOR;
                            player.message("You have promoted " + other.getName() + ": " + other.right.getName());
                            other.message("You have been promoted: " + other.right.getName());
                        }).execute();
                    });
                } else {
                    player.message("Invalid command use; ::promote daniel");
                }
            }
        });
/*/

        commands.add(new Command("mastermine") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.masterMiner.open();
            }
        });


        commands.add(new Command("save", "saveworld", "savegame") {
            @Override
            public void execute(Player player, CommandParser parser) {
                World.save();
                player.send(new SendMessage("All data has been successfully saved."));
            }
        });

        commands.add(new Command("bank") {
            @Override
            public void execute(Player player, CommandParser parser) {
            	if(Area.inWilderness(player)) {
            		player.message("You cannot open the bank in the wilderness.");
            	} else {
                player.bank.open();
            	}
            }
        });

        commands.add(new Command("move") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext(3)) {
                    int x = parser.nextInt();
                    int y = parser.nextInt();
                    int z = parser.nextInt();
                    player.move(player.getPosition().transform(x, y, z));
                } else if (parser.hasNext(2)) {
                    int x = parser.nextInt();
                    int y = parser.nextInt();
                    int z = player.getHeight();
                    player.move(player.getPosition().transform(x, y, z));
                } else return;

                if (player.debug) {
                    player.send(new SendMessage("You have teleported to the coordinates: " + player.getPosition(), MessageColor.BLUE));
                }
            }
        });

        commands.add(new Command("tele") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext(3)) {
                    int x = parser.nextInt();
                    int y = parser.nextInt();
                    int z = parser.nextInt();
                    player.move(new Position(x, y, z));
                } else if (parser.hasNext(2)) {
                    int x = parser.nextInt();
                    int y = parser.nextInt();
                    int z = player.getHeight();
                    player.move(new Position(x, y, z));
                } else return;
                if (player.debug) {
                    player.send(new SendMessage("You have teleported to the coordinates: " + player.getPosition(), MessageColor.BLUE));
                }
            }
        });

        commands.add(new Command("lvl", "setlvl", "setskill", "setlevel") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int skill = parser.nextInt();
                int level = parser.nextInt();
                if (skill <= Skill.SKILL_COUNT) {
                    player.skills.setMaxLevel(skill, level);
                    player.skills.setCombatLevel();
                    player.send(new SendMessage("Your " + Skill.getName(skill) + " level has been set to " + level + "."));
                }
            }
        });

        commands.add(new Command("spellbook") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    String spellbook = parser.nextString();
                    switch (spellbook.toUpperCase()) {
                        case "LUNAR":
                            player.spellbook = Spellbook.LUNAR;
                            break;
                        case "MODERN":
                            player.spellbook = Spellbook.MODERN;
                            break;
                        case "ANCIENT":
                            player.spellbook = Spellbook.ANCIENT;
                            break;
                    }
                    player.interfaceManager.setSidebar(Config.MAGIC_TAB, player.spellbook.getInterfaceId());
                }
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

        commands.add(new Command("master") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.skills.master();
                AchievementHandler.completeAll(player);
                EmoteHandler.unlockAll(player);
                player.send(new SendMessage("Your account is now maxed out.", MessageColor.BLUE));
            }
        });

        commands.add(new Command("item", "pickup") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    int id = parser.nextInt();
                    int amount = 1;
                    if (parser.hasNext()) {
                        amount = Integer.parseInt(parser.nextString().toLowerCase().replace("k", "000").replace("m", "000000").replace("b", "000000000"));
                    }

                    final ItemDefinition def = ItemDefinition.get(id);

                   /* if (def == null || def.getName() == null) {
                        return;
                    }

                    if (def.getName().equalsIgnoreCase("null")) {
                        return;
                    }*/

                    player.inventory.add(id, amount);
                }
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
    }

    @Override
    public boolean canAccess(Player player) {
        return PlayerRight.isPriviledged(player);
    }
}
