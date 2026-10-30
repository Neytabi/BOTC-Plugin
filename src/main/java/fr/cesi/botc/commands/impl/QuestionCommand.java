package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestionCommand implements SubCommand {

    private final Botc main;

    public QuestionCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "question";
    }

    @Override
    public String getDescription() {
        return "Active le mode question secrète au Conteur.";
    }

    @Override
    public String getSyntax() {
        return "/botc question";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void execute(Player player, String[] args) {
        main.getIsAskingQuestion().put(player.getUniqueId(), true);
        player.sendMessage(Component.text(" Pose ta question directement dans le chat, elle sera transmise uniquement aux Conteurs :", NamedTextColor.LIGHT_PURPLE));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.0f);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
