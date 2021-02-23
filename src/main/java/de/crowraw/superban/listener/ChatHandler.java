package de.crowraw.superban.listener;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.Ban;
import de.crowraw.superban.ban.BanRepository;
import de.crowraw.superban.ban.BanType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatHandler implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        BanRepository.loadBanInCache(event.getPlayer().getUniqueId().toString());
        Ban ban = BanRepository.getBanForCache(event.getPlayer().getUniqueId().toString());
        if (ban == null || ban.getBanType() != BanType.CHAT) {
            return;
        } else {

            if (ban.isOver()) {
                BanRepository.removeBan(ban);
                event.getPlayer().sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§4§lYou're unmuted now", "message.unmuted"));
                return;
            }
            int seconds = ban.getTimeLeft()[0];
            int minutes = ban.getTimeLeft()[1];
            int hours = ban.getTimeLeft()[2];
            int days = ban.getTimeLeft()[3];
            int years = ban.getTimeLeft()[4];
            event.setCancelled(true);
            event.getPlayer().
                    sendMessage(SuperBan.getInstance().getPrefix() +
                            SuperBan.getInstance().getConfigUtil().
                                    getStringMessage("§4§lYou are muted! Time until umute:§7 %year% §aYears,§7 %days% §aDays, §7%hours%§a Hours,§7 %minutes% §aMinutes,§7 %seconds%§a Seconds!", "message.muted").
                                    replace("%year%", String.valueOf(years)).
                                    replace("%days%", String.valueOf(days)).
                                    replace("%hours%", String.valueOf(hours)).
                                    replace("%minutes%", String.valueOf(minutes)).
                                    replace("%seconds%", String.valueOf(seconds)));
        }
    }
}
