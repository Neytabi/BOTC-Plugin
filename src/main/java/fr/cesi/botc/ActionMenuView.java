package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ActionMenuView {

    public void openActionMenu(Player admin, BotcPlayer targetBotc) {
        // Un inventaire de 9 cases (1 ligne)
        Component title = Component.text("Action : " + targetBotc.getPlayerName(), NamedTextColor.DARK_RED);
        Inventory inv = Bukkit.createInventory(null, 9, title);

        // Bouton Rôle (Slot 0)
        ItemStack roleItem = new ItemStack(Material.PAPER);
        ItemMeta roleMeta = roleItem.getItemMeta();
        if (roleMeta != null) {
            roleMeta.displayName(Component.text("Attribuer un Rôle", NamedTextColor.AQUA));
            List<Component> roleLore = new ArrayList<>();
            // Ligne 28 corrigée dans ActionMenuView.java
            roleLore.add(Component.text("Rôle actuel : " + targetBotc.getDisplayedRole(), NamedTextColor.YELLOW));
            roleMeta.lore(roleLore);
            roleItem.setItemMeta(roleMeta);
        }
        inv.setItem(0, roleItem);

        // 1. Bouton Vie/Mort (Slot 2)
        ItemStack lifeItem;
        ItemMeta lifeMeta;
        if (targetBotc.isAlive()) {
            lifeItem = new ItemStack(Material.IRON_SWORD);
            lifeMeta = lifeItem.getItemMeta();
            if (lifeMeta != null) {
                lifeMeta.displayName(Component.text("Tuer le joueur", NamedTextColor.RED));
            }
        } else {
            lifeItem = new ItemStack(Material.POTION);
            lifeMeta = lifeItem.getItemMeta();
            if (lifeMeta != null) {
                lifeMeta.displayName(Component.text("Ressusciter le joueur", NamedTextColor.GREEN));
            }
        }
        if (lifeMeta != null) lifeItem.setItemMeta(lifeMeta);
        inv.setItem(2, lifeItem);

        // 2. Bouton Accusation / Vote (Slot 4)
        ItemStack voteItem = new ItemStack(Material.BELL);
        ItemMeta voteMeta = voteItem.getItemMeta();
        if (voteMeta != null) {
            voteMeta.displayName(Component.text("Lancer une accusation (Vote)", NamedTextColor.YELLOW));
            voteItem.setItemMeta(voteMeta);
        }
        inv.setItem(4, voteItem);

        // 3. Bouton Jeton de Fantôme (Slot 6) - Uniquement si le joueur est mort
        if (!targetBotc.isAlive()) {
            ItemStack ghostItem;
            if (targetBotc.hasGhostVote()) {
                ghostItem = new ItemStack(Material.NETHER_STAR);
                ItemMeta ghostMeta = ghostItem.getItemMeta();
                if (ghostMeta != null) {
                    ghostMeta.displayName(Component.text("Retirer le jeton de vote", NamedTextColor.GOLD));
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text("Le fantome a encore son vote.", NamedTextColor.GRAY));
                    ghostMeta.lore(lore);
                    ghostItem.setItemMeta(ghostMeta);
                }
            } else {
                ghostItem = new ItemStack(Material.COAL);
                ItemMeta ghostMeta = ghostItem.getItemMeta();
                if (ghostMeta != null) {
                    ghostMeta.displayName(Component.text("Rendre le jeton de vote", NamedTextColor.DARK_GRAY));
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text("Le fantome a DEJA vote.", NamedTextColor.GRAY));
                    ghostMeta.lore(lore);
                    ghostItem.setItemMeta(ghostMeta);
                }
            }
            inv.setItem(6, ghostItem);
        }

        // Ouverture de l'inventaire
        admin.openInventory(inv);
    }
}