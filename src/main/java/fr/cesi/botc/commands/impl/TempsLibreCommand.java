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

public class TempsLibreCommand implements SubCommand {

    public TempsLibreCommand(Botc main) {
    }

    @Override
    public String getName() {
        return "tempslibre";
    }

    @Override
    public String getDescription() {
        return "Annonce le temps libre et libère tout le monde de force.";
    }

    @Override
    public String getSyntax() {
        return "/botc tempslibre";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                Component.text("🗣️ TEMPS LIBRE 🗣️", NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                Component.text("Dispersez-vous et complotez en secret !", NamedTextColor.GRAY));

        // On force tout le monde à se lever pour qu'ils puissent courir partout
        player.performCommand("botc debout");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(title);
            // Un petit jingle de début de journée léger
            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7f, 1.2f);
        }

        Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GREEN));
        Bukkit.broadcast(
                Component.text("[BOTC] Le temps libre est déclaré. Les discussions privées sont autorisées !",
                        NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
        Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GREEN));

        player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                .append(Component.text("Ouvrir le conseil", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc conseil")));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
