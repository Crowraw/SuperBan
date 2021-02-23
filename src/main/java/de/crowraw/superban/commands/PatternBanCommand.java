package de.crowraw.superban.commands;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.Ban;
import de.crowraw.superban.ban.BanRepository;
import de.crowraw.superban.ban.pattern.BanPattern;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDate;
import java.util.Calendar;

public class PatternBanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permission = SuperBan.getInstance().getConfigUtil().getStringMessage("superban.pattern", "permission.pattern.ban");
        if (sender.hasPermission(permission)) {
            if (args.length == 2) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if(offlinePlayer.getName().equals(sender.getName())){
                    sender.sendMessage(SuperBan.getInstance().getPrefix()+SuperBan.getInstance().getConfigUtil().getStringMessage("§aYou cannot ban yourself!","message.pattern.notbanmyself"));
                    return true;
                }
                if(SuperBan.getInstance().getNoBannable().contains(offlinePlayer.getName())){
                    sender.sendMessage(SuperBan.getInstance().getPrefix()+SuperBan.getInstance().getConfigUtil().getStringMessage("§aYou cannot ban this person!","message.pattern.notpossible"));
                    return true;
                }
                BanPattern banPattern = BanPattern.getPattern(args[1]);
                if (banPattern == null) {
                    sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aPattern was not found!", "message.pattern.not.found"));
                    return true;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, banPattern.getYears());
                calendar.add(Calendar.HOUR, banPattern.getDays() * 24);
                calendar.add(Calendar.HOUR, banPattern.getHours());
                calendar.add(Calendar.MINUTE, banPattern.getMinutes());
                calendar.add(Calendar.SECOND, banPattern.getSeconds());
                BanRepository.addBan(new Ban(banPattern.getType(), calendar.getTime(), offlinePlayer.getUniqueId().toString(),sender.getName(), LocalDate.now().toString()), offlinePlayer.getName());

                sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aPlayer was banned!", "message.pattern.bansuc"));
            } else {
                sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aFalse Syntax: /patternban §7<§aPlayer§7> §a<§7PatternName§a>", "message.pattern.sytnax"));
            }

        } else {
            sender.sendMessage(SuperBan.getInstance().getPrefix() +
                    SuperBan.getInstance().getConfigUtil().getStringMessage("§aYou need following permission: %permission%", "message.pattern.noperm").replace("%permission%", permission));
        }
        return false;
    }
}
