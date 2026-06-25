package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.BotcPlayer;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OrderCommand implements SubCommand {

    private final Botc main;

    public OrderCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "order";
    }

    @Override
    public String getDescription() {
        return "Affiche l'ordre du cercle pour le MJ.";
    }

    @Override
    public String getSyntax() {
        return "/botc order";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!main.isSeatsAssigned()) {
            player.sendMessage(Component.text(
                    " L'ordre n'est pas encore généré ! Lance d'abord un /botc assis pour créer le cercle de cette game.",
                    NamedTextColor.RED));
            return;
        }

        player.sendMessage(
                Component.text("===  ORDRE DU CERCLE DE JEU (Du siège 1 à X) ===", NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD));

        // On récupère et trie les joueurs selon leur index de chaise
        java.util.List<BotcPlayer> orderedPlayers = new java.util.ArrayList<>(main.getPlayersMap().values());
        orderedPlayers.removeIf(bp -> bp.getChairIndex() == -1);
        orderedPlayers.sort(java.util.Comparator.comparingInt(bp -> bp.getChairIndex()));

        if (orderedPlayers.isEmpty()) {
            player.sendMessage(
                    Component.text("Aucun joueur n'est assis sur une chaise actuellement.", NamedTextColor.GRAY));
            return;
        }

        for (BotcPlayer bp : orderedPlayers) {
            // On prépare le tag de vie (Vert si vivant, Rouge  si mort)
            Component statusTag = bp.isAlive()
                    ? Component.text("[VIVANT]", NamedTextColor.GREEN)
                    : Component.text("[MORT ]", NamedTextColor.RED);

            // On affiche la ligne : Siège #1 : Pseudo | [VIVANT] | Rôle : Diablotin
            player.sendMessage(Component.text(" Siège #" + (bp.getChairIndex() + 1) + " : ", NamedTextColor.GOLD)
                    .append(Component.text(bp.getPlayerName(), NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                    .append(Component.text(" | ", NamedTextColor.GRAY))
                    .append(statusTag)
                    .append(Component.text(" | Rôle : ", NamedTextColor.GRAY))
                    .append(Component.text(bp.getDisplayedRole(), NamedTextColor.LIGHT_PURPLE)));
        }
        player.sendMessage(
                Component.text("--------------------------------------------------", NamedTextColor.GRAY));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
