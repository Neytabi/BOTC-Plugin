package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MapVoteCommand implements SubCommand {

    private final Botc main;

    public MapVoteCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "mapvote";
    }

    @Override
    public String getDescription() {
        return "Gère le système de vote pour la prochaine map.";
    }

    @Override
    public String getSyntax() {
        return "/botc mapvote <start|stop|choose>";
    }

    @Override
    public boolean requiresOp() {
        // La sous-commande choose peut être exécutée par les joueurs
        return false;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Syntaxe : " + getSyntax(), NamedTextColor.RED));
            return;
        }

        String subAction = args[1].toLowerCase();

        if (subAction.equals("start") && player.isOp()) {
            if (main.isMapVoteOpen()) {
                player.sendMessage(Component.text("❌ Un scrutin de map est déjà ouvert !", NamedTextColor.RED));
                return;
            }

            main.getMapVotes().clear();
            main.setMapVoteOpen(true);

            final int[] countdown = { 30 };

            Bukkit.broadcast(
                    Component.text("=========================================", NamedTextColor.DARK_PURPLE));
            Bukkit.broadcast(
                    Component.text("🗳️ SCRUTIN OUVERT : ÉLECTION DE LA PROCHAINE MAP !", NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD));
            Bukkit.broadcast(
                    Component.text("Cliquez sur l'arène de votre choix pour voter :", NamedTextColor.LIGHT_PURPLE));

            if (main.getConfig().getConfigurationSection("presets") != null) {
                for (String presetKey : main.getConfig().getConfigurationSection("presets").getKeys(false)) {
                    Component voteButton = Component.text(" 🗺️ Map: ", NamedTextColor.GRAY)
                            .append(Component.text(presetKey, NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                            .append(Component.text(" [CLIQUE POUR VOTER]", NamedTextColor.GREEN)
                                    .decorate(TextDecoration.BOLD)
                                    .clickEvent(net.kyori.adventure.text.event.ClickEvent
                                            .runCommand("/botc mapvote choose " + presetKey)));
                    Bukkit.broadcast(voteButton);
                }
            }
            Bukkit.broadcast(
                    Component.text("=========================================", NamedTextColor.DARK_PURPLE));

            final Player adminPlayer = player;
            var task = new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (!main.isMapVoteOpen()) {
                        this.cancel();
                        return;
                    }

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendActionBar(
                                Component.text("🗳️ Fin des votes de map dans : ", NamedTextColor.LIGHT_PURPLE)
                                        .append(Component.text(countdown[0] + "s", NamedTextColor.YELLOW)
                                                .decorate(TextDecoration.BOLD))
                                        .append(Component.text(" (" + main.getMapVotes().size() + " votes émis)",
                                                NamedTextColor.GRAY)));
                    }

                    if (countdown[0] <= 5 && countdown[0] > 0) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 0.7f);
                        }
                    }

                    if (countdown[0] <= 0) {
                        this.cancel();
                        adminPlayer.performCommand("botc mapvote stop");
                        return;
                    }

                    countdown[0]--;
                }
            }.runTaskTimer(main, 0L, 20L);

            main.setMapVoteTask(task);
            return;
        }

        if (subAction.equals("choose") && args.length > 2) {
            if (!main.isMapVoteOpen()) {
                player.sendMessage(Component.text("❌ Le vote est clos ou a expiré.", NamedTextColor.RED));
                return;
            }

            if (main.getMapVotes().containsKey(player.getUniqueId())) {
                player.sendMessage(Component.text("❌ Vous avez déjà voté pour une map !", NamedTextColor.RED));
                return;
            }

            String choice = args[2].toLowerCase();
            main.getMapVotes().put(player.getUniqueId(), choice);

            Bukkit.broadcast(Component.text(
                    "🗳️ " + player.getName() + " a voté ! (Total : " + main.getMapVotes().size() + " votes)",
                    NamedTextColor.GRAY));
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.5f);
            return;
        }

        if (subAction.equals("stop") && player.isOp()) {
            if (!main.isMapVoteOpen())
                return;
            main.setMapVoteOpen(false);

            if (main.getMapVoteTask() != null) {
                main.getMapVoteTask().cancel();
                main.setMapVoteTask(null);
            }

            if (main.getMapVotes().isEmpty()) {
                Bukkit.broadcast(
                        Component.text("🗳️ Clôture du vote : Aucun joueur n'a émis de choix. Map inchangée.",
                                NamedTextColor.YELLOW));
                return;
            }

            java.util.HashMap<String, Integer> tally = new java.util.HashMap<>();
            for (String voteMap : main.getMapVotes().values()) {
                tally.put(voteMap, tally.getOrDefault(voteMap, 0) + 1);
            }

            String winner = tally.entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .get().getKey();

            Bukkit.broadcast(Component.text("=========================================", NamedTextColor.GOLD));
            Bukkit.broadcast(
                    Component.text("🗳️ LE SCRUTIN EST CLOS !", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

            tally.forEach((mapName, totalVotes) -> {
                Bukkit.broadcast(Component.text(" • " + mapName + " : ", NamedTextColor.GRAY)
                        .append(Component.text(totalVotes + " Vote(s)", NamedTextColor.AQUA)));
            });

            Bukkit.broadcast(
                    Component.text("🏆 LA MAP GAGNANTE EST : " + winner.toUpperCase() + " !", NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text("=========================================", NamedTextColor.GOLD));

            player.performCommand("botc preset select " + winner);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.6f, 1.0f);
            }
            return;
        }

        // Si la sous-action est invalide ou problème de permissions
        if (!player.isOp() && (subAction.equals("start") || subAction.equals("stop"))) {
            player.sendMessage(Component.text("➔ Tu n'es pas le Conteur de cette partie !", NamedTextColor.RED));
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2 && player.isOp()) {
            String input = args[1].toLowerCase();
            if ("start".startsWith(input))
                completions.add("start");
            if ("stop".startsWith(input))
                completions.add("stop");
        }
        return completions;
    }
}
