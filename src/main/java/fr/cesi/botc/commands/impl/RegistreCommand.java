package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.PlayerRegistryView;
import fr.cesi.botc.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegistreCommand implements SubCommand {

    private final Botc main;

    public RegistreCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "registre";
    }

    @Override
    public String getDescription() {
        return "Ouvre votre Registre du Tribunal (Livre interactif).";
    }

    @Override
    public String getSyntax() {
        return "/botc registre";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void execute(Player player, String[] args) {
        new PlayerRegistryView().openRegistryMenu(player, main);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}

