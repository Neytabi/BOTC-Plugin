package fr.cesi.botc.commands;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.impl.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BotcCommandManager implements CommandExecutor, TabCompleter {

    private final Botc main;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public BotcCommandManager(Botc main) {
        this.main = main;

        // Commandes de base
        registerCommand(new VoteOuiCommand(main));
        registerCommand(new HelpCommand());
        registerCommand(new MapVoteCommand(main));
        registerCommand(new ExecuteCommand(main));

        // Configuration & Gestion de partie (Lot 1)
        registerCommand(new SetTribunalCommand(main));
        registerCommand(new PresetCommand(main));
        registerCommand(new ResetCommand(main));
        registerCommand(new SetPlayerDeathCommand(main));
        registerCommand(new SetLightningCommand(main));

        // Configuration des emplacements (Lot 2)
        registerCommand(new AddRoomCommand(main));
        registerCommand(new DelRoomsCommand(main));
        registerCommand(new ShowRoomsCommand(main));
        registerCommand(new AddChairCommand(main));
        registerCommand(new DelChairsCommand(main));
        registerCommand(new ShowChairsCommand(main));

        // Voix, Audio & Visuel (Lot 3)
        registerCommand(new SilenceCommand(main));
        registerCommand(new ParoleAllCommand(main));
        registerCommand(new GrantParoleCommand());
        registerCommand(new VcMuteCommand(main));
        registerCommand(new VcUnmuteCommand(main));
        registerCommand(new OrderCommand(main));
        registerCommand(new ShowNamesCommand(main));
        registerCommand(new HideNamesCommand(main));

        // Phases de jeu & Rôles (Lot 4)
        registerCommand(new DeboutCommand(main));
        registerCommand(new MortCommand());
        registerCommand(new TempsLibreCommand());
        registerCommand(new ConseilCommand(main));
        registerCommand(new NuitCommand(main));
        registerCommand(new JourCommand(main));
        registerCommand(new AssisCommand(main));
        registerCommand(new AdminCommand(main));
    }

    private void registerCommand(SubCommand cmd) {
        subCommands.put(cmd.getName().toLowerCase(), cmd);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Seul un joueur peut executer cette commande.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            if (player.isOp()) {
                player.sendMessage(Component.text("Usage Conteur : /botc help pour voir le guide.", NamedTextColor.RED));
            } else {
                player.sendMessage(Component.text("Usage Joueur : /botc voteoui pour lever la main.", NamedTextColor.YELLOW));
            }
            return true;
        }

        String subName = args[0].toLowerCase();
        SubCommand target = subCommands.get(subName);

        if (target != null) {
            if (target.requiresOp() && !player.isOp()) {
                player.sendMessage(Component.text("-> Tu n'es pas le Conteur de cette partie !", NamedTextColor.RED));
                return true;
            }
            try {
                target.execute(player, args);
            } catch (Exception e) {
                player.sendMessage(Component.text("Erreur lors de l'exécution de la commande.", NamedTextColor.RED));
                main.getLogger().log(Level.SEVERE, "Erreur lors de l'execution de la sous-commande /botc " + subName, e);
            }
            return true;
        }

        player.sendMessage(Component.text("Commande introuvable.", NamedTextColor.RED));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();

            for (SubCommand subCmd : subCommands.values()) {
                if (!subCmd.requiresOp() || player.isOp()) {
                    if (subCmd.getName().startsWith(input)) {
                        completions.add(subCmd.getName());
                    }
                }
            }

            return completions;
        } else if (args.length > 1) {
            String subName = args[0].toLowerCase();
            SubCommand target = subCommands.get(subName);
            if (target != null) {
                if (!target.requiresOp() || player.isOp()) {
                    return target.getSubcommandArguments(player, args);
                }
            }
        }

        return new ArrayList<>();
    }
}