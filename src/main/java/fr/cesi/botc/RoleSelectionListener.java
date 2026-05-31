package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RoleSelectionListener implements Listener {

    private final Botc main;

    public RoleSelectionListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.startsWith("Assigner Rôle : ")) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getItemMeta() == null) return;

        String roleName = PlainTextComponentSerializer.plainText().serialize(clicked.getItemMeta().displayName());
        String targetName = title.replace("Assigner Rôle : ", "").trim();

        // Récupération de la description de l'item cliqué
        String desc = "";
        if (clicked.getItemMeta().hasLore() && clicked.getItemMeta().lore() != null) {
            desc = PlainTextComponentSerializer.plainText().serialize(clicked.getItemMeta().lore().get(0));
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) return;
        BotcPlayer bp = main.getPlayersMap().get(targetPlayer.getUniqueId());
        if (bp == null) return;

        // --- TRAITEMENT SELON LE TYPE DE CLIC ---
        if (event.isLeftClick()) {
            // CLIC GAUCHE : Attribution Normale
            bp.setRole(roleName, desc);
            admin.sendMessage(Component.text("Rôle " + roleName + " assigné normalement à " + targetName, NamedTextColor.GREEN));

            // Notification au joueur (Rôle réel = Rôle affiché)
            sendSecretRoleMessage(targetPlayer, roleName, desc);
        }
        else if (event.isRightClick() && clicked.getType() == Material.BLUE_WOOL) {
            // CLIC DROIT sur un Citadin : Le joueur devient l'Ivrogne, mais croit être ce Citadin !
            bp.setFakeRole("Ivrogne", roleName, "Tu es l'Ivrogne. Tu crois être le " + roleName + " mais tes pouvoirs n'ont aucun effet.");
            admin.sendMessage(Component.text("[IVROGNE] " + targetName + " a été configuré comme Ivrogne croyant être " + roleName + " !", NamedTextColor.GOLD));

            // Le joueur reçoit l'info qu'il est le rôle de Citadin (le mensonge fonctionne !)
            sendSecretRoleMessage(targetPlayer, roleName, desc);
        }

        admin.closeInventory();
    }

    private void sendSecretRoleMessage(Player target, String roleName, String description) {
        target.sendMessage(Component.text("=====================================", NamedTextColor.DARK_RED));
        target.sendMessage(Component.text("TON RÔLE SECRET A ÉTÉ ATTRIBUÉ !", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        target.sendMessage(Component.text("Tu es le : ", NamedTextColor.WHITE).append(Component.text(roleName, NamedTextColor.GREEN).decorate(TextDecoration.BOLD)));
        target.sendMessage(Component.text("Effet : " + description, NamedTextColor.GRAY));
        target.sendMessage(Component.text("=====================================", NamedTextColor.DARK_RED));
        target.playSound(target.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.8f);
    }
}