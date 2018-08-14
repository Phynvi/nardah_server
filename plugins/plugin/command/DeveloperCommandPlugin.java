package plugin.command;

import io.battlerune.Config;
import io.battlerune.content.activity.impl.cerberus.CerberusActivity;
import io.battlerune.content.activity.impl.vorkath.VorkathActivity;
import io.battlerune.content.activity.randomevent.impl.MimeEvent;
import io.battlerune.content.bot.PlayerBot;
import io.battlerune.content.bot.objective.BotObjective;
import io.battlerune.content.clanchannel.ClanRepository;
import io.battlerune.content.clanchannel.channel.ClanChannel;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.pet.PetData;
import io.battlerune.content.pet.Pets;
import io.battlerune.content.skill.SkillRepository;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.content.store.Store;
import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.plugin.extension.CommandExtension;
import io.battlerune.game.task.Task;
import io.battlerune.game.task.impl.ObjectPlacementEvent;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.listener.CombatListenerManager;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.Hitsplat;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.boss.arena.ArenaUtility;
import io.battlerune.game.world.entity.combat.strategy.player.special.CombatSpecial;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.UpdateFlag;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.npc.definition.NpcDefinition;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.entity.actor.player.command.Command;
import io.battlerune.game.world.entity.actor.player.command.CommandParser;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.object.ObjectDefinition;
import io.battlerune.game.world.object.ObjectDirection;
import io.battlerune.game.world.object.ObjectType;
import io.battlerune.game.world.pathfinding.TraversalMap;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendBanner;
import io.battlerune.net.packet.out.SendInputAmount;
import io.battlerune.net.packet.out.SendInputMessage;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;
import io.battlerune.util.RandomUtils;
import io.battlerune.util.Utility;
import io.battlerune.util.parser.impl.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static io.battlerune.game.world.entity.combat.attack.FormulaFactory.*;

public class DeveloperCommandPlugin extends CommandExtension {

