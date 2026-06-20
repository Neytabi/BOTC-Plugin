package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.List;

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
            org.bukkit.inventory.ItemStack livreMJ = new org.bukkit.inventory.ItemStack(
                    org.bukkit.Material.ENCHANTED_BOOK);
            org.bukkit.inventory.meta.ItemMeta meta = livreMJ.getItemMeta();
            if (meta != null) {
                meta.displayName(net.kyori.adventure.text.Component.text("📖 Le Grimoire du Conteur",
                        net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE));
                livreMJ.setItemMeta(meta);
            }
            // Donne le livre s'il ne l'a pas déjà
            if (!player.getInventory().contains(org.bukkit.Material.ENCHANTED_BOOK)) {
                player.getInventory().addItem(livreMJ);
            }
        } else {
            // 👤 Objet des Joueurs (Livre Normal) -> AJOUTÉ ICI !
            org.bukkit.inventory.ItemStack registreJoueur = new org.bukkit.inventory.ItemStack(
                    org.bukkit.Material.BOOK);
            org.bukkit.inventory.meta.ItemMeta meta = registreJoueur.getItemMeta();
            if (meta != null) {
                meta.displayName(net.kyori.adventure.text.Component.text("📜 Registre du Tribunal",
                        net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN));
                registreJoueur.setItemMeta(meta);
            }
            // On lui donne s'il ne l'a pas déjà dans ses poches
            if (!player.getInventory().contains(org.bukkit.Material.BOOK)) {
                player.getInventory().addItem(registreJoueur);
            }
        }

        // =========================================================================
        // 🌟 AJOUT : RE-FIXATION AUTOMATIQUE SUR LA CHAISE APRÈS UN CRASH
        // =========================================================================
        BotcPlayer bp = main.getPlayersMap().get(player.getUniqueId());
        if (main.isSeatsAssigned() && bp != null && bp.getChairIndex() != -1 && !player.isOp()) {
            List<String> chairsStr = main.getConfig().getStringList(main.getPresetPath("chairs"));

            if (bp.getChairIndex() < chairsStr.size()) {
                // On vérifie si le reste du village est actuellement assis
                boolean villageEstAssis = false;
                for (Player onlineP : Bukkit.getOnlinePlayers()) {
                    if (onlineP.isInsideVehicle() && onlineP.getVehicle().getScoreboardTags().contains("botc_chair")) {
                        villageEstAssis = true;
                        break;
                    }
                }

                // Si la partie est en cours et que les gens sont assis, on le remet sur son
                // cheval
                if (villageEstAssis) {
                    String[] parts = chairsStr.get(bp.getChairIndex()).split(",");
                    org.bukkit.World w = Bukkit.getWorld(parts[0]);
                    if (w != null) {
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double z = Double.parseDouble(parts[3]);
                        float yaw = Float.parseFloat(parts[4]);

                        org.bukkit.Location chairLoc = new org.bukkit.Location(w, x, y, z, yaw, 0);
                        org.bukkit.Location horseLoc = chairLoc.clone().add(0, -1.0, 0); // Alignement hauteur corrigé

                        player.teleport(chairLoc);

                        Horse chair = w.spawn(chairLoc, Horse.class, horse -> {
                            horse.setGravity(false);
                            horse.setSilent(true);
                            horse.setInvulnerable(true);
                            horse.setAI(false);
                            horse.setTamed(true);
                            horse.setPersistent(false);
                            horse.addScoreboardTag("botc_chair");
                            horse.addPotionEffect(
                                    new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY,
                                            Integer.MAX_VALUE, 0, false, false));
                        });

                        chair.teleport(horseLoc);
                        var chairTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_chairs");
                        if (chairTeam != null) {
                            chairTeam.addEntry(chair.getUniqueId().toString());
                        }
                        chair.addPassenger(player);
                        player.sendMessage(
                                Component.text("✓ Tu as été replacé automatiquement sur ton siège après ton crash.",
                                        NamedTextColor.GREEN));
                    }
                }
            }
        }

        // --- MESSAGE D'ACCUEIL POUR TOUS LES JOUEURS ---
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
        player.sendMessage(Component.text("  Bienvenue à Ravenswood Bluff (BOTC) !  ", NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
        player.sendMessage(Component.text("• Écoutez attentivement le Conteur.", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Ne trichez pas pendant la nuit.", NamedTextColor.GRAY));
        player.sendMessage(Component.text(
                "• Pour voter lors d'une accusation, cliquez sur le bouton vert dans le chat.", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
        player.sendMessage(Component.text("/botc help pour avoir les commandes", NamedTextColor.GRAY));
        player.sendMessage(Component.text("Plugin made by Pill0N and Neytabi", NamedTextColor.RED));
        player.sendMessage(Component.text("=============================================", NamedTextColor.DARK_PURPLE));
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        // Laissé vide volontairement pour garder les profils actifs en cas de crash !
    }
}