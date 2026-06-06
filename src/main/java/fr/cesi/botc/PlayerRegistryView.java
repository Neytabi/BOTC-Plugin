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

        // Récupération du profil BOTC du joueur qui regarde le menu
        BotcPlayer bp = main.getPlayersMap().get(player.getUniqueId());

        // ==========================================
        // 🗳️ GESTION DU BOUTON VOTE (Slot 2)
        // ==========================================
        ItemStack vote;
        ItemMeta vMeta;

        // Si le joueur est mort ET qu'il a DEJA utilisé son unique vote fantôme
        if (bp != null && !bp.isAlive() && !bp.hasGhostVote()) {
            vote = new ItemStack(Material.RED_WOOL); // 🔴 LAINE ROUGE : Vote épuisé !
            vMeta = vote.getItemMeta();
            if (vMeta != null) {
                vMeta.displayName(Component.text("❌ VOTE IMPOSSIBLE", NamedTextColor.RED));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Tu es mort et ton unique jeton de", NamedTextColor.GRAY));
                lore.add(Component.text("vote fantôme a déjà été consommé.", NamedTextColor.GRAY));
                vMeta.lore(lore);
                vote.setItemMeta(vMeta);
            }
        } else {
            vote = new ItemStack(Material.LIME_WOOL); // 🟢 LAINE VERTE : Peut voter (Vivant ou Fantôme avec jeton)
            vMeta = vote.getItemMeta();
            if (vMeta != null) {
                vMeta.displayName(Component.text("🗳️ LEVER LA MAIN (Voter OUI)", NamedTextColor.GREEN));
                List<Component> lore = new ArrayList<>();
                if (bp != null && !bp.isAlive()) {
                    lore.add(Component.text("⚠️ ATTENTION : C'est ton DERNIER vote fantôme !", NamedTextColor.YELLOW));
                } else {
                    lore.add(Component.text("Clique pour voter OUI à l'accusation en cours.", NamedTextColor.GRAY));
                }
                vMeta.lore(lore);
                vote.setItemMeta(vMeta);
            }
        }
        inv.setItem(2, vote);

        // ==========================================
        // 📢 GESTION DU BOUTON NOMINATION (Slot 6)
        // ==========================================
        ItemStack accuse;
        ItemMeta aMeta;

        // Si le joueur est mort : pas le droit de nommer !
        if (bp != null && !bp.isAlive()) {
            accuse = new ItemStack(Material.BOOK); // 📖 Transformé en bête livre normal (ou BARRIER)
            aMeta = accuse.getItemMeta();
            if (aMeta != null) {
                aMeta.displayName(Component.text("❌ NOMINATION IMPOSSIBLE", NamedTextColor.RED));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Tu es un fantôme. Les morts ne peuvent", NamedTextColor.GRAY));
                lore.add(Component.text("plus lancer d'accusations au Tribunal.", NamedTextColor.GRAY));
                aMeta.lore(lore);
                accuse.setItemMeta(aMeta);
            }
        } else {
            accuse = new ItemStack(Material.WRITABLE_BOOK); // 📝 Plume active pour les vivants
            aMeta = accuse.getItemMeta();
            if (aMeta != null) {
                aMeta.displayName(Component.text("📣 NOMMER UN SUSPECT", NamedTextColor.GOLD));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Clique pour ouvrir le registre des suspects", NamedTextColor.GRAY));
                lore.add(Component.text("et envoyer quelqu'un à l'échafaud.", NamedTextColor.GRAY));
                aMeta.lore(lore);
                accuse.setItemMeta(aMeta);
            }
        }
        inv.setItem(6, accuse);

        // ==========================================
        // 🎭 RAPPEL DU RÔLE SECRET (Slot 4 - Centre)
        // ==========================================
        ItemStack roleInfo = new ItemStack(Material.ENCHANTED_BOOK); // Livre magique brillant
        ItemMeta rMeta = roleInfo.getItemMeta();
        if (rMeta != null) {
            rMeta.displayName(Component.text("🎭 TON RÔLE ACTUEL", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));
            List<Component> rLore = new ArrayList<>();

            // Si le joueur a un rôle enregistré dans son profil BOTC
            if (bp != null && bp.getDisplayedRole() != null && !bp.getDisplayedRole().isEmpty()) {
                rLore.add(Component.text("Tu incarnes : ", NamedTextColor.WHITE)
                        .append(Component.text(bp.getDisplayedRole(), NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
                rLore.add(Component.text("-----------------------------------", NamedTextColor.GRAY));

                // Si tu as une fonction getDisplayedRoleDesc() ou similaire dans ton BotcPlayer :
                rLore.add(Component.text(bp.getRoleDescription(), NamedTextColor.GRAY));
            } else {
                rLore.add(Component.text("Aucun rôle ne t'a été attribué", NamedTextColor.RED));
                rLore.add(Component.text("par le Conteur pour le moment.", NamedTextColor.GRAY));
            }

            rMeta.lore(rLore);
            roleInfo.setItemMeta(rMeta);
        }
        inv.setItem(4, roleInfo); // Posé pile au milieu !

        player.openInventory(inv);
    }
}