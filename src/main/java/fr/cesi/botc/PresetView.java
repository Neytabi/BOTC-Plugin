package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PresetView {

    private final Botc main;

    public PresetView(Botc main) {
        this.main = main;
    }

    public void openPresetMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(" Gestion des Presets Maps", NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD));

        // Remplissage decoratif avec des vitres grises
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.displayName(Component.text(" "));
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // Récupération dynamique de la liste des presets existants
        List<String> presetNames = new ArrayList<>();
        presetNames.add("default");
        if (main.getConfig().getConfigurationSection("presets") != null) {
            Set<String> keys = main.getConfig().getConfigurationSection("presets").getKeys(false);
            for (String key : keys) {
                if (!key.equalsIgnoreCase("default")) {
                    presetNames.add(key);
                }
            }
        }

        String activePreset = main.getActivePreset();
        int slot = 9; // On aligne la liste sur la ligne centrale (slots 9 à 17)

        for (String name : presetNames) {
            if (slot > 17) break; // Limite d'affichage visuel

            boolean isActive = name.equalsIgnoreCase(activePreset);
            ItemStack item = new ItemStack(isActive ? Material.FILLED_MAP : Material.MAP);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                if (isActive) {
                    meta.displayName(Component.text(" " + name.toUpperCase(), NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                    meta.addEnchant(Enchantment.MENDING, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                } else {
                    meta.displayName(Component.text(" " + name, NamedTextColor.YELLOW));
                }

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("---------------------------------", NamedTextColor.GRAY));
                if (isActive) {
                    lore.add(Component.text("> MAP ACTUELLE DE LA PARTIE <", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                } else {
                    lore.add(Component.text(" CLIC GAUCHE : Activer cette arène", NamedTextColor.AQUA));
                }
                if (!name.equals("default")) {
                    lore.add(Component.text(" CLIC DROIT : Supprimer définitivement", NamedTextColor.RED));
                }
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
        }

        // Slot 22 (Bas / Milieu) : Bouton d'ajout
        ItemStack anvil = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = anvil.getItemMeta();
        if (anvilMeta != null) {
            anvilMeta.displayName(Component.text(" Créer une nouvelle Map", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            List<Component> anvilLore = new ArrayList<>();
            anvilLore.add(Component.text("Génère un profil de configuration vierge.", NamedTextColor.GRAY));
            anvilLore.add(Component.text("Tu saisiras le nom directement dans ton chat !", NamedTextColor.YELLOW));
            anvilMeta.lore(anvilLore);
            anvil.setItemMeta(anvilMeta);
        }
        inv.setItem(22, anvil);

        player.openInventory(inv);
    }
}