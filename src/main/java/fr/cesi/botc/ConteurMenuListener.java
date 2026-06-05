package fr.cesi.botc;

import net.kyori.adventure.text.Component;
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
        if (!(event.getWhoClicked() instanceof Player admin)) return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        if (!titleStr.equals("Menu Principal du Conteur")) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Material type = clicked.getType();

        if (type == Material.NETHER_STAR) {
            admin.closeInventory();
            // Ouvre ta GrimoireView existante (qui liste tous tes joueurs)
            new GrimoireView(main).openGrimoire(admin);
        }
        else if (type == Material.CLOCK) {
            admin.closeInventory();
            // Alterne dynamiquement selon l'état actuel de ta variable main.isNight()
            admin.performCommand(main.isNight() ? "botc jour" : "botc nuit");
        }
        else if (type == Material.OAK_STAIRS) {
            admin.closeInventory();

            // --- LOGIQUE D'ALTERNANCE DYNAMIQUE ---
            boolean IsAnyoneSeated = false;

            // On parcourt les joueurs connectés pour voir si l'un d'eux est sur un cheval
            for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (p.isOp()) continue; // On ignore les MJ

                if (p.isInsideVehicle()) {
                    IsAnyoneSeated = true;
                    break; // Un seul joueur assis suffit à savoir qu'on doit les lever
                }
            }

            // Si quelqu'un est assis, on lance le debout, sinon on les assoit !
            if (IsAnyoneSeated) {
                admin.performCommand("botc debout");
            } else {
                admin.performCommand("botc assis");
            }
        }
    }
}