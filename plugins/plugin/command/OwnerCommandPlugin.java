package plugin.command;

import com.nardah.Config;
import com.nardah.content.activity.randomevent.impl.MimeEvent;
import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.content.pet.PetData;
import com.nardah.content.pet.Pets;
import com.nardah.game.plugin.extension.CommandExtension;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.actor.player.command.Command;
import com.nardah.game.world.entity.actor.player.command.CommandParser;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendInputAmount;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;
import com.nardah.util.parser.old.defs.NpcDefinition;

public class OwnerCommandPlugin extends CommandExtension {

    @Override
    protected void register() {


        commands.add(new Command("8wayswitch", "8ws") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (int index = 0; index < 8; index++) {
                    if (player.inventory.get(index) != null && player.inventory.get(index).isEquipable()) {
                        player.equipment.equip(index);
                    }
                }
            }
        });

        commands.add(new Command("broadcast") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String message = parser.nextLine();
                    World.sendBroadcast(1, message, false);
                }
            }
        });

        commands.add(new Command("masspets") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (PetData pet : PetData.values()) {
                    Mob mob = new Mob(pet.getNpc(), player.getPosition());
                    mob.register();
                    World.schedule(Pets.abandon(mob));
                }
            }
        });

        commands.add(new Command("resetplayer") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        DialogueFactory factory = player.dialogueFactory;
                        factory.sendOption("Skills", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("Enter the skill id", 1, input -> {
                                int skill = Integer.parseInt(input);
                                if (skill > -1 && skill < Skill.SKILL_COUNT) {
                                    other.skills.setMaxLevel(skill, skill == 3 ? 10 : 1);
                                    other.skills.setCombatLevel();
                                    other.updateFlags.add(UpdateFlag.APPEARANCE);
                                    player.message(other.getName() + "'s " + Skill.getName(skill) + " was reset");
                                }
                            })));
                        }, "Inventory", () -> {
                            other.inventory.clear();
                            player.message(other.getName() + "'s inventory was cleared");
                            factory.clear();
                        }, "Equipment", () -> {
                            other.equipment.clear();
                            player.message(other.getName() + "'s equipment was cleared");
                            factory.clear();
                        }, "Bank", () -> {
                            other.bank.clear();
                            player.message(other.getName() + "'s bank was cleared");
                            factory.clear();
                        });
                        player.dialogueFactory.execute();
                    });
                } else {
                    player.message("Invalid command use; ::resetplayer daniel");
                }
            }
        });

        commands.add(new Command("wog") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Config.DOUBLE_EXPERIENCE = !Config.DOUBLE_EXPERIENCE;
                World.sendMessage("<col=CF2192>NR: </col>Double experience is now " + (Config.DOUBLE_EXPERIENCE ? "activated" : "de-activated") + ".");
            }
        });

        commands.add(new Command("wp", "wildplayers") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (Player other : World.getPlayers()) {
                    if (other != null && Area.inWilderness(other)) {
                        int level = other.wilderness;
                        player.message("<col=255>" + other.getName() + " (level " + other.skills.getCombatLevel() + ") is in wilderness level " + level + ".");
                    }
                }
            }
        });

        commands.add(new Command("setpt", "setplaytime") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        player.send(new SendInputAmount("Enter the play time", 8, input -> {
                            other.playTime = Integer.parseInt(input);
                        }));
                    });

                } else {
                    player.message("Invalid command use; ::setpt daniel");
                }
            }
        });

        commands.add(new Command("giveitem", "gi") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        player.send(new SendInputAmount("Enter the itemId", 5, input -> {
                            other.inventory.add(new Item(Integer.parseInt(input), 1));
                        }));
                    });

                } else {
                    player.message("Invalid command use; ::giveitem daniel");
                }
            }
        });

        commands.add(new Command("giveexp", "giveexperience") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }
                    World.search(name.toString()).ifPresent(other -> {
                        player.send(new SendInputAmount("Enter the skillid", 5, input -> {
                            other.skills.addExperience(Integer.parseInt(input), Utility.random(500000));
                        }));
                    });

                } else {
                    player.message("Invalid command use; ::kill daniel");
                }
            }
        });

        commands.add(new Command("kill") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }
                    World.search(name.toString()).ifPresent(other -> other.damage(new Hit(other.getCurrentHealth())));

                } else {
                    player.message("Invalid command use; ::kill daniel");
                }
            }
        });

        commands.add(new Command("removeslayertask", "removetask") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }
                    World.search(name.toString()).ifPresent(other -> {
                        other.slayer.setTask(null);
                        other.slayer.setAmount(0);
                        other.message("Your slayer task was reset.");
                    });

                } else {
                    player.message("Invalid command use; ::removetask daniel");
                }
            }
        });

        commands.add(new Command("randomevent") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }
                    World.search(name.toString()).ifPresent(MimeEvent::create);

                } else {
                    player.message("Invalid command use; ::randomevent daniel");
                }
            }
        });

        commands.add(new Command("alltome") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Position position = player.getPosition().copy();
                World.getPlayers().forEach(players -> {
                    if (!players.equals(player)) {
                        players.move(position);
                        players.send(new SendMessage("You have been mass teleported."));
                    }
                });
            }
        });

        commands.add(new Command("setrank") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        DialogueFactory factory = player.dialogueFactory;
                        factory.sendOption("Ironman", () -> {
                            other.right = PlayerRight.IRONMAN;
                            player.message("You have promoted " + other.getName() + ": " + other.right.getName());
                            other.message("You have been promoted: " + other.right.getName());
                        }, "Ultimate Ironman", () -> {
                            other.right = PlayerRight.ULTIMATE_IRONMAN;
                            player.message("You have promoted " + other.getName() + ": " + other.right.getName());
                            other.message("You have been promoted: " + other.right.getName());
                        }, "Hardcore Ironman", () -> {
                            other.right = PlayerRight.HARDCORE_IRONMAN;
                            player.message("You have promoted " + other.getName() + ": " + other.right.getName());
                            other.message("You have been promoted: " + other.right.getName());
                        }).execute();
                    });
                } else {
                    player.message("Invalid command use; ::setrank daniel");
                }
            }
        });

        commands.add(new Command("fight") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext(2)) {
                    int one = parser.nextInt();
                    int two = parser.nextInt();
                    Position start = player.getPosition().copy();
                    if (NpcDefinition.get(one) == null || NpcDefinition.get(two) == null) {
                        player.send(new SendMessage("Definition for one or more of the monsters were null."));
                        return;
                    }
                    Mob boss1 = new Mob(one, new Position(start.getX() - 3, start.getY() + 3));
                    Mob boss2 = new Mob(two, new Position(start.getX() + 3, start.getY() + 3));
                    boss1.register();
                    boss2.register();
                    boss1.walk = false;
                    boss2.walk = false;
                    boss1.definition.setAggressive(false);
                    boss2.definition.setAggressive(false);
                    boss2.attackNpc = true;
                    boss1.attackNpc = true;
                    boss1.definition.setRespawnTime(-1);
                    boss2.definition.setRespawnTime(-1);
                    World.schedule(new Task(1) {
                        int count = 0;

                        @Override
                        protected void execute() {
                            if (count == 0) {
                                boss1.interact(boss2);
                                boss1.speak("I will fight for you, " + player.getName() + "!");
                            } else if (count == 1) {
                                boss2.interact(boss1);
                                boss2.speak("But I will win for you, " + player.getName() + "!");
                            } else if (count == 3) {
                                boss1.speak("3");
                                boss2.speak("3");
                            } else if (count == 4) {
                                boss1.speak("2");
                                boss2.speak("2");
                            } else if (count == 5) {
                                boss1.speak("1");
                                boss2.speak("1");
                            } else if (count == 6) {
                                boss1.speak("Good luck " + boss2.getName() + "!");
                                boss2.speak("Good luck " + boss1.getName() + "!");
                            } else if (count > 7) {
                                cancel();
                            }
                            count++;
                        }

                        @Override
                        protected void onCancel(boolean logout) {
                            boss1.getCombat().attack(boss2);
                            boss2.getCombat().attack(boss1);
                        }
                    });
                } else {
                    player.send(new SendMessage("Invalid command - ::fight 3080 3080"));
                }
            }
        });
    }

    @Override
    public boolean canAccess(Player player) {
        return player.right == PlayerRight.OWNER;
    }

}
