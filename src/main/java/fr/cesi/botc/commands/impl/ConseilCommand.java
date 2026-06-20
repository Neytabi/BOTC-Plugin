package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConseilCommand implements SubCommand {

    private final Botc main;

    public ConseilCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "conseil";
    }

    @Override
    public String getDescription() {
        return "Ouvre le conseil et affiche les boussoles GPS vers le tribunal.";
    }

    @Override
    public String getSyntax() {
        return "/botc conseil";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.setCouncilOpen(true);
        main.getHasNominatedToday().clear();
        main.getHasBeenNominatedToday().clear();
        // 1. Préparation de l'affichage à l'écran
        net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                Component.text("⚖️ LE CONSEIL COMMENCE ⚖️", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                Component.text("Retournez au tribunal ! Fin du temps libre.", NamedTextColor.GRAY));

        // 2. Récupération de la position du Tribunal sauvegardée
        org.bukkit.Location tribunalLoc = null;
        if (main.getConfig().contains(main.getPresetPath("tribunal.x"))) {
            String worldName = main.getConfig().getString(main.getPresetPath("tribunal.world"));
            if (worldName != null) {
                org.bukkit.World world = Bukkit.getWorld(worldName);
                double tx = main.getConfig().getDouble(main.getPresetPath("tribunal.x"));
                double ty = main.getConfig().getDouble(main.getPresetPath("tribunal.y"));
                double tz = main.getConfig().getDouble(main.getPresetPath("tribunal.z"));
                if (world != null)
                    tribunalLoc = new org.bukkit.Location(world, tx, ty, tz);
            }
        }

        final org.bukkit.Location finalTribunal = tribunalLoc;

        // Sécurité si le Conteur a oublié le /botc settribunal
        if (finalTribunal == null) {
            player.sendMessage(Component.text("Erreur : Le tribunal n'est pas configuré ! (/botc settribunal)",
                    NamedTextColor.RED));
            return;
        }

        // 3. Activation des effets et de la boussole pour TOUS les joueurs
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(title);
            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 1.0f, 1.0f);

            // Lancement du GPS de retour au Conseil
            new org.bukkit.scheduler.BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks > 40 || !p.isOnline()) {
                        this.cancel();
                        return;
                    }

                    org.bukkit.Location pLoc = p.getLocation();
                    double distance = pLoc.distance(finalTribunal);

                    // Quand le joueur arrive à moins de 3 blocs du centre
                    if (distance < 3.0) {
                        p.sendActionBar(Component.text("✦ Vous êtes arrivé à votre siège ✦", NamedTextColor.GREEN)
                                .decorate(TextDecoration.BOLD));
                        p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.4f, 1.5f);
                        this.cancel();
                        return;
                    }

                    // Affichage de la distance dans la barre d'action
                    p.sendActionBar(Component.text("➔ Retour au Conseil : ", NamedTextColor.GOLD)
                            .append(Component.text((int) distance + "m", NamedTextColor.YELLOW)
                                    .decorate(TextDecoration.BOLD)));

                    // Calcul du vecteur directionnel pour les particules de lueur
                    org.bukkit.util.Vector direction = finalTribunal.toVector().subtract(pLoc.toVector())
                            .normalize();
                    org.bukkit.Location arrowStart = p.getEyeLocation()
                            .add(p.getLocation().getDirection().multiply(1.2));

                    for (double d = 0; d < 1.0; d += 0.35) {
                        org.bukkit.Location particlePoint = arrowStart.clone().add(direction.clone().multiply(d));
                        p.spawnParticle(org.bukkit.Particle.GLOW, particlePoint, 1, 0, 0, 0, 0);
                    }

                    // Petit tintement d'améthyste régulier
                    if (ticks % 4 == 0) {
                        p.playSound(p.getEyeLocation(), org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.15f, 1.4f);
                    }
                    ticks++;
                }
            }.runTaskTimer(main, 0L, 10L);
        }

        // 4. Message global dans le chat (Parfaitement adapté au Conseil)
        Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GOLD));
        Bukkit.broadcast(Component
                .text("[BOTC] Le Conseil est ouvert ! Suivez les flèches pour regagner immédiatement le tribunal.",
                        NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD));
        Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GOLD));

        player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                .append(Component.text("S'asseoir au tribunal", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc assis")));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
