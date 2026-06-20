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

public class SilenceCommand implements SubCommand {

    private final Botc main;

    public SilenceCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "silence";
    }

    @Override
    public String getDescription() {
        return "Coupe la parole de tous les joueurs (mute global).";
    }

    @Override
    public String getSyntax() {
        return "/botc silence";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        Bukkit.broadcast(
                Component.text("🤫 Le Conteur réclame un silence absolu ! Micros coupés.", NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp())
                continue;

            // On ajoute le joueur à la liste noire (l'API bloquera son micro instantanément)
            main.getVcMutedPlayers().add(p.getUniqueId());
            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 0.4f, 0.5f);
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
