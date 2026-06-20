package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AddChairCommand implements SubCommand {

    private final Botc main;

    public AddChairCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "addchair";
    }

    @Override
    public String getDescription() {
        return "Ajoute une chaise pour les joueurs (conseil).";
    }

    @Override
    public String getSyntax() {
        return "/botc addchair";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        List<String> chairs = main.getConfig().getStringList(main.getPresetPath("chairs"));
        // On stocke sous format : monde,x,y,z,yaw
        org.bukkit.Location loc = player.getLocation();
        String locStr = player.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ","
                + loc.getYaw();

        chairs.add(locStr);
        main.getConfig().set(main.getPresetPath("chairs"), chairs);
        main.saveConfig();

        player.sendMessage(
                Component.text("Chaise #" + chairs.size() + " enregistree avec succes !", NamedTextColor.GREEN));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
