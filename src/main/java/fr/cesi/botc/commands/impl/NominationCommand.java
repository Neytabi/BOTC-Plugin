package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.BotcPlayer;
import fr.cesi.botc.NominationView;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NominationCommand implements SubCommand {

    private final Botc main;

    public NominationCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "nomination";
    }

    @Override
    public String getDescription() {
        return "Ouvre le registre pour nommer un suspect.";
    }

    @Override
    public String getSyntax() {
        return "/botc nomination";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void execute(Player player, String[] args) {
        BotcPlayer bp = main.getPlayersMap().get(player.getUniqueId());
        if (bp != null && !bp.isAlive()) {
            player.sendMessage(Component.text(" Les morts ne peuvent plus désigner de suspect !", NamedTextColor.RED));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        new NominationView().openNominationMenu(player, main);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}

