package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final Botc main;

    // Constructeur pour injecter l'instance de notre plugin principal (DI)
    public PlayerJoinListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!main.getPlayersMap().containsKey(player.getUniqueId())) {
            BotcPlayer newPlayer = new BotcPlayer(player.getUniqueId(), player.getName());
            main.getPlayersMap().put(player.getUniqueId(), newPlayer);
        }

        if (player.isOp()) {
            org.bukkit.inventory.ItemStack livreMJ = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ENCHANTED_BOOK);
            org.bukkit.inventory.meta.ItemMeta meta = livreMJ.getItemMeta();
            if (meta != null) {
                meta.displayName(net.kyori.adventure.text.Component.text("📖 Le Grimoire du Conteur", net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE));
                livreMJ.setItemMeta(meta);
            }
            // Donne le livre s'il ne l'a pas déjà
            if (!player.getInventory().contains(org.bukkit.Material.ENCHANTED_BOOK)) {
                player.getInventory().addItem(livreMJ);
            }
        } else {
            // 👤 Objet des Joueurs (Livre Normal) -> AJOUTÉ ICI !
            org.bukkit.inventory.ItemStack registreJoueur = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BOOK);
            org.bukkit.inventory.meta.ItemMeta meta = registreJoueur.getItemMeta();
            if (meta != null) {
                meta.displayName(net.kyori.adventure.text.Component.text("📜 Registre du Tribunal", net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN));
                registreJoueur.setItemMeta(meta);
            }
            // On lui donne s'il ne l'a pas déjà dans ses poches
            if (!player.getInventory().contains(org.bukkit.Material.BOOK)) {
                player.getInventory().addItem(registreJoueur);
            }
        }

        // --- MESSAGE D'ACCUEIL POUR TOUS LES JOUEURS ---
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
        player.sendMessage(Component.text("  Bienvenue à Ravenswood Bluff (BOTC) !  ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
        player.sendMessage(Component.text("• Écoutez attentivement le Conteur.", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Ne trichez pas pendant la nuit (fermez les yeux/votre écran).", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Pour voter lors d'une accusation, cliquez sur le bouton vert dans le chat.", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
        player.sendMessage(Component.text("/botc help pour avoir les commandes", NamedTextColor.GRAY));
        player.sendMessage(Component.text("Plugin made by Pill0N and Neytabi", NamedTextColor.RED));
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
    }
    @org.bukkit.event.EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        // Optionnel : ne le retirer que si la partie n'est pas en cours, ou le laisser pour que le Conteur gère.
        // Pour un serveur public, on fait souvent :
        main.getPlayersMap().remove(event.getPlayer().getUniqueId());
    }
}