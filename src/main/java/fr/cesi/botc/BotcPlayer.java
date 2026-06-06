package fr.cesi.botc;

import java.util.UUID;

public class BotcPlayer {
    private final UUID playerUUID;
    private final String playerName;
    private boolean isAlive;
    private boolean ghostVote;
    private int chairIndex = -1;

    public int getChairIndex() { return chairIndex; }
    public void setChairIndex(int index) { this.chairIndex = index; }

    // --- NOUVEAU SYSTÈME DE RÔLES ---
    private String realRole = "Citadin (Sans rôle)";
    private String displayedRole = "Citadin (Sans rôle)";
    private String roleDescription = "Tu n'as pas encore de rôle assigné.";

    public BotcPlayer(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.isAlive = true;
        this.ghostVote = true;
    }

    public UUID getPlayerUUID() { return playerUUID; }
    public String getPlayerName() { return playerName; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { this.isAlive = alive; }
    public boolean hasGhostVote() { return ghostVote; }
    public void setGhostVote(boolean ghostVote) { this.ghostVote = ghostVote; }

    public String getRealRole() { return realRole; }
    public String getDisplayedRole() { return displayedRole; }
    public String getRoleDescription() { return roleDescription; }

    // Méthode pour attribuer un rôle normal
    public void setRole(String roleName, String description) {
        this.realRole = roleName;
        this.displayedRole = roleName;
        this.roleDescription = description;
    }

    // Méthode spéciale pour l'Ivrogne (Le Conteur sait qu'il est Ivrogne, mais le joueur voit un faux rôle)
    public void setFakeRole(String real, String displayed, String description) {
        this.realRole = real;
        this.displayedRole = displayed;
        this.roleDescription = description;
    }
}