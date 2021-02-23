package de.crowraw.superban.commands;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.Ban;
import de.crowraw.superban.ban.BanRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permission = SuperBan.getInstance().getConfigUtil().getStringMessage("superban.info", "permission.info");
        if (sender.hasPermission(permission)) {
            if(args.length!=1){
                sender.sendMessage(SuperBan.getInstance().getPrefix()+SuperBan.getInstance().getConfigUtil().getStringMessage("§cFalse syntax: /info §a<§7Player§a>","message.info.syntax"));
             return true;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (BanRepository.isBanned(offlinePlayer.getUniqueId().toString())) {
                BanRepository.loadBanInCache(offlinePlayer.getUniqueId().toString());
                Ban ban = BanRepository.getBanForCache(offlinePlayer.getUniqueId().toString());
                sender.sendMessage("§c----------------------------------------");
                sender.sendMessage("§7Name: §a"+offlinePlayer.getName());
                sender.sendMessage("§7Punisher: §a"+ban.getBanner());
                sender.sendMessage("§7Banned at: §a"+ban.getBannedAt());
                sender.sendMessage("§7Ban-Type: §a"+ ban.getBanType().toString());
                sender.sendMessage("§7Seconds: §a"+ban.getTimeLeft()[0]);
                sender.sendMessage("§7Minutes: §a"+ban.getTimeLeft()[1]);
                sender.sendMessage("§7Hours: §a"+ban.getTimeLeft()[2]);
                sender.sendMessage("§7Days: §a"+ban.getTimeLeft()[3]);
                sender.sendMessage("§7Years: §a"+ban.getTimeLeft()[4]);
                sender.sendMessage("§c----------------------------------------");
            } else {
                sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aThe player is not banned!", "message.info.notbanned"));
            }

        } else {
            sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aSorry, but you need the following permission: %permission%", "message.info.noperm").replace("%permission%", permission));
        }


        return false;
    }
}
