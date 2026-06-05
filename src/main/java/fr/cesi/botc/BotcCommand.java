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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Seul un joueur peut executer cette commande.", NamedTextColor.RED));
            return true;
        }

        // 1. Détection prioritaire et absolue du vote (Accessible par TOUS)
        if (args.length > 0 && args[0].equalsIgnoreCase("voteoui")) {
            main.getVoteManager().registerVote(player);
            return true;
        }

        // 2. Sécurité si aucun argument n'est fourni
        if (args.length == 0) {
            if (player.isOp()) {
                player.sendMessage(Component.text("Usage Conteur : /botc help pour voir le guide.", NamedTextColor.RED));
            } else {
                player.sendMessage(Component.text("Usage Joueur : /botc voteoui pour lever la main.", NamedTextColor.YELLOW));
            }
            return true;
        }

        // 3. Mur de sécurité : Tout ce qui suit (chaises, nuit, jour, etc.) requiert d'être OP
        if (!player.isOp()) {
            player.sendMessage(Component.text("➔ Tu n'es pas le Conteur de cette partie !", NamedTextColor.RED));
            return true;
        }

        // AJOUTER LE POINT CENTRAL DU TRIBUNAL / CONSEIL
        if (args[0].equalsIgnoreCase("settribunal")) {
            org.bukkit.Location loc = player.getLocation();

            main.getConfig().set("tribunal.world", loc.getWorld().getName());
            main.getConfig().set("tribunal.x", loc.getX());
            main.getConfig().set("tribunal.y", loc.getY());
            main.getConfig().set("tribunal.z", loc.getZ());
            main.saveConfig();

            player.sendMessage(Component.text("Le centre de la salle du conseil a été enregistré ici !", NamedTextColor.GREEN));
            return true;
        }

        // ==========================================
        // 6. COMMANDE DE HELP CHRONOLOGIQUE POUR LE CONTEUR (Version Finale Alignée)
        // ==========================================
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(Component.text("=== GUIDE CHRONOLOGIQUE D'UNE PARTIE BOTC ===", NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("Suivez ce déroulé de haut en bas pour mener votre partie :", NamedTextColor.LIGHT_PURPLE));
            player.sendMessage(Component.text("--------------------------------------------------", NamedTextColor.GRAY));

            player.sendMessage(Component.text("ÉTAPE 1 : Configuration Unique du Château (À faire une fois)", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("   -> Enregistrer le centre du Tribunal : /botc settribunal", NamedTextColor.WHITE));
            player.sendMessage(Component.text("   -> Enregistrer l'estrade d'exécution (Morts) : /botc setplayerdeath", NamedTextColor.WHITE));
            player.sendMessage(Component.text("   -> Enregistrer CHAQUE porte de chambre : /botc addroom", NamedTextColor.WHITE));
            player.sendMessage(Component.text("   -> Choisir la cible de l'éclair : /botc setlightning <player/local>", NamedTextColor.WHITE));
            player.sendMessage(Component.text("   -> (Vérifier les chambres : /botc showrooms | Les vider : /botc delrooms)", NamedTextColor.DARK_GRAY));

            player.sendMessage(Component.text("ÉTAPE 2 : Préparation du Lobby (Avant chaque nouvelle game)", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("   -> Réinitialiser les chaises de la partie précédente : /botc reset", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("   -> Assigner les Chaises (Faire le tour du cercle dans l'ordre) : /botc addchair", NamedTextColor.WHITE));
            player.sendMessage(Component.text("   -> (Vérifier les chaises avec les flammes de test : /botc showchairs)", NamedTextColor.DARK_GRAY));
            player.sendMessage(Component.text(" ", NamedTextColor.GRAY));

            player.sendMessage(Component.text("ÉTAPE 3 : Lancement du Tribunal (Début du Jour)", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("   -> Assis et identifier tout le monde (Active les NameTags) : /botc assis", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("   -> Ouvrir le Grimoire pour exécuter, attribuer les rôles et voter : /botc admin", NamedTextColor.LIGHT_PURPLE));
            player.sendMessage(Component.text("      *Tuer via le menu téléporte la cible sur l'estrade avant de la rasseoir !*", NamedTextColor.RED).decorate(TextDecoration.ITALIC));
            player.sendMessage(Component.text(" ", NamedTextColor.GRAY));

            player.sendMessage(Component.text("ÉTAPE 4 : Fin du Conseil (Phase de Complots Libres)", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("   -> Libérer les joueurs (Cache les NameTags + Active la boussole vers Tribunal) : /botc debout", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("      *Rappel vocal : Ordonner le mode Chuchotement (Simple Voice Chat)*", NamedTextColor.AQUA).decorate(TextDecoration.ITALIC));
            player.sendMessage(Component.text(" ", NamedTextColor.GRAY));

            player.sendMessage(Component.text("ÉTAPE 5 : Phase de Nuit (Retour aux quartiers)", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("   -> Lancer la nuit (Libres de marcher, boussole vers SA chambre, tête affichée) : /botc nuit", NamedTextColor.BLUE));
            player.sendMessage(Component.text("      *Les joueurs doivent suivre la flèche 3D à l'écran pour rejoindre leur lit.*", NamedTextColor.DARK_AQUA).decorate(TextDecoration.ITALIC));

            player.sendMessage(Component.text("--------------------------------------------------", NamedTextColor.GRAY));
            return true;
        }

        // ==========================================
        // COMMANDE : /botc shownames (Affiche les pseudos de tout le monde)
        // ==========================================
        if (args[0].equalsIgnoreCase("shownames")) {
            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");

            for (Player p : Bukkit.getOnlinePlayers()) {
                // Si le joueur est dans l'équipe qui cache les pseudos, on l'enlève
                if (nightTeam != null && nightTeam.hasEntry(p.getName())) {
                    nightTeam.removeEntry(p.getName());
                }
            }
            player.sendMessage(Component.text("✓ Tous les pseudos sont désormais VISIBLES.", NamedTextColor.GREEN));
            return true;
        }

        // ==========================================
        // COMMANDE : /botc hidenames (Cache les pseudos de tout le monde)
        // ==========================================
        if (args[0].equalsIgnoreCase("hidenames")) {
            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");

            if (nightTeam == null) {
                player.sendMessage(Component.text("Erreur : L'équipe de nuit n'est pas initialisée.", NamedTextColor.RED));
                return true;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                // On n'inclut pas le conteur dans l'anonymat pour qu'il garde son tag s'il le veut
                if (p.isOp()) continue;

                nightTeam.addEntry(p.getName());
            }
            player.sendMessage(Component.text("✓ Tous les pseudos sont désormais CACHÉS (Anonymat actif).", NamedTextColor.RED));
            return true;
        }

        // ==========================================
        // CONFIGURATION DES CHAMBRES (Avec sauvegarde de l'orientation)
        // ==========================================
        if (args[0].equalsIgnoreCase("addroom")) {
            List<String> rooms = main.getConfig().getStringList("rooms");

            // --- LE CHANGEMENT COMPLET ---
            // Au lieu de regarder un bloc au loin, on prend le bloc exact où se trouve la TÊTE du Conteur !
            org.bukkit.Location eyeLoc = player.getEyeLocation();

            // On calcule la direction cardinale (OUEST, EST, NORD, SUD) vers laquelle regarde le Conteur
            org.bukkit.block.BlockFace facing = player.getFacing();

            // On stocke les coordonnées sous forme de blocs d'entiers (getBlockX, getBlockY, getBlockZ)
            String locStr = eyeLoc.getWorld().getName() + ","
                    + eyeLoc.getBlockX() + ","
                    + eyeLoc.getBlockY() + ","
                    + eyeLoc.getBlockZ() + ","
                    + facing.name();

            rooms.add(locStr);
            main.getConfig().set("rooms", rooms);
            main.saveConfig();

            player.sendMessage(Component.text("Emplacement Chambre #" + rooms.size() + " enregistré pile à la hauteur de ta tête (Y=" + eyeLoc.getBlockY() + ") !", NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("delrooms")) {
            main.getConfig().set("rooms", new ArrayList<String>());
            main.saveConfig();
            player.sendMessage(Component.text("Toutes les chambres ont été effacées.", NamedTextColor.RED));
            return true;
        }

        // GESTION DES CHAMBRES : TEST VISUEL (PARTICULES QUANTIQUE CYCLIQUE)
        if (args[0].equalsIgnoreCase("showrooms")) {
            List<String> rooms = main.getConfig().getStringList("rooms");
            if (rooms.isEmpty()) {
                player.sendMessage(Component.text("Erreur : Aucune chambre enregistrée.", NamedTextColor.RED));
                return true;
            }

            player.sendMessage(Component.text("Affichage des " + rooms.size() + " emplacements de chambres pendant 10 secondes...", NamedTextColor.YELLOW));

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
                        if (w == null) continue;

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
            List<String> chairs = main.getConfig().getStringList("chairs");
            // On stocke sous format : monde,x,y,z,yaw
            org.bukkit.Location loc = player.getLocation();
            String locStr = player.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw();

            chairs.add(locStr);
            main.getConfig().set("chairs", chairs);
            main.saveConfig();

            player.sendMessage(Component.text("Chaise #" + chairs.size() + " enregistree avec succes !", NamedTextColor.GREEN));
            return true;
        }

        // 2. SUPPRIMER TOUTES LES CHAISES
        if (args[0].equalsIgnoreCase("delchairs")) {
            main.getConfig().set("chairs", new ArrayList<String>());
            main.saveConfig();
            player.sendMessage(Component.text("Toutes les chaises ont ete supprimees de la configuration.", NamedTextColor.RED));
            return true;
        }

        // 3. AFFICHER LES CHAISES (VISUEL DE CONFIG)
        if (args[0].equalsIgnoreCase("showchairs")) {
            List<String> chairs = main.getConfig().getStringList("chairs");
            player.sendMessage(Component.text("Affichage des " + chairs.size() + " chaises pendant 10 secondes...", NamedTextColor.YELLOW));

            // On fait apparaître des flammes/particules sur chaque chaise
            for (String chairStr : chairs) {
                String[] parts = chairStr.split(",");
                org.bukkit.World w = Bukkit.getWorld(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                if (w != null) {
                    // On fait spawner des particules de portail colorées pour le Conteur
                    w.spawnParticle(org.bukkit.Particle.WITCH, new org.bukkit.Location(w, x, y + 0.5, z), 20, 0.1, 0.1, 0.1, 0.0);
                }
            }
            return true;
        }

        // ==========================================
        // COMMANDE /botc debout (Libération + Masquage des NameTags)
        // ==========================================
        if (args[0].equalsIgnoreCase("debout")) {
            Bukkit.broadcast(Component.text("[BOTC] Le conseil est terminé, vous pouvez vous lever.", NamedTextColor.GREEN));

            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp()) continue;
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
                            onlineP.playSound(onlineP.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.6f, 0.5f);
                        }
                    }
                }
            }.runTaskTimer(main, 0L, 1L); // 1L = exécution à chaque tick pour un effet fluide à 60fps

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
                final long targetTime = 30000; // Matin du jour suivant (24000 + 1000)

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

            return true;
        }


        // ==========================================
        // 3. COMMANDE /botc assis (Le seul moment où on réactive les NameTags !)
        // ==========================================
        if (args[0].equalsIgnoreCase("assis")) {
            List<String> chairsStr = main.getConfig().getStringList("chairs");
            if (chairsStr.isEmpty()) {
                player.sendMessage(Component.text("Erreur : Aucune chaise enregistrée.", NamedTextColor.RED));
                return true;
            }

            // --- INITIALISATION DU GROUPE VOCAL SIMPLE VOICE CHAT ---
            de.maxhenkel.voicechat.api.VoicechatServerApi voiceApi = null;
            de.maxhenkel.voicechat.api.Group tribunalGroup = null;

            if (main.getVoicechatPlugin() != null && main.getVoicechatPlugin().getVoicechatApi() != null) {
                voiceApi = main.getVoicechatPlugin().getVoicechatApi();

                // On build le salon du Tribunal (Non persistant = s'autodétruit quand il est vide)
                tribunalGroup = voiceApi.groupBuilder()
                        .setName("🏛️ Tribunal")
                        .setPersistent(false)
                        .setType(de.maxhenkel.voicechat.api.Group.Type.NORMAL) // Tout le monde s'entend, peu importe la distance
                        .build();

                // On y ajoute immédiatement le Conteur (le joueur qui tape la commande) pour qu'il puisse animer
                de.maxhenkel.voicechat.api.VoicechatConnection adminConnection = voiceApi.getConnectionOf(player.getUniqueId());
                if (adminConnection != null) {
                    adminConnection.setGroup(tribunalGroup);
                }
            }

            // --- GESTION DU SCOREBOARD POUR LES COLLISIONS ---
            org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            org.bukkit.scoreboard.Team chairTeam = scoreboard.getTeam("botc_chairs");
            // Si l'équipe de chaises n'existe pas, on la crée et on coupe les collisions
            if (chairTeam == null) {
                chairTeam = scoreboard.registerNewTeam("botc_chairs");
                chairTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            }

            org.bukkit.scoreboard.Team nightTeam = scoreboard.getTeam("botc_night");

            int index = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp()) continue;
                if (index >= chairsStr.size()) break;

                if (nightTeam != null && nightTeam.hasEntry(p.getName())) {
                    nightTeam.removeEntry(p.getName());
                }

                String[] parts = chairsStr.get(index).split(",");
                org.bukkit.World w = Bukkit.getWorld(parts[0]);
                if (w != null) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    float yaw = Float.parseFloat(parts[4]);

                    org.bukkit.Location chairLoc = new org.bukkit.Location(w, x, y, z, yaw, 0);
                    org.bukkit.Location horseLoc = chairLoc.clone().add(0, -1.5, 0); // La position 1,5 bloc plus bas

                    p.teleport(chairLoc);

                    // 1. On spawn le cheval à l'air libre (chairLoc) pour éviter le snap automatique vers le haut
                    org.bukkit.entity.Horse chair = w.spawn(chairLoc, org.bukkit.entity.Horse.class, horse -> {
                        horse.setGravity(false);
                        horse.setSilent(true);
                        horse.setInvulnerable(true);
                        horse.setAI(false);
                        horse.setTamed(true);
                        horse.setPersistent(false); // S'efface tout seul si le serveur redémarre !
                        horse.addScoreboardTag("botc_chair");

                        horse.addPotionEffect(new org.bukkit.potion.PotionEffect(
                                org.bukkit.potion.PotionEffectType.INVISIBILITY,
                                Integer.MAX_VALUE,
                                0,
                                false,
                                false
                        ));
                    });

                    // 2. On FORCE le cheval à descendre dans le bloc en le téléportant juste après son spawn
                    chair.teleport(horseLoc);

                    // 3. On ajoute le cheval dans l'équipe sans collision
                    chairTeam.addEntry(chair.getUniqueId().toString());

                    // 4. On assoit le joueur
                    chair.addPassenger(p);
                    index++;
                }
            }

            Bukkit.broadcast(Component.text("[BOTC] Le tribunal commence. Tout le monde est assis et identifié !", NamedTextColor.GOLD));
            return true;
        }

        if (args.length > 0) {
            // 1. ANCIENNE COMMANDE : /botc admin
            if (args[0].equalsIgnoreCase("admin")) {
                grimoireView.openGrimoire(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                // On nettoie les variables de la partie en cours
                main.getConfig().set("chairs", new ArrayList<String>()); // Optionnel : si tu veux replacer les chaises
                // On NE supprime PAS "rooms", comme ça tu n'as pas à re-configurer les portes du château à chaque partie !
                main.saveConfig();

                player.sendMessage(Component.text("✓ Partie réinitialisée. Les chambres restent sauvegardées pour le château !", NamedTextColor.GREEN));
                return true;
            }

            if (args[0].equalsIgnoreCase("setplayerdeath")) {
                org.bukkit.Location loc = player.getLocation();

                main.getConfig().set("death.world", loc.getWorld().getName());
                main.getConfig().set("death.x", loc.getX());
                main.getConfig().set("death.y", loc.getY());
                main.getConfig().set("death.z", loc.getZ());
                main.getConfig().set("death.yaw", loc.getYaw());
                main.getConfig().set("death.pitch", loc.getPitch());
                main.saveConfig();

                player.sendMessage(Component.text("L'emplacement d'exécution des morts a été enregistré ici !", NamedTextColor.GREEN));
                return true;
            }

            // 2. NOUVELLE COMMANDE : /botc setlightning <player/local>
            if (args[0].equalsIgnoreCase("setlightning") && args.length > 1) {
                String mode = args[1].toLowerCase();

                if (mode.equals("player")) {
                    main.getConfig().set("lightning.mode", "player");
                    main.saveConfig();
                    player.sendMessage(Component.text("L'eclair tombera desormais sur le joueur mort.", NamedTextColor.GREEN));
                    return true;
                }

                if (mode.equals("local")) {
                    // On enregistre le mode ET la position actuelle du Conteur
                    main.getConfig().set("lightning.mode", "local");
                    main.getConfig().set("lightning.world", player.getWorld().getName());
                    main.getConfig().set("lightning.x", player.getLocation().getX());
                    main.getConfig().set("lightning.y", player.getLocation().getY());
                    main.getConfig().set("lightning.z", player.getLocation().getZ());
                    main.saveConfig();

                    player.sendMessage(Component.text("L'eclair tombera desormais a ta position actuelle (Tribunal).", NamedTextColor.GREEN));
                    return true;
                }

                player.sendMessage(Component.text("Usage : /botc setlightning <player/local>", NamedTextColor.RED));
                return true;
            }
        }

        player.sendMessage(Component.text("Usage : /botc admin ou /botc setlightning <player/local>", NamedTextColor.RED));
        return true;
    }
    @Override
    public java.util.List<String> onTabComplete(@org.jetbrains.annotations.NotNull org.bukkit.command.CommandSender sender, @org.jetbrains.annotations.NotNull org.bukkit.command.Command command, @org.jetbrains.annotations.NotNull String alias, @org.jetbrains.annotations.NotNull String[] args) {

        java.util.List<String> completions = new java.util.ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("voteoui".startsWith(input)) completions.add("voteoui");

            if (sender.isOp()) {
                if ("admin".startsWith(input)) completions.add("admin");
                if ("nuit".startsWith(input)) completions.add("nuit");
                if ("jour".startsWith(input)) completions.add("jour");
                if ("assis".startsWith(input)) completions.add("assis");
                if ("debout".startsWith(input)) completions.add("debout");
                if ("addchair".startsWith(input)) completions.add("addchair");
                if ("delchairs".startsWith(input)) completions.add("delchairs");
                if ("showchairs".startsWith(input)) completions.add("showchairs");
                if ("setlightning".startsWith(input)) completions.add("setlightning");
                if ("setrole".startsWith(input)) completions.add("setrole");
                if ("help".startsWith(input)) completions.add("help");
                if ("reset".startsWith(input)) completions.add("reset");
                if ("settribunal".startsWith(input)) completions.add("settribunal");
                if ("addroom".startsWith(input)) completions.add("addroom");
                if ("delrooms".startsWith(input)) completions.add("delrooms");
                if ("showrooms".startsWith(input)) completions.add("showrooms");
                if ("setplayerdeath".startsWith(input)) completions.add("setplayerdeath");
                if ("settribunal".startsWith(input)) completions.add("settribunal");
                if ("shownames".startsWith(input)) completions.add("shownames"); // <--- ICI
                if ("hidenames".startsWith(input)) completions.add("hidenames"); // <--- ICI
            }
            return completions;
        }

        // Deuxième argument pour /botc setlightning <player/local>
        if (args.length == 2 && args[0].equalsIgnoreCase("setlightning") && sender.isOp()) {
            String input = args[1].toLowerCase();
            if ("player".startsWith(input)) completions.add("player");
            if ("local".startsWith(input)) completions.add("local");
            return completions;
        }

        return completions;
    }
    // =========================================================================
    // MÉTHODES DE SÉCURITÉ POUR L'ANIMATION DU TEMPS (NE PAS SUPPRIMER)
    // =========================================================================

    private void lancerMecaniquesNuit(Player player) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");
        List<String> roomsStr = main.getConfig().getStringList("rooms");

        // 1. Ambiance de terreur pour tout le monde
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (nightTeam != null) nightTeam.addEntry(p.getName());
            p.playSound(p.getLocation(), org.bukkit.Sound.AMBIENT_CAVE, 1.5f, 0.5f);
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.2f);
            p.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, p.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.0);
        }

        // 2. Attribution des chambres et des boussoles basée sur l'index des joueurs connectés
        int index = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp()) continue;
            // Sécurité : si on a plus de joueurs que de chambres configurées
            if (index >= roomsStr.size()) break;

            String[] roomParts = roomsStr.get(index).split(",");
            org.bukkit.World wRoom = Bukkit.getWorld(roomParts[0]);

            if (wRoom != null) {
                int bx = Integer.parseInt(roomParts[1]);
                int by = Integer.parseInt(roomParts[2]);
                int bz = Integer.parseInt(roomParts[3]);

                org.bukkit.block.Block block = wRoom.getBlockAt(bx, by, bz);

                // 1. On pose d'abord le bloc de tête
                block.setType(org.bukkit.Material.PLAYER_HEAD);

                // 2. CORRECTION DE L'ORIENTATION : On applique la direction sauvegardée
                if (roomParts.length > 4) { // On vérifie si la direction existe dans la config
                    try {
                        org.bukkit.block.BlockFace facing = org.bukkit.block.BlockFace.valueOf(roomParts[4]);

                        // On récupère le BlockData du bloc qu'on vient de poser
                        if (block.getBlockData() instanceof org.bukkit.block.data.Rotatable rotatable) {
                            // On lui applique la rotation (Nord, Sud, Est, Ouest...)
                            rotatable.setRotation(facing);
                            block.setBlockData(rotatable);
                        }
                    } catch (IllegalArgumentException e) {
                        // Sécurité au cas où le texte de la config serait corrompu
                    }
                }

                // 3. On applique la texture du joueur (comme avant)
                if (block.getState() instanceof org.bukkit.block.Skull skullState) {
                    skullState.setOwningPlayer(p);
                    skullState.update();
                }

                final org.bukkit.Location targetRoomLoc = block.getLocation().add(0.5, 0, 0.5);


                // B. Message privé au joueur
                p.sendMessage(Component.text("➔ La nuit tombe ! Rejoins vite tes quartiers : ", NamedTextColor.RED)
                        .append(Component.text("Chambre #" + (index + 1), NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));

                // C. Lancement du GPS Boussole 3D (inchangé, mais cible désormais garantie)
                new org.bukkit.scheduler.BukkitRunnable() {
                    int ticks = 0;
                    @Override
                    public void run() {
                        if (ticks > 50 || !p.isOnline()) { this.cancel(); return; }

                        org.bukkit.Location pLoc = p.getLocation();
                        double distance = pLoc.distance(targetRoomLoc);

                        if (distance < 2.0) {
                            p.sendActionBar(Component.text("✦ Vous êtes en sécurité dans votre chambre ✦", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                            this.cancel();
                            return;
                        }

                        p.sendActionBar(Component.text("➔ Ta Chambre : ", NamedTextColor.GOLD)
                                .append(Component.text((int) distance + "m", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));

                        org.bukkit.util.Vector direction = targetRoomLoc.toVector().subtract(pLoc.toVector()).normalize();
                        org.bukkit.Location arrowStart = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.2));

                        for (double d = 0; d < 1.0; d += 0.35) {
                            org.bukkit.Location particlePoint = arrowStart.clone().add(direction.clone().multiply(d));
                            p.spawnParticle(org.bukkit.Particle.GLOW, particlePoint, 1, 0, 0, 0, 0);
                        }
                        ticks++;
                    }
                }.runTaskTimer(main, 0L, 10L);
            }
            index++; // On passe à la chambre suivante pour le prochain joueur
        }

        Bukkit.broadcast(Component.text("[BOTC] La nuit est tombée... Regagnez vos chambres avant l'arrivée du démon !", NamedTextColor.DARK_BLUE).decorate(TextDecoration.BOLD));
    }

    private void lancerMecaniquesJour(Player player) {
        org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");

        org.bukkit.Location tribunalLoc = null;
        if (main.getConfig().contains("tribunal.x")) {
            org.bukkit.World world = Bukkit.getWorld(main.getConfig().getString("tribunal.world"));
            double tx = main.getConfig().getDouble("tribunal.x");
            double ty = main.getConfig().getDouble("tribunal.y");
            double tz = main.getConfig().getDouble("tribunal.z");
            if (world != null) tribunalLoc = new org.bukkit.Location(world, tx, ty, tz);
        }

        final org.bukkit.Location finalTribunal = tribunalLoc;

        if (finalTribunal == null) {
            player.sendMessage(Component.text("Erreur : Le tribunal n'est pas configuré ! (/botc settribunal)", NamedTextColor.RED));
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (nightTeam != null) nightTeam.addEntry(p.getName());
            p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.0f);

            new org.bukkit.scheduler.BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks > 40 || !p.isOnline()) { this.cancel(); return; }

                    org.bukkit.Location pLoc = p.getLocation();
                    double distance = pLoc.distance(finalTribunal);

                    if (distance < 3.0) {
                        p.sendActionBar(Component.text("✦ Vous êtes arrivé à la salle du conseil ✦", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                        p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.4f, 1.5f);
                        this.cancel();
                        return;
                    }

                    p.sendActionBar(Component.text("➔ Salle du Conseil : ", NamedTextColor.GOLD)
                            .append(Component.text((int) distance + "m", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));

                    org.bukkit.util.Vector direction = finalTribunal.toVector().subtract(pLoc.toVector()).normalize();
                    org.bukkit.Location arrowStart = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.2));

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
        Bukkit.broadcast(Component.text("[BOTC] Le soleil se lève ! Suivez la flèche boussole devant vos yeux pour rejoindre le conseil.", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
    }
}

