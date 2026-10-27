package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.PresetView;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PresetCommand implements SubCommand {

    private final Botc main;

    public PresetCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "preset";
    }

    @Override
    public String getDescription() {
        return "Gestion des maps / presets.";
    }

    @Override
    public String getSyntax() {
        return "/botc preset <list|create|select|delete> [nom]";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            new PresetView(main).openPresetMenu(player);
            return;
        }
        String sub = args[1].toLowerCase();

        switch (sub) {
            case "list":
                player.sendMessage(Component.text("===  LISTE DES MAPS / PRESETS ===", NamedTextColor.DARK_PURPLE)
                        .decorate(TextDecoration.BOLD));
                String active = main.getActivePreset();

                ConfigurationSection presetsSectionList = main.getConfig().getConfigurationSection("presets");
                if (presetsSectionList == null) {
                    player.sendMessage(Component.text("- " + active + " ", NamedTextColor.GREEN)
                            .append(Component.text("[ACTIF]", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
                    return;
                }

                for (String key : presetsSectionList.getKeys(false)) {
                    if (key.equalsIgnoreCase(active)) {
                        player.sendMessage(Component.text("- " + key + " ", NamedTextColor.GREEN)
                                .append(Component.text("[ACTIF]", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
                    } else {
                        player.sendMessage(Component.text("- " + key, NamedTextColor.GRAY));
                    }
                }
                break;

            case "create":
                if (args.length < 3) {
                    player.sendMessage(Component.text("Erreur : Veuillez spécifier un nom de preset.", NamedTextColor.RED));
                    return;
                }
                String createPresetName = args[2].toLowerCase().replaceAll("[^a-z0-9_]", "");

                main.setActivePreset(createPresetName);
                if (!main.getConfig().contains("presets." + createPresetName)) {
                    main.getConfig().set("presets." + createPresetName + ".chairs", new ArrayList<String>());
                    main.getConfig().set("presets." + createPresetName + ".rooms", new ArrayList<String>());
                    main.saveConfig();
                    player.sendMessage(Component.text("v Preset '" + createPresetName + "' créé avec succès et sélectionné !",
                            NamedTextColor.GREEN));
                } else {
                    player.sendMessage(
                            Component.text("Le preset '" + createPresetName + "' existe déjà. Basculement dessus effectué.",
                                    NamedTextColor.YELLOW));
                }
                break;

            case "select":
                if (args.length < 3) {
                    player.sendMessage(Component.text("Erreur : Veuillez spécifier un nom de preset.", NamedTextColor.RED));
                    return;
                }
                String selectPresetName = args[2].toLowerCase().replaceAll("[^a-z0-9_]", "");

                if (!main.getConfig().contains("presets." + selectPresetName) && !selectPresetName.equals("default")) {
                    player.sendMessage(Component.text("x Ce preset n'existe pas !", NamedTextColor.RED));
                    return;
                }
                main.setActivePreset(selectPresetName);
                player.sendMessage(Component.text(" Map changée ! Preset actif désormais : " + selectPresetName,
                        NamedTextColor.GREEN));

                //  APPORT : TÉLÉPORTATION AUTOMATIQUE DU MJ AU TRIBUNAL DE LA NOUVELLE MAP
                if (main.getConfig().contains(main.getPresetPath("tribunal.x"))) {
                    String worldName = main.getConfig().getString(main.getPresetPath("tribunal.world"));
                    if (worldName != null) {
                        org.bukkit.World world = Bukkit.getWorld(worldName);
                        double tx = main.getConfig().getDouble(main.getPresetPath("tribunal.x"));
                        double ty = main.getConfig().getDouble(main.getPresetPath("tribunal.y"));
                        double tz = main.getConfig().getDouble(main.getPresetPath("tribunal.z"));
                        if (world != null) {
                            org.bukkit.Location tribunalSpawn = new org.bukkit.Location(world, tx, ty + 1.0, tz);

                            //  COUPORET DE TP GLOBAL : On embarque tout le monde (Joueurs + MJ)
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.teleport(tribunalSpawn);
                                p.sendMessage(Component.text(
                                        " Flash-TP ! Tout le village a été téléporté au Tribunal de la nouvelle map.",
                                        NamedTextColor.AQUA, TextDecoration.ITALIC));
                                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.0f);
                            }
                        }
                    }
                }
                break;

            case "delete":
                if (args.length < 3) {
                    player.sendMessage(Component.text("Erreur : Veuillez spécifier un nom de preset.", NamedTextColor.RED));
                    return;
                }
                String deletePresetName = args[2].toLowerCase().replaceAll("[^a-z0-9_]", "");

                if (deletePresetName.equals("default")) {
                    player.sendMessage(
                            Component.text("Impossible de supprimer le preset par défaut.", NamedTextColor.RED));
                    return;
                }
                main.getConfig().set("presets." + deletePresetName, null);
                main.saveConfig();
                if (main.getActivePreset().equalsIgnoreCase(deletePresetName)) {
                    main.setActivePreset("default");
                }
                player.sendMessage(
                        Component.text("v Preset '" + deletePresetName + "' définitivement supprimé.", NamedTextColor.GREEN));
                break;

            default:
                player.sendMessage(Component.text("Sous-commande inconnue. Syntaxe : " + getSyntax(), NamedTextColor.RED));
                break;
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            String inputSub = args[1].toLowerCase();
            if ("create".startsWith(inputSub))
                completions.add("create");
            if ("select".startsWith(inputSub))
                completions.add("select");
            if ("list".startsWith(inputSub))
                completions.add("list");
            if ("delete".startsWith(inputSub))
                completions.add("delete");
        } else if (args.length == 3) {
            String inputPreset = args[2].toLowerCase();
            ConfigurationSection presetsSection = main.getConfig().getConfigurationSection("presets");
            if (presetsSection != null) {
                for (String key : presetsSection.getKeys(false)) {
                    if (key.startsWith(inputPreset)) {
                        completions.add(key);
                    }
                }
            }
        }
        return completions;
    }
}