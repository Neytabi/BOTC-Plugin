package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShowChairsCommand implements SubCommand {

    private final Botc main;

    public ShowChairsCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "showchairs";
    }

    @Override
    public String getDescription() {
        return "Affiche l'emplacement de toutes les chaises (particules).";
    }

    @Override
    public String getSyntax() {
        return "/botc showchairs";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        List<String> chairs = main.getConfig().getStringList(main.getPresetPath("chairs"));
        player.sendMessage(Component.text("Affichage des " + chairs.size() + " chaises pendant 10 secondes...",
                NamedTextColor.YELLOW));

        // On fait apparaître des flammes/particules sur chaque chaise
        for (String chairStr : chairs) {
            String[] parts = chairStr.split(",");
            org.bukkit.World w = Bukkit.getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            if (w != null) {
                // On fait spawner des particules de portail colorées pour le Conteur
                w.spawnParticle(org.bukkit.Particle.WITCH, new org.bukkit.Location(w, x, y + 0.5, z), 20, 0.1, 0.1,
                        0.1, 0.0);
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
