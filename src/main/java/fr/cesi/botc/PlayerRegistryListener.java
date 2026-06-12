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
        event.setCancelled(true); // Bloque d'office toutes les manipulations d'items

        int slot = event.getSlot();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        BotcPlayer bp = main.getPlayersMap().get(player.getUniqueId());

        switch (slot) {
            case 0: // 🗳️ CASE 0 : LE VOTE (Laine Verte ou Laine Rouge)
                if (clicked.getType() == Material.RED_WOOL) {
                    player.closeInventory();
                    player.sendMessage(Component.text("🚨 Tu as déjà utilisé ton vote fantôme pour cette partie !", NamedTextColor.RED));
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                } else if (clicked.getType() == Material.LIME_WOOL) {
                    main.getVoteManager().registerVote(player);

                    // Rafraîchissement au tick suivant
                    Bukkit.getScheduler().runTask(main, () -> {
                        new PlayerRegistryView().openRegistryMenu(player, main);
                    });
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
                break;

            case 2: // 📢 CASE 2 : L'ACCUSATION (Livre Écrit ou Livre Normal)
                if (clicked.getType() == Material.BOOK) {
                    player.closeInventory();
                    player.sendMessage(Component.text("🚨 Les morts ne peuvent plus désigner de suspect !", NamedTextColor.RED));
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                } else if (clicked.getType() == Material.WRITABLE_BOOK) {
                    new NominationView().openNominationMenu(player, main);
                }
                break;

            case 4: // 🎭 CASE 4 : LE LIVRE DE RÔLE (Milieu)
                // On ne fait rien du tout au clic, c'est uniquement une case d'affichage d'informations.
                break;

            case 6: // 💬 CASE 6 : LA PLUME (Poser une question secrète)
                player.closeInventory();
                main.getIsAskingQuestion().put(player.getUniqueId(), true);
                player.sendMessage(Component.text("Pose ta question directement dans le chat, elle sera envoyée uniquement aux Conteurs :", NamedTextColor.LIGHT_PURPLE));
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.0f);
                break;

            case 8: // ✋ CASE 8 : LE PAPIER (Demander la parole)
                if (bp != null && bp.isAlive()) {
                    player.sendMessage(Component.text("❌ Tu es vivant ! Tu as déjà le droit de parler au Conseil.", NamedTextColor.RED));
                    return;
                }

                player.closeInventory();
                player.sendMessage(Component.text("Demande de parole envoyée aux Conteurs.", NamedTextColor.GREEN));

                Component prompt = Component.text("💀 Le fantôme " + player.getName() + " demande la parole. ", NamedTextColor.GOLD)
                        .append(Component.text("[ACCORDER]", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc grantparole " + player.getName())));

                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (op.isOp()) {
                        op.sendMessage(prompt);
                        op.playSound(op.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1.0f);
                    }
                }
                break;
        }
    }
}