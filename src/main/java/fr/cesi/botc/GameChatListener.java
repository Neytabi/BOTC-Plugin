package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameChatListener implements Listener {

    private final Botc main;

    public GameChatListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Les admins (Conteurs) ont toujours le droit de parler dans le chat
        if (player.isOp()) return;

        // 1. Traitement de la question secrète au Conteur
        if (main.getIsAskingQuestion().getOrDefault(player.getUniqueId(), false)) {
            event.setCancelled(true);
            main.getIsAskingQuestion().remove(player.getUniqueId()); // On désactive le mode question

            Component log = Component.text("[QUESTION SECRÈTE] ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD)
                    .append(Component.text(player.getName() + " : ", NamedTextColor.YELLOW))
                    .append(Component.text(PlainTextComponentSerializer.plainText().serialize(event.message()), NamedTextColor.WHITE));

            Bukkit.getScheduler().runTask(main, () -> {
                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (op.isOp()) {
                        op.sendMessage(log);
                        op.playSound(op.getLocation(), org.bukkit.Sound.ENTITY_CHICKEN_EGG, 0.6f, 1.2f);
                    }
                }
            });
            player.sendMessage(Component.text(" Votre question a été transmise en privé aux Conteurs.", NamedTextColor.GREEN));
            return;
        }

        // 2. Blocage contextuel du chat (Nuit vs Jour)
        event.setCancelled(true);
        if (main.isNight()) {
            player.sendMessage(Component.text(" C'est la nuit ! Le village dort, interdiction de parler dans le chat.", NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text(" Le chat public est désactivé. Exprimez-vous en vocal ou ouvrez votre Registre pour poser une question au Conteur.", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        Component displayNameComponent = item.getItemMeta().displayName();
        if (displayNameComponent == null) return;

        String itemName = PlainTextComponentSerializer.plainText().serialize(displayNameComponent);

        // Interaction : Bouton de question secrète
        if (itemName.contains("Poser une Question")) {
            event.setCancelled(true);
            main.getIsAskingQuestion().put(player.getUniqueId(), true);
            player.sendMessage(Component.text("Posez votre question directement dans le chat, elle sera masquée aux autres joueurs :", NamedTextColor.LIGHT_PURPLE));
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.0f);
        }

        // Interaction : Bouton d'appel pour demander la parole depuis l'inventaire
        if (itemName.contains("Demander la Parole")) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Demande d'intervention envoyée aux Conteurs.", NamedTextColor.GREEN));

            // Message texte interactif cliquable généré uniquement pour les administrateurs
            Component prompt = Component.text("Le joueur " + player.getName() + " demande la parole au Tribunal. ", NamedTextColor.GOLD)
                    .append(Component.text("[ACCORDER]", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                            .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc grantparole " + player.getName())));

            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.isOp()) {
                    op.sendMessage(prompt);
                    op.playSound(op.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1.0f);
                }
            }
        }
    }
}
