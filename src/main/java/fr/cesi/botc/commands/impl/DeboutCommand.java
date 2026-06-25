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

public class DeboutCommand implements SubCommand {

    private final Botc main;

    public DeboutCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "debout";
    }

    @Override
    public String getDescription() {
        return "Libère les joueurs de leurs sièges (fin de conseil).";
    }

    @Override
    public String getSyntax() {
        return "/botc debout";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.setCouncilOpen(false);
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard()
                .getTeam("botc_night");

        // --- DÉCONNEXION DU GROUPE VOCAL (VERSION DIAGNOSTIC) ---
        if (main.getVoicechatPlugin() != null && main.getVoicechatPlugin().getVoicechatApi() != null) {
            de.maxhenkel.voicechat.api.VoicechatServerApi voiceApi = main.getVoicechatPlugin().getVoicechatApi();

            for (Player p : Bukkit.getOnlinePlayers()) {
                de.maxhenkel.voicechat.api.VoicechatConnection connection = voiceApi
                        .getConnectionOf(p.getUniqueId());
                if (connection != null) {
                    connection.setGroup(null); // Quitte le groupe
                }
            }
        }

        net.kyori.adventure.title.Title deboutTitle = net.kyori.adventure.title.Title.title(
                Component.text("! LEVEZ-VOUS !", NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                Component.text("Vous pouvez quitter votre siège.", NamedTextColor.GRAY));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(deboutTitle);
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_HORSE_GALLOP, 0.5f, 1.2f); // Bruit de pas légers

            if (p.isOp())
                continue;

            // Si le joueur est dans un véhicule tagué "botc_chair"
            if (p.getVehicle() != null && p.getVehicle().getScoreboardTags().contains("botc_chair")) {
                org.bukkit.entity.Entity chair = p.getVehicle();
                chair.removePassenger(p); // On libère le joueur
                chair.remove(); // On détruit le wagon invisible instantanément
            }

            if (nightTeam != null) {
                nightTeam.addEntry(p.getName());
            }
        }

        Bukkit.broadcast(
                Component.text("[BOTC] Le conseil est terminé, vous pouvez vous lever.", NamedTextColor.GREEN));

        player.sendMessage(Component.text("-> Prochaine étape : ", NamedTextColor.AQUA)
                .append(Component.text("Mettre la nuit", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc nuit")));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
