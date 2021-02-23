package de.crowraw.superban.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.crowraw.api.ItemBuilder;
import de.crowraw.superban.SuperBan;
import de.crowraw.superban.ban.Ban;
import de.crowraw.superban.ban.BanRepository;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import sun.misc.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class BanGUI implements Listener {
    private Inventory inventory;
    private Player player;

    public BanGUI(Player player) {
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, SuperBan.getInstance());
        Inventory ableInv = Bukkit.createInventory(null, 6 * 9);
        for (int i = 0; i < 54; i++) {
            ableInv.setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
        }
        inventory = ableInv;
        int rows_mysql = 0;
        int sites = 0;
        try {
            ResultSet resultSet = SuperBan.getInstance().getMySQLUtil().getCon().prepareStatement("SELECT COUNT(*) FROM BAN_STATS").executeQuery();
            resultSet.next();
            rows_mysql = resultSet.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (rows_mysql > 24) {
            sites++;
            rows_mysql = rows_mysql - 24;
        }

        int count = 0;
        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 8; j++) {
                int slot = i * 9 + j;
                count++;
                getHeadForInt(count, slot);
                if (sites >= 1) {
                    ItemStack itemStack = new ItemStack(Material.FEATHER, 1);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName("§aNext");
                    itemStack.setItemMeta(itemMeta);
                    inventory.setItem(53, itemStack);
                }
                player.updateInventory();
            }
        }


        player.updateInventory();

    }

    private void getHeadForInt(int count, int slot) {
        AtomicReference<ItemStack> itemStackAtomicReference = new AtomicReference<>();
        CompletableFuture.supplyAsync(() -> {
            try {
                String execute = "SELECT `UUID` FROM `BAN_STATS` ORDER BY `UUID` LIMIT " + (count - 1) + "," + count;
                ResultSet resultSet = SuperBan.getInstance().getMySQLUtil().getCon().prepareStatement(execute).executeQuery();
                if (!resultSet.next()) {
                    return new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                }
                BanRepository.loadBanInCache(resultSet.getString("UUID"));
                Ban ban = BanRepository.getBanForCache(resultSet.getString("UUID"));
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(ban.getUuid()));
                String name = offlinePlayer.getName();
                ItemStack itemStack = ItemBuilder.createCustomItemStack(Material.PLAYER_HEAD, "§6" + name, 1,
                        "§7UUID:§a " + ban.getUuid(),
                        "§7Banned by: §a" + ban.getBanner(),
                        "§7Banned at: §a" + ban.getBannedAt(),
                        "§7Ban-Type §a" + ban.getBanType(),
                        "§7Seconds: §a" + ban.getTimeLeft()[0],
                        "§7Minutes: §a" + ban.getTimeLeft()[1],
                        "§7Hours: §a" + ban.getTimeLeft()[2],
                        "§7Days: §a" + ban.getTimeLeft()[3],
                        "§7Years: §a" + ban.getTimeLeft()[4]);
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwningPlayer(offlinePlayer);
                itemStack.setItemMeta(skullMeta);
                inventory.setItem(slot, itemStack);
                player.updateInventory();
                return itemStack;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).whenComplete((item, exep) -> {
            if (exep != null) {
                itemStackAtomicReference.set(item);
            }
        });
        itemStackAtomicReference.get();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);
            if(event.getCurrentItem()==null){
                return;
            }
            System.out.println(event.getCurrentItem().getType());
            if (event.getCurrentItem().getType() != Material.PLAYER_HEAD) {
                return;
            }
            ItemStack itemStack = event.getCurrentItem();
            if(itemStack.getItemMeta()==null||itemStack.getItemMeta().getLore()==null){
                return;
            }
            List<String> lore = itemStack.getItemMeta().getLore();
            String loreOne = lore.get(0);
            loreOne=loreOne.replace("§7UUID:§a ","");
            String uuid = loreOne;
            Ban ban = BanRepository.getBanForCache(uuid);
            BanRepository.removeBan(ban);
            OfflinePlayer offlinePlayer=Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            player.sendMessage(SuperBan.getInstance().getPrefix()+SuperBan.getInstance().getConfigUtil().
                    getStringMessage("§aYou unbanned %player%","message.gui.unbanned").replace("%player%",offlinePlayer.getName()));
            // TODO: 03.02.2021 unban on click

        }
    }

    public Inventory getInventory() {
        return inventory;
    }

}
