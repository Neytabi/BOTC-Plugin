package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.BotcPlayer;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NuitCommand implements SubCommand {

    private final Botc main;

    public NuitCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "nuit";
    }

    @Override
    public String getDescription() {
        return "Lance la nuit (animation ciel + marche libre).";
    }

    @Override
    public String getSyntax() {
        return "/botc nuit";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.setNight(true);

        // Activer le cycle pour permettre le mouvement fluide du soleil
        player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, true);

        new org.bukkit.scheduler.BukkitRunnable() {
            long currentTime = player.getWorld().getTime();
            final long targetTime = 18000; // Nuit noire

            @Override
            public void run() {
                // Si on a atteint la nuit noire, on fige le temps et on lance le gameplay
                if (currentTime >= targetTime || currentTime < 1000) {
                    player.getWorld().setTime(targetTime);
                    player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
                    this.cancel();

                    // APPEL DU GAMEPLAY DE NUIT (Une fois le soleil couché)
                    lancerMecaniquesNuit(player);
                    return;
                }

                // Accélération du temps (350 ticks par répétition, exécuté toutes les ticks)
                currentTime += 350;
                player.getWorld().setTime(currentTime);

                // Petit bruit de battement sourd pendant que le soleil décline
                if (currentTime % 1400 == 0) {
                    for (Player onlineP : Bukkit.getOnlinePlayers()) {
                        onlineP.playSound(onlineP.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.6f,
                                0.5f);
                    }
                }
            }
        }.runTaskTimer(main, 0L, 1L); // 1L = exécution à chaque tick pour un effet fluide à 60fps

        player.sendMessage(Component.text("-> Prochaine étape : ", NamedTextColor.AQUA)
                .append(Component.text("Mettre le jour", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc jour")));
    }

    private void lancerMecaniquesNuit(Player player) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");
        List<String> roomsStr = main.getConfig().getStringList(main.getPresetPath("rooms"));

        net.kyori.adventure.title.Title nightTitle = net.kyori.adventure.title.Title.title(
                Component.text(" LA NUIT TOMBE ", NamedTextColor.BLUE).decorate(TextDecoration.BOLD),
                Component.text("Fermez les yeux... Le démon s'éveille.", NamedTextColor.GRAY));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (nightTeam != null)
                nightTeam.addEntry(p.getName());
            p.showTitle(nightTitle);
            p.playSound(p.getLocation(), org.bukkit.Sound.AMBIENT_CAVE, 1.5f, 0.5f);
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.2f);
            p.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, p.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.0);
        }

        //  CORRECTION LOGIQUE : On attribue les chambres selon le CHAIR INDEX fixe de
        // la game !
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp())
                continue;

            BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
            if (bp == null || bp.getChairIndex() == -1)
                continue;

            int index = bp.getChairIndex(); // Récupère le numéro de siège fixe (0, 1, 2...)
            if (index >= roomsStr.size())
                break; // Sécurité si pas assez de chambres configurées

            String[] roomParts = roomsStr.get(index).split(",");
            org.bukkit.World wRoom = Bukkit.getWorld(roomParts[0]);

            if (wRoom != null) {
                int bx = Integer.parseInt(roomParts[1]);
                int by = Integer.parseInt(roomParts[2]);
                int bz = Integer.parseInt(roomParts[3]);

                org.bukkit.block.Block block = wRoom.getBlockAt(bx, by, bz);

                // On force le nettoyage du cache visuel
                block.setType(org.bukkit.Material.AIR);
                block.setType(org.bukkit.Material.PLAYER_HEAD);

                if (roomParts.length > 4) {
                    try {
                        org.bukkit.block.BlockFace facing = org.bukkit.block.BlockFace.valueOf(roomParts[4]);
                        if (block.getBlockData() instanceof org.bukkit.block.data.Rotatable rotatable) {
                            rotatable.setRotation(facing);
                            block.setBlockData(rotatable);
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                //  CORRECTION VISUELLE : Attribution du skin décalée d'un tick pour éviter
                // Steve/Alex
                final Player targetPlayer = p;
                new org.bukkit.scheduler.BukkitRunnable() {
                    @Override
                    public void run() {
                        if (block.getState() instanceof org.bukkit.block.Skull skullState) {
                            skullState.setOwningPlayer(targetPlayer);
                            skullState.update(true, false); // Le true force l'envoi du paquet de skin aux clients
                        }
                    }
                }.runTaskLater(main, 1L); // Délai d'un tick minuscule mais indispensable

                final org.bukkit.Location targetRoomLoc = block.getLocation().add(0.5, 0, 0.5);

                // Message privé basé sur le vrai numéro de chambre aligné à la chaise
                p.sendMessage(Component.text("-> La nuit tombe ! Rejoins vite tes quartiers : ", NamedTextColor.RED)
                        .append(Component.text("Chambre #" + (index + 1), NamedTextColor.YELLOW)
                                .decorate(TextDecoration.BOLD)));

                // Lancement du GPS Boussole 3D towards targeted room
                new org.bukkit.scheduler.BukkitRunnable() {
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (ticks > 50 || !p.isOnline()) {
                            this.cancel();
                            return;
                        }

                        org.bukkit.Location pLoc = p.getLocation();
                        double distance = pLoc.distance(targetRoomLoc);

                        if (distance < 2.0) {
                            p.sendActionBar(
                                    Component.text(" Vous êtes en sécurité dans votre chambre ", NamedTextColor.GREEN)
                                            .decorate(TextDecoration.BOLD));
                            this.cancel();
                            return;
                        }

                        p.sendActionBar(Component.text("-> Ta Chambre : ", NamedTextColor.GOLD)
                                .append(Component.text((int) distance + "m", NamedTextColor.YELLOW)
                                        .decorate(TextDecoration.BOLD)));

                        org.bukkit.util.Vector direction = targetRoomLoc.toVector().subtract(pLoc.toVector())
                                .normalize();
                        org.bukkit.Location arrowStart = p.getEyeLocation()
                                .add(p.getLocation().getDirection().multiply(1.2));

                        for (double d = 0; d < 1.0; d += 0.35) {
                            org.bukkit.Location particlePoint = arrowStart.clone().add(direction.clone().multiply(d));
                            p.spawnParticle(org.bukkit.Particle.GLOW, particlePoint, 1, 0, 0, 0, 0);
                        }
                        ticks++;
                    }
                }.runTaskTimer(main, 0L, 10L);
            }
        }

        Bukkit.broadcast(Component.text("[BOTC] La nuit est tombée... Regagnez vos chambres avant l'arrivée du démon !",
                NamedTextColor.DARK_BLUE).decorate(TextDecoration.BOLD));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
