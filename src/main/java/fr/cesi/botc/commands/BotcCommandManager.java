package fr.cesi.botc.commands;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.impl.AdminCommand;
import fr.cesi.botc.commands.impl.AssisCommand;
import fr.cesi.botc.commands.impl.ConseilCommand;
import fr.cesi.botc.commands.impl.DeboutCommand;
import fr.cesi.botc.commands.impl.ExecuteCommand;
import fr.cesi.botc.commands.impl.HelpCommand;
import fr.cesi.botc.commands.impl.JourCommand;
import fr.cesi.botc.commands.impl.MapVoteCommand;
import fr.cesi.botc.commands.impl.MortCommand;
import fr.cesi.botc.commands.impl.NuitCommand;
import fr.cesi.botc.commands.impl.VoteOuiCommand;
import fr.cesi.botc.commands.impl.GrantParoleCommand;
import fr.cesi.botc.commands.impl.HideNamesCommand;
import fr.cesi.botc.commands.impl.OrderCommand;
import fr.cesi.botc.commands.impl.ParoleAllCommand;
import fr.cesi.botc.commands.impl.ShowNamesCommand;
import fr.cesi.botc.commands.impl.SilenceCommand;
import fr.cesi.botc.commands.impl.TempsLibreCommand;
import fr.cesi.botc.commands.impl.VcMuteCommand;
import fr.cesi.botc.commands.impl.VcUnmuteCommand;
import fr.cesi.botc.commands.impl.AddChairCommand;
import fr.cesi.botc.commands.impl.AddRoomCommand;
import fr.cesi.botc.commands.impl.DelChairsCommand;
import fr.cesi.botc.commands.impl.DelRoomsCommand;
import fr.cesi.botc.commands.impl.ShowChairsCommand;
import fr.cesi.botc.commands.impl.ShowRoomsCommand;
import fr.cesi.botc.commands.impl.PresetCommand;
import fr.cesi.botc.commands.impl.ResetCommand;
import fr.cesi.botc.commands.impl.SetLightningCommand;
import fr.cesi.botc.commands.impl.SetPlayerDeathCommand;
import fr.cesi.botc.commands.impl.SetTribunalCommand;
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

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public BotcCommandManager(Botc main) {

        // Enregistrement des nouvelles sous-commandes
        registerCommand(new VoteOuiCommand(main));
        registerCommand(new HelpCommand());
        registerCommand(new MapVoteCommand(main));
        subCommands.put("execute", new ExecuteCommand(main));
        
        // Lot 1
        subCommands.put("settribunal", new SetTribunalCommand(main));
        subCommands.put("preset", new PresetCommand(main));
        subCommands.put("reset", new ResetCommand(main));
        subCommands.put("setplayerdeath", new SetPlayerDeathCommand(main));
        subCommands.put("setlightning", new SetLightningCommand(main));

        // Lot 2
        subCommands.put("addroom", new AddRoomCommand(main));
        subCommands.put("delrooms", new DelRoomsCommand(main));
        subCommands.put("showrooms", new ShowRoomsCommand(main));
        subCommands.put("addchair", new AddChairCommand(main));
        subCommands.put("delchairs", new DelChairsCommand(main));
        subCommands.put("showchairs", new ShowChairsCommand(main));

        // Lot 3
        subCommands.put("silence", new SilenceCommand(main));
        subCommands.put("paroleall", new ParoleAllCommand(main));
        subCommands.put("grantparole", new GrantParoleCommand(main));
        subCommands.put("vcmute", new VcMuteCommand(main));
        subCommands.put("vcunmute", new VcUnmuteCommand(main));
        subCommands.put("order", new OrderCommand(main));
        subCommands.put("shownames", new ShowNamesCommand(main));
        subCommands.put("hidenames", new HideNamesCommand(main));

        // Lot 4
        subCommands.put("debout", new DeboutCommand(main));
        subCommands.put("mort", new MortCommand(main));
        subCommands.put("tempslibre", new TempsLibreCommand(main));
        subCommands.put("conseil", new ConseilCommand(main));
        subCommands.put("nuit", new NuitCommand(main));
        subCommands.put("jour", new JourCommand(main));
        subCommands.put("assis", new AssisCommand(main));
        subCommands.put("admin", new AdminCommand(main));
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

        // Si la commande n'a pas été trouvée, c'est probablement "voteoui" qui n'a pas de SubCommand, ou une commande invalide.
        // voteoui a été gérée par l'ancien Listener potentiellement ou on peut l'ignorer ici vu qu'elle est censée être gérée via SubCommand si on veut.
        if (target != null) {
            if (target.requiresOp() && !player.isOp()) {
                player.sendMessage(Component.text("-> Tu n'es pas le Conteur de cette partie !", NamedTextColor.RED));
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

        player.sendMessage(Component.text("Commande introuvable.", NamedTextColor.RED));
        return true;
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

            // Ajout du joueur (voteoui) qui n'a pas encore de classe dédiée, au cas où
            if ("voteoui".startsWith(input)) {
                completions.add("voteoui");
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
