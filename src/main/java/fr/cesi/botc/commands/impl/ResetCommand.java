package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ResetCommand implements SubCommand {

    private final Botc main;

    public ResetCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Réinitialise la partie, les inventaires et ranime tout le monde.";
    }

    @Override
    public String getSyntax() {
        return "/botc reset";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        // Cette méthode gère déjà la réanimation globale, le nettoyage visuel
        // et la distribution des bons livres uniques (Grimoire / Registre)
        main.resetGame();

        player.sendMessage(Component.text(
                "v La partie a été réinitialisée ! Tous les inventaires ont été synchronisés avec les objets de connexion.",
                NamedTextColor.GREEN));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
