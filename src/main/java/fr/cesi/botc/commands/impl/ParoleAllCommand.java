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

public class ParoleAllCommand implements SubCommand {

    private final Botc main;

    public ParoleAllCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "paroleall";
    }

    @Override
    public String getDescription() {
        return "Rend la parole à tous les joueurs (unmute global).";
    }

    @Override
    public String getSyntax() {
        return "/botc paroleall";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        Bukkit.broadcast(
                Component.text(" Le Conteur vous redonne la parole. Le débat reprend !", NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp())
                continue;

            // On le retire de la liste noire
            main.getVcMutedPlayers().remove(p.getUniqueId());
            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.2f);
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
