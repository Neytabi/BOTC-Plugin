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

public class JourCommand implements SubCommand {

    private final Botc main;

    public JourCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "jour";
    }

    @Override
    public String getDescription() {
        return "Lance le jour (animation ciel + boussole vers le conseil).";
    }

    @Override
    public String getSyntax() {
        return "/botc jour";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.setNight(false);

        player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, true);

        new org.bukkit.scheduler.BukkitRunnable() {
            long currentTime = player.getWorld().getTime(); // On part de l'heure de la nuit (18000)
            final long targetTime = 30000; // Matin du jour suivant

            @Override
            public void run() {
                // Si le soleil est levé, on recale proprement à 1000 et on lance les boussoles
                if (currentTime >= targetTime) {
                    player.getWorld().setTime(6000);
                    player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
                    this.cancel();

                    // APPEL DU GAMEPLAY DE JOUR (Une fois le matin levé)
                    lancerMecaniquesJour(player);
                    return;
                }

                // Accélération du temps vers l'aube
                currentTime += 350;
                player.getWorld().setTime(currentTime);
            }
        }.runTaskTimer(main, 0L, 1L);

        player.sendMessage(Component.text("-> Prochaine étape : ", NamedTextColor.AQUA)
                .append(Component.text("Annoncer les morts", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc mort")));
    }

    private void lancerMecaniquesJour(Player player) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");

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

        if (finalTribunal == null) {
            player.sendMessage(Component.text("Erreur : Le tribunal n'est pas configuré ! (/botc settribunal)",
                    NamedTextColor.RED));
            return;
        }
        // Titre du réveil
        net.kyori.adventure.title.Title wakeTitle = net.kyori.adventure.title.Title.title(
                Component.text(" LE RÉVEIL ", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                Component.text("Regagnez le Tribunal pour le Conseil.", NamedTextColor.LIGHT_PURPLE));
        for (Player p : Bukkit.getOnlinePlayers()) {

            if (nightTeam != null && nightTeam.hasEntry(p.getName())) {
                nightTeam.removeEntry(p.getName());
            }

            p.showTitle(wakeTitle);
            p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.0f);

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

                    if (distance < 3.0) {
                        p.sendActionBar(
                                Component.text(" Vous êtes arrivé à la salle du conseil ", NamedTextColor.GREEN)
                                        .decorate(TextDecoration.BOLD));
                        p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.4f, 1.5f);
                        this.cancel();
                        return;
                    }

                    p.sendActionBar(Component.text("-> Salle du Conseil : ", NamedTextColor.GOLD)
                            .append(Component.text((int) distance + "m", NamedTextColor.YELLOW)
                                    .decorate(TextDecoration.BOLD)));

                    org.bukkit.util.Vector direction = finalTribunal.toVector().subtract(pLoc.toVector()).normalize();
                    org.bukkit.Location arrowStart = p.getEyeLocation()
                            .add(p.getLocation().getDirection().multiply(1.2));

                    for (double d = 0; d < 1.0; d += 0.35) {
                        org.bukkit.Location particlePoint = arrowStart.clone().add(direction.clone().multiply(d));
                        p.spawnParticle(org.bukkit.Particle.GLOW, particlePoint, 1, 0, 0, 0, 0);
                    }

                    if (ticks % 4 == 0) {
                        p.playSound(p.getEyeLocation(), org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.15f, 1.4f);
                    }
                    ticks++;
                }
            }.runTaskTimer(main, 0L, 10L);
        }
        Bukkit.broadcast(Component
                .text("[BOTC] Le soleil se lève ! Suivez la flèche boussole devant vos yeux pour rejoindre le conseil.",
                        NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
