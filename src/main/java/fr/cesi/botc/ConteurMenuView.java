package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConteurMenuView {

    public void openConteurMenu(Player admin) {
        Component title = Component.text("Menu Principal du Conteur", NamedTextColor.DARK_PURPLE);
        Inventory inv = Bukkit.createInventory(null, 9, title);

        // Bouton 1 : Ouvrir le Grimoire des Joueurs (Slot 2)
        ItemStack grimoire = new ItemStack(Material.NETHER_STAR);
        ItemMeta gMeta = grimoire.getItemMeta();
        if (gMeta != null) {
            gMeta.displayName(Component.text("👁️ Ouvrir le Grimoire", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            grimoire.setItemMeta(gMeta);
        }
        inv.setItem(0, grimoire);

        // Bouton 2 : Gestion du Temps Jour/Nuit (Slot 4)
        ItemStack timeItem = new ItemStack(Material.CLOCK);
        ItemMeta tMeta = timeItem.getItemMeta();
        if (tMeta != null) {
            tMeta.displayName(Component.text("☀️/🌙 Alterner Jour et Nuit", NamedTextColor.AQUA));
            timeItem.setItemMeta(tMeta);
        }
        inv.setItem(1, timeItem);

        // 1. Bouton Annonce des Morts (Un crâne de Wither)
        ItemStack boutonMort = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta metaMort = boutonMort.getItemMeta();
        if (metaMort != null) {
            metaMort.displayName(Component.text("💀 Annoncer les Morts", NamedTextColor.RED).decorate(TextDecoration.BOLD));
            boutonMort.setItemMeta(metaMort);
        }
        inv.setItem(2, boutonMort);

        // 2. Bouton Temps Libre (Une plume)
        ItemStack boutonTempsLibre = new ItemStack(Material.FEATHER);
        ItemMeta metaTempsLibre = boutonTempsLibre.getItemMeta();
        if (metaTempsLibre != null) {
            metaTempsLibre.displayName(Component.text("🗣️ Lancer le Temps Libre", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            boutonTempsLibre.setItemMeta(metaTempsLibre);
        }
        inv.setItem(3, boutonTempsLibre);

        // 3. Bouton Début du Conseil (Une cloche)
        ItemStack boutonConseil = new ItemStack(Material.BELL);
        ItemMeta metaConseil = boutonConseil.getItemMeta();
        if (metaConseil != null) {
            metaConseil.displayName(Component.text("⚖️ Ouvrir le Conseil (GPS)", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            boutonConseil.setItemMeta(metaConseil);
        }
        inv.setItem(4, boutonConseil);

        // Bouton 3 : Gestion du Conseil Assis/Debout (Slot 6)
        ItemStack chairItem = new ItemStack(Material.OAK_STAIRS);
        ItemMeta cMeta = chairItem.getItemMeta();
        if (cMeta != null) {
            cMeta.displayName(Component.text("🪑 Ordre : Assis / Debout", NamedTextColor.YELLOW));
            chairItem.setItemMeta(cMeta);
        }
        inv.setItem(5, chairItem);

        admin.openInventory(inv);
    }
}