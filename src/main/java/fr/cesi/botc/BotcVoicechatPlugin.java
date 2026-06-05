package fr.cesi.botc;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
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
    public void registerEvents(EventRegistration registration) {
        // On écoute le démarrage du serveur SVC
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        // On stocke l'API serveur de Simple Voice Chat dès qu'elle est prête
        this.voicechatApi = event.getVoicechat();
    }

    public VoicechatServerApi getVoicechatApi() {
        return voicechatApi;
    }
}