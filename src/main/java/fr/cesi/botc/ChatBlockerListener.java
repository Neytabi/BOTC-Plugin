package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;

public class ChatBlockerListener implements Listener {
    private final Botc main;

    public ChatBlockerListener(Botc main) { this.main = main; }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        // Si c'est la nuit et que le joueur n'est pas OP (pas le Conteur)
        if (main.isNight() && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage(Component.text("C'est la nuit ! Le village dort, interdiction de parler dans le chat.", NamedTextColor.RED));
        }
    }
    @org.bukkit.event.EventHandler
    public void onPlayerDismount(org.bukkit.event.entity.EntityDismountEvent event) {
        // Si l'entité qui essaie de descendre est bien un joueur
        if (event.getEntity() instanceof org.bukkit.entity.Player player) {

            // Et si le véhicule possède notre tag de chaise BOTC
            if (event.getDismounted().getScoreboardTags().contains("botc_chair")) {

                // Si la partie est en cours (ou simplement si le joueur est censé être assis)
                // On annule purement et simplement l'action de se lever !
                event.setCancelled(true);

                // Optionnel : un petit message discret pour lui rappeler qu'il est bloqué
                player.sendActionBar(net.kyori.adventure.text.Component.text("➔ Vous devez rester assis pendant le Tribunal !", net.kyori.adventure.text.format.NamedTextColor.RED));
            }
        }
    }
}