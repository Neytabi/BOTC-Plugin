package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConteurMenuListener implements Listener {

    private final Botc main;

    public ConteurMenuListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin))
            return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        if (!titleStr.equals("Menu Principal du Conteur"))
            return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR)
            return;

        int slot = event.getSlot();

        // ==========================================================
        // 🎮 LIGNE 1 : SCRIPT DE JEU / GAMEPLAY (Slots 0 à 8)
        // ==========================================================
        if (slot < 9) {
            switch (slot) {
                case 0 -> { // NETHER_STAR : Grimoire
                    admin.closeInventory();
                    new GrimoireView(main).openGrimoire(admin);
                }
                case 1 -> { // CLOCK : Alterner Jour/Nuit
                    admin.closeInventory();
                    admin.performCommand(main.isNight() ? "botc jour" : "botc nuit");
                }
                case 2 -> { // WITHER_SKELETON_SKULL : Annoncer les Morts
                    admin.closeInventory();
                    admin.performCommand("botc mort");
                }
                case 3 -> { // FEATHER : Temps Libre
                    admin.closeInventory();
                    admin.performCommand("botc tempslibre");
                }
                case 4 -> { // BELL : Ouvrir le Conseil
                    admin.closeInventory();
                    admin.performCommand("botc conseil");
                }
                case 5 -> { // OAK_STAIRS : Alternance Assis / Debout dynamique
                    admin.closeInventory();
                    boolean IsAnyoneSeated = false;
                    for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                        if (p.isOp())
                            continue;
                        if (p.isInsideVehicle()) {
                            IsAnyoneSeated = true;
                            break;
                        }
                    }
                    admin.performCommand(IsAnyoneSeated ? "botc debout" : "botc assis");
                }
                case 6 -> { // 🏷️ ACTION : Alterner Pseudos globalement
                    boolean currentlyHidden = main.isNameTagsHidden();
                    if (currentlyHidden) {
                        admin.performCommand("botc shownames");
                    } else {
                        admin.performCommand("botc hidenames");
                    }
                    new ConteurMenuView().openConteurMenu(admin, main);
                }
                case 7 -> { // 🌟 APPORT : JUKEBOX : Silence / Parole All Vocaux
                    admin.closeInventory();
                    if (event.isLeftClick()) {
                        admin.performCommand("botc silence");
                    } else if (event.isRightClick()) {
                        admin.performCommand("botc paroleall");
                    }
                }
                case 8 -> { // 🌟 APPORT : MAP : Lancer ou Arrêter le scrutin de Preset Map
                    admin.closeInventory();
                    if (event.isLeftClick()) {
                        admin.performCommand("botc mapvote start");
                    } else if (event.isRightClick()) {
                        admin.performCommand("botc mapvote stop");
                    }
                }
            }
        }
        // ==========================================================
        // 🛠️ LIGNE 2 : BARRE DE SETUP ET CONFIGURATION (Slots 9 à 17)
        // ==========================================================
        else if (slot >= 9 && slot <= 17) {
            int relativeSlot = slot - 9;

            switch (relativeSlot) {
                case 0 -> { // FILLED_MAP : Menu des Presets
                    admin.closeInventory();
                    new PresetView(main).openPresetMenu(admin);
                }
                case 2 -> { // BEACON : Centre du Tribunal
                    admin.closeInventory();
                    admin.performCommand("botc settribunal");
                }
                case 3 -> { // OAK_STAIRS : Ajouter une Chaise
                    admin.closeInventory();
                    admin.performCommand("botc addchair");
                    admin.playSound(admin.getLocation(), org.bukkit.Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.5f, 1.2f);
                }
                case 4 -> { // IRON_DOOR : Ajouter une Chambre
                    admin.closeInventory();
                    admin.performCommand("botc addroom");
                    admin.playSound(admin.getLocation(), org.bukkit.Sound.BLOCK_WOODEN_DOOR_OPEN, 0.5f, 1.2f);
                }
                case 5 -> { // WITHER_SKELETON_SKULL : Gestion de l'estrade
                    if (event.isLeftClick()) {
                        admin.closeInventory();
                        admin.performCommand("botc setplayerdeath");
                    } else if (event.isRightClick()) {
                        main.getConfig().set(main.getPresetPath("death"), null);
                        main.saveConfig();
                        admin.sendMessage(Component.text(
                                "🗑️ Estrade d'exécution supprimée. Les condamnés mourront désormais sur place !",
                                NamedTextColor.YELLOW));
                        admin.playSound(admin.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
                        new ConteurMenuView().openConteurMenu(admin, main);
                    }
                }
                case 6 -> { // LIGHTNING_ROD : Alterner le mode de l'éclair
                    String currentMode = main.getConfig().getString(main.getPresetPath("lightning.mode"), "player");
                    if (currentMode.equalsIgnoreCase("player")) {
                        admin.performCommand("botc setlightning local");
                    } else {
                        admin.performCommand("botc setlightning player");
                    }
                    new ConteurMenuView().openConteurMenu(admin, main);
                }
                case 7 -> { // BLAZE_POWDER : Particules OU Suppression
                    if (event.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_LEFT) {
                        admin.closeInventory();
                        admin.performCommand("botc delchairs");
                    } else if (event.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_RIGHT) {
                        admin.closeInventory();
                        admin.performCommand("botc delrooms");
                    } else if (event.isLeftClick()) {
                        admin.closeInventory();
                        admin.performCommand("botc showchairs");
                    } else if (event.isRightClick()) {
                        admin.closeInventory();
                        admin.performCommand("botc showrooms");
                    }
                }
                case 8 -> { // BARRIER : Reset
                    admin.closeInventory();
                    admin.performCommand("botc reset");
                }
            }
        }
    }
}