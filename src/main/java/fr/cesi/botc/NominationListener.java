package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NominationListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player accuser)) return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        if (!titleStr.equals("Nommer un suspect")) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;

        if (clicked.getItemMeta() != null && clicked.getItemMeta().hasDisplayName()) {
            String targetName = PlainTextComponentSerializer.plainText().serialize(clicked.getItemMeta().displayName());

            accuser.closeInventory();

            // 📣 ANNONCE MAJESTUEUSE DU DEBAT
            Bukkit.broadcast(Component.text("----------------------------------------", NamedTextColor.GRAY));
            Bukkit.broadcast(Component.text("📣 ACCUSATION FORMELLE !", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text(accuser.getName(), NamedTextColor.YELLOW)
                    .append(Component.text(" accuse publiquement ", NamedTextColor.WHITE))
                    .append(Component.text(targetName, NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .append(Component.text(" d'être le Démon !", NamedTextColor.WHITE)));
            Bukkit.broadcast(Component.text("Le suspect a la parole pour se défendre, préparez vos votes.", NamedTextColor.LIGHT_PURPLE, TextDecoration.ITALIC));
            Bukkit.broadcast(Component.text("----------------------------------------", NamedTextColor.GRAY));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.2f);
            }
        }
    }
}