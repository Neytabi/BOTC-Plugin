// 🔄 REMPLACE TOUTE TA CLASSE NominationView PAR CELLE-CI :
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
        Inventory inv = Bukkit.createInventory(null, 27, title); // 27 cases fixe pour caler la barre du bas

        int slot = 0;
        for (BotcPlayer bp : main.getPlayersMap().values()) {
            if (slot >= 18)
                break; // On laisse les deux premières lignes aux têtes (maximum 18 têtes)
            if (Bukkit.getOfflinePlayer(bp.getPlayerUUID()).isOp())
                continue;
            if (!bp.isAlive())
                continue;
            if (bp.getPlayerName().equalsIgnoreCase(player.getName()))
                continue;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(bp.getPlayerName(), NamedTextColor.YELLOW));
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(bp.getPlayerUUID()));
                head.setItemMeta(meta);
            }
            inv.setItem(slot, head);
            slot++;
        }

        player.openInventory(inv);
    }
}