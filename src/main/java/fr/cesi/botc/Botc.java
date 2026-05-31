package fr.cesi.botc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;

public final class Botc extends JavaPlugin {

    // La structure de données globale accessible par tout le plugin
    private final HashMap<UUID, BotcPlayer> playersMap = new HashMap<>();
    private VoteManager voteManager;

    private boolean isNight = false;

    public boolean isNight() { return isNight; }
    public void setNight(boolean night) { this.isNight = night; }

    @Override
    public void onEnable() {
        // FORCER LA CRÉATION DU DOSSIER ET DE LA CONFIG
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir(); // Crée le dossier /plugins/botc-plugins/
        }

        if (this.getCommand("botc") != null) {
            BotcCommand cmd = new BotcCommand(this);
            this.getCommand("botc").setExecutor(cmd);
            this.getCommand("botc").setTabCompleter(cmd); // <--- AJOUTE CETTE LIGNE
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

        if (this.getCommand("botc") != null) {
            this.getCommand("botc").setExecutor(new BotcCommand(this));
        }

        this.voteManager = new VoteManager(this);

        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!playersMap.containsKey(player.getUniqueId())) {
                BotcPlayer newPlayer = new BotcPlayer(player.getUniqueId(), player.getName());
                playersMap.put(player.getUniqueId(), newPlayer);
            }
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
}