package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashSet;
import java.util.UUID;

public class VoteManager {

    private final Botc main;
    private boolean voteInProgress = false;
    private BotcPlayer accusedPlayer = null;
    private final HashSet<UUID> voters = new HashSet<>(); // Stocke les UUID de ceux qui votent OUI

    public VoteManager(Botc main) {
        this.main = main;
    }

    public void startVote(BotcPlayer target) {
        if (voteInProgress) {
            return;
        }

        this.voteInProgress = true;
        this.accusedPlayer = target;
        this.voters.clear();

        // 1. Annonce théâtrale
        Bukkit.broadcast(Component.text("---------------------------------------------", NamedTextColor.GOLD));
        Bukkit.broadcast(Component.text("[VOTE] Une accusation officielle est lancee contre " + target.getPlayerName() + " !", NamedTextColor.YELLOW));

        // 2. Création du bouton cliquable
        Component boutonOui = Component.text("[CLIQUER ICI POUR VOTER OUI]", NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/botc voteoui"));

        Bukkit.broadcast(Component.text("Si vous voulez l'executer, ").append(boutonOui));
        Bukkit.broadcast(Component.text("Vous avez 10 secondes pour voter.", NamedTextColor.GRAY));
        Bukkit.broadcast(Component.text("---------------------------------------------", NamedTextColor.GOLD));

        // 3. Le Timer de fin de vote (10 secondes)
        new BukkitRunnable() {
            @Override
            public void run() {
                endVote();
            }
        }.runTaskLater(main, 20 * 10L); // 20 ticks * 10 = 10 secondes.
    }

    public void registerVote(Player player) {
        if (!voteInProgress) {
            player.sendMessage(Component.text("Il n'y a aucun vote en cours.", NamedTextColor.RED));
            return;
        }

        BotcPlayer voterBotc = main.getPlayersMap().get(player.getUniqueId());
        if (voterBotc == null) return;

        // Vérification des droits de vote (Règles BOTC)
        if (!voterBotc.isAlive() && !voterBotc.hasGhostVote()) {
            player.sendMessage(Component.text("Tu es mort et tu as deja utilise ton jeton de vote !", NamedTextColor.RED));
            return;
        }

        // Si le joueur a déjà voté OUI, on évite le spam
        if (voters.contains(player.getUniqueId())) {
            player.sendMessage(Component.text("Tu as deja vote OUI pour cette accusation.", NamedTextColor.YELLOW));
            return;
        }

        // Enregistrement du vote
        voters.add(player.getUniqueId());

        // --- LA CORRECTION EST ICI ---
        // Si c'est un fantôme qui vote, on lui retire son jeton TOUT DE SUITE
        if (!voterBotc.isAlive()) {
            voterBotc.setGhostVote(false);
            player.sendMessage(Component.text("[BOTC]  Jeton consommé ! Tu as utilisé ton unique vote de fantôme.", NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text("Ton vote OUI a ete pris en compte !", NamedTextColor.GREEN));
        }

        // Annonce publique anonymisée (pour l'ambiance)
        Bukkit.broadcast(Component.text("[VOTE] " + player.getName() + " a voté OUI ! (Total : " + voters.size() + " voix)", NamedTextColor.GRAY));
    }

    private void endVote() {
        if (!voteInProgress) return;

        Bukkit.broadcast(Component.text("---------------------------------------------", NamedTextColor.GOLD));
        Bukkit.broadcast(Component.text("[BOTC] Fin du temps reglementaire ! Le vote est clos.", NamedTextColor.YELLOW));
        Bukkit.broadcast(Component.text("Resultat pour " + accusedPlayer.getPlayerName() + " : " + voters.size() + " votes OUI.", NamedTextColor.AQUA));
        Bukkit.broadcast(Component.text("Conteur, a toi de trancher si la majorite est atteinte !", NamedTextColor.LIGHT_PURPLE));
        Bukkit.broadcast(Component.text("---------------------------------------------", NamedTextColor.GOLD));

        // NOTE : Plus besoin de la boucle ici, les jetons ont déjà été retirés en direct lors du clic !

        // Réinitialisation du manager
        this.voteInProgress = false;
        this.accusedPlayer = null;
        this.voters.clear();
    }
}