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
            if (slot >= 45) break; // Sécurité : On s'arrête avant la dernière ligne pour laisser la place aux outils !

            // Si le joueur derrière cet UUID est OP (le Conteur), on l'ignore !
            if (Bukkit.getOfflinePlayer(uuid).isOp()) continue;

            BotcPlayer botcPlayer = main.getPlayersMap().get(uuid);
            ItemStack item = createPlayerHead(botcPlayer);

            inv.setItem(slot, item);
            slot++;
        }

        // 🌟 APPORT : Bouton de Téléportation Flash au Tribunal (Slot 45)
        ItemStack btnTp = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpMeta = btnTp.getItemMeta();
        if (tpMeta != null) {
            tpMeta.displayName(Component.text("⚡ Flash-TP au Tribunal", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));
            tpMeta.lore(List.of(
                    Component.text("Clique ici pour te téléporter instantanément", NamedTextColor.GRAY),
                    Component.text("au centre du cercle de discussion.", NamedTextColor.GRAY)
            ));
            btnTp.setItemMeta(tpMeta);
        }
        inv.setItem(45, btnTp);

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

            // Gestion du rôle (Ivrogne ou Normal)
            if (botcPlayer.getRealRole().equals("Ivrogne")) {
                lore.add(Component.text("Rôle REEL : Ivrogne", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                lore.add(Component.text("Rôle AFFICHÉ au joueur : " + botcPlayer.getDisplayedRole(), NamedTextColor.YELLOW));
            } else {
                lore.add(Component.text("Rôle : " + botcPlayer.getRealRole(), NamedTextColor.AQUA));
            }

            // 🌟 AJOUT ERGONOMIQUE : Petite notice d'utilisation pour le Conteur
            lore.add(Component.text(" "));
            lore.add(Component.text("🖱️ CLIC : Gérer la vie et le rôle", NamedTextColor.GRAY));
            lore.add(Component.text("⌨️ SHIFT + CLIC : Se TP sur lui", NamedTextColor.LIGHT_PURPLE));

            meta.lore(lore);
            head.setItemMeta(meta);
        }

        return head;
    }
}