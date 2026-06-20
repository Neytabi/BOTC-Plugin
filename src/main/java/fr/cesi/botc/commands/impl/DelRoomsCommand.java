package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelRoomsCommand implements SubCommand {

    private final Botc main;

    public DelRoomsCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "delrooms";
    }

    @Override
    public String getDescription() {
        return "Supprime toutes les chambres du preset actif.";
    }

    @Override
    public String getSyntax() {
        return "/botc delrooms";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.getConfig().set(main.getPresetPath("rooms"), new ArrayList<String>());
        main.saveConfig();
        player.sendMessage(Component.text("Toutes les chambres ont été effacées.", NamedTextColor.RED));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
