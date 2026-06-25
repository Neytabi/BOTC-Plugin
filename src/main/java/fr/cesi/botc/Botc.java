package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;

public final class Botc extends JavaPlugin {

    private final java.util.Set<java.util.UUID> vcMutedPlayers = new java.util.HashSet<>();

    public java.util.Set<java.util.UUID> getVcMutedPlayers() { return vcMutedPlayers; }

    private final HashMap<UUID, BotcPlayer> playersMap = new HashMap<>();
    private VoteManager voteManager;
    private BotcVoicechatPlugin voicechatPlugin;
    private boolean isNight = false;
    private final java.util.List<java.util.UUID> hasNominatedToday = new java.util.ArrayList<>();
    private final java.util.List<java.util.UUID> hasBeenNominatedToday = new java.util.ArrayList<>();
    private final java.util.HashMap<java.util.UUID, Boolean> isAskingQuestion = new java.util.HashMap<>();
    private boolean isCouncilOpen = false;

    //  SÉCURISATION DU STATUT DES MENUS ET DU VOTE DE MAP
    private boolean nameTagsHidden = false;
    private boolean isMapVoteOpen = false;
    private final java.util.HashMap<java.util.UUID, String> mapVotes = new java.util.HashMap<>();
    private org.bukkit.scheduler.BukkitTask mapVoteTask = null;

    public boolean isNameTagsHidden() { return nameTagsHidden; }
    public void setNameTagsHidden(boolean hidden) { this.nameTagsHidden = hidden; }
    public boolean isMapVoteOpen() { return isMapVoteOpen; }
    public void setMapVoteOpen(boolean open) { this.isMapVoteOpen = open; }
    public java.util.HashMap<java.util.UUID, String> getMapVotes() { return mapVotes; }
    public org.bukkit.scheduler.BukkitTask getMapVoteTask() { return mapVoteTask; }
    public void setMapVoteTask(org.bukkit.scheduler.BukkitTask task) { this.mapVoteTask = task; }

    public java.util.List<java.util.UUID> getHasNominatedToday() { return hasNominatedToday; }
    public java.util.List<java.util.UUID> getHasBeenNominatedToday() { return hasBeenNominatedToday; }
    public java.util.HashMap<java.util.UUID, Boolean> getIsAskingQuestion() { return isAskingQuestion; }
    public boolean isCouncilOpen() { return isCouncilOpen; }
    public void setCouncilOpen(boolean open) { this.isCouncilOpen = open; }

    private boolean seatsAssigned = false;
    public boolean isSeatsAssigned() { return seatsAssigned; }
    public void setSeatsAssigned(boolean assigned) { this.seatsAssigned = assigned; }

    public boolean isNight() { return isNight; }
    private String activePreset = "default";

    public String getActivePreset() { return activePreset; }
    public void setActivePreset(String preset) {
        this.activePreset = preset;
        getConfig().set("active_preset", preset);
        saveConfig();
    }
    public String getPresetPath(String subPath) {
        return "presets." + activePreset + "." + subPath;
    }
    public void setNight(boolean night) { this.isNight = night; }
    private final HashMap<UUID, BossBar> activeBossBars = new HashMap<>();

    @Override
    public void onEnable() {
        if (!getConfig().contains("active_preset")) {
            getConfig().set("active_preset", "default");
            saveConfig();
        }
        this.activePreset = getConfig().getString("active_preset", "default");
        // 1. FORCER LA CRÉATION DU DOSSIER ET DE LA CONFIG
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // 2. ENREGISTREMENT DE LA COMMANDE ET DE SON AUTO-COMPLÉTION
        var botcCommand = this.getCommand("botc");
        if (botcCommand != null) {
            fr.cesi.botc.commands.BotcCommandManager cmd = new fr.cesi.botc.commands.BotcCommandManager(this);
            botcCommand.setExecutor(cmd);
            botcCommand.setTabCompleter(cmd);
        }

        // 3. ÉQUIPE SCOREBOARD POUR LES NAMETAGS
        org.bukkit.scoreboard.Scoreboard sb = org.bukkit.Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team team = sb.getTeam("botc_night");
        if (team == null) {
            team = sb.registerNewTeam("botc_night");
        }
        team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);

        // 4. CONFIGURATION PAR DÉFAUT
        getConfig().options().copyDefaults(true);
        if (!getConfig().contains("lightning.mode")) {
            getConfig().set("lightning.mode", "player");
            saveConfig();
        }

