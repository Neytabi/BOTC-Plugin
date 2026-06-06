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

public class PlayerRegistryListener implements Listener {

    private final Botc main;

    public PlayerRegistryListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        if (!titleStr.equals("Registre du Tribunal")) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.LIME_WOOL) {
            event.setCancelled(true);

            // 1. On enregistre le vote (pense à vérifier que cette méthode passe bien le hasGhostVote à false si le joueur est mort !)
            main.getVoteManager().registerVote(player);

            // 2. LE REFRESH SÉCURISÉ (Attendre 1 tick pour que Minecraft applique le clic)
            org.bukkit.Bukkit.getScheduler().runTask(main, () -> {
                new PlayerRegistryView().openRegistryMenu(player, main);
            });

            // Petit son de validation de vote
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
        if (clicked.getType() == Material.WRITABLE_BOOK) {
            // Bascule vers l'interface dynamique des têtes
            new NominationView().openNominationMenu(player, main);
        }
        // 1. Si le joueur clique sur la Laine Rouge (Plus de jeton de vote)
        if (clicked.getType() == Material.RED_WOOL) {
            event.setCancelled(true);
            player.closeInventory();
            player.sendMessage(Component.text("🚨 Tu as déjà utilisé ton vote fantôme pour cette partie !", NamedTextColor.RED));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // 2. Si le joueur clique sur le Livre Normal (Nomination bloquée pour les morts)
        if (clicked.getType() == Material.BOOK) {
            event.setCancelled(true);
            player.closeInventory();
            player.sendMessage(Component.text("🚨 Les morts ne peuvent plus désigner de suspect !", NamedTextColor.RED));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
    }
}