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

public class GrimoireView {

    private final Botc main;

    public GrimoireView(Botc main) {
        this.main = main;
    }

    public void openGrimoire(Player admin) {
        // Un double coffre fait 54 cases (6 lignes x 9 colonnes)
        Component title = Component.text("Grimoire du Conteur", NamedTextColor.DARK_PURPLE);
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // ==========================================================
        //  ALIGNEMENT DU GRIMOIRE SUR L'ORDRE DES CHAISES
        // ==========================================================
        List<BotcPlayer> sortedPlayers = new ArrayList<>(main.getPlayersMap().values());

        // 1. On filtre les MJ pour ne pas encombrer le Grimoire
        sortedPlayers.removeIf(bp -> Bukkit.getOfflinePlayer(bp.getPlayerUUID()).isOp());

        // 2. On trie : d'abord par index de chaise, puis par ordre alphabétique si pas
        // assis
        sortedPlayers.sort((p1, p2) -> {
            int idx1 = p1.getChairIndex();
            int idx2 = p2.getChairIndex();

            if (idx1 != -1 && idx2 != -1)
                return Integer.compare(idx1, idx2);
            if (idx1 != -1)
                return -1; // p1 a une chaise, il passe devant
            if (idx2 != -1)
                return 1; // p2 a une chaise, il passe devant
            return p1.getPlayerName().compareToIgnoreCase(p2.getPlayerName()); // Tri alphabétique par défaut
        });

        // 3. On remplit l'inventaire avec la liste parfaitement ordonnée
        int slot = 0;
        for (BotcPlayer botcPlayer : sortedPlayers) {
            if (slot >= 45)
                break; // Sécurité : On s'arrête avant la dernière ligne pour laisser la place aux
                       // outils !

            ItemStack item = createPlayerHead(botcPlayer);
            inv.setItem(slot, item);
            slot++;
        }

        //  APPORT : Bouton de Téléportation Flash au Tribunal (Slot 45)
        ItemStack btnTp = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpMeta = btnTp.getItemMeta();
        if (tpMeta != null) {
            tpMeta.displayName(Component.text(" Flash-TP au Tribunal", NamedTextColor.LIGHT_PURPLE)
                    .decorate(TextDecoration.BOLD));
            tpMeta.lore(List.of(
                    Component.text("Clique ici pour te téléporter instantanément", NamedTextColor.GRAY),
                    Component.text("au centre du cercle de discussion.", NamedTextColor.GRAY)));
            btnTp.setItemMeta(tpMeta);
        }
        inv.setItem(45, btnTp);

        // Slot 46 : Liaison NameTag synchro dans le Grimoire
        ItemStack btnNames = new ItemStack(Material.NAME_TAG);
        ItemMeta namesMeta = btnNames.getItemMeta();
        if (namesMeta != null) {
            boolean hidden = main.isNameTagsHidden();
            String status = hidden ? "CACHÉS (Anonymat Actif)" : "VISIBLES";

            namesMeta.displayName(
                    Component.text(" Visibilité des Pseudos", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
            namesMeta.lore(List.of(
                    Component.text("Statut actuel : ", NamedTextColor.GRAY)
                            .append(Component.text(status, hidden ? NamedTextColor.RED : NamedTextColor.GREEN)),
                    Component.text(" CLIC : Alterner la visibilité pour tous", NamedTextColor.GRAY)));
            btnNames.setItemMeta(namesMeta);
        }
        inv.setItem(46, btnNames);

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

            // Définition du nom et du Lore (description) en fonction de son état
            // (Vivant/Mort)
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
                lore.add(Component.text("Rôle AFFICHÉ au joueur : " + botcPlayer.getDisplayedRole(),
                        NamedTextColor.YELLOW));
            } else {
                lore.add(Component.text("Rôle : " + botcPlayer.getRealRole(), NamedTextColor.AQUA));
            }

            //  AJOUT ERGONOMIQUE : Petite notice d'utilisation pour le Conteur
            lore.add(Component.text(" "));
            lore.add(Component.text(" CLIC : Gérer la vie et le rôle", NamedTextColor.GRAY));
            lore.add(Component.text(" SHIFT + CLIC : Se TP sur lui", NamedTextColor.LIGHT_PURPLE));

            meta.lore(lore);
            head.setItemMeta(meta);
        }

        return head;
    }
}