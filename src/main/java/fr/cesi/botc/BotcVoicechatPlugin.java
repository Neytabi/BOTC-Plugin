package fr.cesi.botc;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

public class BotcVoicechatPlugin implements VoicechatPlugin {

    private VoicechatServerApi voicechatApi;

    @Override
    public String getPluginId() {
        // Identifiant unique de ton extension vocale
        return "botc_voice_integration";
    }

    @Override
    public void initialize(VoicechatApi api) {
        // Initialisation globale (vide ici)
    }

    @Override
    public void registerEvents(de.maxhenkel.voicechat.api.events.EventRegistration registration) {
        // On conserve l'écoute du démarrage pour que l'API s'initialise correctement
        registration.registerEvent(de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent.class,
                this::onServerStarted);

        // 🌟 INTERCEPTION DES FLUX AUDIO DU MICRO
        registration.registerEvent(de.maxhenkel.voicechat.api.events.MicrophonePacketEvent.class, event -> {
            var senderConnection = event.getSenderConnection();
            if (senderConnection == null)
                return;

            // Récupération de l'UUID du joueur qui est en train de parler
            java.util.UUID playerUUID = senderConnection.getPlayer().getUuid();

            // 🛡️ EXCLUSION DES MJ : Si le joueur est OP, on quitte immédiatement (il n'est
            // jamais mute)
            org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(playerUUID);
            if (p != null && p.isOp()) {
                return;
            }

            // Si le joueur est un villageois dans la liste noire, on détruit son paquet
            // audio
            fr.cesi.botc.Botc plugin = fr.cesi.botc.Botc.getPlugin(fr.cesi.botc.Botc.class);
            if (plugin.getVcMutedPlayers().contains(playerUUID)) {
                event.cancel(); // Annule la transmission de la voix pour les autres
            }
        });
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        // On stocke l'API serveur de Simple Voice Chat dès qu'elle est prête
        this.voicechatApi = event.getVoicechat();
    }

    public VoicechatServerApi getVoicechatApi() {
        return voicechatApi;
    }
}