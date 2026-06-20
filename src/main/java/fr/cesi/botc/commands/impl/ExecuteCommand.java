package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.BotcPlayer;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ExecuteCommand implements SubCommand {

    private final Botc main;

    public ExecuteCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "execute";
    }

    @Override
    public String getDescription() {
        return "Exécute un joueur manuellement.";
    }

    @Override
    public String getSyntax() {
        return "/botc execute <joueur>";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Utilisation : " + getSyntax(), NamedTextColor.RED));
            return;
        }

        String targetName = args[1];
        BotcPlayer targetBotc = null;
        for (BotcPlayer bp : main.getPlayersMap().values()) {
            if (bp.getPlayerName().equalsIgnoreCase(targetName)) {
                targetBotc = bp;
                break;
            }
        }

        if (targetBotc == null) {
            player.sendMessage(Component.text("Erreur : Joueur non trouvé.", NamedTextColor.RED));
            return;
        }

        if (!targetBotc.isAlive()) {
            player.sendMessage(Component.text("Erreur : Ce joueur est déjà mort.", NamedTextColor.RED));
            return;
        }

        main.executePlayer(targetBotc, player);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            for (BotcPlayer bp : main.getPlayersMap().values()) {
                if (bp.isAlive()) {
                    completions.add(bp.getPlayerName());
                }
            }
        }
        return completions;
    }
}
