package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.UUID;

public class GrimoireListener implements Listener {

    private final Botc main;
    private final ActionMenuView actionMenuView = new ActionMenuView();

    public GrimoireListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // On vérifie que c'est bien un joueur qui a cliqué
        if (!(event.getWhoClicked() instanceof Player admin)) return;

        // On vérifie qu'on est bien dans l'interface du Grimoire
        if (!event.getView().title().equals(Component.text("Grimoire du Conteur", NamedTextColor.DARK_PURPLE))) {
            return;
        }

        // Sécurité : On empêche de voler l'item
        event.setCancelled(true);

        // On vérifie que le Conteur a cliqué sur une tête
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() != Material.PLAYER_HEAD) return;

        // On récupère les métadonnées de la tête
        SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
        if (meta == null || meta.getOwningPlayer() == null) return;

        // Récupération du profil du joueur ciblé
        UUID targetUUID = meta.getOwningPlayer().getUniqueId();
        BotcPlayer targetBotc = main.getPlayersMap().get(targetUUID);

        if (targetBotc != null) {
            // ON OUVRE LE SOUS-MENU !
            new ActionMenuView().openActionMenu(admin, targetBotc);
        }
    }
}