        // 5. ENREGISTREMENT DES LISTENERS
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new GrimoireListener(this), this);
        getServer().getPluginManager().registerEvents(new ActionMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new RoleSelectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatBlockerListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new ConteurMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRegistryListener(this), this);
        getServer().getPluginManager().registerEvents(new NominationListener(this), this);
        getServer().getPluginManager().registerEvents(new PresetListener(this), this);
        getServer().getPluginManager().registerEvents(new GameChatListener(this), this);

        this.voteManager = new VoteManager(this);

        // 6. SYNC DES JOUEURS DÉJÀ CONNECTÉS
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!playersMap.containsKey(player.getUniqueId())) {
                BotcPlayer newPlayer = new BotcPlayer(player.getUniqueId(), player.getName());
                playersMap.put(player.getUniqueId(), newPlayer);
            }
        }

        // 7. LIAISON SIMPLE VOICE CHAT API + CORRECTEUR APÈS UN UN RELOAD PLUGMAN
        de.maxhenkel.voicechat.api.BukkitVoicechatService service = getServer().getServicesManager().load(de.maxhenkel.voicechat.api.BukkitVoicechatService.class);
        if (service != null) {
            this.voicechatPlugin = new BotcVoicechatPlugin();
            service.registerPlugin(this.voicechatPlugin);
            getLogger().info("Liaison initiale avec Simple Voice Chat API réussie !");

            // ---  LE FIX DE FORCAGE POUR PLUGMAN  ---
            try {
                de.maxhenkel.voicechat.api.VoicechatApi foundApi = null;

                // On extrait l'API active cachée dans les entrailles du service de MaxHenkel
                for (java.lang.reflect.Field field : service.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object val = field.get(service);

                    if (val instanceof de.maxhenkel.voicechat.api.VoicechatApi) {
                        foundApi = (de.maxhenkel.voicechat.api.VoicechatApi) val;
                        break;
                    }

                    // Si elle se cache dans une sous-collection de plugins déjà enregistrés
                    if (val instanceof java.util.Collection<?> collection) {
                        for (Object obj : collection) {
                            if (obj == null) continue;
                            for (java.lang.reflect.Field f2 : obj.getClass().getDeclaredFields()) {
                                f2.setAccessible(true);
                                Object maybeApi = f2.get(obj);
                                if (maybeApi instanceof de.maxhenkel.voicechat.api.VoicechatApi) {
                                    foundApi = (de.maxhenkel.voicechat.api.VoicechatApi) maybeApi;
                                    break;
                                }
                            }
                            if (foundApi != null) break;
                        }
                    }
                    if (foundApi != null) break;
                }

                // Si on a localisé l'API en mémoire, on la ré-injecte de force dans ton plugin tout neuf
                if (foundApi != null) {
                    for (java.lang.reflect.Field field : this.voicechatPlugin.getClass().getDeclaredFields()) {
                        if (field.getType().isAssignableFrom(foundApi.getClass()) || field.getType().getName().contains("Voicechat")) {
                            field.setAccessible(true);
                            field.set(this.voicechatPlugin, foundApi);
                            getLogger().info(" [PLUGMAN REPAIR] Connexion au serveur vocal restaurée avec succès !");
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                getLogger().warning("Impossible de forcer la restauration de l'API vocale : " + e.getMessage());
            }
            // -------------------------------------------
        }

        getLogger().info("Le plugin BOTC s'est correctement lance !");
    }

    @Override
    public void onDisable() {
        playersMap.clear();
    }

    public HashMap<UUID, BotcPlayer> getPlayersMap() {
        return playersMap;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public BotcVoicechatPlugin getVoicechatPlugin() {
        return this.voicechatPlugin;
    }

    // --- MODIFICATION DE TA MÉTHODE resetGame() ---
    public void resetGame() {
        this.nameTagsHidden = false;
        this.isMapVoteOpen = false;
        this.mapVotes.clear();
        if (this.mapVoteTask != null) {
            this.mapVoteTask.cancel();
            this.mapVoteTask = null;
        }
        this.clearAllRoleBossBars();
        this.hasNominatedToday.clear();
        this.hasBeenNominatedToday.clear();
        this.isAskingQuestion.clear();
        this.seatsAssigned = false;
        this.isCouncilOpen = false;

        //  On vide simplement la liste des mutes, le BotcVoicechatPlugin s'occupe du reste.
        this.vcMutedPlayers.clear();

        //  CORRECTION MULTI-MAP : On utilise getPresetPath("rooms")
        List<String> roomsStr = getConfig().getStringList(getPresetPath("rooms"));
        for (String roomStr : roomsStr) {
            String[] parts = roomStr.split(",");
            org.bukkit.World w = Bukkit.getWorld(parts[0]);
            if (w != null) {
                int bx = Integer.parseInt(parts[1]);
                int by = Integer.parseInt(parts[2]);
                int bz = Integer.parseInt(parts[3]);
                w.getBlockAt(bx, by, bz).setType(org.bukkit.Material.AIR);
            }
        }

        for (BotcPlayer bp : playersMap.values()) {
            bp.setAlive(true);
            bp.setGhostVote(true);
            bp.setChairIndex(-1);

            org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(bp.getPlayerUUID());
            if (p != null) {
                p.getInventory().setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR));
                p.setGameMode(org.bukkit.GameMode.SURVIVAL);

                //  SYNCHRONISATION PARFAITE AVEC PLAYERJOINLISTENER
                p.getInventory().clear();
                if (p.isOp()) {
                    org.bukkit.inventory.ItemStack livreMJ = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ENCHANTED_BOOK);
                    org.bukkit.inventory.meta.ItemMeta meta = livreMJ.getItemMeta();
                    if (meta != null) {
                        meta.displayName(Component.text(" Le Grimoire du Conteur", NamedTextColor.DARK_PURPLE));
                        livreMJ.setItemMeta(meta);
                    }
                    p.getInventory().setItem(0, livreMJ);
                } else {
                    org.bukkit.inventory.ItemStack registreJoueur = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BOOK);
                    org.bukkit.inventory.meta.ItemMeta meta = registreJoueur.getItemMeta();
                    if (meta != null) {
                        meta.displayName(Component.text(" Registre du Tribunal", NamedTextColor.DARK_GREEN));
                        registreJoueur.setItemMeta(meta);
                    }
                    p.getInventory().setItem(0, registreJoueur);
                }
            }
        }
        org.bukkit.Bukkit.broadcast(net.kyori.adventure.text.Component.text("[BOTC] Le Conteur a reinitialise la partie. Tout le monde est vivant et les jetons de vote sont rendus !", net.kyori.adventure.text.format.NamedTextColor.GOLD));
    }

    public void prononcerMort(org.bukkit.entity.Player victim) {
        if (victim == null || !victim.isOnline()) return;

        net.kyori.adventure.title.Title deathTitle = net.kyori.adventure.title.Title.title(
                Component.text(" TU ES MORT ", net.kyori.adventure.text.format.NamedTextColor.DARK_RED)
                        .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD),
                Component.text("Tu deviens un fantôme. Il te reste 1 ultime vote.", net.kyori.adventure.text.format.NamedTextColor.GRAY)
        );

        victim.showTitle(deathTitle);
        victim.playSound(victim.getLocation(), org.bukkit.Sound.ENTITY_WITHER_DEATH, 0.7f, 0.5f);
        victim.getWorld().strikeLightningEffect(victim.getLocation());

        victim.sendMessage(Component.text("=============================================", net.kyori.adventure.text.format.NamedTextColor.DARK_RED));
        victim.sendMessage(Component.text(" BIENVENUE DANS L'AU-DELÀ", net.kyori.adventure.text.format.NamedTextColor.RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));
        victim.sendMessage(Component.text("- Tu n'as plus de rôle actif et tu ne peux plus nommer de suspect.", net.kyori.adventure.text.format.NamedTextColor.GRAY));
        victim.sendMessage(Component.text("- Ton bouton de vote dans ton Registre est encore actif pour UN SEUL vote.", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
        victim.sendMessage(Component.text("=============================================", net.kyori.adventure.text.format.NamedTextColor.DARK_RED));
    }

    public void executePlayer(BotcPlayer targetBotc, org.bukkit.command.CommandSender admin) {
        targetBotc.setAlive(false);

        if (targetBotc.getRealRole().equalsIgnoreCase("Diablotin")) {
            long vivantsRestants = playersMap.values().stream().filter(bp -> bp != null && bp.isAlive()).count();

            if (vivantsRestants >= 5) {
                BotcPlayer scarletWoman = null;
                for (BotcPlayer bp : playersMap.values()) {
                    if (bp.getRealRole().equalsIgnoreCase("Femme Ecarlate") && bp.isAlive()) {
                        scarletWoman = bp;
                        break;
                    }
                }

                if (scarletWoman != null) {
                    scarletWoman.setRole("Diablotin", "Tu as herite du role de Diablotin suite a la mort de ton maitre !");

                    Player pScarlet = Bukkit.getPlayer(scarletWoman.getPlayerUUID());
                    if (pScarlet != null) {
                        pScarlet.sendMessage(Component.text("=====================================", NamedTextColor.DARK_RED));
                        pScarlet.sendMessage(Component.text("LA FEMME ECARLATE S'EVEILLE !", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                        pScarlet.sendMessage(Component.text("Le Diablotin est mort. Tu deviens le nouveau DIABLOTIN !", NamedTextColor.RED));
                        pScarlet.sendMessage(Component.text("=====================================", NamedTextColor.DARK_RED));
                    }
                    if (admin != null) {
                        admin.sendMessage(Component.text("[PROTEGE] Le Diablotin est mort, mais la Femme Ecarlate a pris sa place automatiquement !", NamedTextColor.GOLD));
                    }
                }
            }
        }

        Bukkit.broadcast(Component.text("[BOTC] Une ombre plane sur le village... " + targetBotc.getPlayerName() + " est mort.", NamedTextColor.RED));

        final Player pTarget = Bukkit.getPlayer(targetBotc.getPlayerUUID());

        if (pTarget != null) {
            final org.bukkit.entity.Entity ancienneChamberChair = pTarget.getVehicle();
            if (ancienneChamberChair != null) {
                ancienneChamberChair.removePassenger(pTarget);
            }

            if (getConfig().contains("death.x")) {
                org.bukkit.World w = Bukkit.getWorld(getConfig().getString("death.world", "world"));
                double x = getConfig().getDouble("death.x");
                double y = getConfig().getDouble("death.y");
                double z = getConfig().getDouble("death.z");
                float yaw = (float) getConfig().getDouble("death.yaw");
                float pitch = (float) getConfig().getDouble("death.pitch");

                if (w != null) {
                    pTarget.teleport(new org.bukkit.Location(w, x, y, z, yaw, pitch));
                }
            }

            String lightMode = getConfig().getString("lightning.mode", "player");

            if (lightMode.equalsIgnoreCase("local") && getConfig().contains("lightning.x")) {
                org.bukkit.World world = Bukkit.getWorld(getConfig().getString("lightning.world", "world"));
                double lx = getConfig().getDouble("lightning.x");
                double ly = getConfig().getDouble("lightning.y");
                double lz = getConfig().getDouble("lightning.z");

                if (world != null) {
                    world.strikeLightningEffect(new org.bukkit.Location(world, lx, ly, lz));
                }
            } else {
                pTarget.getWorld().strikeLightningEffect(pTarget.getLocation());
            }

            pTarget.getInventory().setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.BARRIER));
            pTarget.getWorld().spawnParticle(org.bukkit.Particle.LARGE_SMOKE, pTarget.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.05);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                prononcerMort(pTarget);
                // Si la partie est en cours, replacer le mort à sa place de tribunal
                if (isSeatsAssigned() && targetBotc.getChairIndex() != -1) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "botc assis");
                }
            }, 60L);
        }
    }
    public void rafraichirRoleBossBar(Player player, String roleName) {
        // Si le joueur a déjà une ancienne BossBar de rôle, on la cache
        if (activeBossBars.containsKey(player.getUniqueId())) {
            player.hideBossBar(activeBossBars.get(player.getUniqueId()));
        }

        // Création d'un bandeau violet/or super propre en haut de l'écran
        BossBar roleBar = BossBar.bossBar(
                Component.text(" Ton Rôle Secret : ", NamedTextColor.LIGHT_PURPLE)
                        .append(Component.text(roleName, NamedTextColor.GOLD).decorate(TextDecoration.BOLD)),
                1.0f, // Remplie à 100%
                BossBar.Color.PURPLE,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(roleBar);
        activeBossBars.put(player.getUniqueId(), roleBar);
    }

    // 4. Ajoute cette méthode pour tout effacer quand la partie se réinitialise :
    public void clearAllRoleBossBars() {
        for (UUID uuid : activeBossBars.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.hideBossBar(activeBossBars.get(uuid));
            }
        }
        activeBossBars.clear();
    }
}