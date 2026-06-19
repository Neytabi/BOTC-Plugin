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

import java.util.List;

public class ConteurMenuView {

    public void openConteurMenu(Player admin, Botc main) {
        Component title = Component.text("Menu Principal du Conteur", NamedTextColor.DARK_PURPLE);
        Inventory inv = Bukkit.createInventory(null, 18, title);

        // Bouton 1 : Ouvrir le Grimoire des Joueurs (Slot 0)
        ItemStack grimoire = new ItemStack(Material.NETHER_STAR);
        ItemMeta gMeta = grimoire.getItemMeta();
        if (gMeta != null) {
            gMeta.displayName(Component.text("Ouvrir le Grimoire", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            grimoire.setItemMeta(gMeta);
        }
        inv.setItem(0, grimoire);

        // Bouton 2 : Gestion du Temps Jour/Nuit (Slot 1)
        ItemStack timeItem = new ItemStack(Material.CLOCK);
        ItemMeta tMeta = timeItem.getItemMeta();
        if (tMeta != null) {
            tMeta.displayName(Component.text("Alterner Jour et Nuit", NamedTextColor.AQUA));
            timeItem.setItemMeta(tMeta);
        }
        inv.setItem(1, timeItem);

        // Bouton 3 : Annonce des Morts (Slot 2)
        ItemStack boutonMort = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta metaMort = boutonMort.getItemMeta();
        if (metaMort != null) {
            metaMort.displayName(Component.text("Annoncer les Morts", NamedTextColor.RED).decorate(TextDecoration.BOLD));
            boutonMort.setItemMeta(metaMort);
        }
        inv.setItem(2, boutonMort);

        // Bouton 4 : Temps Libre (Slot 3)
        ItemStack boutonTempsLibre = new ItemStack(Material.FEATHER);
        ItemMeta metaTempsLibre = boutonTempsLibre.getItemMeta();
        if (metaTempsLibre != null) {
            metaTempsLibre.displayName(Component.text("Lancer le Temps Libre", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            boutonTempsLibre.setItemMeta(metaTempsLibre);
        }
        inv.setItem(3, boutonTempsLibre);

        // Bouton 5 : Début du Conseil (Slot 4)
        ItemStack boutonConseil = new ItemStack(Material.BELL);
        ItemMeta metaConseil = boutonConseil.getItemMeta();
        if (metaConseil != null) {
            metaConseil.displayName(Component.text("Ouvrir le Conseil (GPS)", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            boutonConseil.setItemMeta(metaConseil);
        }
        inv.setItem(4, boutonConseil);

        // Bouton 6 : Gestion du Conseil Assis/Debout (Slot 5)
        ItemStack chairItem = new ItemStack(Material.OAK_STAIRS);
        ItemMeta cMeta = chairItem.getItemMeta();
        if (cMeta != null) {
            cMeta.displayName(Component.text("Ordre : Assis / Debout", NamedTextColor.YELLOW));
            chairItem.setItemMeta(cMeta);
        }
        inv.setItem(5, chairItem);

        // 🌟 APPORT : Bouton 7 : Visibilité des Pseudos (Slot 6)
        ItemStack nameTagsItem = new ItemStack(Material.NAME_TAG);
        ItemMeta ntMeta = nameTagsItem.getItemMeta();
        if (ntMeta != null) {
            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");
            boolean hidden = nightTeam != null && !nightTeam.getEntries().isEmpty();
            String status = hidden ? "CACHÉS (Anonymat)" : "VISIBLES";

            ntMeta.displayName(Component.text("🏷️ Visibilité Pseudos : " + status, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
            ntMeta.lore(List.of(
                    Component.text("CLIC : Basculer l'affichage des nametags", NamedTextColor.GRAY),
                    Component.text("pour masquer ou afficher les noms en jeu.", NamedTextColor.GRAY)
            ));
            nameTagsItem.setItemMeta(ntMeta);
        }
        inv.setItem(6, nameTagsItem);

        // 🌟 APPORT : Bouton 8 : Coupe-parole Vocal Proximité SVC (Slot 7)
        ItemStack muteItem = new ItemStack(Material.JUKEBOX);
        ItemMeta mGlobalMeta = muteItem.getItemMeta();
        if (mGlobalMeta != null) {
            mGlobalMeta.displayName(Component.text("🎙️ Modération Vocale (SVC)", NamedTextColor.RED).decorate(TextDecoration.BOLD));
            mGlobalMeta.lore(List.of(
                    Component.text("CLIC GAUCHE : Réclamer le silence (/botc silence)", NamedTextColor.GRAY),
                    Component.text("CLIC DROIT : Réactiver les micros (/botc paroleall)", NamedTextColor.GRAY)
            ));
            muteItem.setItemMeta(mGlobalMeta);
        }
        inv.setItem(7, muteItem);

        // 🌟 APPORT : Bouton 9 : Système de Scrutin / Vote de Map (Slot 8)
        ItemStack voteMapItem = new ItemStack(Material.MAP);
        ItemMeta vmMeta = voteMapItem.getItemMeta();
        if (vmMeta != null) {
            vmMeta.displayName(Component.text("🗳️ Lancer un Vote de Map", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));
            vmMeta.lore(List.of(
                    Component.text("CLIC GAUCHE : Ouvrir le vote aux joueurs", NamedTextColor.GRAY),
                    Component.text("CLIC DROIT : Clôturer et charger la map gagnante", NamedTextColor.GRAY)
            ));
            voteMapItem.setItemMeta(vmMeta);
        }
        inv.setItem(8, voteMapItem);


        // ==========================================================
        // 🛠️ BARRE DE SETUP ET CONFIGURATION (Dernière ligne du Menu)
        // ==========================================================
        int baseSlot = inv.getSize() - 9; // Calcule automatiquement le début de la ligne du bas (Slot 9)

        // Slot +0 : 🗺️ Menu des Presets Maps
        ItemStack btnPreset = new ItemStack(Material.FILLED_MAP);
        var pMeta = btnPreset.getItemMeta();
        if (pMeta != null) {
            pMeta.displayName(Component.text("Gérer les Maps / Presets", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));
            pMeta.lore(List.of(Component.text("Clique ici pour créer, sélectionner ou", NamedTextColor.GRAY),
                    Component.text("supprimer tes arènes de jeu.", NamedTextColor.GRAY)));
            btnPreset.setItemMeta(pMeta);
        }
        inv.setItem(baseSlot, btnPreset);

        // Slot +1 : Vitre séparatrice
        ItemStack separator = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        var sMeta = separator.getItemMeta();
        if (sMeta != null) { sMeta.displayName(Component.text(" ")); separator.setItemMeta(sMeta); }
        inv.setItem(baseSlot + 1, separator);

        // Slot +2 : 🏛️ Enregistrer le centre du Tribunal
        ItemStack btnTribunal = new ItemStack(Material.BEACON);
        var tribMeta = btnTribunal.getItemMeta();
        if (tribMeta != null) {
            tribMeta.displayName(Component.text("Enregistrer le centre du Tribunal", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            tribMeta.lore(List.of(Component.text("Définit ta position actuelle comme le", NamedTextColor.GRAY),
                    Component.text("point central du cercle de discussion.", NamedTextColor.GRAY)));
            btnTribunal.setItemMeta(tribMeta);
        }
        inv.setItem(baseSlot + 2, btnTribunal);

        // Slot +3 : 🪑 Ajouter une Chaise
        ItemStack btnChair = new ItemStack(Material.OAK_STAIRS);
        var setupChairMeta = btnChair.getItemMeta();
        if (setupChairMeta != null) {
            setupChairMeta.displayName(Component.text("Ajouter un Siège au Cercle", NamedTextColor.YELLOW));
            setupChairMeta.lore(List.of(Component.text("Enregistre ta position comme une chaise.", NamedTextColor.GRAY),
                    Component.text("Fais le tour du cercle dans l'ordre !", NamedTextColor.GRAY)));
            btnChair.setItemMeta(setupChairMeta);
        }
        inv.setItem(baseSlot + 3, btnChair);

        // Slot +4 : 🚪 Ajouter une Chambre
        ItemStack btnRoom = new ItemStack(Material.IRON_DOOR);
        var rMeta = btnRoom.getItemMeta();
        if (rMeta != null) {
            rMeta.displayName(Component.text("Enregistrer une Porte de Chambre", NamedTextColor.AQUA));
            rMeta.lore(List.of(Component.text("Enregistre l'emplacement de la chambre", NamedTextColor.GRAY),
                    Component.text("et placera automatiquement la nuit la tete du joueurs associé ", NamedTextColor.GRAY),
                    Component.text("a cette chambre dans le sens oposé de votre vue.", NamedTextColor.GRAY)));
            btnRoom.setItemMeta(rMeta);
        }
        inv.setItem(baseSlot + 4, btnRoom);

        // Slot +5 : 💀 Enregistrer ou Supprimer l'Estrade de Mort
        ItemStack btnDeath = new ItemStack(Material.WITHER_SKELETON_SKULL);
        var dMeta = btnDeath.getItemMeta();
        if (dMeta != null) {
            dMeta.displayName(Component.text("Estrade d'Exécution", NamedTextColor.RED).decorate(TextDecoration.BOLD));
            dMeta.lore(List.of(
                    Component.text("CLIC GAUCHE : Enregistrer ta position", NamedTextColor.GRAY),
                    Component.text("CLIC DROIT : Supprimer (Exécution sur place)", NamedTextColor.DARK_RED)
            ));
            btnDeath.setItemMeta(dMeta);
        }
        inv.setItem(baseSlot + 5, btnDeath);

        // 🏷️ Visibilité des Pseudos (Slot 6) - Version Sécurisée sans doublon
        ItemStack nameTagsButton = new ItemStack(Material.NAME_TAG);
        ItemMeta nameTagsMeta = nameTagsButton.getItemMeta();
        if (nameTagsMeta != null) {
            boolean hidden = main.isNameTagsHidden();
            String status = hidden ? "CACHÉS (Anonymat Actif)" : "VISIBLES";

            nameTagsMeta.displayName(Component.text("🏷️ Visibilité Pseudos : ", NamedTextColor.YELLOW)
                    .append(Component.text(status, hidden ? NamedTextColor.RED : NamedTextColor.GREEN).decorate(TextDecoration.BOLD)));
            nameTagsMeta.lore(List.of(
                    Component.text("CLIC : Basculer l'affichage des nametags", NamedTextColor.GRAY),
                    Component.text("pour masquer ou afficher les noms en jeu.", NamedTextColor.GRAY)
            ));
            nameTagsButton.setItemMeta(nameTagsMeta);
        }
        inv.setItem(6, nameTagsButton);

        // Slot +7 : ✨ Outils de vérification et de Nettoyage (Blaze Powder)
        ItemStack btnTest = new ItemStack(Material.BLAZE_POWDER);
        var testMeta = btnTest.getItemMeta();
        if (testMeta != null) {
            testMeta.displayName(Component.text("Gestion des Repères", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            testMeta.lore(List.of(
                    Component.text("CLIC GAUCHE : Voir les CHAISES", NamedTextColor.GRAY),
                    Component.text("CLIC DROIT : Voir les CHAMBRES", NamedTextColor.GRAY),
                    Component.text("SHIFT + GAUCHE : Supprimer toutes les chaises", NamedTextColor.RED),
                    Component.text("SHIFT + DROIT : Supprimer toutes les chambres", NamedTextColor.RED)
            ));
            btnTest.setItemMeta(testMeta);
        }
        inv.setItem(baseSlot + 7, btnTest);

        // Slot +8 : 🔄 Bouton de Reset global de partie
        ItemStack btnReset = new ItemStack(Material.BARRIER);
        var resetMeta = btnReset.getItemMeta();
        if (resetMeta != null) {
            resetMeta.displayName(Component.text("Réinitialiser la Partie", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));
            resetMeta.lore(List.of(Component.text("Ressuscite tout le monde, nettoie les", NamedTextColor.GRAY),
                    Component.text("rôles et randomise les places", NamedTextColor.GRAY)));
            btnReset.setItemMeta(resetMeta);
        }
        inv.setItem(baseSlot + 8, btnReset);

        admin.openInventory(inv);
    }
}