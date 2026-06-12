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
        // 🏛️ PARTIE A : CLICS DANS LE REGISTRE DU TRIBUNAL (Menu Principal)
        // ==========================================================
        if (titleStr.equals("Registre du Tribunal")) {
            event.setCancelled(true);
            int slot = event.getSlot();
            UUID uuid = accuser.getUniqueId();
            BotcPlayer bp = main.getPlayersMap().get(uuid);

            // 1. CLIC SUR L'ACCUSATION (Case 2)
            if (slot == 2) {
                // 🌟 FIX image_cee982.png : Si le joueur est mort, on le jette immédiatement
                if (bp != null && !bp.isAlive()) {
                    accuser.closeInventory();
                    accuser.sendMessage(Component.text("❌ NOMINATION IMPOSSIBLE", NamedTextColor.RED).decorate(TextDecoration.BOLD));
                    accuser.sendMessage(Component.text("Tu es un fantôme. Les morts ne peuvent plus lancer d'accusations au Tribunal.", NamedTextColor.GRAY));
                    accuser.playSound(accuser.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.6f, 1.0f);
                    return;
                }

                // Sécurité : Est-ce que le conseil est ouvert ?
                if (!main.isCouncilOpen()) {
                    accuser.closeInventory();
                    accuser.sendMessage(Component.text("❌ Le tribunal n'est pas en séance ! Impossible de lancer d'accusation pour le moment.", NamedTextColor.RED));
                    return;
                }

                // Si vivant + conseil ouvert, on lui ouvre la liste des suspects
                new NominationView().openNominationMenu(accuser, main);
            }

            // 2. CLIC SUR LA PLUME / QUESTION (Case 4) -> Dispo tout le temps, même hors conseil !
            else if (slot == 4) {
                accuser.closeInventory();
                main.getIsAskingQuestion().put(uuid, true);
                accuser.sendMessage(Component.text("Pose ta question directement dans le chat, elle sera envoyée uniquement aux Conteurs :", NamedTextColor.LIGHT_PURPLE));
                accuser.playSound(accuser.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.0f);
            }

            // 3. CLIC SUR LE PAPIER / PAROLE (Case 6) -> Dispo tout le temps pour les morts !
            else if (slot == 6) {
                accuser.closeInventory();
                if (bp != null && bp.isAlive()) {
                    accuser.sendMessage(Component.text("❌ Tu es vivant ! Tu as déjà le droit de parler au Conseil.", NamedTextColor.RED));
                    return;
                }

                accuser.sendMessage(Component.text("Demande de parole envoyée aux Conteurs.", NamedTextColor.GREEN));

                Component prompt = Component.text("💀 Le fantôme " + accuser.getName() + " demande la parole. ", NamedTextColor.GOLD)
                        .append(Component.text("[ACCORDER]", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc grantparole " + accuser.getName())));

                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (op.isOp()) {
                        op.sendMessage(prompt);
                        op.playSound(op.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1.0f);
                    }
                }
            }
            return;
        }

        // ==========================================================
        // 👥 PARTIE B : CLICS DANS LA SÉLECTION DES SUSPECTS
        // ==========================================================
        if (titleStr.equals("Nommer un suspect")) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;

            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            if (meta == null || meta.getOwningPlayer() == null) return;

            UUID accuserUUID = accuser.getUniqueId();
            UUID targetUUID = meta.getOwningPlayer().getUniqueId();

            // Vérification des limites de nomination quotidiennes
            if (main.getHasNominatedToday().contains(accuserUUID)) {
                accuser.closeInventory();
                accuser.sendMessage(Component.text("❌ Vous avez déjà accusé un suspect durant ce conseil !", NamedTextColor.RED));
                return;
            }
            if (main.getHasBeenNominatedToday().contains(targetUUID)) {
                accuser.closeInventory();
                accuser.sendMessage(Component.text("❌ Ce joueur a déjà fait l'objet d'une accusation aujourd'hui !", NamedTextColor.RED));
                return;
            }

            String targetName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
            accuser.closeInventory();

            // Enregistrement du verrou de nomination unique
            main.getHasNominatedToday().add(accuserUUID);
            main.getHasBeenNominatedToday().add(targetUUID);

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
        }
    }
}