package fr.cesi.botc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ActionMenuListener implements Listener {

    private final Botc main;

    public ActionMenuListener(Botc main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;

        Component titleComponent = event.getView().title();
        String titleStr = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        // 🌟 CORRECTION : Ce menu n'intercepte PLUS DU TOUT le Registre ni le Tribunal
        if (!titleStr.startsWith("Action : ")) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String targetName = titleStr.replace("Action : ", "").trim();

        BotcPlayer targetBotc = null;
        for (BotcPlayer bp : main.getPlayersMap().values()) {
            if (bp.getPlayerName().equalsIgnoreCase(targetName)) {
                targetBotc = bp;
                break;
            }
        }

        if (targetBotc == null) return;

        Material type = clickedItem.getType();

        if (type == Material.PAPER) {
            new RoleSelectionView().openRoleMenu(admin, targetBotc);
        }

        if (type == Material.IRON_SWORD) {
            targetBotc.setAlive(false);

            if (targetBotc.getRealRole().equalsIgnoreCase("Diablotin")) {
                long vivantsRestants = main.getPlayersMap().values().stream().filter(BotcPlayer::isAlive).count();

                if (vivantsRestants >= 5) {
                    BotcPlayer scarletWoman = null;
                    for (BotcPlayer bp : main.getPlayersMap().values()) {
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
                        admin.sendMessage(Component.text("[PROTEGE] Le Diablotin est mort, mais la Femme Ecarlate a pris sa place automatiquement !", NamedTextColor.GOLD));
                    }
                }
            }

            admin.closeInventory();

            Bukkit.broadcast(Component.text("[BOTC] Une ombre plane sur le village... " + targetBotc.getPlayerName() + " est mort.", NamedTextColor.RED));

            final Player pTarget = Bukkit.getPlayer(targetBotc.getPlayerUUID());

            if (pTarget != null) {
                final org.bukkit.entity.Entity ancienneChamberChair = pTarget.getVehicle();
                if (ancienneChamberChair != null) {
                    ancienneChamberChair.removePassenger(pTarget);
                }

                if (main.getConfig().contains("death.x")) {
                    org.bukkit.World w = Bukkit.getWorld(main.getConfig().getString("death.world", "world"));
                    double x = main.getConfig().getDouble("death.x");
                    double y = main.getConfig().getDouble("death.y");
                    double z = main.getConfig().getDouble("death.z");
                    float yaw = (float) main.getConfig().getDouble("death.yaw");
                    float pitch = (float) main.getConfig().getDouble("death.pitch");

                    if (w != null) {
                        pTarget.teleport(new org.bukkit.Location(w, x, y, z, yaw, pitch));
                    }
                }

                String lightMode = main.getConfig().getString("lightning.mode", "player");

                if (lightMode.equalsIgnoreCase("local") && main.getConfig().contains("lightning.x")) {
                    org.bukkit.World world = Bukkit.getWorld(main.getConfig().getString("lightning.world", "world"));
                    double lx = main.getConfig().getDouble("lightning.x");
                    double ly = main.getConfig().getDouble("lightning.y");
                    double lz = main.getConfig().getDouble("lightning.z");

                    if (world != null) {
                        world.strikeLightningEffect(new org.bukkit.Location(world, lx, ly, lz));
                    }
                } else {
                    pTarget.getWorld().strikeLightningEffect(pTarget.getLocation());
                }

                pTarget.getInventory().setHelmet(new ItemStack(Material.BARRIER));
                pTarget.getWorld().spawnParticle(org.bukkit.Particle.LARGE_SMOKE, pTarget.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.05);
                pTarget.setGameMode(org.bukkit.GameMode.ADVENTURE);

                net.kyori.adventure.title.Title deathTitle = net.kyori.adventure.title.Title.title(
                        Component.text("💀 TU ES MORT 💀", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD),
                        Component.text("Tu deviens un fantome. Il te reste 1 ultime vote.", NamedTextColor.GRAY)
                );
                pTarget.showTitle(deathTitle);

                pTarget.playSound(pTarget.getLocation(), org.bukkit.Sound.ENTITY_WITHER_DEATH, 0.8f, 0.5f);

                pTarget.sendMessage(Component.text("=============================================", NamedTextColor.DARK_RED));
                pTarget.sendMessage(Component.text("👻 BIENVENUE DANS L'AU-DELA", NamedTextColor.RED).decorate(TextDecoration.BOLD));
                pTarget.sendMessage(Component.text("• Tu n'as plus de role actif et tu ne peux plus nommer de suspect.", NamedTextColor.GRAY));
                pTarget.sendMessage(Component.text("• Ton bouton de vote dans ton Registre reste actif pour UN SEUL vote.", NamedTextColor.YELLOW));
                pTarget.sendMessage(Component.text("=============================================", NamedTextColor.DARK_RED));

                if (ancienneChamberChair != null && ancienneChamberChair.isValid()) {
                    new org.bukkit.scheduler.BukkitRunnable() {
                        @Override
                        public void run() {
                            if (pTarget.isOnline()) {
                                pTarget.teleport(ancienneChamberChair.getLocation());
                                ancienneChamberChair.addPassenger(pTarget);
                                pTarget.sendMessage(Component.text("➔ Tu es mort, mais ton esprit retourne s'asseoir au Conseil.", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));
                            }
                        }
                    }.runTaskLater(main, 30L);
                }
            }

        } else if (type == Material.POTION) {
            targetBotc.setAlive(true);
            admin.closeInventory();

            Bukkit.broadcast(Component.text("[BOTC] Les cieux s'ouvrent ! " + targetBotc.getPlayerName() + " ressuscite !", NamedTextColor.GREEN));

            Player pTarget = Bukkit.getPlayer(targetBotc.getPlayerUUID());
            if (pTarget != null) {
                pTarget.getInventory().setHelmet(new ItemStack(Material.AIR));
                pTarget.getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER, pTarget.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.1);
                pTarget.playSound(pTarget.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
                pTarget.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
        } else if (type == Material.BELL) {
            admin.closeInventory();
            main.getVoteManager().startVote(targetBotc);
        } else if (type == Material.NETHER_STAR || type == Material.COAL) {
            targetBotc.setGhostVote(!targetBotc.hasGhostVote());
            new ActionMenuView().openActionMenu(admin, targetBotc);
        }
    }
}