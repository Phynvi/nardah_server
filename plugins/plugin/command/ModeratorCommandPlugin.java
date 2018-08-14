package plugin.command;

import java.util.concurrent.TimeUnit;

import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.plugin.extension.CommandExtension;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.entity.actor.player.command.Command;
import io.battlerune.game.world.entity.actor.player.command.CommandParser;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendInputAmount;
import io.battlerune.net.packet.out.SendMessage;

public class ModeratorCommandPlugin extends CommandExtension {

    @Override
    protected void register() {
        commands.add(new Command("mute") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        if (PlayerRight.isPriviledged(other) && !PlayerRight.isDeveloper(player)) {
                            player.message("@or2@You do not have permission to mute this player!");
                            return;
                        }

                        DialogueFactory factory = player.dialogueFactory;
                        factory.sendOption("Mute by day", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this mute to last for?", 2, input -> {
                                other.punishment.mute(Integer.parseInt(input), TimeUnit.DAYS);
                                factory.clear();
                            })));
                        }, "Mute by hour", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this mute to last for?", 3, input -> {
                                other.punishment.mute(Integer.parseInt(input), TimeUnit.HOURS);
                                factory.clear();
                            })));
                        }, "Mute by minute", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this mute to last for?", 3, input -> {
                                other.punishment.mute(Integer.parseInt(input), TimeUnit.MINUTES);
                                factory.clear();
                            })));
                        }, "Mute forever", () -> {
                            factory.onAction(() -> {
                                other.punishment.mute(9999, TimeUnit.DAYS);
                                factory.clear();
                            });
                        }).execute();
                    });
                } else {
                    player.message("@or2@Invalid command use; ::mute Adam");
                }
            }
        });

        commands.add(new Command("unmute") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        other.punishment.unmute();
                        other.dialogueFactory.sendStatement("@or2@You have been unmuted!").execute();
                        player.message("@or2@unmute was complete");

                    });
                } else {
                    player.message("@or2@Invalid command use; ::unmute Adam");
                }
            }
        });

        commands.add(new Command("jail") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        if (PlayerRight.isPriviledged(other) && !PlayerRight.isDeveloper(player)) {
                            player.message("@or2@You do not have permission to jail this player!");
                            return;
                        }

                        DialogueFactory factory = player.dialogueFactory;
                        factory.sendOption("Jail by day", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this jail to last for?", 2, input -> {
                                other.punishment.jail(Integer.parseInt(input), TimeUnit.DAYS);
                                factory.clear();
                            })));
                        }, "Jail by hour", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this jail to last for?", 3, input -> {
                                other.punishment.jail(Integer.parseInt(input), TimeUnit.HOURS);
                                factory.clear();
                            })));
                        }, "Jail by minute", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this jail to last for?", 3, input -> {
                                other.punishment.jail(Integer.parseInt(input), TimeUnit.MINUTES);
                                factory.clear();
                            })));
                        }, "Jail forever", () -> {
                            factory.onAction(() -> {
                                other.punishment.jail(9999, TimeUnit.DAYS);
                                factory.clear();
                            });
                        }).execute();
                    });
                } else {
                    player.message("@or2@Invalid command use; ::jail daniel");
                }
            }
        });

        commands.add(new Command("unjail") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }
                    World.search(name.toString()).ifPresent(other -> {
                        other.punishment.unJail();
                        other.dialogueFactory.sendStatement("@or2@You have been unjailed!").execute();
                        player.message("@or2@unjail was complete");
                    });

                } else {
                    player.message("@or2@Invalid command use; ::unjail daniel");
                }
            }
        });

        commands.add(new Command("private", "nego") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, new Position(3108, 3161, 3));
                player.send(new SendMessage("@or2@Welcome to the Negotiation Zone, " + player.getName() + "."));
            }
        });
        commands.add(new Command("private2", "nego2") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, new Position(3208, 3218, 3));
                player.send(new SendMessage("@or2@Welcome to the Second Negotiation Zone, " + player.getName() + "."));
            }
        });
   
    commands.add(new Command("private3", "nego3") {
        @Override
        public void execute(Player player, CommandParser parser) {
            Teleportation.teleport(player, new Position(3203, 3472, 3));
            player.send(new SendMessage("@or2@Welcome to the Third Negotiation Zone, " + player.getName() + "."));
        }
    });
    
    commands.add(new Command("private4", "nego4") {
        @Override
        public void execute(Player player, CommandParser parser) {
            Teleportation.teleport(player, new Position(2726, 3491, 1));
            player.send(new SendMessage("@or2@Welcome to the Fourth Negotiation Zone, " + player.getName() + "."));
        }
    });
        commands.add(new Command("staffzone", "st") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, new Position(2602, 3874, 0));
                player.send(new SendMessage("@or2@Welcome to the staffzone, " + player.getName() + "."));
            }
        });

        commands.add(new Command("kick") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String name = parser.nextLine();
                    World.kickPlayer(p -> p.getName().equalsIgnoreCase(name));
                    player.send(new SendMessage("@or2@You have kicked " + name + "!"));
                }
            }
        });

        commands.add(new Command("teletome", "t2m", "tele2m") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {

                    final String name = parser.nextLine();

                    if (World.search(name).isPresent()) {
                        final Player target = World.search(name).get();
                        if (target.isBot) {
                            player.send(new SendMessage("@or2@You can't teleport bot to you!"));
                            return;
                        }
                        if (target.getCombat().inCombat() && !PlayerRight.isDeveloper(player)) {
                            player.message("@or2@That player is currently in combat!");
                            return;
                        }

                        target.move(player.getPosition());
                        target.instance = player.instance;
                    } else {
                        player.send(new SendMessage("@or2@The player '" + name + "' @or2@either doesn't exist, or is offline."));
                    }
                }
            }
        });

        commands.add(new Command("teleto", "t2") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {

                    final String name = parser.nextLine();

                    if (World.search(name).isPresent()) {
                        final Player target = World.search(name).get();
                        player.move(target.getPosition());
                        player.instance = target.instance;
                    } else {
                        player.send(new SendMessage("@or2@The player '" + name + "' @or2@either doesn't exist, or is offline."));
                    }
                }
            }
        });

    }

    @Override
    public boolean canAccess(Player player) {
        return PlayerRight.isManagement(player);
    }

}
