package de.crowraw.superban.commands;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.Ban;
import de.crowraw.superban.ban.BanRepository;
import de.crowraw.superban.ban.BanType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDate;
import java.util.Calendar;

public class MuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permission = SuperBan.getInstance().getConfigUtil().getStringMessage("superban.mute", "plugin.mute.permission");
        if (sender.hasPermission(permission)) {
            if (args.length == 6) {
                try {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(player.getName().equals(sender.getName())){
                        sender.sendMessage(SuperBan.getInstance().getPrefix()+SuperBan.getInstance().getConfigUtil().getStringMessage("§aYou cannot ban yourself!","message.mute.notbanmyself"));
                        return true;
                    }
                    if(SuperBan.getInstance().getNoBannable().contains(player.getName())){
                        sender.sendMessage(SuperBan.getInstance().getPrefix()+SuperBan.getInstance().getConfigUtil().getStringMessage("§aYou cannot ban this person!","message.mute.notpossible"));
                        return true;
                    }

                    int years = Integer.parseInt(args[1]);
                    int days = Integer.parseInt(args[2]);
                    int hours = Integer.parseInt(args[3]);
                    int minutes = Integer.parseInt(args[4]);
                    int seconds = Integer.parseInt(args[5]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.YEAR, years);
                    calendar.add(Calendar.HOUR, days * 24);
                    calendar.add(Calendar.HOUR, hours);
                    calendar.add(Calendar.MINUTE, minutes);
                    calendar.add(Calendar.SECOND, seconds);
                    System.out.println(sender.getName());
                    if (player == null) {
                        BanRepository.addBan(new Ban(BanType.CHAT, calendar.getTime(), player.getUniqueId().toString(),sender.getName(), LocalDate.now().toString()), "noplayer");
                    } else {
                        BanRepository.addBan(new Ban(BanType.CHAT, calendar.getTime(), player.getUniqueId().toString(),sender.getName(), LocalDate.now().toString()), player.getName());
                    }
                    sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§aThe player was muted!", "message.mute.bannedsuc"));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§cError: §7" + e.toString());
                }


            } else {
                sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§cFalse syntax:" +
                        " §a/mute <§7Player§a> <§7Years§a> <§7Days§a> <§7Hours§a> <§7Minutes§a> <§7Seconds§a>", "message.mute.syntax"));
            }

        } else {
            sender.sendMessage(SuperBan.getInstance().getPrefix() +
                    SuperBan.getInstance().getConfigUtil().getStringMessage("§cYou have no permission for this! Needed permission: §a%permission%",
                            "message.mute.nopermission").replace("%permission%", permission));
        }
        return false;
    }
}

