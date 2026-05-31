package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrimoireView {

    private final Botc main;

    public GrimoireView(Botc main) {
        this.main = main;
    }

    public void openGrimoire(Player admin) {
        // Un double coffre fait 54 cases (6 lignes x 9 colonnes)
        Component title = Component.text("Grimoire du Conteur", NamedTextColor.DARK_PURPLE);
        Inventory inv = Bukkit.createInventory(null, 54, title);

        int slot = 0;
        // On parcourt tous les joueurs enregistrés dans notre modèle (la HashMap)
        for (UUID uuid : main.getPlayersMap().keySet()) {
            if (slot >= 54) break; // Sécurité pour ne pas dépasser la taille du coffre

            BotcPlayer botcPlayer = main.getPlayersMap().get(uuid);
            ItemStack item = createPlayerHead(botcPlayer);

            inv.setItem(slot, item);
            slot++;
        }

        // On ouvre magiquement l'inventaire à l'admin (le Conteur)
        admin.openInventory(inv);
    }

    private ItemStack createPlayerHead(BotcPlayer botcPlayer) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            // On attribue le pseudo du joueur à la tête pour afficher son vrai skin
            Player target = Bukkit.getPlayer(botcPlayer.getPlayerUUID());
            if (target != null) {
                meta.setOwningPlayer(target);
            }

            // Définition du nom et du Lore (description) en fonction de son état (Vivant/Mort)
            List<Component> lore = new ArrayList<>();

            if (botcPlayer.isAlive()) {
                meta.displayName(Component.text(botcPlayer.getPlayerName(), NamedTextColor.GREEN));
                lore.add(Component.text("Statut : VIVANT", NamedTextColor.GREEN));
            } else {
                meta.displayName(Component.text(botcPlayer.getPlayerName(), NamedTextColor.RED));
                lore.add(Component.text("Statut : MORT", NamedTextColor.RED));

                if (botcPlayer.hasGhostVote()) {
                    lore.add(Component.text("Jeton de vote : DISPONIBLE", NamedTextColor.YELLOW));
                } else {
                    lore.add(Component.text("Jeton de vote : UTILISÉ", NamedTextColor.GRAY));
                }
            }
            // À la place de l'ancienne ligne de rôle, mets ce bloc de conditions :
            if (botcPlayer.getRealRole().equals("Ivrogne")) {
                lore.add(Component.text("Rôle REEL : Ivrogne", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                lore.add(Component.text("Rôle AFFICHÉ au joueur : " + botcPlayer.getDisplayedRole(), NamedTextColor.YELLOW));
            } else {
                lore.add(Component.text("Rôle : " + botcPlayer.getRealRole(), NamedTextColor.AQUA));
            }

            meta.lore(lore);
            head.setItemMeta(meta);
        }

        return head;
    }
}