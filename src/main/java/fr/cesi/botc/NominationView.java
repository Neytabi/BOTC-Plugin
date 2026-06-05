package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class NominationView {

    public void openNominationMenu(Player player, Botc main) {
        Component title = Component.text("Nommer un suspect", NamedTextColor.RED);

        // Taille ajustable : 27 cases par défaut (extensible à 54 si nécessaire)
        Inventory inv = Bukkit.createInventory(null, 27, title);

        int slot = 0;
        for (BotcPlayer bp : main.getPlayersMap().values()) {
            // On demande à Bukkit de vérifier si l'UUID du BotcPlayer est OP
            if (Bukkit.getOfflinePlayer(bp.getPlayerUUID()).isOp()) continue;
            // On n'affiche que les suspects potentiels encore vivants
            if (!bp.isAlive()) continue;
            if (bp.getPlayerName().equalsIgnoreCase(player.getName())) continue; // Ne pas s'accuser soi-même

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta != null) {
                meta.displayName(Component.text(bp.getPlayerName(), NamedTextColor.YELLOW));
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(bp.getPlayerUUID()));
                head.setItemMeta(meta);
            }

            inv.setItem(slot, head);
            slot++;
            if (slot >= 27) break; // Sécurité de taille
        }

        player.openInventory(inv);
    }
}