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
        // ⚖️ COMMANDE : /botc order (Affiche l'ordre du cercle pour le MJ)
        // ==========================================
        if (args[0].equalsIgnoreCase("order")) {
            if (!main.isSeatsAssigned()) {
                player.sendMessage(Component.text("🚨 L'ordre n'est pas encore généré ! Lance d'abord un /botc assis pour créer le cercle de cette game.", NamedTextColor.RED));
                return true;
            }

            player.sendMessage(Component.text("=== 🔄 ORDRE DU CERCLE DE JEU (Du siège 1 à X) ===", NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD));

            // On récupère et trie les joueurs selon leur index de chaise
            java.util.List<BotcPlayer> orderedPlayers = new java.util.ArrayList<>(main.getPlayersMap().values());
            orderedPlayers.removeIf(bp -> bp.getChairIndex() == -1);
            orderedPlayers.sort(java.util.Comparator.comparingInt(BotcPlayer::getChairIndex));

            if (orderedPlayers.isEmpty()) {
                player.sendMessage(Component.text("Aucun joueur n'est assis sur une chaise actuellement.", NamedTextColor.GRAY));
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
            player.sendMessage(Component.text("--------------------------------------------------", NamedTextColor.GRAY));
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
            org.bukkit.scoreboard.Team nightTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("botc_night");

            // --- DÉCONNEXION DU GROUPE VOCAL (VERSION DIAGNOSTIC) ---
            if (main.getVoicechatPlugin() != null && main.getVoicechatPlugin().getVoicechatApi() != null) {
                de.maxhenkel.voicechat.api.VoicechatServerApi voiceApi = main.getVoicechatPlugin().getVoicechatApi();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    de.maxhenkel.voicechat.api.VoicechatConnection connection = voiceApi.getConnectionOf(p.getUniqueId());
                    if (connection != null) {
                        connection.setGroup(null); // Quitte le groupe
                    }
                }
            }

            net.kyori.adventure.title.Title deboutTitle = net.kyori.adventure.title.Title.title(
                    Component.text("! LEVEZ-VOUS !", NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                    Component.text("Vous pouvez quitter votre siège.", NamedTextColor.GRAY)
            );

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(deboutTitle);
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_HORSE_GALLOP, 0.5f, 1.2f); // Bruit de pas légers

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

            Bukkit.broadcast(Component.text("[BOTC] Le conseil est terminé, vous pouvez vous lever.", NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("mort")) {
            if (!player.isOp()) return true;

            net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                    Component.text("💀 ANNONCE DES MORTS 💀", NamedTextColor.RED).decorate(TextDecoration.BOLD),
                    Component.text("Écoutez attentivement le Conteur...", NamedTextColor.GRAY)
            );

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(title);
                // Un bruit d'éclair lointain pour l'ambiance sombre
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.8f);
            }

            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.RED));
            Bukkit.broadcast(Component.text("[BOTC] Silence ! Le Conteur va annoncer les victimes de la nuit.", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("tempslibre")) {
            if (!player.isOp()) return true;

            net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                    Component.text("🗣️ TEMPS LIBRE 🗣️", NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                    Component.text("Dispersez-vous et complotez en secret !", NamedTextColor.GRAY)
            );

            // On force tout le monde à se lever pour qu'ils puissent courir partout
            player.performCommand("botc debout");

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(title);
                // Un petit jingle de début de journée léger
                p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7f, 1.2f);
            }

            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GREEN));
            Bukkit.broadcast(Component.text("[BOTC] Le temps libre est déclaré. Les discussions privées sont autorisées !", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("conseil")) {
            if (!player.isOp()) return true;

            // 1. Préparation de l'affichage à l'écran
            net.kyori.adventure.title.Title title = net.kyori.adventure.title.Title.title(
                    Component.text("⚖️ LE CONSEIL COMMENCE ⚖️", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                    Component.text("Retournez au tribunal ! Fin du temps libre.", NamedTextColor.GRAY)
            );

            // 2. Récupération de la position du Tribunal sauvegardée
            org.bukkit.Location tribunalLoc = null;
            if (main.getConfig().contains("tribunal.x")) {
                String worldName = main.getConfig().getString("tribunal.world");
                if (worldName != null) {
                    org.bukkit.World world = Bukkit.getWorld(worldName);
                    double tx = main.getConfig().getDouble("tribunal.x");
                    double ty = main.getConfig().getDouble("tribunal.y");
                    double tz = main.getConfig().getDouble("tribunal.z");
                    if (world != null) tribunalLoc = new org.bukkit.Location(world, tx, ty, tz);
                }
            }

            final org.bukkit.Location finalTribunal = tribunalLoc;

            // Sécurité si le Conteur a oublié le /botc settribunal
            if (finalTribunal == null) {
                player.sendMessage(Component.text("Erreur : Le tribunal n'est pas configuré ! (/botc settribunal)", NamedTextColor.RED));
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
                        if (ticks > 40 || !p.isOnline()) { this.cancel(); return; }

                        org.bukkit.Location pLoc = p.getLocation();
                        double distance = pLoc.distance(finalTribunal);

                        // Quand le joueur arrive à moins de 3 blocs du centre
                        if (distance < 3.0) {
                            p.sendActionBar(Component.text("✦ Vous êtes arrivé à votre siège ✦", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                            p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.4f, 1.5f);
                            this.cancel();
                            return;
                        }

                        // Affichage de la distance dans la barre d'action
                        p.sendActionBar(Component.text("➔ Retour au Conseil : ", NamedTextColor.GOLD)
                                .append(Component.text((int) distance + "m", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));

                        // Calcul du vecteur directionnel pour les particules de lueur
                        org.bukkit.util.Vector direction = finalTribunal.toVector().subtract(pLoc.toVector()).normalize();
                        org.bukkit.Location arrowStart = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.2));

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
            Bukkit.broadcast(Component.text("[BOTC] Le Conseil est ouvert ! Suivez les flèches pour regagner immédiatement le tribunal.", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
            Bukkit.broadcast(Component.text("=============================================", NamedTextColor.GOLD));

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

            // --- 🌟 ALGORITHME DE RÉPARTITION FIXE ET UNIQUE 🌟 ---
            // Si c'est le premier "/botc assis" depuis le reset, on mélange et on fixe les places !
            if (!main.isSeatsAssigned()) {
                List<BotcPlayer> joueursAAsseoir = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.isOp()) {
                        BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
                        if (bp != null) joueursAAsseoir.add(bp);
                    }
                }

                // On mélange Aléatoirement l'ordre des joueurs
                java.util.Collections.shuffle(joueursAAsseoir);

                // On leur attribue définitivement un numéro de chaise pour TOUTE la game
                for (int i = 0; i < joueursAAsseoir.size(); i++) {
                    joueursAAsseoir.get(i).setChairIndex(i);
                }
                main.setSeatsAssigned(true); // C'est verrouillé jusqu'au prochain /botc reset !
            }
            // Sécurité : Si un joueur s'est connecté en retard (mid-game), on lui donne une chaise vide restante
            else {
                List<Integer> chaisesPrises = new ArrayList<>();
                for (BotcPlayer bp : main.getPlayersMap().values()) {
                    if (bp.getChairIndex() != -1) chaisesPrises.add(bp.getChairIndex());
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp()) continue;
                    BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
                    if (bp != null && bp.getChairIndex() == -1) {
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
                    de.maxhenkel.voicechat.api.VoicechatConnection connection = voiceApi.getConnectionOf(p.getUniqueId());
                    if (connection != null) connection.setGroup(tribunalGroup);
                }
            }

            // --- PLACEMENT PHYSIQUE SUR LES CHAISES FIXES ---
            org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            org.bukkit.scoreboard.Team chairTeam = scoreboard.getTeam("botc_chairs");
            if (chairTeam == null) {
                chairTeam = scoreboard.registerNewTeam("botc_chairs");
                chairTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            }

            org.bukkit.scoreboard.Team nightTeam = scoreboard.getTeam("botc_night");

            net.kyori.adventure.title.Title assisTitle = net.kyori.adventure.title.Title.title(
                    Component.text("🪑 TOUT LE MONDE ASSIS 🪑", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                    Component.text("Le Tribunal est en séance. Silence dans les rangs.", NamedTextColor.GRAY)
            );

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(assisTitle);
                p.playSound(p.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.0f, 0.5f);

                if (p.isOp()) continue;

                BotcPlayer bp = main.getPlayersMap().get(p.getUniqueId());
                if (bp == null || bp.getChairIndex() == -1) continue;

                int indexChaise = bp.getChairIndex();
                if (indexChaise >= chairsStr.size()) continue; // Sécurité si pas assez de chaises physiques

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
                    org.bukkit.Location horseLoc = chairLoc.clone().add(0, -1.5, 0);

                    p.teleport(chairLoc);

                    org.bukkit.entity.Horse chair = w.spawn(chairLoc, org.bukkit.entity.Horse.class, horse -> {
                        horse.setGravity(false);
                        horse.setSilent(true);
                        horse.setInvulnerable(true);
                        horse.setAI(false);
                        horse.setTamed(true);
                        horse.setPersistent(false);
                        horse.addScoreboardTag("botc_chair");
                        horse.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                    });

                    chair.teleport(horseLoc);
                    chairTeam.addEntry(chair.getUniqueId().toString());
                    chair.addPassenger(p);
                }
            }

            Bukkit.broadcast(Component.text("[BOTC] Le tribunal commence. Tout le monde est assis à sa place attribuée !", NamedTextColor.GOLD));
            return true;
        }

        if (args.length > 0) {
            // 1. ANCIENNE COMMANDE : /botc admin
            if (args[0].equalsIgnoreCase("admin")) {
                grimoireView.openGrimoire(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                // 1. On appelle la vraie méthode qui réanime tout le monde et clean les BossBars
                main.resetGame();

                // 2. On évite de vider "chairs" pour ne pas avoir à re-sélectionner les sièges du village !
                player.sendMessage(Component.text("✓ La partie a été réinitialisée ! Tous les joueurs sont de nouveau vivants et prêts. Placement des joueurs réinitialisés", NamedTextColor.GREEN));
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
                if ("shownames".startsWith(input)) completions.add("shownames");
                if ("hidenames".startsWith(input)) completions.add("hidenames");
                if ("mort".startsWith(input)) completions.add("mort");
                if ("tempslibre".startsWith(input)) completions.add("tempslibre");
                if ("conseil".startsWith(input)) completions.add("conseil");
                if ("order".startsWith(input)) completions.add("order");
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
        // 1. Ambiance de terreur et titre pour tout le monde
        net.kyori.adventure.title.Title nightTitle = net.kyori.adventure.title.Title.title(
                Component.text("🌙 LA NUIT TOMBE 🌙", NamedTextColor.BLUE).decorate(TextDecoration.BOLD),
                Component.text("Fermez les yeux... Le démon s'éveille.", NamedTextColor.GRAY)
        );
        // 1. Ambiance de terreur pour tout le monde
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (nightTeam != null) nightTeam.addEntry(p.getName());
            p.showTitle(nightTitle);
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

                // 🌟 LE FIX CLÉ : On force le bloc à devenir de l'AIR pour détruire l'ancienne tête et vider le cache client !
                block.setType(org.bukkit.Material.AIR);

                // Maintenant on peut poser la nouvelle tête, elle s'actualisera à coup sûr
                block.setType(org.bukkit.Material.PLAYER_HEAD);

                if (roomParts.length > 4) {
                    try {
                        org.bukkit.block.BlockFace facing = org.bukkit.block.BlockFace.valueOf(roomParts[4]);
                        if (block.getBlockData() instanceof org.bukkit.block.data.Rotatable rotatable) {
                            rotatable.setRotation(facing);
                            block.setBlockData(rotatable);
                        }
                    } catch (IllegalArgumentException ignored) {}
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
            String worldName = main.getConfig().getString("tribunal.world");
            if (worldName != null) {
                org.bukkit.World world = Bukkit.getWorld(worldName);
                double tx = main.getConfig().getDouble("tribunal.x");
                double ty = main.getConfig().getDouble("tribunal.y");
                double tz = main.getConfig().getDouble("tribunal.z");
                if (world != null) tribunalLoc = new org.bukkit.Location(world, tx, ty, tz);
            }
        }

        final org.bukkit.Location finalTribunal = tribunalLoc;

        if (finalTribunal == null) {
            player.sendMessage(Component.text("Erreur : Le tribunal n'est pas configuré ! (/botc settribunal)", NamedTextColor.RED));
            return;
        }
        // Titre du réveil
        net.kyori.adventure.title.Title wakeTitle = net.kyori.adventure.title.Title.title(
                Component.text("🌅 LE RÉVEIL 🌅", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                Component.text("Regagnez le Tribunal pour le Conseil.", NamedTextColor.LIGHT_PURPLE)
        );
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

