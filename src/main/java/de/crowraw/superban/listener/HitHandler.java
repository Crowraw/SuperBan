package de.crowraw.superban.listener;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.Ban;
import de.crowraw.superban.ban.BanRepository;
import de.crowraw.superban.ban.BanType;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitHandler implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Arrow) {
            if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
                Player player = ((Player) ((Arrow) event.getDamager()).getShooter());
                event.setCancelled(checkForBan(player));
                return;
            }
        }
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            event.setCancelled(checkForBan(player));
        }
    }

    private boolean checkForBan(Player player) {
        Ban ban = BanRepository.getBanForCache(player.getUniqueId().toString());
        if (ban == null || ban.getBanType() != BanType.PVP) {
            return false;
        }
        if (ban.isOver()) {
            BanRepository.removeBan(ban);
            return false;
        }
        int seconds = ban.getTimeLeft()[0];
        int minutes = ban.getTimeLeft()[1];
        int hours = ban.getTimeLeft()[2];
        int days = ban.getTimeLeft()[3];
        int years = ban.getTimeLeft()[4];

        player.sendTitle(SuperBan.getInstance().getConfigUtil().getStringMessage("§4PvP-Ban", "message.pvpban.bannedfrompvp"), "§7You cannot fight!");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f, 1f);
        player.sendMessage(SuperBan.getInstance().getPrefix() +
                SuperBan.getInstance().getConfigUtil().
                        getStringMessage("§4§lYou were banned from pvp! §7You cannot participate in pvp until the ban is over! §4§lTime until unban:§7 %year% §aYears,§7 %days% §aDays, §7%hours%§a Hours,§7 %minutes% §aMinutes,§7 %seconds%§a Seconds!", "message.pvpban.cannotattack").
                        replace("%year%", String.valueOf(years)).
                        replace("%days%", String.valueOf(days)).
                        replace("%hours%", String.valueOf(hours)).
                        replace("%minutes%", String.valueOf(minutes)).
                        replace("%seconds%", String.valueOf(seconds)));
        return true;
    }
}
