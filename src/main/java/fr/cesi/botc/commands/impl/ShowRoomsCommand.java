package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShowRoomsCommand implements SubCommand {

    private final Botc main;

    public ShowRoomsCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "showrooms";
    }

    @Override
    public String getDescription() {
        return "Affiche l'emplacement de toutes les chambres (particules).";
    }

    @Override
    public String getSyntax() {
        return "/botc showrooms";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        List<String> rooms = main.getConfig().getStringList(main.getPresetPath("rooms"));
        if (rooms.isEmpty()) {
            player.sendMessage(Component.text("Erreur : Aucune chambre enregistrée.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(
                Component.text("Affichage des " + rooms.size() + " emplacements de chambres pendant 10 secondes...",
                        NamedTextColor.YELLOW));

        // On lance un compteur asynchrone qui va s'exécuter toutes les secondes
        new org.bukkit.scheduler.BukkitRunnable() {
            int secondsPassed = 0;

            @Override
            public void run() {
                // Arrêt automatique au bout de 10 secondes
                if (secondsPassed >= 10) {
                    player.sendMessage(Component.text("Fin de l'affichage des chambres.", NamedTextColor.GRAY));
                    this.cancel(); // Coupe le timer
                    return;
                }

                // Apparition des particules pour ce cycle
                for (String roomStr : rooms) {
                    String[] parts = roomStr.split(",");
                    org.bukkit.World w = Bukkit.getWorld(parts[0]);
                    if (w == null)
                        continue;

                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    // On descend la quantité à 5 particules par seconde (au lieu de 20 d'un coup)
                    // pour que ce soit fluide et très propre visuellement
                    w.spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER,
                            new org.bukkit.Location(w, x + 0.5, y + 0.5, z + 0.5),
                            5, 0.1, 0.1, 0.1, 0.0);
                }

                secondsPassed++;
            }
        }.runTaskTimer(main, 0L, 20L); // 20L = 20 ticks = 1 seconde en jeu.
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
