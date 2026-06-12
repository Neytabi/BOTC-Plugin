package fr.cesi.botc;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemInteractListener implements Listener {

    private final Botc main;

    public ItemInteractListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // --- INTERACTION DU CONTEUR (OP) ---
        if (player.isOp() && item.getType() == Material.ENCHANTED_BOOK) {
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String name = PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName());
                if (name.contains("Le Grimoire du Conteur")) {
                    event.setCancelled(true);
                    new ConteurMenuView().openConteurMenu(player, main);
                }
            }
            return;
        }

        // --- INTERACTION DES JOUEURS (NON-OP) ---
        if (!player.isOp() && item.getType() == Material.BOOK) {
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String name = PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName());
                if (name.contains("Registre du Tribunal")) {
                    event.setCancelled(true);
                    new PlayerRegistryView().openRegistryMenu(player, main);
                }
            }
        }
    }
}