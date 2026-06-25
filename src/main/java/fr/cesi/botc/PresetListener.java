package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PresetListener implements Listener {

    private final Botc main;
    private final PresetView presetView;
    private static final HashMap<UUID, Boolean> chatPromptPlayers = new HashMap<>();

    public PresetListener(Botc main) {
        this.main = main;
        this.presetView = new PresetView(main);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = plainText(event.getView().title());
        if (!title.contains("Gestion des Presets Maps")) return;

        event.setCancelled(true); // Empêche de voler les items de l'interface

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // 1. ACTION CRÉATION (L'ENCLUME)
        if (clicked.getType() == Material.ANVIL) {
            player.closeInventory();
            chatPromptPlayers.put(player.getUniqueId(), true);
            player.sendMessage(Component.text(" TAPE LE NOM DE LA NOUVELLE MAP DANS TON CHAT (Sans espaces, ou tape 'annuler') :", NamedTextColor.LIGHT_PURPLE));
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.5f, 1.0f);
            return;
        }

        // 2. ACTION SÉLECTION / SUPPRESSION (LES CARTES)
        if (clicked.getType() == Material.MAP || clicked.getType() == Material.FILLED_MAP) {
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;

            String displayName = plainText(meta.displayName());
            String presetName = displayName.replace("", "").trim().toLowerCase();

            // CLIC DROIT -> SUPPRIMER
            if (event.isRightClick()) {
                if (presetName.equals("default")) {
                    player.sendMessage(Component.text("x Impossible de supprimer l'arène par défaut.", NamedTextColor.RED));
                    return;
                }
                main.getConfig().set("presets." + presetName, null);
                main.saveConfig();

                if (main.getActivePreset().equalsIgnoreCase(presetName)) {
                    main.setActivePreset("default");
                }

                player.sendMessage(Component.text("v Preset '" + presetName + "' supprimé de la base de données.", NamedTextColor.GREEN));
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 0.6f, 0.8f);
                presetView.openPresetMenu(player); // Rafraîchissement dynamique
            }
            // CLIC GAUCHE -> SÉLECTIONNER
            else if (event.isLeftClick()) {
                main.setActivePreset(presetName);
                player.sendMessage(Component.text(" Basculement de map réussi ! Preset chargé : " + presetName, NamedTextColor.GREEN));
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1.2f);
                presetView.openPresetMenu(player); // Met à jour la carte brillante
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!chatPromptPlayers.containsKey(player.getUniqueId())) return;

        event.setCancelled(true); // Bloque l'envoi du message aux autres joueurs du chat
        chatPromptPlayers.remove(player.getUniqueId());

        String message = plainText(event.message()).trim();
        if (message.equalsIgnoreCase("annuler")) {
            player.sendMessage(Component.text("x Création annulée.", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(main, () -> presetView.openPresetMenu(player));
            return;
        }

        // Nettoyage automatique du texte (Alphanumérique uniquement)
        String cleanName = message.toLowerCase().replaceAll("[^a-z0-9_]", "");
        if (cleanName.isEmpty()) {
            player.sendMessage(Component.text("x Nom invalide (Lettres et chiffres uniquement, sans caractères spéciaux).", NamedTextColor.RED));
            Bukkit.getScheduler().runTask(main, () -> presetView.openPresetMenu(player));
            return;
        }

        // Enregistrement sécurisé synchronisé sur le thread principal de Spigot
        Bukkit.getScheduler().runTask(main, () -> {
            main.setActivePreset(cleanName);
            if (!main.getConfig().contains("presets." + cleanName)) {
                main.getConfig().set("presets." + cleanName + ".chairs", new ArrayList<String>());
                main.getConfig().set("presets." + cleanName + ".rooms", new ArrayList<String>());
                main.saveConfig();
                player.sendMessage(Component.text("v Nouveau preset '" + cleanName + "' initialisé et sélectionné !", NamedTextColor.GREEN));
            }
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.2f);
            presetView.openPresetMenu(player);
        });
    }

    private String plainText(Component component) {
        if (component == null) return "";
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
}