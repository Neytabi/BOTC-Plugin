package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetLightningCommand implements SubCommand {

    private final Botc main;

    public SetLightningCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "setlightning";
    }

    @Override
    public String getDescription() {
        return "Définit le type d'éclair (player/local).";
    }

    @Override
    public String getSyntax() {
        return "/botc setlightning <player/local>";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage : " + getSyntax(), NamedTextColor.RED));
            return;
        }
        String mode = args[1].toLowerCase();

        if (mode.equals("player")) {
            main.getConfig().set(main.getPresetPath("lightning.mode"), "player");
            main.saveConfig();
            player.sendMessage(
                    Component.text("L'éclair tombera désormais sur le joueur mort.", NamedTextColor.GREEN));
            return;
        }

        if (mode.equals("local")) {
            main.getConfig().set(main.getPresetPath("lightning.mode"), "local");
            main.getConfig().set(main.getPresetPath("lightning.world"), player.getWorld().getName());
            main.getConfig().set(main.getPresetPath("lightning.x"), player.getLocation().getX());
            main.getConfig().set(main.getPresetPath("lightning.y"), player.getLocation().getY());
            main.getConfig().set(main.getPresetPath("lightning.z"), player.getLocation().getZ());
            main.saveConfig();

            player.sendMessage(Component.text(
                    "L'éclair tombera désormais à ta position actuelle de tribunal sur cette map.",
                    NamedTextColor.GREEN));
            return;
        }

        player.sendMessage(Component.text("Usage : " + getSyntax(), NamedTextColor.RED));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            if ("player".startsWith(input))
                completions.add("player");
            if ("local".startsWith(input))
                completions.add("local");
        }
        return completions;
    }
}
