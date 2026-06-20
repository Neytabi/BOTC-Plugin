package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HideNamesCommand implements SubCommand {

    private final Botc main;

    public HideNamesCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "hidenames";
    }

    @Override
    public String getDescription() {
        return "Cache les pseudos de tout le monde (anonymat).";
    }

    @Override
    public String getSyntax() {
        return "/botc hidenames";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard()
                .getTeam("botc_night");

        if (nightTeam == null) {
            player.sendMessage(
                    Component.text("Erreur : L'équipe de nuit n'est pas initialisée.", NamedTextColor.RED));
            return;
        }
        main.setNameTagsHidden(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp())
                continue;
            nightTeam.addEntry(p.getName());
        }
        player.sendMessage(
                Component.text("✓ Tous les pseudos sont désormais CACHÉS (Anonymat actif).", NamedTextColor.RED));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