    @Override
    protected void register() {
        commands.add(new Command("rngbank") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.bank.clear();
                while (player.bank.size() < player.bank.capacity()) {
                    Item item = new Item(RandomUtils.inclusive(1, 20_000));
                    if (!item.getName().contains("null"))
                        player.bank.depositFromNothing(item.unnoted(), RandomUtils.inclusive(0, 9));
                }

                System.out.println(Arrays.toString(player.bank.tabAmounts));
            }
        });


        commands.add(new Command("longbroadcast") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String message = parser.nextLine();
                    World.sendBroadcast(9999, message, false);
                    //player.message("broadcast time" + time + "!");
                    World.sendStaffMessage("Plebs");
                }
            }
        });

        commands.add(new Command("cerb") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, new Position(1240, 1226, 0), 20, () -> CerberusActivity.create(player));
            }
        });


        commands.add(new Command("pnpc") {
            @Override

            public void execute(Player player, CommandParser parser) {
                final String message = "That player was not valid, please re-select a player.";

                Player other = player.attributes.get("PLAYER_PANEL_KEY", Player.class);
                player.send(new SendInputMessage("Enter id", 10, input -> {
                    if (other != null) {
                        other.playerAssistant.transform(Integer.parseInt(input));
                    }
                    player.send(new SendMessage(other == null ? message : "You have turned " + other.getName() + " into " + NpcDefinition.get(Integer.parseInt(input)).getName() + ".", MessageColor.DARK_BLUE));
                }));

            }
        });

        commands.add(new Command("giveall") {
            @Override
            public void execute(Player player, CommandParser parser) {

                int itemID = parser.nextInt();
                int value = 1;
                if (parser.hasNext()) {
                    value = parser.nextInt();
                    for (Player players : World.getPlayers()) {
                        if (players != null) {
                            players.inventory.add(new Item(itemID, value));
                            players.send(new SendMessage(player.getUsername() + "You have all received a random Item From Adam!"));
                            players.send(new SendMessage(player.getUsername() + "@red@This is a token of appreciation from Adam himself!"));

                        }
                    }
                }

            }
        });

        commands.add(new Command("ban") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }

                    World.search(name.toString()).ifPresent(other -> {
                        if (PlayerRight.isPriviledged(other) && !PlayerRight.isDeveloper(player)) {
                            player.message("You do not have permission to ban this player!");
                            return;
                        }

                        DialogueFactory factory = player.dialogueFactory;
                        factory.sendOption("Ban by day", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this ban to last for?", 2, input ->
                            {
                                other.punishment.banUser(Integer.parseInt(input), TimeUnit.DAYS);
                                factory.clear();
                            })));
                        }, "Ban by hour", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this ban to last for?", 3, input -> {
                                other.punishment.banUser(Integer.parseInt(input), TimeUnit.HOURS);
                                factory.clear();
                            })));
                        }, "Ban by minute", () -> {
                            factory.onAction(() -> player.send(new SendInputAmount("How long do you want this ban to last for?", 3, input -> {
                                other.punishment.banUser(Integer.parseInt(input), TimeUnit.MINUTES);
                                factory.clear();
                            })));
                        }, "Ban forever", () -> {
                            factory.onAction(() -> {
                                other.punishment.banUser(9999, TimeUnit.DAYS);
                                factory.clear();
                            });
                        }).execute();
                    });
                } else {
                    player.message("Invalid command use; ::ban anomaly");
                }
            }
        });

        commands.add(new Command("unban") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    StringBuilder name = new StringBuilder(parser.nextString());

                    while (parser.hasNext()) {
                        name.append(" ").append(parser.nextString());
                    }
                    World.search(name.toString()).ifPresent(other -> {
                        other.punishment.unBan();
                        player.message("@red@Player has been unbanned");
                    });

                } else {
                    player.message("@redInvalid command use; ::unjail anomaly");
                }
            }
        });

        commands.add(new Command("gearup") {
            @Override
            public void execute(Player player, CommandParser parser) {
//                FreeForAll.generateGear();
//                FreeForAll.startTournament = true;
//                player.message("Tournament started with "+FreeForAll.getCurrentGear()+" gear!");
            }
        });
        commands.add(new Command("displaytest") {//btw u are retarded :)
            @Override
            public void execute(Player player, CommandParser parser) {
                // PlayerGuide guide = new PlayerGuide(player);
                //  guideplayer2.open(player);
            }
        });

        commands.add(new Command("vorkath") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Teleportation.teleport(player, new Position(2272, 4055, 0), 20, () -> VorkathActivity.create(player));
            }
        });

        commands.add(new Command("spawnglod") {
                         @Override
                         public void execute(Player player, CommandParser parser) {
                             //  World.schedule(2, () -> Arena.register());
                             ArenaUtility.generateSpawn();

                         }
                     }
        );
        commands.add(new Command("reloadclans") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Path path = Paths.get("./data/content/clan/");
                File[] files = path.toFile().listFiles();

                if (files == null) {
                    player.message("No clan files were found.");
                    return;
                }

                int failed = 0;
                for (File file : files) {
                    String owner = file.getName().replaceAll(".json", "").toLowerCase().trim();
                    if (ClanRepository.getChannel(owner) == null) {
                        player.message("<col=FF0000>Reloading " + file.getName());
                        ClanChannel.load(owner);
                        failed++;
                    }
                }
                player.message("<col=FF0000>Reloaded " + failed + "</col> clans.");
            }
        });

        commands.add(new Command("clansloaded") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Path path = Paths.get("./data/content/clan/");
                File[] files = path.toFile().listFiles();

                if (files == null) {
                    player.message("No clan files were found.");
                    return;
                }

                int loaded = 0;
                int failed = 0;
                int total = 0;

                for (File file : files) {
                    if (ClanRepository.getChannel(file.getName().replaceAll(".json", "").toLowerCase().trim()) != null) {
                        loaded++;
                    } else {
                        player.message("<col=FF0000>Failed to load " + file.getName());
                        failed++;
                    }
                    total++;
                }

                player.message("Loaded <col=FF0000>" + loaded + "</col> out of <col=FF0000>" + total + "</col>, failed to load <col=FF0000>" + failed);
            }
        });

        commands.add(new Command("leet", "l33t") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (int index = 0; index < 6; index++) {
                    player.skills.setLevel(index, 99999);
                }
            }
        });

        commands.add(new Command("up") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.move(player.getPosition().transform(0, 0, 1));
            }
        });

        commands.add(new Command("down") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.move(player.getPosition().transform(0, 0, -1));
            }
        });


        commands.add(new Command("skill", "lvl", "level") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext(2)) {
                    player.skills.setMaxLevel(parser.nextInt(), parser.nextInt());
                }
            }
        });

        commands.add(new Command("myregion") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.send(new SendMessage(String.format("region=%d", player.getRegion().getId())));
            }
        });

        commands.add(new Command("refreshffa") {
            @Override
            public void execute(Player player, CommandParser parser) {
//                FreeForAll.gameStarted = false;
//                FreeForAll.startTournament = false;
//                FreeForAll.game.clear();
//                FreeForAll.WAIT_TIMER += 80;
//                FreeForAll.GAME_TIMER += 900;
//                player.message("Reseted the ffa!");
            }
        });

        commands.add(new Command("botcount", "botsize") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int size = World.getBotCount();
                int amount = 5000;
                player.send(new SendBanner("There are currently:", size + amount + " bots online mother fucker!", 0X770077));
            }
        });

        commands.add(new Command("clearbots", "clearbot") {
            @Override
            public void execute(Player player, CommandParser parser) {
                World.getPlayers().forEach(it -> {
                    if (it.isBot) {
                        it.unregister();
                    }
                });
            }
        });

        commands.add(new Command("bot") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int count = parser.nextInt();
                for (int i = 1; i <= count; i++) {
                    PlayerBot bot = new PlayerBot("New Bot " + i);
                    bot.register();
                    BotObjective.WALK_TO_BANK.init(bot);
                }
            }
        });

        commands.add(new Command("resetbank") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.bank.clear();
                player.bank.refresh();
                player.message("You have refreshed your bank.");
            }
        });

        commands.add(new Command("spec") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int amount = 100;
                if (parser.hasNext()) {
                    amount = parser.nextInt();
                }
                CombatSpecial.restore(player, amount);
            }
        });

        commands.add(new Command("copyme") {
            @Override
            public void execute(Player player, CommandParser parser) {
                final String name = parser.nextLine();
                Optional<Player> other = World.search(name);
                other.ifPresent(player.playerAssistant::copy);
            }
        });

        commands.add(new Command("health") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.skills.modifyLevel(hp -> 10_000, Skill.HITPOINTS, 0, 10_000);
                player.skills.modifyLevel(hp -> 10_000, Skill.PRAYER, 0, 10_000);
                player.skills.refresh(Skill.HITPOINTS);
                player.skills.refresh(Skill.PRAYER);
            }
        });

        commands.add(new Command("lnpcs") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int size = parser.nextInt();
                int diameter = size * size;
                for (int index = 0; index < diameter; index++) {
                    Position position = player.getPosition().transform(index % size, index / size, 0);
                    Npc man = new Npc(3080, position);
                    man.skills.setLevel(3, 450);
                    man.register();
                }
//                for (Npc npc : spawned) {
//                    if (npc == middle) continue;
//                    if (npc.getCombat().inCombat()) break;
//                    npc.getCombat().attack(middle);
//                }
            }
        });


        commands.add(new Command("snpc", "searchnpc") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String name = parser.nextLine();

                    for (int i = 0; i < NpcDefinition.DEFINITIONS.length; i++) {
                        NpcDefinition def = NpcDefinition.get(i);
                        if (def == null) {
                            continue;
                        }

                        String npcName = def.getName();

                        if (npcName == null) {
                            continue;
                        }

                        if (npcName.contains(name)) {
                            player.send(new SendMessage(String.format("%s=%d", npcName, i)));
                        }

                    }
                }
            }
        });

        commands.add(new Command("update") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    int time = parser.nextInt();
                    World.update(time, false);
                }
            }
        });

        commands.add(new Command("debug") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String type = parser.nextString().toLowerCase();

                    switch (type) {

                        case "s":
                        case "server":
                            Config.SERVER_DEBUG = !Config.SERVER_DEBUG;
                            player.send(new SendMessage(String.format("server debug=%s", Config.SERVER_DEBUG ? "on" : "off"), MessageColor.DEVELOPER));
                            break;

                        case "p":
                        case "player":
                            player.debug = !player.debug;
                            player.send(new SendMessage(String.format("player debug=%s", player.debug ? "on" : "off"), MessageColor.DEVELOPER));
                            break;
                    }
                }
            }
        });

        commands.add(new Command("dumpequip") {
            @Override
            public void execute(Player player, CommandParser parser) {
                System.out.print("");
                for (final Item item : player.equipment.toArray()) {
                    if (item == null) {
                        continue;
                    }
                    if (item.getAmount() > 1) {
                        System.out.printf("new Item(%s, %s), ", item.getId(), item.getAmount());
                    } else {
                        System.out.printf("new Item(%s), ", item.getId());
                    }
                }
                System.out.print("");
                System.out.println();
            }
        });

        commands.add(new Command("dumpinv") {
            @Override
            public void execute(Player player, CommandParser parser) {
                boolean justId = parser.hasNext();

                System.out.print("{ ");
                for (final Item item : player.inventory) {
                    if (item == null) {
                        continue;
                    }

                    if (justId) {
                        System.out.print(item.getId() + ", ");
                    } else {
                        if (item.getAmount() > 1) {
                            System.out.printf("new Item(%s, %s), ", item.getId(), item.getAmount());
                        } else {
                            System.out.printf("new Item(%s), ", item.getId());
                        }
                    }
                }

                System.out.print(" }");
                System.out.println();
            }
        });

        commands.add(new Command("dumpinv2") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (final Item item : player.inventory.toArray()) {
                    if (item != null) {
                        int price = item.getSellValue();
                        double bonus = 11;
                        System.out.println("         {");
                        System.out.println("            \"id\":" + item.getId() + ",");
                        System.out.println("            \"amount\":100");
                        System.out.println("         },");
                    }
//                    System.out.println();
                }

            }
        });

        commands.add(new Command("die") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.damage(new Hit(player.getCurrentHealth(), Hitsplat.NORMAL));
            }
        });
        commands.add(new Command("god") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.skills.setLevel(0, 1500);
                player.skills.setLevel(1, 1500);
                player.skills.setLevel(2, 1500);
                player.skills.setLevel(3, 1500);
                player.skills.setLevel(4, 1500);
            }
        });

        commands.add(new Command("test1") {
            @Override
            public void execute(Player player, CommandParser parser) {

                player.equipment.clear();
                player.message("clurd.");

            }
        });


        commands.add(new Command("face") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    Direction direction = Direction.valueOf(parser.nextString().toUpperCase());
                    player.resetFace();
                    player.face(direction);
                    player.send(new SendMessage("You are now facing direction: " + direction.name().toLowerCase() + "."));
                }
            }
        });

        commands.add(new Command("store", "shop") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    final String name = parser.nextLine();
                    Store.STORES.get(name).open(player);
                }
            }
        });

        commands.add(new Command("playnpc") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.playerAssistant.transform(parser.nextInt());
            }
        });

        commands.add(new Command("region") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Set<Direction> directions = EnumSet.noneOf(Direction.class);
                boolean projectiles = false;
                boolean exact = false;

                while (parser.hasNext()) {
                    switch (parser.nextString().toLowerCase()) {
                        case "n":
                            directions.add(Direction.NORTH);
                            break;
                        case "e":
                            directions.add(Direction.EAST);
                            break;
                        case "s":
                            directions.add(Direction.SOUTH);
                            break;
                        case "w":
                            directions.add(Direction.WEST);
                            break;
                        case "ne":
                            directions.add(Direction.NORTH_EAST);
                            break;
                        case "nw":
                            directions.add(Direction.NORTH_WEST);
                            break;
                        case "se":
                            directions.add(Direction.SOUTH_EAST);
                            break;
                        case "sw":
                            directions.add(Direction.SOUTH_WEST);
                            break;
                        case "all":
                            directions.addAll(Arrays.asList(Direction.values()));
                            break;
                        case "p":
                            projectiles = true;
                            break;
                        case "ex":
                            exact = true;
                            break;
                    }
                }

                for (int y = -20; y < 20; y++) {
                    for (int x = -40; x < 40; x++) {
                        Position position = new Position(player.getX() + x, player.getY() - y, player.getHeight());
                        boolean clear = !exact;
                        for (Direction direction : directions) {
                            if (exact) {
                                if (TraversalMap.isTraversable(position, direction, projectiles)) {
                                    clear = true;
                                }
                            } else {
                                if (!TraversalMap.isTraversable(position, direction, projectiles)) {
                                    clear = false;
                                }
                            }
                        }

                        if (x == 0 && y == 0) {
                            System.out.print(" +");
                        } else if (!clear) {
                            System.out.print(" *");
                        } else {
                            System.out.print("  ");
                        }
                    }
                    System.out.println();
                }
                System.out.println();
            }
        });

        commands.add(new Command("clip") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext(2)) {
                    int dx = parser.nextInt();
                    int dy = parser.nextInt();

                    if (parser.hasNext())
                        player.movement.dijkstraPath(player.getPosition().transform(dx, dy));
                    else
                        player.movement.simplePath(player.getPosition().transform(dx, dy));
                }
            }
        });

        commands.add(new Command("ro", "regionobjects") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (int y = -20; y < 20; y++) {
                    for (int x = -40; x < 40; x++) {
                        Position position = new Position(player.getX() + x, player.getY() - y, player.getHeight());

                        if (x == 0 && y == 0) {
                            System.out.print(" +");
                        } else if (position.getRegion().containsObject(position)) {
                            System.out.print(" *");
                        } else {
                            System.out.print("  ");
                        }
                    }
                    System.out.println();
                }
            }
        });

        commands.add(new Command("mobs") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (int y = -20; y < 20; y++) {
                    for (int x = -40; x < 40; x++) {
                        Position position = new Position(player.getX() + x, player.getY() - y, player.getHeight());

                        if (x == 0 && y == 0) {
                            System.out.print(" +");
                        } else if (position.getRegion().getTile(position.getHeight(), position.getX() & 0x3F, position.getY() & 0x3F).isMobOnTile()) {
                            System.out.print(" *");
                        } else {
                            System.out.print("  ");
                        }
                    }
                    System.out.println();
                }
            }
        });

        commands.add(new Command("obj", "object") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int id = parser.nextInt();
                int rotation = 0;
                if (parser.hasNext()) {
                    rotation = parser.nextInt();
                }
                CustomGameObject gameObject = new CustomGameObject(id, player.getPosition().copy(), ObjectDirection.valueOf(rotation).orElse(ObjectDirection.WEST), ObjectType.INTERACTABLE);
                World.schedule(new ObjectPlacementEvent(gameObject, 50));
                player.send(new SendMessage("Spawned temporary object " + id + "."));
            }
        });

        commands.add(new Command("dumpbank") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int[] amounts = player.bank.tabAmounts;
                Item[] items = player.bank.toArray();
                String data = " ";
                for (int am : amounts) {
                    data += am + ", ";
                }

                System.out.println("\tpublic final static int[] tabAmounts = {" + data + "};");
                System.out.println("");
                data = "\n\t\t";
                int c = 0;
                int iamounts = 0;
                for (Item it : items) {
                    if (it == null) continue;
                    iamounts = it.isStackable() ? 10000 : 100;
                    data += "new Item(" + it.getId() + ", " + iamounts + "), ";
                    if (++c % 5 == 0) {
                        data += "\n\t\t";
                    }
                }

                data += "\n\t";
                System.out.println("\tprivate Item[] bankItems = {" + data + "};");
            }
        });

        commands.add(new Command("hit") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.damage(new Hit(parser.nextInt()));
            }
        });

        commands.add(new Command("setinst", "setinstance") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.instance = parser.nextByte();
                player.send(new SendMessage(player.instance));
            }
        });

        commands.add(new Command("gfx", "graphic") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    int id = parser.nextInt();
                    boolean high = parser.hasNext();
                    player.graphic(new Graphic(id, high));
                    player.send(new SendMessage("Performing graphic = " + id));
                }

            }
        });

        commands.add(new Command("int", "inter") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int id = parser.nextInt();
                player.interfaceManager.open(id);
                player.send(new SendMessage("Opening interface: " + id, MessageColor.LIGHT_PURPLE));
            }
        });
        commands.add(new Command("tab", "tabs") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int id = parser.nextInt();
                player.interfaceManager.setSidebar(Config.MAGIC_TAB, id);
                player.send(new SendMessage("Opening interface: " + id, MessageColor.LIGHT_PURPLE));
            }
        });

        commands.add(new Command("anim") {
            @Override
            public void execute(Player player, CommandParser parser) {
                int id = parser.nextInt();
                player.animate(new Animation(id));
                player.send(new SendMessage("Performing animation = " + id));

            }
        });

        commands.add(new Command("npc") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    int id = parser.nextInt();
                    Npc npc = new Npc(id, player.getPosition(), Config.NPC_WALKING_RADIUS);
                    npc.walk = false;
                    npc.register();

                    if (id == 3080) {
                        npc.skills.setNpcMaxLevel(3, 99_999);
                        npc.locking.lock();
                    }
                    if (id == 2267 || id == 2266 || id == 2265) {
                        npc.locking.lock();
                    }
                    if (id == 2075) {
                        npc.skills.setNpcMaxLevel(3, 800);
                    }
                    player.send(new SendMessage("Npc " + id + " has been spawned."));
                }
            }
        });

        commands.add(new Command("spawn") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (parser.hasNext()) {
                    int id = parser.nextInt();
                    Npc npc = new Npc(id, player.getPosition(), 0, Direction.NORTH);
                    npc.register();
                    System.out.println("  {");
                    System.out.println("    \"id\": " + id + ",");
                    System.out.println("    \"radius\": \"0\",");
                    System.out.println("    \"facing\": \"NORTH\",");
                    System.out.println("    \"position\": {");
                    System.out.println("      \"x\": " + player.getPosition().getX() + ",");
                    System.out.println("      \"y\": " + player.getPosition().getY() + ",");
                    System.out.println("      \"height\": " + player.getPosition().getHeight() + "");
                    System.out.println("    }");
                    System.out.println("  },");
                    player.send(new SendMessage("Npc " + id + " has been spawned."));
                }
            }
        });

        commands.add(new Command("sobj", "searchobj") {
            @Override
            public void execute(Player player, CommandParser parser) {
                final String search = parser.nextLine();
                player.send(new SendMessage("Searching fs for object name: " + search, MessageColor.DEVELOPER));
                int count = 0;
                for (ObjectDefinition def : ObjectDefinition.definitions) {
                    if (def == null) continue;
                    if (def.name.contains(search.toString())) {
                        player.send(new SendMessage(def.getId() + ": " + def.name, MessageColor.DEVELOPER));
                        count++;
                    }
                }
                player.send(new SendMessage(count + " results were found for the query: " + search, MessageColor.DEVELOPER));
            }
        });

        commands.add(new Command("reint") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.playerAssistant.clearSendStrings();
            }
        });

        commands.add(new Command("hide", "invis") {
            @Override
            public void execute(Player player, CommandParser parser) {
                player.setVisible(!player.isVisible());
                player.send(new SendMessage(String.format("You are now %s.", player.isVisible() ? "visible" : "hidden")));
            }
        });

        commands.add(new Command("getnpcroll") {
            @Override
            public void execute(Player player, CommandParser parser) {
                Npc npc = new Npc(parser.nextInt(), player.getPosition());
                NpcDefinition definition = NpcDefinition.get(npc.id);
                npc.definition = definition;
                npc.setBonuses(definition.getBonuses());

                for (int index = 0; index < definition.getSkills().length; index++) {
                    npc.skills.setNpcMaxLevel(index, definition.getSkills()[index]);
                }

                CombatStrategy<? super Player> strategy = player.getStrategy();
                CombatType type = strategy.getCombatType();

                player.getCombat().addModifier(strategy);

                double attackRoll = rollOffensive(player, npc, type.getFormula());
                double defenceRoll = rollDefensive(player, npc, type.getFormula());
                double chance = attackRoll / (attackRoll + defenceRoll);
                double accuracy = (int) (chance * 10000) / 100.0;

                int max = getModifiedMaxHit(player, npc, type);
                max = player.getCombat().modifyDamage(npc, max);

                player.message("");
                player.message("You have <col=FF0000>" + accuracy + "%</col> accuracy against " + npc.getName() + ".");
                player.message("Your max hit against " + npc.getName() + " is <col=FF0000>" + max);
                player.message("Attack roll: <col=FF0000>" + (int) attackRoll + "</col>  ---  Defence roll: <col=FF0000>" + (int) defenceRoll);

                String rolls = "";
                rolls += "accuracy: <col=FF0000>" + player.getCombat().modifyAccuracy(npc, 100) + "%</col>  ---  ";
                rolls += "defence: <col=FF0000>" + player.getCombat().modifyDefensive(npc, 100) + "%</col>  --- ";
                rolls += "damage: <col=FF0000>" + player.getCombat().modifyDamage(npc, 100) + "%";

                String levels = "";
                levels += "attack: <col=FF0000>" + player.getCombat().modifyAttackLevel(npc, 100) + "%</col> -- ";
                levels += "strength: <col=FF0000>" + player.getCombat().modifyStrengthLevel(npc, 100) + "%</col> -- ";
                levels += "defence: <col=FF0000>" + player.getCombat().modifyDefenceLevel(npc, 100) + "%</col> -- ";
                levels += "ranged: <col=FF0000>" + player.getCombat().modifyRangedLevel(npc, 100) + "%</col> -- ";
                levels += "magic: <col=FF0000>" + player.getCombat().modifyMagicLevel(npc, 100) + "%";

                player.getCombat().removeModifier(strategy);
                player.message(rolls, levels);
            }
        });


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
                    World.sendStaffMessage("Plebs");
                }
            }
        });

        commands.add(new Command("masspets") {
            @Override
            public void execute(Player player, CommandParser parser) {
                for (PetData pet : PetData.values()) {
                    Npc npc = new Npc(pet.getNpc(), player.getPosition());
                    npc.register();
                    World.schedule(Pets.abandon(npc));
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
                    player.message("Invalid command use; ::setpt Adam");
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
                    Npc boss1 = new Npc(one, new Position(start.getX() - 3, start.getY() + 3));
                    Npc boss2 = new Npc(two, new Position(start.getX() + 3, start.getY() + 3));
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

        commands.add(new Command("getrolla") {
            @Override
            public void execute(Player player, CommandParser parser) {
                StringBuilder name = new StringBuilder(parser.nextString());
                while (parser.hasNext()) name.append(" ").append(parser.nextString());
                Optional<Player> search = World.search(name.toString().trim());

                search.ifPresent(other -> {
                    int max = getModifiedMaxHit(player, other, CombatType.MELEE);
                    player.send(new SendMessage("Your max hit against " + other.getName() + " is <col=FF0000>" + max));
                });
            }
        });


        commands.add(new Command("getroll") {
            @Override
            public void execute(Player player, CommandParser parser) {
                StringBuilder name = new StringBuilder(parser.nextString());
                while (parser.hasNext()) name.append(" ").append(parser.nextString());
                Optional<Player> search = World.search(name.toString().trim());

                search.ifPresent(other -> {
                    CombatStrategy<? super Player> strategy = player.getStrategy();

                    CombatType type = strategy.getCombatType();
                    player.getCombat().addModifier(strategy);

                    double attackRoll = rollOffensive(player, other, type.getFormula());
                    double defenceRoll = rollDefensive(player, other, type.getFormula());
                    double chance = attackRoll / (attackRoll + defenceRoll);
                    double accuracy = (int) (chance * 10000) / 100.0;

                    int max = getModifiedMaxHit(player, other, type);
                    max = player.getCombat().modifyDamage(other, max);

                    player.send(new SendMessage(""));
                    player.send(new SendMessage("You have <col=FF0000>" + accuracy + "%</col> accuracy against " + other.getName() + "."));
                    player.send(new SendMessage("Your max hit against " + other.getName() + " is <col=FF0000>" + max));
                    player.send(new SendMessage("Attack roll: <col=FF0000>" + (int) attackRoll + "</col>  ---  Defence roll: <col=FF0000>" + (int) defenceRoll));

                    String rolls = "";
                    rolls += "accuracy: <col=FF0000>" + player.getCombat().modifyAccuracy(other, 100) + "%</col>  ---  ";
                    rolls += "defence: <col=FF0000>" + player.getCombat().modifyDefensive(other, 100) + "%</col>  --- ";
                    rolls += "damage: <col=FF0000>" + player.getCombat().modifyDamage(other, 100) + "%";

                    String levels = "";
                    levels += "attack: <col=FF0000>" + player.getCombat().modifyAttackLevel(other, 100) + "%</col> -- ";
                    levels += "strength: <col=FF0000>" + player.getCombat().modifyStrengthLevel(other, 100) + "%</col> -- ";
                    levels += "defence: <col=FF0000>" + player.getCombat().modifyDefenceLevel(other, 100) + "%</col> -- ";
                    levels += "ranged: <col=FF0000>" + player.getCombat().modifyRangedLevel(other, 100) + "%</col> -- ";
                    levels += "magic: <col=FF0000>" + player.getCombat().modifyMagicLevel(other, 100) + "%";

                    player.getCombat().removeModifier(strategy);
                    player.message(rolls, levels);
                });
            }
        });

        commands.add(new Command("reload") {
            @Override
            public void execute(Player player, CommandParser parser) {
                if (!parser.hasNext()) {
                    player.send(new SendMessage("Invalid use of the reload command. Usage: ::reload object"));
                    return;
                }

                while (parser.hasNext()) {
                    switch (parser.nextString().toUpperCase()) {
                        case "LISTENER":
                            CombatListenerManager.load();
                            player.send(new SendMessage("Combat listeners have been successfully loaded."));
                            break;
                        case "PROJECTILE":
                        case "PROJ":
                            new CombatProjectileParser().run();
                            player.send(new SendMessage("Projectiles have been successfully loaded."));
                            break;
                        case "ITEM":
                            ItemDefinition.createParser().run();
                            player.send(new SendMessage("Items have been successfully loaded."));
                            break;
                        case "OBJECT":
                        case "OBJ":
                            new GlobalObjectParser().run();
                            player.send(new SendMessage("Objects have been successfully loaded."));
                            break;
                        case "DROP":
                        case "DROPS":
                            new NpcDropParser().run();
                            player.send(new SendMessage("Drops have been successfully loaded."));
                            break;
                        case "COMBAT":
                            new CombatProjectileParser().run();
                            player.send(new SendMessage("Combat projectiles have been successfully loaded."));
                            World.getNpcs().forEach(Npc::unregister);
                            NpcDefinition.createParser().run();
                            new NpcSpawnParser().run();
                            new NpcForceChatParser().run();
                            player.send(new SendMessage("Npc spawns have been successfully loaded."));
                            ItemDefinition.createParser().run();
                            player.send(new SendMessage("Items have been successfully loaded."));
                            CombatListenerManager.load();
                            player.send(new SendMessage("Combat listeners have been successfully loaded."));
                            break;
                        case "NPC":
                        case "SPAWN":
                            World.getNpcs().forEach(Npc::unregister);
                            NpcDefinition.createParser().run();
                            new NpcSpawnParser().run();
                            new NpcForceChatParser().run();
                            new NpcDropParser().run();
                            player.send(new SendMessage("Npc spawns have been successfully loaded."));
                            break;
                        case "SKILL":
                        case "SKILLS":
                            SkillRepository.load();
                            player.send(new SendMessage("Skills have been successfully loaded."));
                            break;
                        case "STORE":
                        case "SHOP":
                            Store.STORES.clear();
                            new StoreParser().run();
                            player.send(new SendMessage("Stores have been successfully loaded."));
                            break;

                        default:
                            player.send(new SendMessage("No reload entry was found."));
                            break;
                    }
                }

            }
        });

    }

    @Override
    public boolean canAccess(Player player) {
        return PlayerRight.isDeveloper(player);
    }

}
