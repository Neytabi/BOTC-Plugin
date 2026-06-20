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

public class ActionMenuListener implements Listener {

    private final Botc main;

    public ActionMenuListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        // 🌟 CORRECTION : Ce menu n'intercepte PLUS DU TOUT le Registre ni le Tribunal
        if (!titleStr.startsWith("Action : ")) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String targetName = titleStr.replace("Action : ", "").trim();

        BotcPlayer targetBotc = null;
        for (BotcPlayer bp : main.getPlayersMap().values()) {
            if (bp.getPlayerName().equalsIgnoreCase(targetName)) {
                targetBotc = bp;
                break;
            }
        }

        if (targetBotc == null) return;

        Material type = clickedItem.getType();

        if (type == Material.PAPER) {
            new RoleSelectionView().openRoleMenu(admin, targetBotc);
        }

        if (type == Material.IRON_SWORD) {
            main.executePlayer(targetBotc, admin);
            admin.closeInventory();

        } else if (type == Material.POTION) {
            targetBotc.setAlive(true);
            admin.closeInventory();

            Bukkit.broadcast(Component.text("[BOTC] Les cieux s'ouvrent ! " + targetBotc.getPlayerName() + " ressuscite !", NamedTextColor.GREEN));

            Player pTarget = Bukkit.getPlayer(targetBotc.getPlayerUUID());
            if (pTarget != null) {
                pTarget.getInventory().setHelmet(new ItemStack(Material.AIR));
                pTarget.getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, pTarget.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.1);
                pTarget.playSound(pTarget.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
                pTarget.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
        } else if (type == Material.BELL) {
            admin.closeInventory();
            main.getVoteManager().startVote(targetBotc);
        } else if (type == Material.NETHER_STAR || type == Material.COAL) {
            targetBotc.setGhostVote(!targetBotc.hasGhostVote());
            new ActionMenuView().openActionMenu(admin, targetBotc);
        }
    }
}