package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerRegistryView {

    public void openRegistryMenu(Player player) {
        Component title = Component.text("Registre du Tribunal", NamedTextColor.DARK_GREEN);
        Inventory inv = Bukkit.createInventory(null, 9, title);

        // Bouton Vote OUI (Slot 2)
        ItemStack vote = new ItemStack(Material.LIME_WOOL);
        ItemMeta vMeta = vote.getItemMeta();
        if (vMeta != null) {
            vMeta.displayName(Component.text("🗳️ LEVER LA MAIN (Voter OUI)", NamedTextColor.GREEN));
            vote.setItemMeta(vMeta);
        }
        inv.setItem(2, vote);

        // Bouton Accuser / Nommer (Slot 6)
        ItemStack accuse = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta aMeta = accuse.getItemMeta();
        if (aMeta != null) {
            aMeta.displayName(Component.text("📣 NOMMER UN SUSPECT", NamedTextColor.RED));
            accuse.setItemMeta(aMeta);
        }
        inv.setItem(6, accuse);

        player.openInventory(inv);
    }
}