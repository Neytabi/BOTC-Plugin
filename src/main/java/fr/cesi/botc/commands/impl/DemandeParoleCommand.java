package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.BotcPlayer;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DemandeParoleCommand implements SubCommand {

    private final Botc main;

    public DemandeParoleCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "demandeparole";
    }

    @Override
    public String getDescription() {
        return "Demande la parole au Conteur (réservé aux fantômes).";
    }

    @Override
    public String getSyntax() {
        return "/botc demandeparole";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void execute(Player player, String[] args) {
        BotcPlayer bp = main.getPlayersMap().get(player.getUniqueId());
        if (bp != null && bp.isAlive()) {
            player.sendMessage(Component.text(" Tu es vivant ! Tu as déjà le droit de t'exprimer au Conseil.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text(" Demande de parole envoyée aux Conteurs.", NamedTextColor.GREEN));

        Component prompt = Component.text(" Le fantôme " + player.getName() + " demande la parole. ", NamedTextColor.GOLD)
                .append(Component.text("[ACCORDER]", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/botc grantparole " + player.getName())));

        for (Player op : Bukkit.getOnlinePlayers()) {
            if (op.isOp()) {
                op.sendMessage(prompt);
                op.playSound(op.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1.0f);
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}

