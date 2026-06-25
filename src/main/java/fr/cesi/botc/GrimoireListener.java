package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.UUID;

public class GrimoireListener implements Listener {

    private final Botc main;

    public GrimoireListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;

        // Extraction propre du titre
        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        if (!titleStr.equals("Grimoire du Conteur")) return;
        event.setCancelled(true); // Sécurité anti-vol d'items

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int slot = event.getSlot();

        // ==========================================================
        //  ACTION 1 : CLIC SUR L'ENDER PEARL (Slot 45 - Flash TP)
        // ==========================================================
        if (slot == 45) {
            admin.closeInventory();

            // On va chercher le point central du Tribunal indexé sur le PRESET ACTUEL
            if (main.getConfig().contains(main.getPresetPath("tribunal.x"))) {
                String worldName = main.getConfig().getString(main.getPresetPath("tribunal.world"));
                double x = main.getConfig().getDouble(main.getPresetPath("tribunal.x"));
                double y = main.getConfig().getDouble(main.getPresetPath("tribunal.y"));
                double z = main.getConfig().getDouble(main.getPresetPath("tribunal.z"));

                org.bukkit.World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    // On TP l'admin en conservant son orientation de regard actuelle (Yaw/Pitch)
                    org.bukkit.Location loc = new org.bukkit.Location(world, x, y, z, admin.getLocation().getYaw(), admin.getLocation().getPitch());
                    admin.teleport(loc);
                    admin.playSound(admin.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    admin.sendMessage(Component.text(" Flash ! Tu as été rapatrié au centre du Tribunal.", NamedTextColor.GREEN));
                }
            } else {
                admin.sendMessage(Component.text("x Le tribunal n'est pas encore configuré sur cette map ! Cliquez sur la Balise de votre menu principal.", NamedTextColor.RED));
            }
            return;
        }

        // ==========================================================
        //  ACTION 2 : CLIC SUR UNE TÊTE DE JOUEUR
        // ==========================================================
        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
            if (meta == null || meta.getOwningPlayer() == null) return;

            //  A. SOUS-LOGIQUE : LE SHIFT-CLIC (Téléportation directe sur le joueur)
            if (event.isShiftClick()) {
                Player targetPlayer = meta.getOwningPlayer().getPlayer();

                if (targetPlayer != null && targetPlayer.isOnline()) {
                    admin.closeInventory();
                    admin.teleport(targetPlayer.getLocation());
                    admin.playSound(admin.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);
                    admin.sendMessage(Component.text(" Téléportation réussie auprès de " + targetPlayer.getName() + " !", NamedTextColor.GREEN));
                } else {
                    admin.sendMessage(Component.text("x Ce joueur s'est déconnecté du serveur.", NamedTextColor.RED));
                }
                return; // On stoppe la méthode ici pour éviter d'ouvrir le sous-menu d'action !
            }

            //  B. SOUS-LOGIQUE : CLIC CLASSIQUE (Ouverture du menu d'attribution de rôle / exécution)
            UUID targetUUID = meta.getOwningPlayer().getUniqueId();
            BotcPlayer targetBotc = main.getPlayersMap().get(targetUUID);

            if (targetBotc != null) {
                new ActionMenuView().openActionMenu(admin, targetBotc);
            }
        }
    }
}