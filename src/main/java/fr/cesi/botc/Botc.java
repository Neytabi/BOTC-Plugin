package fr.cesi.botc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;

public final class Botc extends JavaPlugin {

    // La structure de données globale accessible par tout le plugin
    private final HashMap<UUID, BotcPlayer> playersMap = new HashMap<>();
    private VoteManager voteManager;
    private BotcVoicechatPlugin voicechatPlugin;

    private boolean isNight = false;

    public boolean isNight() { return isNight; }
    public void setNight(boolean night) { this.isNight = night; }

    @Override
    public void onEnable() {
        // FORCER LA CRÉATION DU DOSSIER ET DE LA CONFIG
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir(); // Crée le dossier /plugins/botc-plugins/
        }

        var botcCommand = this.getCommand("botc");
        if (botcCommand != null) {
            BotcCommand cmd = new BotcCommand(this);
            botcCommand.setExecutor(cmd);
            botcCommand.setTabCompleter(cmd);
        }

        // Crée une équipe Scoreboard pour masquer les pseudos si elle n'existe pas
        org.bukkit.scoreboard.Scoreboard sb = org.bukkit.Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team team = sb.getTeam("botc_night");
        if (team == null) {
            team = sb.registerNewTeam("botc_night");
        }
        team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);

        // Si la config n'existe pas, on met des valeurs de base par défaut en RAM et on sauvegarde
        getConfig().options().copyDefaults(true);
        if (!getConfig().contains("lightning.mode")) {
            getConfig().set("lightning.mode", "player");
            saveConfig();
        }

        getLogger().info("Le plugin BOTC s'est correctement lance !");

        // Tes enregistrements habituels
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new GrimoireListener(this), this);
        getServer().getPluginManager().registerEvents(new ActionMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new RoleSelectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatBlockerListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new ConteurMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRegistryListener(this), this);
        getServer().getPluginManager().registerEvents(new NominationListener(), this);

        if (botcCommand != null) {
            botcCommand.setExecutor(new BotcCommand(this));
        }

        this.voteManager = new VoteManager(this);

        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!playersMap.containsKey(player.getUniqueId())) {
                BotcPlayer newPlayer = new BotcPlayer(player.getUniqueId(), player.getName());
                playersMap.put(player.getUniqueId(), newPlayer);
            }
        }
        de.maxhenkel.voicechat.api.BukkitVoicechatService service = getServer().getServicesManager().load(de.maxhenkel.voicechat.api.BukkitVoicechatService.class);
        if (service != null) {
            this.voicechatPlugin = new BotcVoicechatPlugin();
            service.registerPlugin(this.voicechatPlugin);
            getLogger().info("Liaison avec Simple Voice Chat API réussie !");
        }
    }

    @Override
    public void onDisable() {
        // Nettoyage à la fermeture du serveur
        playersMap.clear();
    }

    // Le fameux "symbol" manquant que Maven cherchons !
    public HashMap<UUID, BotcPlayer> getPlayersMap() {
        return playersMap;
    }
    public VoteManager getVoteManager() {
        return voteManager;
    }
    public void resetGame() {
        for (BotcPlayer bp : playersMap.values()) {
            bp.setAlive(true);
            bp.setGhostVote(true);

            org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(bp.getPlayerUUID());
            if (p != null) {
                p.getInventory().setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR));
                p.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
        }
        org.bukkit.Bukkit.broadcast(net.kyori.adventure.text.Component.text("[BOTC] Le Conteur a reinitialise la partie. Tout le monde est vivant et les jetons de vote sont rendus !", net.kyori.adventure.text.format.NamedTextColor.GOLD));
    }
    public BotcVoicechatPlugin getVoicechatPlugin() {
        return this.voicechatPlugin;
    }
}
