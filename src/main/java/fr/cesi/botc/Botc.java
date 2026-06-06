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

    private final HashMap<UUID, BotcPlayer> playersMap = new HashMap<>();
    private VoteManager voteManager;
    private BotcVoicechatPlugin voicechatPlugin;
    private boolean isNight = false;

    private boolean seatsAssigned = false;
    public boolean isSeatsAssigned() { return seatsAssigned; }
    public void setSeatsAssigned(boolean assigned) { this.seatsAssigned = assigned; }

    public boolean isNight() { return isNight; }
    public void setNight(boolean night) { this.isNight = night; }
    private final HashMap<UUID, BossBar> activeBossBars = new HashMap<>();

    @Override
    public void onEnable() {
        // 1. FORCER LA CRÉATION DU DOSSIER ET DE LA CONFIG
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // 2. ENREGISTREMENT DE LA COMMANDE ET DE SON AUTO-COMPLÉTION
        var botcCommand = this.getCommand("botc");
        if (botcCommand != null) {
            BotcCommand cmd = new BotcCommand(this);
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
        getServer().getPluginManager().registerEvents(new NominationListener(), this);

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

            // --- 🪄 LE FIX DE FORCAGE POUR PLUGMAN 🪄 ---
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
                            getLogger().info("⚠️ [PLUGMAN REPAIR] Connexion au serveur vocal restaurée avec succès !");
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

    public void resetGame() {
        this.clearAllRoleBossBars();
        this.seatsAssigned = false;

        List<String> roomsStr = getConfig().getStringList("rooms");
        for (String roomStr : roomsStr) {
            String[] parts = roomStr.split(",");
            org.bukkit.World w = Bukkit.getWorld(parts[0]);
            if (w != null) {
                int bx = Integer.parseInt(parts[1]);
                int by = Integer.parseInt(parts[2]);
                int bz = Integer.parseInt(parts[3]);
                // On remplace la tête par de l'air pour que le château soit propre pour la prochaine game
                w.getBlockAt(bx, by, bz).setType(org.bukkit.Material.AIR);
            }
        }

        for (BotcPlayer bp : playersMap.values()) {
            bp.setAlive(true);
            bp.setGhostVote(true);

            // 🌟 AJOUT : On efface son attribution de chaise
            bp.setChairIndex(-1);

            org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(bp.getPlayerUUID());
            if (p != null) {
                p.getInventory().setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR));
                p.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
        }
        org.bukkit.Bukkit.broadcast(net.kyori.adventure.text.Component.text("[BOTC] Le Conteur a reinitialise la partie. Tout le monde est vivant et les jetons de vote sont rendus !", net.kyori.adventure.text.format.NamedTextColor.GOLD));
    }

    public void prononcerMort(org.bukkit.entity.Player victim) {
        if (victim == null || !victim.isOnline()) return;

        net.kyori.adventure.title.Title deathTitle = net.kyori.adventure.title.Title.title(
                Component.text("💀 TU ES MORT 💀", net.kyori.adventure.text.format.NamedTextColor.DARK_RED)
                        .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD),
                Component.text("Tu deviens un fantôme. Il te reste 1 ultime vote.", net.kyori.adventure.text.format.NamedTextColor.GRAY)
        );

        victim.showTitle(deathTitle);
        victim.playSound(victim.getLocation(), org.bukkit.Sound.ENTITY_WITHER_DEATH, 0.7f, 0.5f);
        victim.getWorld().strikeLightningEffect(victim.getLocation());

        victim.sendMessage(Component.text("=============================================", net.kyori.adventure.text.format.NamedTextColor.DARK_RED));
        victim.sendMessage(Component.text("👻 BIENVENUE DANS L'AU-DELÀ", net.kyori.adventure.text.format.NamedTextColor.RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));
        victim.sendMessage(Component.text("• Tu n'as plus de rôle actif et tu ne peux plus nommer de suspect.", net.kyori.adventure.text.format.NamedTextColor.GRAY));
        victim.sendMessage(Component.text("• Ton bouton de vote dans ton Registre est encore actif pour UN SEUL vote.", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
        victim.sendMessage(Component.text("=============================================", net.kyori.adventure.text.format.NamedTextColor.DARK_RED));
    }
    public void rafraichirRoleBossBar(Player player, String roleName) {
        // Si le joueur a déjà une ancienne BossBar de rôle, on la cache
        if (activeBossBars.containsKey(player.getUniqueId())) {
            player.hideBossBar(activeBossBars.get(player.getUniqueId()));
        }

        // Création d'un bandeau violet/or super propre en haut de l'écran
        BossBar roleBar = BossBar.bossBar(
                Component.text("🎭 Ton Rôle Secret : ", NamedTextColor.LIGHT_PURPLE)
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