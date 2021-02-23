package de.crowraw.superban.commands;

import de.crowraw.superban.SuperBan;
import de.crowraw.superban.util.BanGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GUICommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String permission = SuperBan.getInstance().getConfigUtil().getStringMessage("superban.info", "permission.info");
            if (sender.hasPermission(permission)) {
                if (args.length != 0) {
                    sender.sendMessage(SuperBan.getInstance().getPrefix() + SuperBan.getInstance().getConfigUtil().getStringMessage("§cFalse syntax: /gui", "message.gui.syntax"));
                    return true;
                }
                Inventory inv = new BanGUI(player).getInventory();
                player.openInventory(inv);
               player.updateInventory();
            }
        }

        return false;

    }
}
