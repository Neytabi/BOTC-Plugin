package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShowNamesCommand implements SubCommand {

    private final Botc main;

    public ShowNamesCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "shownames";
    }

    @Override
    public String getDescription() {
        return "Rend tous les pseudos visibles.";
    }

    @Override
    public String getSyntax() {
        return "/botc shownames";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard()
                .getTeam("botc_night");
        main.setNameTagsHidden(false);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (nightTeam != null && nightTeam.hasEntry(p.getName())) {
                nightTeam.removeEntry(p.getName());
            }
        }
        player.sendMessage(Component.text("v Tous les pseudos sont désormais VISIBLES.", NamedTextColor.GREEN));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
