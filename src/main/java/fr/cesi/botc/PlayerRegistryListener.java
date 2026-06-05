package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRegistryListener implements Listener {

    private final Botc main;

    public PlayerRegistryListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        if (!titleStr.equals("Registre du Tribunal")) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.LIME_WOOL) {
            player.closeInventory();
            main.getVoteManager().registerVote(player); // Déclenche ton système de vote existant
        }
        else if (clicked.getType() == Material.WRITABLE_BOOK) {
            // Bascule vers l'interface dynamique des têtes
            new NominationView().openNominationMenu(player, main);
        }
    }
}