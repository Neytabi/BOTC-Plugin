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

public class RoleSelectionView {

    public void openRoleMenu(Player admin, BotcPlayer target) {
        Component title = Component.text("Assigner Rôle : " + target.getPlayerName(), NamedTextColor.BLUE);
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // --- CITADINS (Laine Bleue) ---
        // Clic Gauche = Donner le rôle | Clic Droit = Assigner ce faux rôle à l'Ivrogne
        addRoleItem(inv, 0, "Lavandière", Material.BLUE_WOOL, "Première nuit : Apprend quel joueur a un rôle Citadin spécifique.");
        addRoleItem(inv, 1, "Enquêteur", Material.BLUE_WOOL, "Première nuit : Apprend quel joueur a un rôle Sbire spécifique.");
        addRoleItem(inv, 2, "Chef", Material.BLUE_WOOL, "Première nuit : Apprend le nombre de paires de Démons côte à côte.");
        addRoleItem(inv, 3, "Archiviste", Material.BLUE_WOOL, "Première nuit : Apprend quel joueur a un rôle Étranger spécifique.");
        addRoleItem(inv, 4, "Empathe", Material.BLUE_WOOL, "Chaque nuit : Apprend le nombre de Méchants vivants à ses côtés (0, 1, 2).");
        addRoleItem(inv, 5, "Voyante", Material.BLUE_WOOL, "Chaque nuit : Teste 2 joueurs pour trouver le Diablotin (Attention au Leurre).");
        addRoleItem(inv, 6, "Fossoyeur", Material.BLUE_WOOL, "Chaque nuit : Si quelqu'un a été exécuté, apprend son vrai rôle.");
        addRoleItem(inv, 7, "Croque-mort", Material.BLUE_WOOL, "Si meurt la nuit, désigne un joueur pour apprendre son identité.");
        addRoleItem(inv, 8, "Moine", Material.BLUE_WOOL, "Chaque nuit : Protège un autre joueur contre le Démon.");
        addRoleItem(inv, 9, "Garde / Soldat", Material.BLUE_WOOL, "Ne peut pas mourir du Diablotin la nuit.");
        addRoleItem(inv, 10, "Pourfendeur", Material.BLUE_WOOL, "Une fois par partie (Jour) : Désigne une cible. Si c'est le Diablotin, il meurt.");
        addRoleItem(inv, 11, "Vierge", Material.BLUE_WOOL, "Si nominée par un Citadin, le nominateur est exécuté instantanément.");
        addRoleItem(inv, 12, "Maire", Material.BLUE_WOOL, "Peut être sauvé la nuit. Si 3 vivants sans exécution : Victoire des Citadins.");

        // --- ÉTRANGERS (Laine Jaune) ---
        addRoleItem(inv, 18, "Majordome", Material.YELLOW_WOOL, "Doit désigner un Maître la nuit et voter uniquement si son Maître vote.");
        addRoleItem(inv, 19, "Reclus", Material.YELLOW_WOOL, "Peut faussement apparaître comme un rôle Méchant ou un Démon.");
        addRoleItem(inv, 20, "Saint", Material.YELLOW_WOOL, "Si le Saint est exécuté au vote, les Citadins perdent instantanément.");
        addRoleItem(inv, 21, "Ivrogne (Pur)", Material.YELLOW_WOOL, "Rôle technique. Utilisez le CLIC DROIT sur un Citadin pour créer un Ivrogne.");

        // --- SBIRES (Laine Orange) ---
        addRoleItem(inv, 27, "Empoisonneur", Material.ORANGE_WOOL, "Chaque nuit : Empoisonne un joueur (pouvoirs inutiles ou fausses infos).");
        addRoleItem(inv, 28, "Baron", Material.ORANGE_WOOL, "En jeu : Ajoute automatiquement deux rôles Étrangers dans la partie.");
        addRoleItem(inv, 29, "Espion", Material.ORANGE_WOOL, "Apparaît comme Citadin. Première nuit : Reçoit les rôles de TOUS les joueurs.");
        addRoleItem(inv, 30, "Femme Écarlate", Material.ORANGE_WOOL, "Si >=5 vivants à la mort du Diablotin, elle devient le nouveau Diablotin.");

        // --- DÉMONS (Laine Rouge) ---
        addRoleItem(inv, 36, "Diablotin", Material.RED_WOOL, "Chef Démon. Tue la nuit. Peut se tuer pour passer son rôle à un Sbire.");

        admin.openInventory(inv);
    }

    private void addRoleItem(Inventory inv, int slot, String name, Material mat, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, NamedTextColor.WHITE));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(desc, NamedTextColor.GRAY));
            if (mat == Material.BLUE_WOOL) {
                lore.add(Component.text(""));
                lore.add(Component.text("[CLIC GAUCHE] Attribuer normalement", NamedTextColor.GREEN));
                lore.add(Component.text("[CLIC DROIT] Attribuer en tant qu'IVROGNE", NamedTextColor.GOLD));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }
}