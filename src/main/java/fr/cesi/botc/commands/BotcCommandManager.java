package fr.cesi.botc.commands;

import fr.cesi.botc.Botc;
import fr.cesi.botc.BotcCommand;
import fr.cesi.botc.commands.impl.ExecuteCommand;
import fr.cesi.botc.commands.impl.HelpCommand;
import fr.cesi.botc.commands.impl.MapVoteCommand;
import fr.cesi.botc.commands.impl.VoteOuiCommand;
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

public class BotcCommandManager implements CommandExecutor, TabCompleter {

    private final Botc main;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final BotcCommand legacyCommand;

    public BotcCommandManager(Botc main) {
        this.main = main;
        this.legacyCommand = new BotcCommand(main);

        // Enregistrement des nouvelles sous-commandes
        registerCommand(new VoteOuiCommand(main));
        registerCommand(new HelpCommand());
        registerCommand(new MapVoteCommand(main));
        subCommands.put("execute", new ExecuteCommand(main));
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

        // Si la commande a été migrée vers le nouveau système
        if (target != null) {
            if (target.requiresOp() && !player.isOp()) {
                player.sendMessage(Component.text("➔ Tu n'es pas le Conteur de cette partie !", NamedTextColor.RED));
                return true;
            }
            try {
                target.execute(player, args);
            } catch (Exception e) {
                player.sendMessage(Component.text("Erreur lors de l'exécution de la commande.", NamedTextColor.RED));
                e.printStackTrace();
            }
            return true;
        }

        // Sinon, on passe au système legacy (BotcCommand d'origine)
        return legacyCommand.onCommand(sender, command, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();

            // Auto-complétion des nouvelles commandes
            for (SubCommand subCmd : subCommands.values()) {
                if (!subCmd.requiresOp() || player.isOp()) {
                    if (subCmd.getName().startsWith(input)) {
                        completions.add(subCmd.getName());
                    }
                }
            }

            // Auto-complétion des anciennes commandes
            List<String> legacyCompletions = legacyCommand.onTabComplete(sender, command, label, args);
            if (legacyCompletions != null) {
                completions.addAll(legacyCompletions);
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

            // Fallback
            return legacyCommand.onTabComplete(sender, command, label, args);
        }

        return new ArrayList<>();
    }
}
