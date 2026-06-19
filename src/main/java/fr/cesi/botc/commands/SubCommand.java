package fr.cesi.botc.commands;

import org.bukkit.entity.Player;

import java.util.List;

public interface SubCommand {

    /**
     * @return Le nom de la commande (ex: "voteoui")
     */
    String getName();

    /**
     * @return La description de ce que fait la commande
     */
    String getDescription();

    /**
     * @return La syntaxe d'utilisation (ex: "/botc mapvote <start|stop>")
     */
    String getSyntax();

    /**
     * @return true si seul un OP peut utiliser cette commande
     */
    boolean requiresOp();

    /**
     * Logique principale de la commande
     *
     * @param player Le joueur qui a tapé la commande
     * @param args   Les arguments (args[0] est le nom de la sous-commande)
     */
    void execute(Player player, String[] args);

    /**
     * @param player Le joueur qui tape
     * @param args   Les arguments actuels
     * @return Une liste de suggestions pour l'auto-complétion
     */
    List<String> getSubcommandArguments(Player player, String[] args);
}
