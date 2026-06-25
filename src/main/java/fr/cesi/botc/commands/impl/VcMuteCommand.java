package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VcMuteCommand implements SubCommand {

    private final Botc main;

    public VcMuteCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "vcmute";
    }

    @Override
    public String getDescription() {
        return "Mute un joueur spécifique dans le chat vocal.";
    }

    @Override
    public String getSyntax() {
        return "/botc vcmute <joueur>";
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
            main.getVcMutedPlayers().add(target.getUniqueId());
            target.sendMessage(
                    Component.text(" Le Conteur a temporairement coupé ton micro.", NamedTextColor.RED));
            player.sendMessage(
                    Component.text("v Micro de " + target.getName() + " coupé via l'API.", NamedTextColor.GREEN));
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
