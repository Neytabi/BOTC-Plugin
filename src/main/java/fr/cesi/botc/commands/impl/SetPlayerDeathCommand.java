package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetPlayerDeathCommand implements SubCommand {

    private final Botc main;

    public SetPlayerDeathCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "setplayerdeath";
    }

    @Override
    public String getDescription() {
        return "Définit le lieu d'apparition des fantômes (morts).";
    }

    @Override
    public String getSyntax() {
        return "/botc setplayerdeath";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        org.bukkit.Location loc = player.getLocation();

        main.getConfig().set(main.getPresetPath("death.world"), loc.getWorld().getName());
        main.getConfig().set(main.getPresetPath("death.x"), loc.getX());
        main.getConfig().set(main.getPresetPath("death.y"), loc.getY());
        main.getConfig().set(main.getPresetPath("death.z"), loc.getZ());
        main.getConfig().set(main.getPresetPath("death.yaw"), loc.getYaw());
        main.getConfig().set(main.getPresetPath("death.pitch"), loc.getPitch());
        main.saveConfig();

        player.sendMessage(Component.text(
                "L'emplacement d'exécution des morts a été enregistré ici pour le preset actif !",
                NamedTextColor.GREEN));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
