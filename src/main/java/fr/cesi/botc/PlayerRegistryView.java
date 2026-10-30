package fr.cesi.botc;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerRegistryView {

    public void openRegistryMenu(Player player, Botc main) {
        BotcPlayer bp = main.getPlayersMap().get(player.getUniqueId());

        boolean isAlive = bp == null || bp.isAlive();
        boolean hasGhostVote = bp != null && bp.hasGhostVote();
        String roleName = (bp != null && bp.getDisplayedRole() != null && !bp.getDisplayedRole().isEmpty())
                ? bp.getDisplayedRole() : "Citadin (Sans rôle)";
        String roleDesc = (bp != null && bp.getRoleDescription() != null && !bp.getRoleDescription().isEmpty())
                ? bp.getRoleDescription() : "Aucun pouvoir secret pour l'instant.";

        // ================= PAGE 1 : IDENTITÉ & RÔLE =================
        Component page1 = Component.text()
                .append(Component.text(" REGISTRE DU TRIBUNAL\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                .append(Component.text(" Ravenswood Bluff \n", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
                .append(Component.text("-------------------\n", NamedTextColor.GRAY))
                .append(Component.text("Citoyen : ", NamedTextColor.BLACK))
                .append(Component.text(player.getName() + "\n", NamedTextColor.DARK_BLUE, TextDecoration.BOLD))
                .append(Component.text("État : ", NamedTextColor.BLACK))
                .append(isAlive 
                        ? Component.text("VIVANT\n", NamedTextColor.DARK_GREEN, TextDecoration.BOLD) 
                        : Component.text("MORT (Fantôme)\n", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                .append(Component.text("Vote fantôme : ", NamedTextColor.BLACK))
                .append(isAlive
                        ? Component.text("Actif\n", NamedTextColor.DARK_GREEN)
                        : (hasGhostVote 
                                ? Component.text("DISPONIBLE (1x)\n", NamedTextColor.GOLD, TextDecoration.BOLD)
                                : Component.text("UTILISÉ\n", NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)))
                .append(Component.text("-------------------\n", NamedTextColor.GRAY))
                .append(Component.text("RÔLE SECRET :\n", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
                .append(Component.text("» " + roleName + "\n", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(roleDesc + "\n\n", NamedTextColor.BLACK))
                .append(Component.text("[ ✉ Poser une Question ]", NamedTextColor.DARK_AQUA, TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.runCommand("/botc question"))
                        .hoverEvent(HoverEvent.showText(Component.text("Poser une question secrète au Conteur", NamedTextColor.GRAY))))
                .build();

        // ================= PAGE 2 : ACTIONS AU TRIBUNAL =================
        TextComponent.Builder page2Builder = Component.text()
                .append(Component.text(" ACTIONS DU CONSEIL\n", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                .append(Component.text("-------------------\n", NamedTextColor.GRAY));

        // 1. Bouton de Vote
        if (isAlive || hasGhostVote) {
            page2Builder.append(Component.text("LEVER LA MAIN :\n", NamedTextColor.BLACK, TextDecoration.BOLD))
                    .append(Component.text(" [ ✋ VOTER OUI ]\n", NamedTextColor.DARK_GREEN, TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/botc voteoui"))
                            .hoverEvent(HoverEvent.showText(Component.text(
                                    isAlive ? "Clique pour voter OUI à l'accusation en cours" : "ATTENTION : C'est ton DERNIER vote !", 
                                    isAlive ? NamedTextColor.GREEN : NamedTextColor.RED))))
                    .append(Component.text(isAlive ? "Vote illimité tant que vivant.\n\n" : "Consomme ton ultime jeton !\n\n", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));
        } else {
            page2Builder.append(Component.text("VOTE IMPOSSIBLE :\n", NamedTextColor.BLACK, TextDecoration.BOLD))
                    .append(Component.text(" [ ✕ VOTE ÉPUISÉ ]\n", NamedTextColor.DARK_RED, TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Tu as déjà utilisé ton vote de fantôme !", NamedTextColor.RED))))
                    .append(Component.text("Ton vote a déjà été utilisé.\n\n", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));
        }

        // 2. Bouton de Nomination
        if (isAlive) {
            page2Builder.append(Component.text("ACCUSATION :\n", NamedTextColor.BLACK, TextDecoration.BOLD))
                    .append(Component.text(" [ ⚖ NOMMER UN SUSPECT ]\n", NamedTextColor.GOLD, TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/botc nomination"))
                            .hoverEvent(HoverEvent.showText(Component.text("Ouvrir la liste pour envoyer quelqu'un à l'échafaud", NamedTextColor.YELLOW))))
                    .append(Component.text("Une accusation max par jour.\n\n", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));
        } else {
            page2Builder.append(Component.text("ACCUSATION :\n", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                    .append(Component.text(" Les morts ne peuvent plus désigner de suspect.\n\n", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));
        }

        // 3. Demande de Parole (pour les fantômes)
        if (!isAlive) {
            page2Builder.append(Component.text("INTERVENTION :\n", NamedTextColor.BLACK, TextDecoration.BOLD))
                    .append(Component.text(" [ 🗣 DEMANDER LA PAROLE ]\n", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/botc demandeparole"))
                            .hoverEvent(HoverEvent.showText(Component.text("Demander poliment la parole au Conteur", NamedTextColor.LIGHT_PURPLE))))
                    .append(Component.text("Avertit les Conteurs dans le chat.\n", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));
        }

        Component page2 = page2Builder.build();

        Book book = Book.book(
                Component.text("Registre du Tribunal"),
                Component.text("Le Conteur"),
                List.of(page1, page2)
        );

        player.openBook(book);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.8f, 1.0f);
    }
}
