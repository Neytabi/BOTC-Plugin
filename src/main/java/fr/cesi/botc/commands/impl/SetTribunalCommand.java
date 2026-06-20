package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetTribunalCommand implements SubCommand {

    private final Botc main;

    public SetTribunalCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "settribunal";
    }

    @Override
    public String getDescription() {
        return "Définit le centre du tribunal pour la map actuelle.";
    }

    @Override
    public String getSyntax() {
        return "/botc settribunal";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        org.bukkit.Location loc = player.getLocation();

        main.getConfig().set(main.getPresetPath("tribunal.world"), loc.getWorld().getName());
        main.getConfig().set(main.getPresetPath("tribunal.x"), loc.getX());
        main.getConfig().set(main.getPresetPath("tribunal.y"), loc.getY());
        main.getConfig().set(main.getPresetPath("tribunal.z"), loc.getZ());
        main.saveConfig();

        player.sendMessage(
                Component.text("Le centre de la salle du conseil a été enregistré ici !", NamedTextColor.GREEN));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
