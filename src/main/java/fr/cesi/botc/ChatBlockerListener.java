package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public class ChatBlockerListener implements Listener {
    private final Botc main;

    public ChatBlockerListener(Botc main) { 
        this.main = main; 
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        // Si l'entité qui essaie de descendre est bien un joueur
        if (event.getEntity() instanceof Player player) {
            // Et si le véhicule possède notre tag de chaise BOTC
            if (event.getDismounted().getScoreboardTags().contains("botc_chair")) {
                // Si la partie est en cours (ou simplement si le joueur est censé être assis)
                // On annule purement et simplement l'action de se lever !
                event.setCancelled(true);
                player.sendActionBar(Component.text("-> Vous devez rester assis pendant le Tribunal !", NamedTextColor.RED));
            }
        }
    }
}