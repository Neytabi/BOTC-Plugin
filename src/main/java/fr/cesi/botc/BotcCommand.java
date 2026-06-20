package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BotcCommand implements CommandExecutor, org.bukkit.command.TabCompleter {

    private final Botc main;
    private final GrimoireView grimoireView;

    public BotcCommand(Botc main) {
        this.main = main;
        this.grimoireView = new GrimoireView(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Seul un joueur peut executer cette commande.", NamedTextColor.RED));
            return true;
        }





        // 2. Sécurité si aucun argument n'est fourni
        if (args.length == 0) {
            if (player.isOp()) {
                player.sendMessage(
                        Component.text("Usage Conteur : /botc help pour voir le guide.", NamedTextColor.RED));
            } else {
                player.sendMessage(
                        Component.text("Usage Joueur : /botc voteoui pour lever la main.", NamedTextColor.YELLOW));
            }
            return true;
        }

        // 3. Mur de sécurité : Tout ce qui suit (chaises, nuit, jour, etc.) requiert
        // d'être OP
        if (!player.isOp()) {
            player.sendMessage(Component.text("➔ Tu n'es pas le Conteur de cette partie !", NamedTextColor.RED));
            return true;
        }

        // AJOUTER LE POINT CENTRAL DU TRIBUNAL / CONSEIL
        if (args[0].equalsIgnoreCase("settribunal")) {
            org.bukkit.Location loc = player.getLocation();

            main.getConfig().set(main.getPresetPath("tribunal.world"), loc.getWorld().getName());
            main.getConfig().set(main.getPresetPath("tribunal.x"), loc.getX());
            main.getConfig().set(main.getPresetPath("tribunal.y"), loc.getY());
            main.getConfig().set(main.getPresetPath("tribunal.z"), loc.getZ());
            main.saveConfig();

            player.sendMessage(
                    Component.text("Le centre de la salle du conseil a été enregistré ici !", NamedTextColor.GREEN));
            return true;
        }

        // ==========================================
        // 🗺️ GESTION DES PRESETS MULTI-MAPS (/botc preset ...)
        // ==========================================
        if (args[0].equalsIgnoreCase("preset")) {
            if (args.length < 2) {
                new PresetView(main).openPresetMenu(player);
                return true;
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
                    return true;
                }

                for (String key : main.getConfig().getConfigurationSection("presets").getKeys(false)) {
                    if (key.equalsIgnoreCase(active)) {
                        player.sendMessage(Component.text("• " + key + " ", NamedTextColor.GREEN)
                                .append(Component.text("[ACTIF]", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)));
                    } else {
                        player.sendMessage(Component.text("• " + key, NamedTextColor.GRAY));
                    }
                }
                return true;
            }

            if (args.length < 3) {
                player.sendMessage(Component.text("Erreur : Veuillez spécifier un nom de preset.", NamedTextColor.RED));
                return true;
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
                return true;
            }

            // 3. SÉLECTIONNER UN PRESET
            if (sub.equals("select")) {
                if (!main.getConfig().contains("presets." + presetName) && !presetName.equals("default")) {
                    player.sendMessage(Component.text("❌ Ce preset n'existe pas !", NamedTextColor.RED));
                    return true;
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
                return true;
            }

            // 4. SUPPRIMER UN PRESET
            if (sub.equals("delete")) {
                if (presetName.equals("default")) {
                    player.sendMessage(
                            Component.text("Impossible de supprimer le preset par défaut.", NamedTextColor.RED));
                    return true;
                }
                main.getConfig().set("presets." + presetName, null);
                main.saveConfig();
                if (main.getActivePreset().equalsIgnoreCase(presetName)) {
                    main.setActivePreset("default");
                }
                player.sendMessage(
                        Component.text("✓ Preset '" + presetName + "' définitivement supprimé.", NamedTextColor.GREEN));
                return true;
            }
        }

        // ==========================================================
        // 🎙️ GESTION DU SON : COUPE-PAROLE GLOBAL DU CONTEUR (API)
        // ==========================================================
        if (args[0].equalsIgnoreCase("silence")) {
            if (!player.isOp())
                return true;

            Bukkit.broadcast(
                    Component.text("🤫 Le Conteur réclame un silence absolu ! Micros coupés.", NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp())
                    continue;

                // On ajoute le joueur à la liste noire (l'API bloquera son micro
                // instantanément)
                main.getVcMutedPlayers().add(p.getUniqueId());
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 0.4f, 0.5f);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("paroleall")) {
            if (!player.isOp())
                return true;

            Bukkit.broadcast(
                    Component.text("🗣️ Le Conteur vous redonne la parole. Le débat reprend !", NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp())
                    continue;

                // On le retire de la liste noire
                main.getVcMutedPlayers().remove(p.getUniqueId());
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.2f);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("vcmute") && args.length > 1) {
            if (!player.isOp())
                return true;
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                main.getVcMutedPlayers().add(target.getUniqueId());
                target.sendMessage(
                        Component.text("🤫 Le Conteur a temporairement coupé ton micro.", NamedTextColor.RED));
                player.sendMessage(
                        Component.text("✓ Micro de " + target.getName() + " coupé via l'API.", NamedTextColor.GREEN));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("vcunmute") && args.length > 1) {
            if (!player.isOp())
                return true;
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                main.getVcMutedPlayers().remove(target.getUniqueId());
                target.sendMessage(
                        Component.text("🗣️ Le Conteur t'autorise de nouveau à parler.", NamedTextColor.GREEN));
                player.sendMessage(Component.text("✓ Micro de " + target.getName() + " réactivé via l'API.",
                        NamedTextColor.GREEN));
            }
            return true;
        }



        // ==========================================
        // COMMANDE : /botc shownames
        // ==========================================
        if (args[0].equalsIgnoreCase("shownames")) {
            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard()
                    .getTeam("botc_night");
            main.setNameTagsHidden(false);

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (nightTeam != null && nightTeam.hasEntry(p.getName())) {
                    nightTeam.removeEntry(p.getName());
                }
            }
            player.sendMessage(Component.text("✓ Tous les pseudos sont désormais VISIBLES.", NamedTextColor.GREEN));
            return true;
        }

        // ==========================================
        // ⚖️ COMMANDE : /botc order (Affiche l'ordre du cercle pour le MJ)
        // ==========================================
        if (args[0].equalsIgnoreCase("order")) {
            if (!main.isSeatsAssigned()) {
                player.sendMessage(Component.text(
                        "🚨 L'ordre n'est pas encore généré ! Lance d'abord un /botc assis pour créer le cercle de cette game.",
                        NamedTextColor.RED));
                return true;
            }

            player.sendMessage(
                    Component.text("=== 🔄 ORDRE DU CERCLE DE JEU (Du siège 1 à X) ===", NamedTextColor.DARK_PURPLE)
                            .decorate(TextDecoration.BOLD));

            // On récupère et trie les joueurs selon leur index de chaise
            java.util.List<BotcPlayer> orderedPlayers = new java.util.ArrayList<>(main.getPlayersMap().values());
            orderedPlayers.removeIf(bp -> bp.getChairIndex() == -1);
            orderedPlayers.sort(java.util.Comparator.comparingInt(BotcPlayer::getChairIndex));

            if (orderedPlayers.isEmpty()) {
                player.sendMessage(
                        Component.text("Aucun joueur n'est assis sur une chaise actuellement.", NamedTextColor.GRAY));
                return true;
            }

            for (BotcPlayer bp : orderedPlayers) {
                // On prépare le tag de vie (Vert si vivant, Rouge 💀 si mort)
                Component statusTag = bp.isAlive()
                        ? Component.text("[VIVANT]", NamedTextColor.GREEN)
                        : Component.text("[MORT 💀]", NamedTextColor.RED);

                // On affiche la ligne : Siège #1 : Pseudo | [VIVANT] | Rôle : Diablotin
                player.sendMessage(Component.text("🪑 Siège #" + (bp.getChairIndex() + 1) + " : ", NamedTextColor.GOLD)
                        .append(Component.text(bp.getPlayerName(), NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                        .append(Component.text(" | ", NamedTextColor.GRAY))
                        .append(statusTag)
                        .append(Component.text(" | Rôle : ", NamedTextColor.GRAY))
                        .append(Component.text(bp.getDisplayedRole(), NamedTextColor.LIGHT_PURPLE)));
            }
            player.sendMessage(
                    Component.text("--------------------------------------------------", NamedTextColor.GRAY));
            return true;
        }

        // ==========================================
        // COMMANDE : /botc hidenames (Cache les pseudos de tout le monde)
        // ==========================================
        // ==========================================
        // COMMANDE : /botc hidenames
        // ==========================================
        if (args[0].equalsIgnoreCase("hidenames")) {
            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard()
                    .getTeam("botc_night");

            if (nightTeam == null) {
                player.sendMessage(
                        Component.text("Erreur : L'équipe de nuit n'est pas initialisée.", NamedTextColor.RED));
                return true;
            }
            main.setNameTagsHidden(true);

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp())
                    continue;
                nightTeam.addEntry(p.getName());
            }
            player.sendMessage(
                    Component.text("✓ Tous les pseudos sont désormais CACHÉS (Anonymat actif).", NamedTextColor.RED));
            return true;
        }

        // ==========================================
        // CONFIGURATION DES CHAMBRES (Avec sauvegarde de l'orientation)
        // =========================================
        if (args[0].equalsIgnoreCase("addroom")) {
            List<String> rooms = main.getConfig().getStringList(main.getPresetPath("rooms"));
            org.bukkit.Location eyeLoc = player.getEyeLocation();
            org.bukkit.block.BlockFace facing = player.getFacing();

            String locStr = eyeLoc.getWorld().getName() + ","
                    + eyeLoc.getBlockX() + ","
                    + eyeLoc.getBlockY() + ","
                    + eyeLoc.getBlockZ() + ","
                    + facing.name();

            rooms.add(locStr);
            main.getConfig().set(main.getPresetPath("rooms"), rooms); // 🌟 CORRIGÉ ICI
            main.saveConfig();

            player.sendMessage(Component.text("Emplacement Chambre #" + rooms.size() + " enregistré dans le preset '"
                    + main.getActivePreset() + "' (Y=" + eyeLoc.getBlockY() + ") !", NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("delrooms")) {
            main.getConfig().set(main.getPresetPath("rooms"), new ArrayList<String>());
            main.saveConfig();
            player.sendMessage(Component.text("Toutes les chambres ont été effacées.", NamedTextColor.RED));
            return true;
        }

        // GESTION DES CHAMBRES : TEST VISUEL (PARTICULES QUANTIQUE CYCLIQUE)
        if (args[0].equalsIgnoreCase("showrooms")) {
            List<String> rooms = main.getConfig().getStringList(main.getPresetPath("rooms"));
            if (rooms.isEmpty()) {
                player.sendMessage(Component.text("Erreur : Aucune chambre enregistrée.", NamedTextColor.RED));
                return true;
            }

            player.sendMessage(
                    Component.text("Affichage des " + rooms.size() + " emplacements de chambres pendant 10 secondes...",
                            NamedTextColor.YELLOW));

            // On lance un compteur asynchrone qui va s'exécuter toutes les secondes
            new org.bukkit.scheduler.BukkitRunnable() {
                int secondsPassed = 0;

                @Override
                public void run() {
                    // Arrêt automatique au bout de 10 secondes
                    if (secondsPassed >= 10) {
                        player.sendMessage(Component.text("Fin de l'affichage des chambres.", NamedTextColor.GRAY));
                        this.cancel(); // Coupe le timer
                        return;
                    }

                    // Apparition des particules pour ce cycle
                    for (String roomStr : rooms) {
                        String[] parts = roomStr.split(",");
                        org.bukkit.World w = Bukkit.getWorld(parts[0]);
                        if (w == null)
                            continue;

                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double z = Double.parseDouble(parts[3]);

                        // On descend la quantité à 5 particules par seconde (au lieu de 20 d'un coup)
                        // pour que ce soit fluide et très propre visuellement
                        w.spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER,
                                new org.bukkit.Location(w, x + 0.5, y + 0.5, z + 0.5),
                                5, 0.1, 0.1, 0.1, 0.0);
                    }

                    secondsPassed++;
                }
            }.runTaskTimer(main, 0L, 20L); // 20L = 20 ticks = 1 seconde en jeu.

            return true;
        }

        // --- GESTION DES CHAISES ---

        // 1. AJOUTER UNE CHAISE
        if (args[0].equalsIgnoreCase("addchair")) {
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
            return true;
        }

        // 2. SUPPRIMER TOUTES LES CHAISES
        if (args[0].equalsIgnoreCase("delchairs")) {
            main.getConfig().set(main.getPresetPath("chairs"), new ArrayList<String>());
            main.saveConfig();
            player.sendMessage(
                    Component.text("Toutes les chaises ont ete supprimees de la configuration.", NamedTextColor.RED));
            return true;
        }

        // 3. AFFICHER LES CHAISES (VISUEL DE CONFIG)
        if (args[0].equalsIgnoreCase("showchairs")) {
            List<String> chairs = main.getConfig().getStringList(main.getPresetPath("chairs"));
            player.sendMessage(Component.text("Affichage des " + chairs.size() + " chaises pendant 10 secondes...",
                    NamedTextColor.YELLOW));

            // On fait apparaître des flammes/particules sur chaque chaise
            for (String chairStr : chairs) {
                String[] parts = chairStr.split(",");
                org.bukkit.World w = Bukkit.getWorld(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                if (w != null) {
                    // On fait spawner des particules de portail colorées pour le Conteur
                    w.spawnParticle(org.bukkit.Particle.WITCH, new org.bukkit.Location(w, x, y + 0.5, z), 20, 0.1, 0.1,
                            0.1, 0.0);
                }
            }
            return true;
        }

        // ==========================================
        // COMMANDE /botc debout (Libération + Masquage des NameTags)
        // ==========================================
        if (args[0].equalsIgnoreCase("debout")) {
            main.setCouncilOpen(false);
            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard()
                    .getTeam("botc_night");

            // --- DÉCONNEXION DU GROUPE VOCAL (VERSION DIAGNOSTIC) ---
            if (main.getVoicechatPlugin() != null && main.getVoicechatPlugin().getVoicechatApi() != null) {
                de.maxhenkel.voicechat.api.VoicechatServerApi voiceApi = main.getVoicechatPlugin().getVoicechatApi();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    de.maxhenkel.voicechat.api.VoicechatConnection connection = voiceApi
                            .getConnectionOf(p.getUniqueId());
                    if (connection != null) {
                        connection.setGroup(null); // Quitte le groupe
                    }
                }
            }

            net.kyori.adventure.title.Title deboutTitle = net.kyori.adventure.title.Title.title(
                    Component.text("! LEVEZ-VOUS !", NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                    Component.text("Vous pouvez quitter votre siège.", NamedTextColor.GRAY));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(deboutTitle);
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_HORSE_GALLOP, 0.5f, 1.2f); // Bruit de pas légers

                if (p.isOp())
                    continue;

                // Si le joueur est dans un véhicule tagué "botc_chair"
                if (p.getVehicle() != null && p.getVehicle().getScoreboardTags().contains("botc_chair")) {
                    org.bukkit.entity.Entity chair = p.getVehicle();
                    chair.removePassenger(p); // On libère le joueur
                    chair.remove(); // On détruit le wagon invisible instantanément
                }

                if (nightTeam != null) {
                    nightTeam.addEntry(p.getName());
                }
            }

            Bukkit.broadcast(
                    Component.text("[BOTC] Le conseil est terminé, vous pouvez vous lever.", NamedTextColor.GREEN));

            player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                    .append(Component.text("Mettre la nuit", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc nuit")));

            return true;
        }

        if (args[0].equalsIgnoreCase("mort")) {
            if (!player.isOp())
                return true;

            net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                    Component.text("💀 ANNONCE DES MORTS 💀", NamedTextColor.RED).decorate(TextDecoration.BOLD),
                    Component.text("Écoutez attentivement le Conteur...", NamedTextColor.GRAY));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(title);
                // Un bruit d'éclair lointain pour l'ambiance sombre
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.8f);
            }

            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.RED));
            Bukkit.broadcast(Component
                    .text("[BOTC] Silence ! Le Conteur va annoncer les victimes de la nuit.", NamedTextColor.DARK_RED)
                    .decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.RED));

            player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                    .append(Component.text("Temps libre", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc tempslibre")));

            return true;
        }

        if (args[0].equalsIgnoreCase("tempslibre")) {
            if (!player.isOp())
                return true;

            net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                    Component.text("🗣️ TEMPS LIBRE 🗣️", NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                    Component.text("Dispersez-vous et complotez en secret !", NamedTextColor.GRAY));

            // On force tout le monde à se lever pour qu'ils puissent courir partout
            player.performCommand("botc debout");

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(title);
                // Un petit jingle de début de journée léger
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7f, 1.2f);
            }

            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GREEN));
            Bukkit.broadcast(
                    Component.text("[BOTC] Le temps libre est déclaré. Les discussions privées sont autorisées !",
                            NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GREEN));

            player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                    .append(Component.text("Ouvrir le conseil", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc conseil")));

            return true;
        }
        if (args[0].equalsIgnoreCase("grantparole") && args.length > 1) {
            if (!player.isOp())
                return true;
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                Bukkit.broadcast(Component
                        .text("Silence ! Le Conteur accorde officiellement la parole à " + target.getName() + ".",
                                NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 0.8f, 1.4f);
                }
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("conseil")) {
            if (!player.isOp())
                return true;
            main.setCouncilOpen(true);
            main.getHasNominatedToday().clear();
            main.getHasBeenNominatedToday().clear();
            // 1. Préparation de l'affichage à l'écran
            net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                    Component.text("⚖️ LE CONSEIL COMMENCE ⚖️", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                    Component.text("Retournez au tribunal ! Fin du temps libre.", NamedTextColor.GRAY));

            // 2. Récupération de la position du Tribunal sauvegardée
            org.bukkit.Location tribunalLoc = null;
            if (main.getConfig().contains(main.getPresetPath("tribunal.x"))) {
                String worldName = main.getConfig().getString(main.getPresetPath("tribunal.world"));
                if (worldName != null) {
                    org.bukkit.World world = Bukkit.getWorld(worldName);
                    double tx = main.getConfig().getDouble(main.getPresetPath("tribunal.x"));
                    double ty = main.getConfig().getDouble(main.getPresetPath("tribunal.y"));
                    double tz = main.getConfig().getDouble(main.getPresetPath("tribunal.z"));
                    if (world != null)
                        tribunalLoc = new org.bukkit.Location(world, tx, ty, tz);
                }
            }

            final org.bukkit.Location finalTribunal = tribunalLoc;

            // Sécurité si le Conteur a oublié le /botc settribunal
            if (finalTribunal == null) {
                player.sendMessage(Component.text("Erreur : Le tribunal n'est pas configuré ! (/botc settribunal)",
                        NamedTextColor.RED));
                return true;
            }

            // 3. Activation des effets et de la boussole pour TOUS les joueurs
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(title);
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 1.0f, 1.0f);

                // Lancement du GPS de retour au Conseil
                new org.bukkit.scheduler.BukkitRunnable() {
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (ticks > 40 || !p.isOnline()) {
                            this.cancel();
                            return;
                        }

                        org.bukkit.Location pLoc = p.getLocation();
                        double distance = pLoc.distance(finalTribunal);

                        // Quand le joueur arrive à moins de 3 blocs du centre
                        if (distance < 3.0) {
                            p.sendActionBar(Component.text("✦ Vous êtes arrivé à votre siège ✦", NamedTextColor.GREEN)
                                    .decorate(TextDecoration.BOLD));
                            p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.4f, 1.5f);
                            this.cancel();
                            return;
                        }

                        // Affichage de la distance dans la barre d'action
                        p.sendActionBar(Component.text("➔ Retour au Conseil : ", NamedTextColor.GOLD)
                                .append(Component.text((int) distance + "m", NamedTextColor.YELLOW)
                                        .decorate(TextDecoration.BOLD)));

                        // Calcul du vecteur directionnel pour les particules de lueur
                        org.bukkit.util.Vector direction = finalTribunal.toVector().subtract(pLoc.toVector())
                                .normalize();
                        org.bukkit.Location arrowStart = p.getEyeLocation()
                                .add(p.getLocation().getDirection().multiply(1.2));

                        for (double d = 0; d < 1.0; d += 0.35) {
                            org.bukkit.Location particlePoint = arrowStart.clone().add(direction.clone().multiply(d));
                            p.spawnParticle(org.bukkit.Particle.GLOW, particlePoint, 1, 0, 0, 0, 0);
                        }

                        // Petit tintement d'améthyste régulier
                        if (ticks % 4 == 0) {
                            p.playSound(p.getEyeLocation(), org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.15f, 1.4f);
                        }
                        ticks++;
                    }
                }.runTaskTimer(main, 0L, 10L);
            }

            // 4. Message global dans le chat (Parfaitement adapté au Conseil)
            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GOLD));
            Bukkit.broadcast(Component
                    .text("[BOTC] Le Conseil est ouvert ! Suivez les flèches pour regagner immédiatement le tribunal.",
                            NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GOLD));

            player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                    .append(Component.text("S'asseoir au tribunal", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc assis")));

            return true;
        }

        // ==========================================
        // COMMANDE /botc nuit (Version Animation Ciel + Marche Libre)
        // ==========================================
        if (args[0].equalsIgnoreCase("nuit")) {
            main.setNight(true);

            // Activer le cycle pour permettre le mouvement fluide du soleil
            player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, true);

            new org.bukkit.scheduler.BukkitRunnable() {
                long currentTime = player.getWorld().getTime();
                final long targetTime = 18000; // Nuit noire

                @Override
                public void run() {
                    // Si on a atteint la nuit noire, on fige le temps et on lance le gameplay
                    if (currentTime >= targetTime || currentTime < 1000) {
                        player.getWorld().setTime(targetTime);
                        player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
                        this.cancel();

                        // APPEL DU GAMEPLAY DE NUIT (Une fois le soleil couché)
                        lancerMecaniquesNuit(player);
                        return;
                    }

                    // Accélération du temps (350 ticks par répétition, exécuté toutes les ticks)
                    currentTime += 350;
                    player.getWorld().setTime(currentTime);

                    // Petit bruit de battement sourd pendant que le soleil décline
                    if (currentTime % 1400 == 0) {
                        for (Player onlineP : Bukkit.getOnlinePlayers()) {
                            onlineP.playSound(onlineP.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.6f,
                                    0.5f);
                        }
                    }
                }
            }.runTaskTimer(main, 0L, 1L); // 1L = exécution à chaque tick pour un effet fluide à 60fps

            player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                    .append(Component.text("Mettre le jour", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc jour")));

            return true;
        }

        // ==========================================
        // COMMANDE /botc jour (Version Animation Ciel + Boussole Conseil)
        // ==========================================
        if (args[0].equalsIgnoreCase("jour")) {
            main.setNight(false);

            player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, true);

            new org.bukkit.scheduler.BukkitRunnable() {
                long currentTime = player.getWorld().getTime(); // On part de l'heure de la nuit (18000)
                final long targetTime = 30000; // Matin du jour suivant

                @Override
                public void run() {
                    // Si le soleil est levé, on recale proprement à 1000 et on lance les boussoles
                    if (currentTime >= targetTime) {
                        player.getWorld().setTime(6000);
                        player.getWorld().setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
                        this.cancel();

                        // APPEL DU GAMEPLAY DE JOUR (Une fois le matin levé)
                        lancerMecaniquesJour(player);
                        return;
                    }

                    // Accélération du temps vers l'aube
                    currentTime += 350;
                    player.getWorld().setTime(currentTime);
                }
            }.runTaskTimer(main, 0L, 1L);

            player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                    .append(Component.text("Annoncer les morts", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc mort")));

            return true;
        }

        // ==========================================
        // 3. COMMANDE /botc assis (Le seul moment où on réactive les NameTags !)
        // ==========================================
        if (args[0].equalsIgnoreCase("assis")) {
            List<String> chairsStr = main.getConfig().getStringList(main.getPresetPath("chairs"));
            if (chairsStr.isEmpty()) {
                player.sendMessage(Component.text("Erreur : Aucune chaise enregistrée.", NamedTextColor.RED));
                return true;
            }

            // --- 🌟 ALGORITHME DE RÉPARTITION FIXE ET UNIQUE 🌟 ---
            // Si c'est le premier "/botc assis" depuis le reset, on mélange et on fixe les
            // places !
            if (!main.isSeatsAssigned()) {
                List<BotcPlayer> joueursAAsseoir = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.isOp()) {
                        BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
                        if (bp != null)
                            joueursAAsseoir.add(bp);
                    }
                }
                java.util.Collections.shuffle(joueursAAsseoir);
                for (int i = 0; i < joueursAAsseoir.size(); i++) {
                    joueursAAsseoir.get(i).setChairIndex(i);
                }
                main.setSeatsAssigned(true);
            }
            // Sécurité reconnexions et retardataires
            else {
                List<Integer> chaisesPrises = new ArrayList<>();
                for (BotcPlayer bp : main.getPlayersMap().values()) {
                    if (bp.getChairIndex() != -1)
                        chaisesPrises.add(bp.getChairIndex());
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp())
                        continue;
                    BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
                    if (bp != null && bp.getChairIndex() == -1) { // 🌟 N'attribue une place que s'il n'en a pas
                        for (int i = 0; i < chairsStr.size(); i++) {
                            if (!chaisesPrises.contains(i)) {
                                bp.setChairIndex(i);
                                chaisesPrises.add(i);
                                break;
                            }
                        }
                    }
                }
            }

            // --- INITIALISATION DU GROUPE VOCAL SIMPLE VOICE CHAT ---
            if (main.getVoicechatPlugin() != null && main.getVoicechatPlugin().getVoicechatApi() != null) {
                de.maxhenkel.voicechat.api.VoicechatServerApi voiceApi = main.getVoicechatPlugin().getVoicechatApi();
                de.maxhenkel.voicechat.api.Group tribunalGroup = voiceApi.groupBuilder()
                        .setName("🏛️ Tribunal")
                        .setPersistent(false)
                        .setType(de.maxhenkel.voicechat.api.Group.Type.NORMAL)
                        .build();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    de.maxhenkel.voicechat.api.VoicechatConnection connection = voiceApi
                            .getConnectionOf(p.getUniqueId());
                    if (connection != null)
                        connection.setGroup(tribunalGroup);
                }
            }

            // --- PLACEMENT PHYSIQUE SUR LES CHAISES FIXES ---
            org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            org.bukkit.scoreboard.Team chairTeam = scoreboard.getTeam("botc_chairs");
            if (chairTeam == null) {
                chairTeam = scoreboard.registerNewTeam("botc_chairs");
                chairTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE,
                        org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            }

            org.bukkit.scoreboard.Team nightTeam = scoreboard.getTeam("botc_night");

            net.kyori.adventure.title.Title assisTitle = net.kyori.adventure.title.Title.title(
                    Component.text("🪑 TOUT LE MONDE ASSIS 🪑", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                    Component.text("Le Tribunal est en séance. Silence dans les rangs.", NamedTextColor.GRAY));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(assisTitle);
                p.playSound(p.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.0f, 0.5f);

                if (p.isOp())
                    continue;

                BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
                if (bp == null || bp.getChairIndex() == -1)
                    continue;

                int indexChaise = bp.getChairIndex();
                if (indexChaise >= chairsStr.size())
                    continue; // Sécurité si pas assez de chaises physiques

                if (nightTeam != null && nightTeam.hasEntry(p.getName())) {
                    nightTeam.removeEntry(p.getName());
                }

                String[] parts = chairsStr.get(indexChaise).split(",");
                org.bukkit.World w = Bukkit.getWorld(parts[0]);
                if (w != null) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    float yaw = Float.parseFloat(parts[4]);

                    org.bukkit.Location chairLoc = new org.bukkit.Location(w, x, y, z, yaw, 0);
                    org.bukkit.Location horseLoc = chairLoc.clone().add(0, -1.0, 0);

                    p.teleport(chairLoc);

                    org.bukkit.entity.Horse chair = w.spawn(chairLoc, org.bukkit.entity.Horse.class, horse -> {
                        horse.setGravity(false);
                        horse.setSilent(true);
                        horse.setInvulnerable(true);
                        horse.setAI(false);
                        horse.setTamed(true);
                        horse.setPersistent(false);
                        horse.addScoreboardTag("botc_chair");
                        horse.addPotionEffect(new org.bukkit.potion.PotionEffect(
                                org.bukkit.potion.PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                    });

                    chair.teleport(horseLoc);
                    chairTeam.addEntry(chair.getUniqueId().toString());
                    chair.addPassenger(p);
                }
            }

            Bukkit.broadcast(
                    Component.text("[BOTC] Le tribunal commence. Tout le monde est assis à sa place attribuée !",
                            NamedTextColor.GOLD));

            player.sendMessage(Component.text("➔ Prochaine étape : ", NamedTextColor.AQUA)
                    .append(Component.text("Libérer les joueurs", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc debout")));

            return true;
        }

        if (args.length > 0) {
            // 1. ANCIENNE COMMANDE : /botc admin
            if (args[0].equalsIgnoreCase("admin")) {
                grimoireView.openGrimoire(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                // Cette méthode gère déjà la réanimation globale, le nettoyage visuel
                // et la distribution des bons livres uniques (Grimoire / Registre)
                main.resetGame();

                player.sendMessage(Component.text(
                        "✓ La partie a été réinitialisée ! Tous les inventaires ont été synchronisés avec les objets de connexion.",
                        NamedTextColor.GREEN));
                return true;
            }

            // 🔄 REMPLACE TOUT LE BLOC "if (args[0].equalsIgnoreCase("setplayerdeath"))"
            // PAR CELUI-CI :
            if (args[0].equalsIgnoreCase("setplayerdeath")) {
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
                return true;
            }

            // 🔄 REMPLACE TOUT LE BLOC "if (args[0].equalsIgnoreCase("setlightning") ...)"
            // PAR CELUI-CI :
            if (args[0].equalsIgnoreCase("setlightning") && args.length > 1) {
                String mode = args[1].toLowerCase();

                if (mode.equals("player")) {
                    main.getConfig().set(main.getPresetPath("lightning.mode"), "player");
                    main.saveConfig();
                    player.sendMessage(
                            Component.text("L'éclair tombera désormais sur le joueur mort.", NamedTextColor.GREEN));
                    return true;
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
                    return true;
                }

                player.sendMessage(Component.text("Usage : /botc setlightning <player/local>", NamedTextColor.RED));
                return true;
            }
        }

        player.sendMessage(
                Component.text("Usage : /botc admin ou /botc setlightning <player/local>", NamedTextColor.RED));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(
            @org.jetbrains.annotations.NotNull org.bukkit.command.CommandSender sender,
            @org.jetbrains.annotations.NotNull org.bukkit.command.Command command,
            @org.jetbrains.annotations.NotNull String alias, @org.jetbrains.annotations.NotNull String[] args) {

        java.util.List<String> completions = new java.util.ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("voteoui".startsWith(input))
                completions.add("voteoui");

            if (sender.isOp()) {
                if ("admin".startsWith(input))
                    completions.add("admin");
                if ("nuit".startsWith(input))
                    completions.add("nuit");
                if ("jour".startsWith(input))
                    completions.add("jour");
                if ("assis".startsWith(input))
                    completions.add("assis");
                if ("debout".startsWith(input))
                    completions.add("debout");
                if ("addchair".startsWith(input))
                    completions.add("addchair");
                if ("delchairs".startsWith(input))
                    completions.add("delchairs");
                if ("showchairs".startsWith(input))
                    completions.add("showchairs");
                if ("setlightning".startsWith(input))
                    completions.add("setlightning");
                if ("setrole".startsWith(input))
                    completions.add("setrole");
                if ("help".startsWith(input))
                    completions.add("help");
                if ("reset".startsWith(input))
                    completions.add("reset");
                if ("settribunal".startsWith(input))
                    completions.add("settribunal");
                if ("addroom".startsWith(input))
                    completions.add("addroom");
                if ("delrooms".startsWith(input))
                    completions.add("delrooms");
                if ("showrooms".startsWith(input))
                    completions.add("showrooms");
                if ("setplayerdeath".startsWith(input))
                    completions.add("setplayerdeath");
                if ("settribunal".startsWith(input))
                    completions.add("settribunal");
                if ("shownames".startsWith(input))
                    completions.add("shownames");
                if ("hidenames".startsWith(input))
                    completions.add("hidenames");
                if ("mort".startsWith(input))
                    completions.add("mort");
                if ("tempslibre".startsWith(input))
                    completions.add("tempslibre");
                if ("conseil".startsWith(input))
                    completions.add("conseil");
                if ("order".startsWith(input))
                    completions.add("order");
                // --- DANS LE BLOC args.length == 1 ---
                if ("preset".startsWith(input))
                    completions.add("preset");

                // --- AJOUTE CES DEUX BLOCS EN BAS DE LA MÉTHODE ---
                if (args.length == 2 && args[0].equalsIgnoreCase("preset") && sender.isOp()) {
                    String inputSub = args[1].toLowerCase();
                    if ("create".startsWith(inputSub))
                        completions.add("create");
                    if ("select".startsWith(inputSub))
                        completions.add("select");
                    if ("list".startsWith(inputSub))
                        completions.add("list");
                    if ("delete".startsWith(inputSub))
                        completions.add("delete");
                    return completions;
                }

                if (args.length == 3 && args[0].equalsIgnoreCase("preset") && sender.isOp()) {
                    String inputPreset = args[2].toLowerCase();
                    if (main.getConfig().getConfigurationSection("presets") != null) {
                        for (String key : main.getConfig().getConfigurationSection("presets").getKeys(false)) {
                            if (key.startsWith(inputPreset))
                                completions.add(key);
                        }
                    }
                    return completions;
                }
                if (sender.isOp()) {
                    if ("silence".startsWith(input))
                        completions.add("silence");
                    if ("paroleall".startsWith(input))
                        completions.add("paroleall");
                    if ("vcmute".startsWith(input))
                        completions.add("vcmute");
                    if ("vcunmute".startsWith(input))
                        completions.add("vcunmute");
                    if ("mapvote".startsWith(input))
                        completions.add("mapvote");
                }

                // Onglets de deuxième niveau pour /botc mapvote ...
                if (args.length == 2 && args[0].equalsIgnoreCase("mapvote") && sender.isOp()) {
                    String inputSub = args[1].toLowerCase();
                    if ("start".startsWith(inputSub))
                        completions.add("start");
                    if ("stop".startsWith(inputSub))
                        completions.add("stop");
                    return completions;
                }
            }
            return completions;
        }

        // Deuxième argument pour /botc setlightning <player/local>
        if (args.length == 2 && args[0].equalsIgnoreCase("setlightning") && sender.isOp()) {
            String input = args[1].toLowerCase();
            if ("player".startsWith(input))
                completions.add("player");
            if ("local".startsWith(input))
                completions.add("local");
            return completions;
        }

        return completions;
    }
    // =========================================================================
    // MÉTHODES DE SÉCURITÉ POUR L'ANIMATION DU TEMPS (NE PAS SUPPRIMER)
    // =========================================================================

    private void lancerMecaniquesNuit(Player player) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");
        List<String> roomsStr = main.getConfig().getStringList(main.getPresetPath("rooms"));

        net.kyori.adventure.title.Title nightTitle = net.kyori.adventure.title.Title.title(
                Component.text("🌙 LA NUIT TOMBE 🌙", NamedTextColor.BLUE).decorate(TextDecoration.BOLD),
                Component.text("Fermez les yeux... Le démon s'éveille.", NamedTextColor.GRAY));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (nightTeam != null)
                nightTeam.addEntry(p.getName());
            p.showTitle(nightTitle);
            p.playSound(p.getLocation(), org.bukkit.Sound.AMBIENT_CAVE, 1.5f, 0.5f);
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.2f);
            p.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, p.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.0);
        }

        // 🌟 CORRECTION LOGIQUE : On attribue les chambres selon le CHAIR INDEX fixe de
        // la game !
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp())
                continue;

            BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
            if (bp == null || bp.getChairIndex() == -1)
                continue;

            int index = bp.getChairIndex(); // Récupère le numéro de siège fixe (0, 1, 2...)
            if (index >= roomsStr.size())
                break; // Sécurité si pas assez de chambres configurées

            String[] roomParts = roomsStr.get(index).split(",");
            org.bukkit.World wRoom = Bukkit.getWorld(roomParts[0]);

            if (wRoom != null) {
                int bx = Integer.parseInt(roomParts[1]);
                int by = Integer.parseInt(roomParts[2]);
                int bz = Integer.parseInt(roomParts[3]);

                org.bukkit.block.Block block = wRoom.getBlockAt(bx, by, bz);

                // On force le nettoyage du cache visuel
                block.setType(org.bukkit.Material.AIR);
                block.setType(org.bukkit.Material.PLAYER_HEAD);

                if (roomParts.length > 4) {
                    try {
                        org.bukkit.block.BlockFace facing = org.bukkit.block.BlockFace.valueOf(roomParts[4]);
                        if (block.getBlockData() instanceof org.bukkit.block.data.Rotatable rotatable) {
                            rotatable.setRotation(facing);
                            block.setBlockData(rotatable);
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                // 🌟 CORRECTION VISUELLE : Attribution du skin décalée d'un tick pour éviter
                // Steve/Alex
                final Player targetPlayer = p;
                new org.bukkit.scheduler.BukkitRunnable() {
                    @Override
                    public void run() {
                        if (block.getState() instanceof org.bukkit.block.Skull skullState) {
                            skullState.setOwningPlayer(targetPlayer);
                            skullState.update(true, false); // Le true force l'envoi du paquet de skin aux clients
                        }
                    }
                }.runTaskLater(main, 1L); // Délai d'un tick minuscule mais indispensable

                final org.bukkit.Location targetRoomLoc = block.getLocation().add(0.5, 0, 0.5);

                // Message privé basé sur le vrai numéro de chambre aligné à la chaise
                p.sendMessage(Component.text("➔ La nuit tombe ! Rejoins vite tes quartiers : ", NamedTextColor.RED)
                        .append(Component.text("Chambre #" + (index + 1), NamedTextColor.YELLOW)
                                .decorate(TextDecoration.BOLD)));

                // Lancement du GPS Boussole 3D towards targeted room
                new org.bukkit.scheduler.BukkitRunnable() {
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (ticks > 50 || !p.isOnline()) {
                            this.cancel();
                            return;
                        }

                        org.bukkit.Location pLoc = p.getLocation();
                        double distance = pLoc.distance(targetRoomLoc);

                        if (distance < 2.0) {
                            p.sendActionBar(
                                    Component.text("✦ Vous êtes en sécurité dans votre chambre ✦", NamedTextColor.GREEN)
                                            .decorate(TextDecoration.BOLD));
                            this.cancel();
                            return;
                        }

                        p.sendActionBar(Component.text("➔ Ta Chambre : ", NamedTextColor.GOLD)
                                .append(Component.text((int) distance + "m", NamedTextColor.YELLOW)
                                        .decorate(TextDecoration.BOLD)));

                        org.bukkit.util.Vector direction = targetRoomLoc.toVector().subtract(pLoc.toVector())
                                .normalize();
                        org.bukkit.Location arrowStart = p.getEyeLocation()
                                .add(p.getLocation().getDirection().multiply(1.2));

                        for (double d = 0; d < 1.0; d += 0.35) {
                            org.bukkit.Location particlePoint = arrowStart.clone().add(direction.clone().multiply(d));
                            p.spawnParticle(org.bukkit.Particle.GLOW, particlePoint, 1, 0, 0, 0, 0);
                        }
                        ticks++;
                    }
                }.runTaskTimer(main, 0L, 10L);
            }
        }

        Bukkit.broadcast(Component.text("[BOTC] La nuit est tombée... Regagnez vos chambres avant l'arrivée du démon !",
                NamedTextColor.DARK_BLUE).decorate(TextDecoration.BOLD));
    }

    private void lancerMecaniquesJour(Player player) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");

        org.bukkit.Location tribunalLoc = null;
        if (main.getConfig().contains(main.getPresetPath("tribunal.x"))) {
            String worldName = main.getConfig().getString(main.getPresetPath("tribunal.world"));
            if (worldName != null) {
                org.bukkit.World world = Bukkit.getWorld(worldName);
                double tx = main.getConfig().getDouble(main.getPresetPath("tribunal.x"));
                double ty = main.getConfig().getDouble(main.getPresetPath("tribunal.y"));
                double tz = main.getConfig().getDouble(main.getPresetPath("tribunal.z"));
                if (world != null)
                    tribunalLoc = new org.bukkit.Location(world, tx, ty, tz);
            }
        }

        final org.bukkit.Location finalTribunal = tribunalLoc;

        if (finalTribunal == null) {
            player.sendMessage(Component.text("Erreur : Le tribunal n'est pas configuré ! (/botc settribunal)",
                    NamedTextColor.RED));
            return;
        }
        // Titre du réveil
        net.kyori.adventure.title.Title wakeTitle = net.kyori.adventure.title.Title.title(
                Component.text("🌅 LE RÉVEIL 🌅", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                Component.text("Regagnez le Tribunal pour le Conseil.", NamedTextColor.LIGHT_PURPLE));
        for (Player p : Bukkit.getOnlinePlayers()) {

            if (nightTeam != null && nightTeam.hasEntry(p.getName())) {
                nightTeam.removeEntry(p.getName());
            }

            p.showTitle(wakeTitle);
            p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.0f);

            new org.bukkit.scheduler.BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks > 40 || !p.isOnline()) {
                        this.cancel();
                        return;
                    }

                    org.bukkit.Location pLoc = p.getLocation();
                    double distance = pLoc.distance(finalTribunal);

                    if (distance < 3.0) {
                        p.sendActionBar(
                                Component.text("✦ Vous êtes arrivé à la salle du conseil ✦", NamedTextColor.GREEN)
                                        .decorate(TextDecoration.BOLD));
                        p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.4f, 1.5f);
                        this.cancel();
                        return;
                    }

                    p.sendActionBar(Component.text("➔ Salle du Conseil : ", NamedTextColor.GOLD)
                            .append(Component.text((int) distance + "m", NamedTextColor.YELLOW)
                                    .decorate(TextDecoration.BOLD)));

                    org.bukkit.util.Vector direction = finalTribunal.toVector().subtract(pLoc.toVector()).normalize();
                    org.bukkit.Location arrowStart = p.getEyeLocation()
                            .add(p.getLocation().getDirection().multiply(1.2));

                    for (double d = 0; d < 1.0; d += 0.35) {
                        org.bukkit.Location particlePoint = arrowStart.clone().add(direction.clone().multiply(d));
                        p.spawnParticle(org.bukkit.Particle.GLOW, particlePoint, 1, 0, 0, 0, 0);
                    }

                    if (ticks % 4 == 0) {
                        p.playSound(p.getEyeLocation(), org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.15f, 1.4f);
                    }
                    ticks++;
                }
            }.runTaskTimer(main, 0L, 10L);
        }
        Bukkit.broadcast(Component
                .text("[BOTC] Le soleil se lève ! Suivez la flèche boussole devant vos yeux pour rejoindre le conseil.",
                        NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD));
    }
}
