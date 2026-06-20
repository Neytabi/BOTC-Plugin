package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AddRoomCommand implements SubCommand {

    private final Botc main;

    public AddRoomCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "addroom";
    }

    @Override
    public String getDescription() {
        return "Ajoute une chambre (lit) pour les joueurs.";
    }

    @Override
    public String getSyntax() {
        return "/botc addroom";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        List<String> rooms = main.getConfig().getStringList(main.getPresetPath("rooms"));
        org.bukkit.Location eyeLoc = player.getEyeLocation();
        org.bukkit.block.BlockFace facing = player.getFacing();

        String locStr = eyeLoc.getWorld().getName() + ","
                + eyeLoc.getBlockX() + ","
                + eyeLoc.getBlockY() + ","
                + eyeLoc.getBlockZ() + ","
                + facing.name();

        rooms.add(locStr);
        main.getConfig().set(main.getPresetPath("rooms"), rooms);
        main.saveConfig();

        player.sendMessage(Component.text("Emplacement Chambre #" + rooms.size() + " enregistré dans le preset '"
                + main.getActivePreset() + "' (Y=" + eyeLoc.getBlockY() + ") !", NamedTextColor.GREEN));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
