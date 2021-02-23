package de.crowraw.superban.commands;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.BanRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SorryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permission = SuperBan.getInstance().getConfigUtil().getStringMessage("superban.sorry", "permission.unban");
        if (sender.hasPermission(permission)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (BanRepository.isBanned(offlinePlayer.getUniqueId().toString())) {
                BanRepository.loadBanInCache(offlinePlayer.getUniqueId().toString());
                BanRepository.removeBan(BanRepository.getBanForCache(offlinePlayer.getUniqueId().toString()));
                sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aThe player was unbanned/unmuted", "message.sorry.succes"));
            } else {
                sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aThe player is not banned!", "message.sorry.notfound"));
            }


        } else {
            sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aSorry, but you need the following permission: %permission%", "message.sorry.noperm").replace("%permission%", permission));
        }


        return false;
    }
}
