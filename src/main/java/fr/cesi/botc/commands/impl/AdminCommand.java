package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.GrimoireView;
import fr.cesi.botc.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand implements SubCommand {

    private final Botc main;
    private final GrimoireView grimoireView;

    public AdminCommand(Botc main) {
        this.main = main;
        this.grimoireView = new GrimoireView(main);
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getDescription() {
        return "Ouvre le grimoire du Conteur.";
    }

    @Override
    public String getSyntax() {
        return "/botc admin";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        grimoireView.openGrimoire(player);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
