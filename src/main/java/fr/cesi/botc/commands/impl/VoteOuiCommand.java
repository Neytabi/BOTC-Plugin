package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VoteOuiCommand implements SubCommand {

    private final Botc main;

    public VoteOuiCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "voteoui";
    }

    @Override
    public String getDescription() {
        return "Lève la main pour voter.";
    }

    @Override
    public String getSyntax() {
        return "/botc voteoui";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.getVoteManager().registerVote(player);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
