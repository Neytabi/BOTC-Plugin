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

import java.util.ArrayList;
import java.util.List;

public class PlayerRegistryView {

    public void openRegistryMenu(Player player, Botc main) {
        Component title = Component.text("Registre du Tribunal", NamedTextColor.DARK_GREEN);
        Inventory inv = Bukkit.createInventory(null, 9, title);

        BotcPlayer bp = main.getPlayersMap().get(player.getUniqueId());

        // ==========================================
        //  GESTION DU BOUTON VOTE (Slot 0)
        // ==========================================
        ItemStack vote;
        ItemMeta vMeta;

        if (bp != null && !bp.isAlive() && !bp.hasGhostVote()) {
            vote = new ItemStack(Material.RED_WOOL);
            vMeta = vote.getItemMeta();
            if (vMeta != null) {
                vMeta.displayName(Component.text("x VOTE IMPOSSIBLE", NamedTextColor.RED));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Tu es mort et ton unique jeton de", NamedTextColor.GRAY));
                lore.add(Component.text("vote fantôme a déjà été consommé.", NamedTextColor.GRAY));
                vMeta.lore(lore);
                vote.setItemMeta(vMeta);
            }
        } else {
            vote = new ItemStack(Material.LIME_WOOL);
            vMeta = vote.getItemMeta();
            if (vMeta != null) {
                vMeta.displayName(Component.text(" LEVER LA MAIN (Voter OUI)", NamedTextColor.GREEN));
                List<Component> lore = new ArrayList<>();
                if (bp != null && !bp.isAlive()) {
                    lore.add(Component.text(" ATTENTION : C'est ton DERNIER vote fantôme !", NamedTextColor.YELLOW));
                } else {
                    lore.add(Component.text("Clique pour voter OUI à l'accusation en cours.", NamedTextColor.GRAY));
                }
                vMeta.lore(lore);
                vote.setItemMeta(vMeta);
            }
        }
        inv.setItem(0, vote);

        // ==========================================
        //  GESTION DU BOUTON NOMINATION (Slot 2)
        // ==========================================
        ItemStack accuse;
        ItemMeta aMeta;

        if (bp != null && !bp.isAlive()) {
            accuse = new ItemStack(Material.BOOK);
            aMeta = accuse.getItemMeta();
            if (aMeta != null) {
                aMeta.displayName(Component.text("x NOMINATION IMPOSSIBLE", NamedTextColor.RED));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Tu es un fantôme. Les morts ne peuvent", NamedTextColor.GRAY));
                lore.add(Component.text("plus lancer d'accusations au Tribunal.", NamedTextColor.GRAY));
                aMeta.lore(lore);
                accuse.setItemMeta(aMeta);
            }
        } else {
            accuse = new ItemStack(Material.WRITABLE_BOOK);
            aMeta = accuse.getItemMeta();
            if (aMeta != null) {
                aMeta.displayName(Component.text(" NOMMER UN SUSPECT", NamedTextColor.GOLD));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Clique pour ouvrir le registre des suspects", NamedTextColor.GRAY));
                lore.add(Component.text("et envoyer quelqu'un à l'échafaud.", NamedTextColor.GRAY));
                aMeta.lore(lore);
                accuse.setItemMeta(aMeta);
            }
        }
        inv.setItem(2, accuse);

        // ==========================================
        //  RAPPEL DU RÔLE SECRET (Slot 4 - Centre)
        // ==========================================
        ItemStack roleInfo = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta rMeta = roleInfo.getItemMeta();
        if (rMeta != null) {
            rMeta.displayName(Component.text(" TON RÔLE ACTUEL", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));
            List<Component> rLore = new ArrayList<>();

            if (bp != null && bp.getDisplayedRole() != null && !bp.getDisplayedRole().isEmpty()) {
                rLore.add(Component.text("Tu incarnes : ", NamedTextColor.WHITE)
                        .append(Component.text(bp.getDisplayedRole(), NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
                rLore.add(Component.text("-----------------------------------", NamedTextColor.GRAY));
                rLore.add(Component.text(bp.getRoleDescription(), NamedTextColor.GRAY));
            } else {
                rLore.add(Component.text("Aucun rôle ne t'a été attribué", NamedTextColor.RED));
                rLore.add(Component.text("par le Conteur pour le moment.", NamedTextColor.GRAY));
            }

            rMeta.lore(rLore);
            roleInfo.setItemMeta(rMeta);
        }
        inv.setItem(4, roleInfo);

        // ==========================================
        //  GESTION DU BOUTON QUESTION (Slot 6)
        // ==========================================
        ItemStack question = new ItemStack(Material.FEATHER);
        ItemMeta qMeta = question.getItemMeta();
        if (qMeta != null) {
            qMeta.displayName(Component.text(" POSER UNE QUESTION", NamedTextColor.AQUA));
            List<Component> qLore = new ArrayList<>();
            qLore.add(Component.text("Clique pour envoyer une question privée", NamedTextColor.GRAY));
            qLore.add(Component.text("aux Conteurs directement depuis le chat.", NamedTextColor.GRAY));
            qMeta.lore(qLore);
            question.setItemMeta(qMeta);
        }
        inv.setItem(6, question);

        // ==========================================
        //  GESTION DU BOUTON PAROLE (Slot 8)
        // ==========================================
        ItemStack parole = new ItemStack(Material.PAPER);
        ItemMeta pMeta = parole.getItemMeta();
        if (pMeta != null) {
            pMeta.displayName(Component.text(" DEMANDER LA PAROLE", NamedTextColor.GREEN));
            List<Component> pLore = new ArrayList<>();
            pLore.add(Component.text("Bouton réservé aux fantômes pour", NamedTextColor.GRAY));
            pLore.add(Component.text("demander une intervention au Conseil.", NamedTextColor.GRAY));
            pMeta.lore(pLore);
            parole.setItemMeta(pMeta);
        }
        inv.setItem(8, parole);

        player.openInventory(inv);
    }
}