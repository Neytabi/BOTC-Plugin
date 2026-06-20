package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.PresetView;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
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

        // 1. LISTER LES PRESETS
        if (sub.equals("list")) {
            player.sendMessage(Component.text("=== 🗺️ LISTE DES MAPS / PRESETS ===", NamedTextColor.DARK_PURPLE)
                    .decorate(TextDecoration.BOLD));
            String active = main.getActivePreset();

            if (main.getConfig().getConfigurationSection("presets") == null) {
                player.sendMessage(Component.text("• " + active + " ", NamedTextColor.GREEN)
                        .append(Component.text("[ACTIF]", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
                return;
            }

            for (String key : main.getConfig().getConfigurationSection("presets").getKeys(false)) {
                if (key.equalsIgnoreCase(active)) {
                    player.sendMessage(Component.text("• " + key + " ", NamedTextColor.GREEN)
                            .append(Component.text("[ACTIF]", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
                } else {
                    player.sendMessage(Component.text("• " + key, NamedTextColor.GRAY));
                }
            }
            return;
        }

        if (args.length < 3) {
            player.sendMessage(Component.text("Erreur : Veuillez spécifier un nom de preset.", NamedTextColor.RED));
            return;
        }
        String presetName = args[2].toLowerCase().replaceAll("[^a-z0-9_]", ""); // Sécurité d'écriture

        // 2. CRÉER UN PRESET
        if (sub.equals("create")) {
            main.setActivePreset(presetName);
            if (!main.getConfig().contains("presets." + presetName)) {
                main.getConfig().set("presets." + presetName + ".chairs", new ArrayList<String>());
                main.getConfig().set("presets." + presetName + ".rooms", new ArrayList<String>());
                main.saveConfig();
                player.sendMessage(Component.text("✓ Preset '" + presetName + "' créé avec succès et sélectionné !",
                        NamedTextColor.GREEN));
            } else {
                player.sendMessage(
                        Component.text("Le preset '" + presetName + "' existe déjà. Basculement dessus effectué.",
                                NamedTextColor.YELLOW));
            }
            return;
        }

        // 3. SÉLECTIONNER UN PRESET
        if (sub.equals("select")) {
            if (!main.getConfig().contains("presets." + presetName) && !presetName.equals("default")) {
                player.sendMessage(Component.text("❌ Ce preset n'existe pas !", NamedTextColor.RED));
                return;
            }
            main.setActivePreset(presetName);
            player.sendMessage(Component.text("🗺️ Map changée ! Preset actif désormais : " + presetName,
                    NamedTextColor.GREEN));

            // 🌟 APPORT : TÉLÉPORTATION AUTOMATIQUE DU MJ AU TRIBUNAL DE LA NOUVELLE MAP
            if (main.getConfig().contains(main.getPresetPath("tribunal.x"))) {
                String worldName = main.getConfig().getString(main.getPresetPath("tribunal.world"));
                if (worldName != null) {
                    org.bukkit.World world = Bukkit.getWorld(worldName);
                    double tx = main.getConfig().getDouble(main.getPresetPath("tribunal.x"));
                    double ty = main.getConfig().getDouble(main.getPresetPath("tribunal.y"));
                    double tz = main.getConfig().getDouble(main.getPresetPath("tribunal.z"));
                    if (world != null) {
                        org.bukkit.Location tribunalSpawn = new org.bukkit.Location(world, tx, ty + 1.0, tz);

                        // 🌟 COUPORET DE TP GLOBAL : On embarque tout le monde (Joueurs + MJ)
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.teleport(tribunalSpawn);
                            p.sendMessage(Component.text(
                                    "⚡ Flash-TP ! Tout le village a été téléporté au Tribunal de la nouvelle map.",
                                    NamedTextColor.AQUA, TextDecoration.ITALIC));
                            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.0f);
                        }
                    }
                }
            }
            return;
        }

        // 4. SUPPRIMER UN PRESET
        if (sub.equals("delete")) {
            if (presetName.equals("default")) {
                player.sendMessage(
                        Component.text("Impossible de supprimer le preset par défaut.", NamedTextColor.RED));
                return;
            }
            main.getConfig().set("presets." + presetName, null);
            main.saveConfig();
            if (main.getActivePreset().equalsIgnoreCase(presetName)) {
                main.setActivePreset("default");
            }
            player.sendMessage(
                    Component.text("✓ Preset '" + presetName + "' définitivement supprimé.", NamedTextColor.GREEN));
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
            if (main.getConfig().getConfigurationSection("presets") != null) {
                for (String key : main.getConfig().getConfigurationSection("presets").getKeys(false)) {
                    if (key.startsWith(inputPreset)) {
                        completions.add(key);
                    }
                }
            }
        }
        return completions;
    }
}
