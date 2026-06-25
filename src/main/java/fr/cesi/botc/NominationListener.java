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
import org.bukkit.inventory.meta.SkullMeta;
import java.util.UUID;

public class NominationListener implements Listener {

    private final Botc main;

    public NominationListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player accuser)) return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        // ==========================================================
        //  UNIQUEMENT LA SÉLECTION DES SUSPECTS
        // ==========================================================
        if (!titleStr.equals("Nommer un suspect")) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;

        SkullMeta meta = (SkullMeta) clicked.getItemMeta();
        if (meta == null || meta.getOwningPlayer() == null) return;

        UUID accuserUUID = accuser.getUniqueId();
        UUID targetUUID = meta.getOwningPlayer().getUniqueId();

        // Vérification des limites de nomination (la Vierge est immunisée à la limite quotidienne)
        BotcPlayer targetBP = main.getPlayersMap().get(targetUUID);
        boolean isVierge = targetBP != null && targetBP.getDisplayedRole().equalsIgnoreCase("Vierge");

        if (!isVierge && main.getHasBeenNominatedToday().contains(targetUUID)) {
            accuser.closeInventory();
            accuser.sendMessage(Component.text("x Ce joueur a déjà fait l'objet d'une accusation aujourd'hui !", NamedTextColor.RED));
            return;
        }

        String targetName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        accuser.closeInventory();

        // Enregistrement du verrou de nomination unique pour le joueur cible (sauf Vierge)
        if (!isVierge) {
            main.getHasBeenNominatedToday().add(targetUUID);
        }

        Bukkit.broadcast(Component.text("----------------------------------------", NamedTextColor.GRAY));
        Bukkit.broadcast(Component.text("ACCUSATION FORMELLE !", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        Bukkit.broadcast(Component.text(accuser.getName(), NamedTextColor.YELLOW)
                .append(Component.text(" accuse publiquement ", NamedTextColor.WHITE))
                .append(Component.text(targetName, NamedTextColor.RED).decorate(TextDecoration.BOLD))
                .append(Component.text(" d'être le Démon !", NamedTextColor.WHITE)));
        Bukkit.broadcast(Component.text("Le suspect a la parole pour se défendre, préparez vos votes.", NamedTextColor.LIGHT_PURPLE, TextDecoration.ITALIC));
        Bukkit.broadcast(Component.text("----------------------------------------", NamedTextColor.GRAY));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.2f);
        }

        // Logique spécifique à la Vierge
        if (isVierge) {
            BotcPlayer accuserBP = main.getPlayersMap().get(accuserUUID);
            
            boolean viergeIvre = targetBP != null && targetBP.getRealRole().equalsIgnoreCase("Ivrogne");
            boolean accuserIvre = accuserBP != null && accuserBP.getRealRole().equalsIgnoreCase("Ivrogne");
            
            if (!viergeIvre && !accuserIvre) {
                // Envoyer le message au MJ
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp()) {
                        p.sendMessage(Component.text(" ", NamedTextColor.RED)
                                .append(Component.text("La Vierge a été nommée !", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
                        p.sendMessage(Component.text("-> ", NamedTextColor.AQUA)
                                .append(Component.text("Tuer l'accusateur (" + accuser.getName() + ")", NamedTextColor.RED).decorate(TextDecoration.BOLD))
                                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc execute " + accuser.getName())));
                    }
                }
            }
        }
    }
}