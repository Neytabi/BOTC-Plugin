package fr.cesi.botc.commands.impl;

import fr.cesi.botc.Botc;
import fr.cesi.botc.BotcPlayer;
import fr.cesi.botc.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AssisCommand implements SubCommand {

    private final Botc main;

    public AssisCommand(Botc main) {
        this.main = main;
    }

    @Override
    public String getName() {
        return "assis";
    }

    @Override
    public String getDescription() {
        return "Place tout le monde sur des chaises pour le conseil.";
    }

    @Override
    public String getSyntax() {
        return "/botc assis";
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public void execute(Player player, String[] args) {
        List<String> chairsStr = main.getConfig().getStringList(main.getPresetPath("chairs"));
        if (chairsStr.isEmpty()) {
            player.sendMessage(Component.text("Erreur : Aucune chaise enregistrée.", NamedTextColor.RED));
            return;
        }

        // ---  ALGORITHME DE RÉPARTITION FIXE ET UNIQUE  ---
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
                if (bp != null && bp.getChairIndex() == -1) { //  N'attribue une place que s'il n'en a pas
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
                    .setName(" Tribunal")
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
                Component.text(" TOUT LE MONDE ASSIS ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
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

        player.sendMessage(Component.text("-> Prochaine étape : ", NamedTextColor.AQUA)
                .append(Component.text("Libérer les joueurs", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" [CLIQUE POUR EXECUTER]", NamedTextColor.GREEN))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/botc debout")));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}
