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

public class GrantParoleCommand implements SubCommand {

    public GrantParoleCommand(Botc main) {
    }

    @Override
    public String getName() {
        return "grantparole";
    }

    @Override
    public String getDescription() {
        return "Accorde officiellement la parole à un joueur précis pendant le silence.";
    }

    @Override
    public String getSyntax() {
        return "/botc grantparole <joueur>";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage : " + getSyntax(), NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target != null) {
            Bukkit.broadcast(Component
                    .text("Silence ! Le Conteur accorde officiellement la parole à " + target.getName() + ".",
                            NamedTextColor.GREEN)
                    .decorate(TextDecoration.BOLD));
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 0.8f, 1.4f);
            }
        } else {
            player.sendMessage(Component.text("Joueur introuvable.", NamedTextColor.RED));
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(input)) {
                    completions.add(p.getName());
                }
            }
        }
        return completions;
    }
}
