package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelChairsCommand implements SubCommand {

    private final Botc main;

    public DelChairsCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "delchairs";
    }

    @Override
    public String getDescription() {
        return "Supprime toutes les chaises du preset actif.";
    }

    @Override
    public String getSyntax() {
        return "/botc delchairs";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.getConfig().set(main.getPresetPath("chairs"), new ArrayList<String>());
        main.saveConfig();
        player.sendMessage(
                Component.text("Toutes les chaises ont ete supprimees de la configuration.", NamedTextColor.RED));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
