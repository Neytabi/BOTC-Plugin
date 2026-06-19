package fr.cesi.botc.commands.impl;

import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Affiche le guide chronologique pour le Conteur.";
    }

    @Override
    public String getSyntax() {
        return "/botc help";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(
                Component.text("=== GUIDE CHRONOLOGIQUE D'UNE PARTIE BOTC ===", NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("Suivez ce déroulé de haut en bas pour mener votre partie :",
                NamedTextColor.LIGHT_PURPLE));
        player.sendMessage(
                Component.text("--------------------------------------------------", NamedTextColor.GRAY));

        player.sendMessage(
                Component.text("ÉTAPE 1 : Configuration Unique du Château (À faire une fois)", NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("   -> Enregistrer le centre du Tribunal : /botc settribunal",
                NamedTextColor.WHITE));
        player.sendMessage(Component.text("   -> Enregistrer l'estrade d'exécution (Morts) : /botc setplayerdeath",
                NamedTextColor.WHITE));
        player.sendMessage(
                Component.text("   -> Enregistrer CHAQUE porte de chambre : /botc addroom", NamedTextColor.WHITE));
        player.sendMessage(Component.text("   -> Choisir la cible de l'éclair : /botc setlightning <player/local>",
                NamedTextColor.WHITE));
        player.sendMessage(
                Component.text("   -> (Vérifier les chambres : /botc showrooms | Les vider : /botc delrooms)",
                        NamedTextColor.DARK_GRAY));

        player.sendMessage(
                Component.text("ÉTAPE 2 : Préparation du Lobby (Avant chaque nouvelle game)", NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("   -> Réinitialiser les chaises de la partie précédente : /botc reset",
                NamedTextColor.YELLOW));
        player.sendMessage(
                Component.text("   -> Assigner les Chaises (Faire le tour du cercle dans l'ordre) : /botc addchair",
                        NamedTextColor.WHITE));
        player.sendMessage(
                Component.text("   -> (Vérifier les chaises avec les flammes de test : /botc showchairs)",
                        NamedTextColor.DARK_GRAY));
        player.sendMessage(Component.text(" ", NamedTextColor.GRAY));

        player.sendMessage(Component.text("ÉTAPE 3 : Lancement du Tribunal (Début du Jour)", NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        player.sendMessage(
                Component.text("   -> Assis et identifier tout le monde (Active les NameTags) : /botc assis",
                        NamedTextColor.YELLOW));
        player.sendMessage(
                Component.text("   -> Ouvrir le Grimoire pour exécuter, attribuer les rôles et voter : /botc admin",
                        NamedTextColor.LIGHT_PURPLE));
        player.sendMessage(
                Component.text("      *Tuer via le menu téléporte la cible sur l'estrade avant de la rasseoir !*",
                        NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        player.sendMessage(Component.text(" ", NamedTextColor.GRAY));

        player.sendMessage(
                Component.text("ÉTAPE 4 : Fin du Conseil (Phase de Complots Libres)", NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text(
                "   -> Libérer les joueurs (Cache les NameTags + Active la boussole vers Tribunal) : /botc debout",
                NamedTextColor.YELLOW));
        player.sendMessage(
                Component.text("      *Rappel vocal : Ordonner le mode Chuchotement (Simple Voice Chat)*",
                        NamedTextColor.AQUA).decorate(TextDecoration.ITALIC));
        player.sendMessage(Component.text(" ", NamedTextColor.GRAY));

        player.sendMessage(Component.text("ÉTAPE 5 : Phase de Nuit (Retour aux quartiers)", NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text(
                "   -> Lancer la nuit (Libres de marcher, boussole vers SA chambre, tête affichée) : /botc nuit",
                NamedTextColor.BLUE));
        player.sendMessage(
                Component.text("      *Les joueurs doivent suivre la flèche 3D à l'écran pour rejoindre leur lit.*",
                        NamedTextColor.DARK_AQUA).decorate(TextDecoration.ITALIC));

        player.sendMessage(
                Component.text("--------------------------------------------------", NamedTextColor.GRAY));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
