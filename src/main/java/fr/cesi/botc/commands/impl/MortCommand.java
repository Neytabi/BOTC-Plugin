package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MortCommand implements SubCommand {

    private final Botc main;

    public MortCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "mort";
    }

    @Override
    public String getDescription() {
        return "Annonce le moment où le Conteur donne les victimes de la nuit.";
    }

    @Override
    public String getSyntax() {
        return "/botc mort";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                Component.text("💀 ANNONCE DES MORTS 💀", NamedTextColor.RED).decorate(TextDecoration.BOLD),
                Component.text("Écoutez attentivement le Conteur...", NamedTextColor.GRAY));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(title);
            // Un bruit d'éclair lointain pour l'ambiance sombre
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.8f);
        }

        Bukkit.broadcast(Component.text("=============================================", NamedTextColor.RED));
        Bukkit.broadcast(Component
                .text("[BOTC] Silence ! Le Conteur va annoncer les victimes de la nuit.", NamedTextColor.DARK_RED)
                .decorate(TextDecoration.BOLD));
        Bukkit.broadcast(Component.text("=============================================", NamedTextColor.RED));

        player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                .append(Component.text("Temps libre", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc tempslibre")));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
